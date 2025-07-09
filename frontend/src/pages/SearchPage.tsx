import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  Container,
  Grid,
  Typography,
  Box,
  Paper,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Slider,
  Chip,
  Button,
  Divider,
} from '@mui/material'
import { FilterList as FilterIcon } from '@mui/icons-material'
import AuctionCard from '@/components/auction/AuctionCard'
import SearchBar from '@/components/common/SearchBar'
import { useGetAuctionsQuery } from '@/store/api/auctionApi'
import { formatCurrency } from '@/utils/format'

const SearchPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [filters, setFilters] = useState({
    category: searchParams.get('category') || '',
    status: '',
    priceRange: [0, 10000000],
    source: '',
  })

  // Build query parameters for API
  const apiParams = {
    q: searchParams.get('q') || undefined,
    category: filters.category || undefined,
    status: filters.status || undefined,
  }

  const { data: auctionsData = [], isLoading } = useGetAuctionsQuery(apiParams)
  const auctions = Array.isArray(auctionsData) ? auctionsData : (auctionsData as any).items || []
  
  // Filter by price range locally since backend doesn't support it yet
  const filteredAuctions = auctions.filter((auction: any) => {
    return auction.currentPrice >= filters.priceRange[0] && 
           auction.currentPrice <= filters.priceRange[1]
  })

  const categories = [
    { value: '', label: 'All Categories' },
    { value: 'electronics', label: 'Electronics' },
    { value: 'vehicles', label: 'Vehicles' },
    { value: 'furniture', label: 'Furniture' },
    { value: 'industrial', label: 'Industrial Equipment' },
  ]

  const statuses = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'ENDING_SOON', label: 'Ending Soon' },
    { value: 'SCHEDULED', label: 'Upcoming' },
  ]

  const sources = [
    { value: '', label: 'All Sources' },
    { value: 'Government Surplus', label: 'Government Surplus' },
    { value: 'Vehicle Auctions', label: 'Vehicle Auctions' },
  ]


  const handleFilterChange = (field: string, value: any) => {
    setFilters({ ...filters, [field]: value })
  }

  const handleSearch = (query: string) => {
    setSearchParams({ ...Object.fromEntries(searchParams), q: query })
  }

  const clearFilters = () => {
    setFilters({
      category: '',
      status: '',
      priceRange: [0, 10000000],
      source: '',
    })
    setSearchParams({})
  }

  return (
    <Container maxWidth="lg" sx={{ py: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Search Auctions
      </Typography>

      <Box sx={{ mb: 3 }}>
        <SearchBar onSearch={handleSearch} />
      </Box>

      <Grid container spacing={3}>
        {/* Filters Sidebar */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <FilterIcon sx={{ mr: 1 }} />
              <Typography variant="h6">Filters</Typography>
            </Box>

            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel>Category</InputLabel>
              <Select
                value={filters.category}
                onChange={(e) => handleFilterChange('category', e.target.value)}
                label="Category"
              >
                {categories.map((cat) => (
                  <MenuItem key={cat.value} value={cat.value}>
                    {cat.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={filters.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                label="Status"
              >
                {statuses.map((status) => (
                  <MenuItem key={status.value} value={status.value}>
                    {status.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Box sx={{ mb: 3 }}>
              <Typography gutterBottom>Price Range</Typography>
              <Slider
                value={filters.priceRange}
                onChange={(e, value) => handleFilterChange('priceRange', value)}
                valueLabelDisplay="auto"
                valueLabelFormat={(value) => formatCurrency(value)}
                min={0}
                max={10000000}
                step={10000}
              />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                <Typography variant="caption">
                  {formatCurrency(filters.priceRange[0])}
                </Typography>
                <Typography variant="caption">
                  {formatCurrency(filters.priceRange[1])}
                </Typography>
              </Box>
            </Box>

            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel>Source</InputLabel>
              <Select
                value={filters.source}
                onChange={(e) => handleFilterChange('source', e.target.value)}
                label="Source"
              >
                {sources.map((source) => (
                  <MenuItem key={source.value} value={source.value}>
                    {source.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Button fullWidth variant="outlined" onClick={clearFilters}>
              Clear Filters
            </Button>
          </Paper>
        </Grid>

        {/* Results */}
        <Grid item xs={12} md={9}>
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6">
              {filteredAuctions.length} Auctions Found
            </Typography>
          </Box>

          <Grid container spacing={3}>
            {filteredAuctions.map((auction) => (
              <Grid item xs={12} sm={6} lg={4} key={auction.id}>
                <AuctionCard auction={auction} />
              </Grid>
            ))}
          </Grid>

          {filteredAuctions.length === 0 && (
            <Paper sx={{ p: 4, textAlign: 'center' }}>
              <Typography variant="h6" color="text.secondary">
                No auctions found matching your criteria
              </Typography>
              <Button 
                variant="outlined" 
                sx={{ mt: 2 }} 
                onClick={clearFilters}
              >
                Clear Filters
              </Button>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Container>
  )
}

export default SearchPage
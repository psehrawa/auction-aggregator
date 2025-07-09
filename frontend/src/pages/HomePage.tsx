import { useState, useEffect } from 'react'
import {
  Container,
  Typography,
  Box,
  Grid,
  Button,
  CircularProgress,
  Paper,
  Tab,
  Tabs,
} from '@mui/material'
import { TrendingUp, Timer, LocalOffer, Category } from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'
import AuctionCard from '@/components/auction/AuctionCard'
import SearchBar from '@/components/common/SearchBar'
import CategoryGrid from '@/components/home/CategoryGrid'
import HeroSection from '@/components/home/HeroSection'
import { 
  useGetTrendingAuctionsQuery,
  useGetEndingSoonAuctionsQuery,
  allMockAuctions 
} from '@/store/api/auctionApi'

const HomePage = () => {
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState(0)
  
  // Use real API calls
  const { data: trendingAuctions = [], isLoading: trendingLoading } = useGetTrendingAuctionsQuery(12)
  const { data: endingSoonAuctions = [], isLoading: endingSoonLoading } = useGetEndingSoonAuctionsQuery(12)
  
  // For great deals, use all auctions and filter
  const { data: allAuctions = [] } = useGetTrendingAuctionsQuery(20)
  const greatDeals = (allAuctions.items || allAuctions as any[])
    .filter((a: any) => a.status === 'ACTIVE')
    .filter((a: any) => (a.currentPrice / a.startingPrice) < 1.5)
    .slice(0, 12)

  const handleSearch = (query: string) => {
    navigate(`/search?q=${encodeURIComponent(query)}`)
  }

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue)
  }

  return (
    <>
      <HeroSection />
      
      <Container maxWidth="lg" sx={{ py: 4 }}>
        {/* Search Section */}
        <Paper elevation={3} sx={{ p: 4, mb: 6 }}>
          <Typography variant="h5" gutterBottom align="center">
            Find Your Next Great Deal
          </Typography>
          <SearchBar onSearch={handleSearch} />
        </Paper>

        {/* Categories Section */}
        <Box sx={{ mb: 6 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
            <Category sx={{ mr: 2, color: 'primary.main' }} />
            <Typography variant="h4" component="h2">
              Browse Categories
            </Typography>
          </Box>
          <CategoryGrid />
        </Box>

        {/* Featured Auctions */}
        <Box sx={{ mb: 6 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
            <Tabs value={activeTab} onChange={handleTabChange}>
              <Tab 
                icon={<TrendingUp />} 
                label="Trending Now" 
                iconPosition="start"
              />
              <Tab 
                icon={<Timer />} 
                label="Ending Soon" 
                iconPosition="start"
              />
              <Tab 
                icon={<LocalOffer />} 
                label="Great Deals" 
                iconPosition="start"
              />
            </Tabs>
          </Box>

          {/* Trending Auctions */}
          {activeTab === 0 && (
            <>
              {trendingLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : trendingAuctions && trendingAuctions.length > 0 ? (
                <Grid container spacing={3}>
                  {trendingAuctions.map((auction: any) => (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={auction.id}>
                      <AuctionCard auction={auction} />
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography variant="h6" color="text.secondary">
                    No trending auctions available
                  </Typography>
                </Box>
              )}
            </>
          )}

          {/* Ending Soon */}
          {activeTab === 1 && (
            <>
              {endingSoonLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : endingSoonAuctions && endingSoonAuctions.length > 0 ? (
                <Grid container spacing={3}>
                  {endingSoonAuctions.map((auction: any) => (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={auction.id}>
                      <AuctionCard auction={auction} />
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography variant="h6" color="text.secondary">
                    No ending soon auctions available
                  </Typography>
                </Box>
              )}
            </>
          )}

          {/* Great Deals */}
          {activeTab === 2 && (
            <>
              {greatDeals && greatDeals.length > 0 ? (
                <Grid container spacing={3}>
                  {greatDeals.map((auction: any) => (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={auction.id}>
                      <AuctionCard auction={auction} />
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography variant="h6" color="text.secondary">
                    No great deals available
                  </Typography>
                </Box>
              )}
            </>
          )}

          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/search')}
            >
              View All Auctions
            </Button>
          </Box>
        </Box>

        {/* Info Section */}
        <Grid container spacing={4} sx={{ mt: 6 }}>
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, textAlign: 'center', height: '100%' }}>
              <Typography variant="h6" gutterBottom>
                Wide Selection
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Browse thousands of auctions from multiple sources including government surplus, vehicles, electronics, and more.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, textAlign: 'center', height: '100%' }}>
              <Typography variant="h6" gutterBottom>
                Real-time Bidding
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Place bids in real-time with instant notifications. Never miss an opportunity with our advanced bidding system.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, textAlign: 'center', height: '100%' }}>
              <Typography variant="h6" gutterBottom>
                Secure Payments
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Multiple payment options including UPI, cards, and wallets. All transactions are secure and protected.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </>
  )
}

export default HomePage
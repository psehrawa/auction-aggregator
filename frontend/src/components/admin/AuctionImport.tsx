import { useState } from 'react'
import {
  Box,
  Typography,
  Button,
  Chip,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Checkbox,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Tabs,
  Tab,
  Badge,
  Grid,
} from '@mui/material'
import {
  Visibility,
  CheckCircle,
  Cancel,
  ImportExport,
} from '@mui/icons-material'

interface ScrapedAuction {
  id: string
  source: string
  title: string
  location: string
  startingPrice: number
  endTime: string
  category: string
  status: 'pending' | 'approved' | 'rejected'
  scrapedAt: string
  imageUrl: string
}

const AuctionImport = () => {
  const [selectedTab, setSelectedTab] = useState(0)
  const [selectedAuctions, setSelectedAuctions] = useState<string[]>([])
  const [filterSource, setFilterSource] = useState('all')
  const [previewAuction, setPreviewAuction] = useState<ScrapedAuction | null>(null)
  
  const [scrapedAuctions, setScrapedAuctions] = useState<ScrapedAuction[]>([
    {
      id: '1',
      source: 'SBI Auctions',
      title: '3 BHK Flat in Mumbai, Andheri West',
      location: 'Mumbai, Maharashtra',
      startingPrice: 8500000,
      endTime: '2024-01-20T15:00:00',
      category: 'real-estate',
      status: 'pending',
      scrapedAt: '2024-01-10T14:30:00',
      imageUrl: 'https://picsum.photos/200/150?random=1',
    },
    {
      id: '2',
      source: 'HDFC Bank E-Auctions',
      title: 'Commercial Property in Bangalore',
      location: 'Bangalore, Karnataka',
      startingPrice: 15000000,
      endTime: '2024-01-25T12:00:00',
      category: 'real-estate',
      status: 'pending',
      scrapedAt: '2024-01-10T15:00:00',
      imageUrl: 'https://picsum.photos/200/150?random=2',
    },
    {
      id: '3',
      source: 'ICICI Bank Properties',
      title: 'Agricultural Land 5 Acres',
      location: 'Pune, Maharashtra',
      startingPrice: 4500000,
      endTime: '2024-01-18T10:00:00',
      category: 'real-estate',
      status: 'approved',
      scrapedAt: '2024-01-10T13:15:00',
      imageUrl: 'https://picsum.photos/200/150?random=3',
    },
    {
      id: '4',
      source: 'Bajaj Finance Auctions',
      title: 'Honda City 2019 Model',
      location: 'Delhi',
      startingPrice: 450000,
      endTime: '2024-01-15T16:00:00',
      category: 'vehicles',
      status: 'pending',
      scrapedAt: '2024-01-10T12:00:00',
      imageUrl: 'https://picsum.photos/200/150?random=4',
    },
    {
      id: '5',
      source: 'SBI Auctions',
      title: 'Gold Jewelry Set - 50 grams',
      location: 'Chennai, Tamil Nadu',
      startingPrice: 250000,
      endTime: '2024-01-14T11:00:00',
      category: 'jewelry',
      status: 'rejected',
      scrapedAt: '2024-01-10T14:30:00',
      imageUrl: 'https://picsum.photos/200/150?random=5',
    },
  ])

  const pendingAuctions = scrapedAuctions.filter(a => a.status === 'pending')
  const approvedAuctions = scrapedAuctions.filter(a => a.status === 'approved')
  const rejectedAuctions = scrapedAuctions.filter(a => a.status === 'rejected')

  const getCurrentTabAuctions = () => {
    switch (selectedTab) {
      case 0:
        return pendingAuctions
      case 1:
        return approvedAuctions
      case 2:
        return rejectedAuctions
      default:
        return []
    }
  }

  const filteredAuctions = getCurrentTabAuctions().filter(
    auction => filterSource === 'all' || auction.source === filterSource
  )

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedAuctions(filteredAuctions.map(a => a.id))
    } else {
      setSelectedAuctions([])
    }
  }

  const handleSelectAuction = (id: string) => {
    if (selectedAuctions.includes(id)) {
      setSelectedAuctions(selectedAuctions.filter(aId => aId !== id))
    } else {
      setSelectedAuctions([...selectedAuctions, id])
    }
  }

  const handleApprove = (ids: string[]) => {
    setScrapedAuctions(scrapedAuctions.map(a => 
      ids.includes(a.id) ? { ...a, status: 'approved' } : a
    ))
    setSelectedAuctions([])
  }

  const handleReject = (ids: string[]) => {
    setScrapedAuctions(scrapedAuctions.map(a => 
      ids.includes(a.id) ? { ...a, status: 'rejected' } : a
    ))
    setSelectedAuctions([])
  }

  const handleImport = () => {
    // Import approved auctions to main database
    alert(`Importing ${approvedAuctions.length} approved auctions to the main platform...`)
  }

  const uniqueSources = Array.from(new Set(scrapedAuctions.map(a => a.source)))

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">Import Scraped Auctions</Typography>
        <Box>
          {selectedTab === 1 && (
            <Button
              startIcon={<ImportExport />}
              variant="contained"
              onClick={handleImport}
              disabled={approvedAuctions.length === 0}
            >
              Import to Platform ({approvedAuctions.length})
            </Button>
          )}
        </Box>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        Review and approve scraped auctions before importing them to the main platform. 
        Ensure all information is accurate and images are properly loaded.
      </Alert>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={selectedTab} onChange={(_, v) => setSelectedTab(v)}>
          <Tab 
            label={
              <Badge badgeContent={pendingAuctions.length} color="warning">
                <Box component="span">Pending Review</Box>
              </Badge>
            } 
          />
          <Tab 
            label={
              <Badge badgeContent={approvedAuctions.length} color="success">
                <Box component="span">Approved</Box>
              </Badge>
            } 
          />
          <Tab 
            label={
              <Badge badgeContent={rejectedAuctions.length} color="error">
                <Box component="span">Rejected</Box>
              </Badge>
            } 
          />
        </Tabs>
      </Paper>

      <Box sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'center' }}>
        <FormControl size="small" sx={{ minWidth: 200 }}>
          <InputLabel>Filter by Source</InputLabel>
          <Select
            value={filterSource}
            label="Filter by Source"
            onChange={(e) => setFilterSource(e.target.value)}
          >
            <MenuItem value="all">All Sources</MenuItem>
            {uniqueSources.map(source => (
              <MenuItem key={source} value={source}>{source}</MenuItem>
            ))}
          </Select>
        </FormControl>
        
        {selectedAuctions.length > 0 && selectedTab === 0 && (
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button
              startIcon={<CheckCircle />}
              variant="contained"
              color="success"
              onClick={() => handleApprove(selectedAuctions)}
            >
              Approve ({selectedAuctions.length})
            </Button>
            <Button
              startIcon={<Cancel />}
              variant="contained"
              color="error"
              onClick={() => handleReject(selectedAuctions)}
            >
              Reject ({selectedAuctions.length})
            </Button>
          </Box>
        )}
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              {selectedTab === 0 && (
                <TableCell padding="checkbox">
                  <Checkbox
                    checked={selectedAuctions.length === filteredAuctions.length && filteredAuctions.length > 0}
                    indeterminate={selectedAuctions.length > 0 && selectedAuctions.length < filteredAuctions.length}
                    onChange={(e) => handleSelectAll(e.target.checked)}
                  />
                </TableCell>
              )}
              <TableCell>Image</TableCell>
              <TableCell>Title</TableCell>
              <TableCell>Source</TableCell>
              <TableCell>Location</TableCell>
              <TableCell>Starting Price</TableCell>
              <TableCell>End Time</TableCell>
              <TableCell>Category</TableCell>
              <TableCell>Scraped At</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredAuctions.map((auction) => (
              <TableRow key={auction.id}>
                {selectedTab === 0 && (
                  <TableCell padding="checkbox">
                    <Checkbox
                      checked={selectedAuctions.includes(auction.id)}
                      onChange={() => handleSelectAuction(auction.id)}
                    />
                  </TableCell>
                )}
                <TableCell>
                  <img 
                    src={auction.imageUrl} 
                    alt={auction.title}
                    style={{ width: 80, height: 60, objectFit: 'cover' }}
                  />
                </TableCell>
                <TableCell>
                  <Typography variant="body2" sx={{ maxWidth: 300 }}>
                    {auction.title}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Chip label={auction.source} size="small" />
                </TableCell>
                <TableCell>{auction.location}</TableCell>
                <TableCell>₹{auction.startingPrice.toLocaleString()}</TableCell>
                <TableCell>{new Date(auction.endTime).toLocaleDateString()}</TableCell>
                <TableCell>
                  <Chip label={auction.category} size="small" variant="outlined" />
                </TableCell>
                <TableCell>{new Date(auction.scrapedAt).toLocaleString()}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <IconButton 
                      size="small" 
                      onClick={() => setPreviewAuction(auction)}
                      title="Preview"
                    >
                      <Visibility />
                    </IconButton>
                    {selectedTab === 0 && (
                      <>
                        <IconButton 
                          size="small" 
                          color="success"
                          onClick={() => handleApprove([auction.id])}
                          title="Approve"
                        >
                          <CheckCircle />
                        </IconButton>
                        <IconButton 
                          size="small" 
                          color="error"
                          onClick={() => handleReject([auction.id])}
                          title="Reject"
                        >
                          <Cancel />
                        </IconButton>
                      </>
                    )}
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Preview Dialog */}
      <Dialog open={!!previewAuction} onClose={() => setPreviewAuction(null)} maxWidth="md" fullWidth>
        <DialogTitle>Auction Preview</DialogTitle>
        <DialogContent>
          {previewAuction && (
            <Box>
              <img 
                src={previewAuction.imageUrl} 
                alt={previewAuction.title}
                style={{ width: '100%', height: 300, objectFit: 'cover', marginBottom: 16 }}
              />
              <Typography variant="h6" gutterBottom>{previewAuction.title}</Typography>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Source</Typography>
                  <Typography>{previewAuction.source}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Location</Typography>
                  <Typography>{previewAuction.location}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Starting Price</Typography>
                  <Typography>₹{previewAuction.startingPrice.toLocaleString()}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">End Time</Typography>
                  <Typography>{new Date(previewAuction.endTime).toLocaleString()}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Category</Typography>
                  <Typography>{previewAuction.category}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Status</Typography>
                  <Chip 
                    label={previewAuction.status} 
                    color={
                      previewAuction.status === 'approved' ? 'success' : 
                      previewAuction.status === 'rejected' ? 'error' : 'warning'
                    } 
                  />
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPreviewAuction(null)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default AuctionImport
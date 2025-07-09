import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Container,
  Grid,
  Typography,
  Box,
  Paper,
  Button,
  TextField,
  Chip,
  Divider,
  List,
  ListItem,
  ListItemText,
  Avatar,
  IconButton,
  Tabs,
  Tab,
} from '@mui/material'
import {
  Timer,
  Visibility,
  FavoriteBorder,
  Share,
  Gavel,
  LocationOn,
  Category,
  OpenInNew,
  Description,
} from '@mui/icons-material'
import { formatCurrency, formatDate } from '@/utils/format'
import { useGetAuctionQuery } from '@/store/api/auctionApi'
import toast from 'react-hot-toast'

interface TabPanelProps {
  children?: React.ReactNode
  index: number
  value: number
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props
  return (
    <div hidden={value !== index} {...other}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  )
}

const AuctionDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [bidAmount, setBidAmount] = useState('')
  const [tabValue, setTabValue] = useState(0)

  const { data: auction, isLoading, error } = useGetAuctionQuery(id || '', { skip: !id })

  if (isLoading) {
    return (
      <Container>
        <Typography variant="h5">Loading...</Typography>
      </Container>
    )
  }

  if (error || !auction) {
    return (
      <Container>
        <Typography variant="h5">Auction not found</Typography>
        <Button onClick={() => navigate('/')}>Go Back</Button>
      </Container>
    )
  }

  const nextMinBid = (auction as any).currentPrice + ((auction as any).bidIncrement || 1000)

  const handlePlaceBid = () => {
    const bid = parseFloat(bidAmount)
    if (isNaN(bid) || bid < nextMinBid) {
      toast.error(`Bid must be at least ${formatCurrency(nextMinBid)}`)
      return
    }
    
    toast.success('Bid placed successfully!')
    setBidAmount('')
  }

  const timeRemaining = () => {
    const end = new Date((auction as any).endTime)
    const now = new Date()
    const diff = end.getTime() - now.getTime()
    
    if (diff <= 0) return 'Auction Ended'
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    
    if (days > 0) return `${days}d ${hours}h ${minutes}m`
    if (hours > 0) return `${hours}h ${minutes}m`
    return `${minutes}m`
  }

  return (
    <Container maxWidth="lg" sx={{ py: 3 }}>
      <Grid container spacing={3}>
        {/* Images Section */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2 }}>
            <Box
              component="img"
              src={auction.images?.[0]?.url || 'https://picsum.photos/600/400'}
              alt={auction.title}
              sx={{ width: '100%', height: 400, objectFit: 'cover' }}
            />
          </Paper>
        </Grid>

        {/* Auction Info Section */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              {auction.title}
            </Typography>

            <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
              <Chip label={(auction as any).status} color="primary" />
              <Chip label={(auction as any).categoryId} variant="outlined" />
              {(auction as any).tags?.map((tag: string) => (
                <Chip key={tag} label={tag} size="small" />
              ))}
            </Box>

            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Timer sx={{ mr: 1 }} />
                <Typography variant="h6">{timeRemaining()}</Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Visibility sx={{ mr: 1 }} />
                <Typography>{(auction as any).viewCount || 0} views</Typography>
              </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">
                  Current Bid
                </Typography>
                <Typography variant="h4" color="primary">
                  {formatCurrency((auction as any).currentPrice)}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">
                  Starting Price
                </Typography>
                <Typography variant="h5">
                  {formatCurrency((auction as any).startingPrice)}
                </Typography>
              </Grid>
            </Grid>

            {/* Bidding Section */}
            {(auction as any).status === 'ACTIVE' && (
              <Box sx={{ mb: 3 }}>
                <Typography variant="subtitle1" gutterBottom>
                  Place Your Bid (Min: {formatCurrency(nextMinBid)})
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <TextField
                    fullWidth
                    type="number"
                    placeholder={nextMinBid.toString()}
                    value={bidAmount}
                    onChange={(e) => setBidAmount(e.target.value)}
                  />
                  <Button
                    variant="contained"
                    size="large"
                    onClick={handlePlaceBid}
                    startIcon={<Gavel />}
                  >
                    Bid Now
                  </Button>
                </Box>
              </Box>
            )}

            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button variant="outlined" startIcon={<FavoriteBorder />}>
                Watch
              </Button>
              {(auction as any).sourceUrl && (
                <Button
                  variant="outlined"
                  startIcon={<OpenInNew />}
                  onClick={() => window.open((auction as any).sourceUrl, '_blank')}
                >
                  View Source
                </Button>
              )}
              {(auction as any).sourcePdfUrl && (
                <Button
                  variant="outlined"
                  startIcon={<Description />}
                  onClick={() => window.open((auction as any).sourcePdfUrl, '_blank')}
                >
                  PDF
                </Button>
              )}
              <IconButton>
                <Share />
              </IconButton>
            </Box>
          </Paper>
        </Grid>

        {/* Details Tabs */}
        <Grid item xs={12}>
          <Paper>
            <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)}>
              <Tab label="Description" />
              <Tab label="Bid History" />
              <Tab label="Seller Info" />
            </Tabs>

            <TabPanel value={tabValue} index={0}>
              <Typography paragraph>{auction.description}</Typography>
              <Box sx={{ mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Additional Details
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                      <Category sx={{ mr: 1, color: 'text.secondary' }} />
                      <Typography>
                        Category: {auction.categoryId}
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                      <LocationOn sx={{ mr: 1, color: 'text.secondary' }} />
                      <Typography>
                        Location: Mumbai, Maharashtra
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </Box>
            </TabPanel>

            <TabPanel value={tabValue} index={1}>
              <List>
                {[1, 2, 3, 4, 5].map((i) => (
                  <ListItem key={i}>
                    <Avatar sx={{ mr: 2 }}>{i}</Avatar>
                    <ListItemText
                      primary={`Bidder ${i} - ${formatCurrency(auction.currentPrice - i * 5000)}`}
                      secondary={`${i} minutes ago`}
                    />
                  </ListItem>
                ))}
              </List>
            </TabPanel>

            <TabPanel value={tabValue} index={2}>
              <Typography variant="h6" gutterBottom>
                Seller Information
              </Typography>
              <Typography paragraph>
                Seller ID: {auction.sellerId}
              </Typography>
              <Typography>
                Member since: {formatDate(auction.createdAt)}
              </Typography>
            </TabPanel>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  )
}

export default AuctionDetailPage
import { Box, Container, Typography, Button, Grid } from '@mui/material'
import { useNavigate } from 'react-router-dom'

const HeroSection = () => {
  const navigate = useNavigate()

  return (
    <Box
      sx={{
        backgroundImage: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
        color: 'white',
        py: 8,
        mb: 4,
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4} alignItems="center">
          <Grid item xs={12} md={6}>
            <Typography variant="h2" component="h1" gutterBottom>
              India's Premier Auction Aggregator
            </Typography>
            <Typography variant="h5" paragraph>
              Discover amazing deals from government surplus, vehicle auctions, 
              and more - all in one place.
            </Typography>
            <Box sx={{ mt: 4 }}>
              <Button 
                variant="contained" 
                size="large" 
                color="secondary"
                onClick={() => navigate('/search')}
                sx={{ mr: 2 }}
              >
                Browse Auctions
              </Button>
              <Button 
                variant="outlined" 
                size="large" 
                sx={{ color: 'white', borderColor: 'white' }}
                onClick={() => navigate('/register')}
              >
                Start Selling
              </Button>
            </Box>
          </Grid>
          <Grid item xs={12} md={6}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h3" sx={{ mb: 2 }}>
                Live Auctions
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Box sx={{ bgcolor: 'rgba(255,255,255,0.1)', p: 2, borderRadius: 2 }}>
                    <Typography variant="h4">1,234</Typography>
                    <Typography>Active Auctions</Typography>
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box sx={{ bgcolor: 'rgba(255,255,255,0.1)', p: 2, borderRadius: 2 }}>
                    <Typography variant="h4">₹50L+</Typography>
                    <Typography>Total Value</Typography>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          </Grid>
        </Grid>
      </Container>
    </Box>
  )
}

export default HeroSection
import { useState } from 'react'
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  LinearProgress,
  Chip,
  Paper,
  Grid,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  FormControlLabel,
  Switch,
  IconButton,
} from '@mui/material'
import {
  PlayArrow,
  ExpandMore,
  ContentCopy,
  CheckCircle,
} from '@mui/icons-material'

interface ScrapeOptions {
  requiresJS: boolean
  contentSelector: string
  titleSelector: string
  priceSelector: string
  locationSelector: string
  dateSelector: string
  waitTime?: number
}

const RealTimeScraper = () => {
  const [url, setUrl] = useState('')
  const [scraping, setScraping] = useState(false)
  const [result, setResult] = useState<any>(null)
  const [error, setError] = useState('')
  const [options, setOptions] = useState<ScrapeOptions>({
    requiresJS: false,
    contentSelector: '',
    titleSelector: '',
    priceSelector: '',
    locationSelector: '',
    dateSelector: '',
  })

  // Predefined configurations for known sites
  const presetConfigs = [
    {
      name: 'Kotak Bank Auctions',
      config: {
        requiresJS: true,
        contentSelector: '.container, .search-results, .auction-list',
        titleSelector: '.property-title, .asset-name, h3, h4',
        priceSelector: '.reserve-price, .price, .amount, [class*="price"]',
        locationSelector: '.location, .city, .address, [class*="location"]',
        dateSelector: '.auction-date, .end-date, .date, time',
        waitTime: 5000,
      }
    },
    {
      name: 'Generic Bank Auction',
      config: {
        requiresJS: true,
        contentSelector: '.auction-listing, .property-listing, .search-results',
        titleSelector: 'h1, h2, h3, .title, .property-name',
        priceSelector: '.reserve-price, .starting-price, .price, [class*="price"]',
        locationSelector: '.location, .address, .city, [class*="location"]',
        dateSelector: '.auction-date, .end-date, .date, time',
        waitTime: 3000,
      }
    },
    {
      name: 'Government Portal',
      config: {
        requiresJS: false,
        contentSelector: 'table, .auction-table, .listing-table',
        titleSelector: 'td:nth-child(2), .property-description',
        priceSelector: 'td:contains("Reserve"), .reserve-amount',
        locationSelector: 'td:contains("Location"), .property-location',
        dateSelector: 'td:contains("Date"), .auction-date',
      }
    },
    {
      name: 'Generic Auction Site',
      config: {
        requiresJS: false,
        contentSelector: '.auction-listing, .property-listing, .item-listing',
        titleSelector: 'h1, h2, .title, .heading',
        priceSelector: '.price, .amount, .cost, [class*="price"]',
        locationSelector: '.location, .address, .city',
        dateSelector: '.date, .end-date, .auction-date, time',
      }
    }
  ]

  const handleScrape = async () => {
    if (!url) {
      setError('Please enter a URL')
      return
    }

    setScraping(true)
    setError('')
    setResult(null)

    try {
      const response = await fetch('/api/v1/scrapers/real/scrape', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          url,
          options: {
            ...options,
            requiresJS: options.requiresJS ? 'true' : 'false',
          },
          async: false,
        }),
      })

      const data = await response.json()
      
      if (data.status === 'completed' && data.data) {
        setResult(data.data)
        
        // Auto-extract auction data
        const extractResponse = await fetch('/api/v1/scrapers/real/extract-auction', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(data.data),
        })
        
        const auctionData = await extractResponse.json()
        setResult(prev => ({ ...prev, auctionData }))
      } else {
        setError(data.message || 'Scraping failed')
      }
    } catch (err: any) {
      setError(`Error: ${err.message || err}`)
    } finally {
      setScraping(false)
    }
  }

  const applyPreset = (preset: any) => {
    setOptions(preset.config)
  }

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text)
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Real-Time Web Scraper
      </Typography>

      <Alert severity="info" sx={{ mb: 2 }}>
        Enter any website URL to extract auction data. The scraper will automatically detect and extract relevant information.
      </Alert>

      <Alert severity="warning" sx={{ mb: 3 }}>
        <strong>For Kotak Bank auctions:</strong> Use <code>auctions.kotak.com</code> (not the main homepage). 
        Other bank auction sites: Look for "e-auction" or "property auction" sections on their websites.
      </Alert>

      <Grid container spacing={3}>
        {/* Input Section */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Scraper Configuration
              </Typography>

              <TextField
                fullWidth
                label="Website URL"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="https://example.com/auctions"
                margin="normal"
              />

              <Box sx={{ mt: 2, mb: 2 }}>
                <Typography variant="subtitle2" gutterBottom>
                  Quick Presets:
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  {presetConfigs.map((preset) => (
                    <Chip
                      key={preset.name}
                      label={preset.name}
                      onClick={() => applyPreset(preset)}
                      color="primary"
                      variant="outlined"
                    />
                  ))}
                </Box>
              </Box>

              <Accordion>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <Typography>Advanced Options</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={options.requiresJS}
                        onChange={(e) => setOptions({ ...options, requiresJS: e.target.checked })}
                      />
                    }
                    label="Site requires JavaScript rendering"
                  />

                  <TextField
                    fullWidth
                    label="Content Selector"
                    value={options.contentSelector}
                    onChange={(e) => setOptions({ ...options, contentSelector: e.target.value })}
                    margin="normal"
                    helperText="CSS selector for main content area"
                  />

                  <TextField
                    fullWidth
                    label="Title Selector"
                    value={options.titleSelector}
                    onChange={(e) => setOptions({ ...options, titleSelector: e.target.value })}
                    margin="normal"
                  />

                  <TextField
                    fullWidth
                    label="Price Selector"
                    value={options.priceSelector}
                    onChange={(e) => setOptions({ ...options, priceSelector: e.target.value })}
                    margin="normal"
                  />

                  <TextField
                    fullWidth
                    label="Location Selector"
                    value={options.locationSelector}
                    onChange={(e) => setOptions({ ...options, locationSelector: e.target.value })}
                    margin="normal"
                  />

                  <TextField
                    fullWidth
                    label="Date Selector"
                    value={options.dateSelector}
                    onChange={(e) => setOptions({ ...options, dateSelector: e.target.value })}
                    margin="normal"
                  />

                  {options.requiresJS && (
                    <TextField
                      fullWidth
                      type="number"
                      label="Wait Time (ms)"
                      value={options.waitTime || 0}
                      onChange={(e) => setOptions({ ...options, waitTime: parseInt(e.target.value) })}
                      margin="normal"
                      helperText="Time to wait for content to load"
                    />
                  )}
                </AccordionDetails>
              </Accordion>

              <Button
                fullWidth
                variant="contained"
                startIcon={scraping ? <LinearProgress /> : <PlayArrow />}
                onClick={handleScrape}
                disabled={scraping}
                sx={{ mt: 3 }}
              >
                {scraping ? 'Scraping...' : 'Start Scraping'}
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Results Section */}
        <Grid item xs={12} md={6}>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {result && (
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Scraping Results
                </Typography>

                {/* Extracted Auction Data */}
                {result.auctionData && (
                  <Paper sx={{ p: 2, mb: 2, bgcolor: 'success.light' }}>
                    <Typography variant="subtitle1" gutterBottom>
                      <CheckCircle sx={{ mr: 1, verticalAlign: 'middle' }} />
                      Extracted Auction Data
                    </Typography>
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <Typography variant="body2">
                          <strong>Title:</strong> {result.auctionData.title || 'N/A'}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2">
                          <strong>Price:</strong> ₹{result.auctionData.price || '0'}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2">
                          <strong>Location:</strong> {result.auctionData.location || 'N/A'}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2">
                          <strong>End Date:</strong> {result.auctionData.endDate || 'N/A'}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2">
                          <strong>Category:</strong> {result.auctionData.category || 'N/A'}
                        </Typography>
                      </Grid>
                      {result.auctionData.images && result.auctionData.images.length > 0 && (
                        <Grid item xs={12}>
                          <Typography variant="body2">
                            <strong>Images Found:</strong> {result.auctionData.images.length}
                          </Typography>
                          <Box sx={{ display: 'flex', gap: 1, mt: 1, flexWrap: 'wrap' }}>
                            {result.auctionData.images.slice(0, 3).map((img: string, idx: number) => (
                              <img 
                                key={idx} 
                                src={img} 
                                alt={`Auction ${idx}`}
                                style={{ width: 80, height: 60, objectFit: 'cover' }}
                              />
                            ))}
                          </Box>
                        </Grid>
                      )}
                    </Grid>
                  </Paper>
                )}

                {/* Raw Data */}
                <Accordion>
                  <AccordionSummary expandIcon={<ExpandMore />}>
                    <Typography>Raw Scraped Data</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Box sx={{ position: 'relative' }}>
                      <IconButton
                        size="small"
                        onClick={() => copyToClipboard(JSON.stringify(result.extractedData, null, 2))}
                        sx={{ position: 'absolute', right: 0, top: 0 }}
                      >
                        <ContentCopy />
                      </IconButton>
                      <pre style={{ overflow: 'auto', fontSize: '12px' }}>
                        {JSON.stringify(result.extractedData, null, 2)}
                      </pre>
                    </Box>
                  </AccordionDetails>
                </Accordion>

                {/* Page Info */}
                <Box sx={{ mt: 2 }}>
                  <Typography variant="body2">
                    <strong>Page Title:</strong> {result.title}
                  </Typography>
                  <Typography variant="body2">
                    <strong>Images Found:</strong> {result.images?.length || 0}
                  </Typography>
                  <Typography variant="body2">
                    <strong>Scraped At:</strong> {new Date(result.scrapedAt).toLocaleString()}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>
    </Box>
  )
}

export default RealTimeScraper
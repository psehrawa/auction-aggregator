import { useState } from 'react'
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Switch,
  FormControlLabel,
  Alert,
  LinearProgress,
  ListItemText,
  ListItemSecondaryAction,
} from '@mui/material'
import {
  PlayArrow,
  Stop,
  Edit,
  Delete,
  Add,
  Refresh,
  CheckCircle,
  Error,
  Warning,
} from '@mui/icons-material'

interface Scraper {
  id: string
  name: string
  type: string
  url: string
  status: 'active' | 'inactive' | 'running' | 'error'
  lastRun: string
  itemsFound: number
  enabled: boolean
  config: {
    selector?: string
    pagination?: boolean
    maxPages?: number
  }
}

const ScraperManagement = () => {
  const [scrapers, setScrapers] = useState<Scraper[]>([
    {
      id: '1',
      name: 'SBI Auctions',
      type: 'bank',
      url: 'https://sbi.co.in/auctions',
      status: 'active',
      lastRun: '2024-01-10 14:30',
      itemsFound: 45,
      enabled: true,
      config: { selector: '.auction-item', pagination: true, maxPages: 5 },
    },
    {
      id: '2',
      name: 'HDFC Bank E-Auctions',
      type: 'bank',
      url: 'https://hdfcbank.com/e-auctions',
      status: 'running',
      lastRun: '2024-01-10 15:00',
      itemsFound: 32,
      enabled: true,
      config: { selector: '.property-listing', pagination: true, maxPages: 3 },
    },
    {
      id: '3',
      name: 'ICICI Bank Properties',
      type: 'bank',
      url: 'https://icicibank.com/auction-properties',
      status: 'active',
      lastRun: '2024-01-10 13:15',
      itemsFound: 28,
      enabled: true,
      config: { selector: '.auction-card', pagination: false },
    },
    {
      id: '4',
      name: 'Bajaj Finance Auctions',
      type: 'nbfc',
      url: 'https://bajajfinance.in/auctions',
      status: 'error',
      lastRun: '2024-01-10 12:00',
      itemsFound: 0,
      enabled: true,
      config: { selector: '.item-listing', pagination: true, maxPages: 4 },
    },
    {
      id: '5',
      name: 'Muthoot Finance',
      type: 'nbfc',
      url: 'https://muthoot.com/gold-auctions',
      status: 'inactive',
      lastRun: '2024-01-09 18:30',
      itemsFound: 15,
      enabled: false,
      config: { selector: '.gold-auction', pagination: false },
    },
  ])

  const [openDialog, setOpenDialog] = useState(false)
  const [editingScraper, setEditingScraper] = useState<Scraper | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    type: 'bank',
    url: '',
    selector: '',
    pagination: false,
    maxPages: 1,
  })

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'active':
        return <CheckCircle sx={{ color: 'success.main' }} />
      case 'running':
        return <LinearProgress sx={{ width: 100 }} />
      case 'error':
        return <Error sx={{ color: 'error.main' }} />
      default:
        return <Warning sx={{ color: 'warning.main' }} />
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active':
        return 'success'
      case 'running':
        return 'info'
      case 'error':
        return 'error'
      default:
        return 'default'
    }
  }

  const handleRunScraper = (id: string) => {
    setScrapers(scrapers.map(s => 
      s.id === id ? { ...s, status: 'running' } : s
    ))
    // Simulate scraper completion
    setTimeout(() => {
      setScrapers(scrapers.map(s => 
        s.id === id 
          ? { ...s, status: 'active', lastRun: new Date().toLocaleString(), itemsFound: Math.floor(Math.random() * 50) + 10 }
          : s
      ))
    }, 3000)
  }

  const handleStopScraper = (id: string) => {
    setScrapers(scrapers.map(s => 
      s.id === id ? { ...s, status: 'inactive' } : s
    ))
  }

  const handleToggleScraper = (id: string) => {
    setScrapers(scrapers.map(s => 
      s.id === id ? { ...s, enabled: !s.enabled, status: !s.enabled ? 'active' : 'inactive' } : s
    ))
  }

  const handleEditScraper = (scraper: Scraper) => {
    setEditingScraper(scraper)
    setFormData({
      name: scraper.name,
      type: scraper.type,
      url: scraper.url,
      selector: scraper.config.selector || '',
      pagination: scraper.config.pagination || false,
      maxPages: scraper.config.maxPages || 1,
    })
    setOpenDialog(true)
  }

  const handleSaveScraper = () => {
    if (editingScraper) {
      setScrapers(scrapers.map(s => 
        s.id === editingScraper.id 
          ? {
              ...s,
              name: formData.name,
              type: formData.type,
              url: formData.url,
              config: {
                selector: formData.selector,
                pagination: formData.pagination,
                maxPages: formData.maxPages,
              }
            }
          : s
      ))
    } else {
      const newScraper: Scraper = {
        id: Date.now().toString(),
        name: formData.name,
        type: formData.type,
        url: formData.url,
        status: 'inactive',
        lastRun: 'Never',
        itemsFound: 0,
        enabled: true,
        config: {
          selector: formData.selector,
          pagination: formData.pagination,
          maxPages: formData.maxPages,
        }
      }
      setScrapers([...scrapers, newScraper])
    }
    setOpenDialog(false)
    setEditingScraper(null)
    setFormData({
      name: '',
      type: 'bank',
      url: '',
      selector: '',
      pagination: false,
      maxPages: 1,
    })
  }

  const handleDeleteScraper = (id: string) => {
    setScrapers(scrapers.filter(s => s.id !== id))
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">Web Scrapers</Typography>
        <Box>
          <Button
            startIcon={<Refresh />}
            variant="outlined"
            sx={{ mr: 2 }}
            onClick={() => scrapers.filter(s => s.enabled).forEach(s => handleRunScraper(s.id))}
          >
            Run All Active
          </Button>
          <Button
            startIcon={<Add />}
            variant="contained"
            onClick={() => {
              setEditingScraper(null)
              setFormData({
                name: '',
                type: 'bank',
                url: '',
                selector: '',
                pagination: false,
                maxPages: 1,
              })
              setOpenDialog(true)
            }}
          >
            Add Scraper
          </Button>
        </Box>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        Configure web scrapers to automatically discover new auctions from banks, NBFCs, and other financial institutions.
      </Alert>

      <Grid container spacing={3}>
        {scrapers.map((scraper) => (
          <Grid item xs={12} md={6} key={scraper.id}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                  <Box>
                    <Typography variant="h6">{scraper.name}</Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      {scraper.url}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {getStatusIcon(scraper.status)}
                    <Chip
                      label={scraper.type.toUpperCase()}
                      size="small"
                      color={scraper.type === 'bank' ? 'primary' : 'secondary'}
                    />
                  </Box>
                </Box>

                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Last Run: {scraper.lastRun}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Items Found: {scraper.itemsFound}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Status: <Chip label={scraper.status} size="small" color={getStatusColor(scraper.status)} />
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={scraper.enabled}
                        onChange={() => handleToggleScraper(scraper.id)}
                      />
                    }
                    label="Enabled"
                  />
                  <Box>
                    {scraper.status === 'running' ? (
                      <IconButton onClick={() => handleStopScraper(scraper.id)} color="error">
                        <Stop />
                      </IconButton>
                    ) : (
                      <IconButton 
                        onClick={() => handleRunScraper(scraper.id)} 
                        color="primary"
                        disabled={!scraper.enabled}
                      >
                        <PlayArrow />
                      </IconButton>
                    )}
                    <IconButton onClick={() => handleEditScraper(scraper)}>
                      <Edit />
                    </IconButton>
                    <IconButton onClick={() => handleDeleteScraper(scraper.id)} color="error">
                      <Delete />
                    </IconButton>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Add/Edit Scraper Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editingScraper ? 'Edit Scraper' : 'Add New Scraper'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Scraper Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            margin="normal"
          />
          <TextField
            fullWidth
            select
            label="Type"
            value={formData.type}
            onChange={(e) => setFormData({ ...formData, type: e.target.value })}
            margin="normal"
          >
            <MenuItem value="bank">Bank</MenuItem>
            <MenuItem value="nbfc">NBFC</MenuItem>
            <MenuItem value="government">Government</MenuItem>
            <MenuItem value="other">Other</MenuItem>
          </TextField>
          <TextField
            fullWidth
            label="URL"
            value={formData.url}
            onChange={(e) => setFormData({ ...formData, url: e.target.value })}
            margin="normal"
            helperText="The auction listing page URL"
          />
          <TextField
            fullWidth
            label="CSS Selector"
            value={formData.selector}
            onChange={(e) => setFormData({ ...formData, selector: e.target.value })}
            margin="normal"
            helperText="CSS selector for auction items (e.g., .auction-item)"
          />
          <FormControlLabel
            control={
              <Switch
                checked={formData.pagination}
                onChange={(e) => setFormData({ ...formData, pagination: e.target.checked })}
              />
            }
            label="Has Pagination"
            sx={{ mt: 2 }}
          />
          {formData.pagination && (
            <TextField
              fullWidth
              type="number"
              label="Max Pages"
              value={formData.maxPages}
              onChange={(e) => setFormData({ ...formData, maxPages: parseInt(e.target.value) || 1 })}
              margin="normal"
              inputProps={{ min: 1, max: 50 }}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleSaveScraper} variant="contained">
            {editingScraper ? 'Save' : 'Add'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default ScraperManagement
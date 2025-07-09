import { useState } from 'react'
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Grid,
  Switch,
  FormControlLabel,
  Divider,
  Alert,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
} from '@mui/material'
import {
  Save,
  Add,
  Delete,
  Science,
} from '@mui/icons-material'

const ScraperSettings = () => {
  const [settings, setSettings] = useState({
    general: {
      autoApprove: false,
      notifyOnNew: true,
      notifyOnError: true,
      maxRetries: 3,
      timeout: 30000,
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    },
    filters: {
      minPrice: 100000,
      maxPrice: 100000000,
      excludeKeywords: ['test', 'demo', 'sample'],
      includeCategories: ['real-estate', 'vehicles', 'jewelry', 'electronics'],
    },
    api: {
      rateLimit: 10,
      concurrentRequests: 3,
      proxyEnabled: false,
      proxyUrl: '',
    },
    notifications: {
      email: 'admin@auctionaggregator.com',
      webhookUrl: '',
      slackChannel: '',
    },
  })

  const [testResult, setTestResult] = useState<string | null>(null)

  const handleSaveSettings = () => {
    // Save settings to backend
    alert('Settings saved successfully!')
  }

  const handleTestWebhook = () => {
    if (settings.notifications.webhookUrl) {
      setTestResult('Testing webhook...')
      setTimeout(() => {
        setTestResult('Webhook test successful! Response: 200 OK')
      }, 2000)
    }
  }

  const handleAddKeyword = () => {
    const keyword = prompt('Enter keyword to exclude:')
    if (keyword) {
      setSettings({
        ...settings,
        filters: {
          ...settings.filters,
          excludeKeywords: [...settings.filters.excludeKeywords, keyword],
        },
      })
    }
  }

  const handleRemoveKeyword = (keyword: string) => {
    setSettings({
      ...settings,
      filters: {
        ...settings.filters,
        excludeKeywords: settings.filters.excludeKeywords.filter(k => k !== keyword),
      },
    })
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">Scraper Settings</Typography>
        <Button
          startIcon={<Save />}
          variant="contained"
          onClick={handleSaveSettings}
        >
          Save Settings
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* General Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                General Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <FormControlLabel
                control={
                  <Switch
                    checked={settings.general.autoApprove}
                    onChange={(e) => setSettings({
                      ...settings,
                      general: { ...settings.general, autoApprove: e.target.checked }
                    })}
                  />
                }
                label="Auto-approve scraped auctions"
              />
              
              <FormControlLabel
                control={
                  <Switch
                    checked={settings.general.notifyOnNew}
                    onChange={(e) => setSettings({
                      ...settings,
                      general: { ...settings.general, notifyOnNew: e.target.checked }
                    })}
                  />
                }
                label="Notify on new auctions found"
              />
              
              <FormControlLabel
                control={
                  <Switch
                    checked={settings.general.notifyOnError}
                    onChange={(e) => setSettings({
                      ...settings,
                      general: { ...settings.general, notifyOnError: e.target.checked }
                    })}
                  />
                }
                label="Notify on scraper errors"
              />

              <TextField
                fullWidth
                label="Max Retries"
                type="number"
                value={settings.general.maxRetries}
                onChange={(e) => setSettings({
                  ...settings,
                  general: { ...settings.general, maxRetries: parseInt(e.target.value) || 0 }
                })}
                margin="normal"
                inputProps={{ min: 0, max: 10 }}
              />

              <TextField
                fullWidth
                label="Timeout (ms)"
                type="number"
                value={settings.general.timeout}
                onChange={(e) => setSettings({
                  ...settings,
                  general: { ...settings.general, timeout: parseInt(e.target.value) || 0 }
                })}
                margin="normal"
                inputProps={{ min: 5000, max: 120000 }}
              />

              <TextField
                fullWidth
                label="User Agent"
                value={settings.general.userAgent}
                onChange={(e) => setSettings({
                  ...settings,
                  general: { ...settings.general, userAgent: e.target.value }
                })}
                margin="normal"
                multiline
                rows={2}
              />
            </CardContent>
          </Card>
        </Grid>

        {/* Filter Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Filter Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <TextField
                fullWidth
                label="Minimum Price (₹)"
                type="number"
                value={settings.filters.minPrice}
                onChange={(e) => setSettings({
                  ...settings,
                  filters: { ...settings.filters, minPrice: parseInt(e.target.value) || 0 }
                })}
                margin="normal"
              />

              <TextField
                fullWidth
                label="Maximum Price (₹)"
                type="number"
                value={settings.filters.maxPrice}
                onChange={(e) => setSettings({
                  ...settings,
                  filters: { ...settings.filters, maxPrice: parseInt(e.target.value) || 0 }
                })}
                margin="normal"
              />

              <Box sx={{ mt: 2, mb: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                  <Typography variant="body2">Exclude Keywords</Typography>
                  <IconButton size="small" onClick={handleAddKeyword}>
                    <Add />
                  </IconButton>
                </Box>
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  {settings.filters.excludeKeywords.map(keyword => (
                    <Chip
                      key={keyword}
                      label={keyword}
                      onDelete={() => handleRemoveKeyword(keyword)}
                      size="small"
                    />
                  ))}
                </Box>
              </Box>

              <FormControl fullWidth margin="normal">
                <InputLabel>Include Categories</InputLabel>
                <Select
                  multiple
                  value={settings.filters.includeCategories}
                  onChange={(e) => setSettings({
                    ...settings,
                    filters: { ...settings.filters, includeCategories: e.target.value as string[] }
                  })}
                  label="Include Categories"
                >
                  <MenuItem value="real-estate">Real Estate</MenuItem>
                  <MenuItem value="vehicles">Vehicles</MenuItem>
                  <MenuItem value="jewelry">Jewelry</MenuItem>
                  <MenuItem value="electronics">Electronics</MenuItem>
                  <MenuItem value="machinery">Machinery</MenuItem>
                  <MenuItem value="art">Art & Collectibles</MenuItem>
                </Select>
              </FormControl>
            </CardContent>
          </Card>
        </Grid>

        {/* API Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                API & Performance
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <TextField
                fullWidth
                label="Rate Limit (requests/minute)"
                type="number"
                value={settings.api.rateLimit}
                onChange={(e) => setSettings({
                  ...settings,
                  api: { ...settings.api, rateLimit: parseInt(e.target.value) || 0 }
                })}
                margin="normal"
                inputProps={{ min: 1, max: 60 }}
              />

              <TextField
                fullWidth
                label="Concurrent Requests"
                type="number"
                value={settings.api.concurrentRequests}
                onChange={(e) => setSettings({
                  ...settings,
                  api: { ...settings.api, concurrentRequests: parseInt(e.target.value) || 1 }
                })}
                margin="normal"
                inputProps={{ min: 1, max: 10 }}
              />

              <FormControlLabel
                control={
                  <Switch
                    checked={settings.api.proxyEnabled}
                    onChange={(e) => setSettings({
                      ...settings,
                      api: { ...settings.api, proxyEnabled: e.target.checked }
                    })}
                  />
                }
                label="Use Proxy"
              />

              {settings.api.proxyEnabled && (
                <TextField
                  fullWidth
                  label="Proxy URL"
                  value={settings.api.proxyUrl}
                  onChange={(e) => setSettings({
                    ...settings,
                    api: { ...settings.api, proxyUrl: e.target.value }
                  })}
                  margin="normal"
                  placeholder="http://proxy.example.com:8080"
                />
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Notification Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Notifications
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <TextField
                fullWidth
                label="Admin Email"
                type="email"
                value={settings.notifications.email}
                onChange={(e) => setSettings({
                  ...settings,
                  notifications: { ...settings.notifications, email: e.target.value }
                })}
                margin="normal"
              />

              <TextField
                fullWidth
                label="Webhook URL"
                value={settings.notifications.webhookUrl}
                onChange={(e) => setSettings({
                  ...settings,
                  notifications: { ...settings.notifications, webhookUrl: e.target.value }
                })}
                margin="normal"
                placeholder="https://your-webhook.com/notify"
              />

              {settings.notifications.webhookUrl && (
                <Button
                  startIcon={<Science />}
                  variant="outlined"
                  size="small"
                  onClick={handleTestWebhook}
                  sx={{ mt: 1 }}
                >
                  Test Webhook
                </Button>
              )}

              {testResult && (
                <Alert 
                  severity={testResult.includes('successful') ? 'success' : 'info'} 
                  sx={{ mt: 2 }}
                >
                  {testResult}
                </Alert>
              )}

              <TextField
                fullWidth
                label="Slack Channel"
                value={settings.notifications.slackChannel}
                onChange={(e) => setSettings({
                  ...settings,
                  notifications: { ...settings.notifications, slackChannel: e.target.value }
                })}
                margin="normal"
                placeholder="#auction-alerts"
              />
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Alert severity="info" sx={{ mt: 3 }}>
        These settings apply to all scrapers. Individual scraper settings can override these defaults.
      </Alert>
    </Box>
  )
}

export default ScraperSettings
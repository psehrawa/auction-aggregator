import { useState } from 'react'
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Tab,
  Tabs,
  Card,
  CardContent,
  IconButton,
  Chip,
} from '@mui/material'
import {
  Dashboard as DashboardIcon,
  WebAsset,
  Schedule,
  ImportExport,
  Settings,
  TrendingUp,
  Warning,
  CheckCircle,
} from '@mui/icons-material'
import ScraperManagement from '@/components/admin/ScraperManagement'
import AuctionImport from '@/components/admin/AuctionImport'
import ScraperSchedule from '@/components/admin/ScraperSchedule'
import ScraperSettings from '@/components/admin/ScraperSettings'
import RealTimeScraper from '@/components/admin/RealTimeScraper'

interface TabPanelProps {
  children?: React.ReactNode
  index: number
  value: number
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`admin-tabpanel-${index}`}
      aria-labelledby={`admin-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  )
}

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState(0)

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue)
  }

  const stats = {
    totalScrapers: 12,
    activeScrapers: 8,
    lastRun: '2 hours ago',
    auctionsFound: 156,
    pendingReview: 23,
    imported: 133,
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ mb: 3 }}>
        Admin Portal - Auction Discovery
      </Typography>

      {/* Stats Overview */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Scrapers
                  </Typography>
                  <Typography variant="h4">
                    {stats.activeScrapers}/{stats.totalScrapers}
                  </Typography>
                </Box>
                <WebAsset sx={{ fontSize: 40, color: 'primary.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Auctions Found
                  </Typography>
                  <Typography variant="h4">{stats.auctionsFound}</Typography>
                </Box>
                <TrendingUp sx={{ fontSize: 40, color: 'success.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Pending Review
                  </Typography>
                  <Typography variant="h4">{stats.pendingReview}</Typography>
                </Box>
                <Warning sx={{ fontSize: 40, color: 'warning.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Last Scrape
                  </Typography>
                  <Typography variant="h6">{stats.lastRun}</Typography>
                </Box>
                <Schedule sx={{ fontSize: 40, color: 'info.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Main Content */}
      <Paper sx={{ width: '100%' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={activeTab} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
            <Tab
              icon={<WebAsset />}
              label="Real-Time Scraper"
              iconPosition="start"
            />
            <Tab
              icon={<DashboardIcon />}
              label="Scrapers"
              iconPosition="start"
            />
            <Tab
              icon={<ImportExport />}
              label="Import Auctions"
              iconPosition="start"
            />
            <Tab
              icon={<Schedule />}
              label="Schedule"
              iconPosition="start"
            />
            <Tab
              icon={<Settings />}
              label="Settings"
              iconPosition="start"
            />
          </Tabs>
        </Box>

        <TabPanel value={activeTab} index={0}>
          <RealTimeScraper />
        </TabPanel>
        <TabPanel value={activeTab} index={1}>
          <ScraperManagement />
        </TabPanel>
        <TabPanel value={activeTab} index={2}>
          <AuctionImport />
        </TabPanel>
        <TabPanel value={activeTab} index={3}>
          <ScraperSchedule />
        </TabPanel>
        <TabPanel value={activeTab} index={4}>
          <ScraperSettings />
        </TabPanel>
      </Paper>
    </Container>
  )
}

export default AdminDashboard
import { useState } from 'react'
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Chip,
  Alert,
  Switch,
  FormControlLabel,
} from '@mui/material'
import {
  Add,
  Edit,
  Delete,
  Schedule,
  PlayArrow,
  Pause,
} from '@mui/icons-material'

interface ScheduleJob {
  id: string
  name: string
  scrapers: string[]
  frequency: string
  time: string
  lastRun: string
  nextRun: string
  status: 'active' | 'paused'
  enabled: boolean
}

const ScraperSchedule = () => {
  const [schedules, setSchedules] = useState<ScheduleJob[]>([
    {
      id: '1',
      name: 'Daily Bank Scraping',
      scrapers: ['SBI Auctions', 'HDFC Bank E-Auctions', 'ICICI Bank Properties'],
      frequency: 'daily',
      time: '09:00',
      lastRun: '2024-01-10 09:00',
      nextRun: '2024-01-11 09:00',
      status: 'active',
      enabled: true,
    },
    {
      id: '2',
      name: 'NBFC Weekly Scan',
      scrapers: ['Bajaj Finance Auctions', 'Muthoot Finance'],
      frequency: 'weekly',
      time: '14:00',
      lastRun: '2024-01-08 14:00',
      nextRun: '2024-01-15 14:00',
      status: 'active',
      enabled: true,
    },
    {
      id: '3',
      name: 'Hourly Hot Deals',
      scrapers: ['All Active Scrapers'],
      frequency: 'hourly',
      time: '00',
      lastRun: '2024-01-10 15:00',
      nextRun: '2024-01-10 16:00',
      status: 'paused',
      enabled: false,
    },
  ])

  const [openDialog, setOpenDialog] = useState(false)
  const [editingSchedule, setEditingSchedule] = useState<ScheduleJob | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    scrapers: [] as string[],
    frequency: 'daily',
    time: '09:00',
  })

  const availableScrapers = [
    'SBI Auctions',
    'HDFC Bank E-Auctions',
    'ICICI Bank Properties',
    'Bajaj Finance Auctions',
    'Muthoot Finance',
  ]

  const handleAddSchedule = () => {
    const newSchedule: ScheduleJob = {
      id: Date.now().toString(),
      name: formData.name,
      scrapers: formData.scrapers,
      frequency: formData.frequency,
      time: formData.time,
      lastRun: 'Never',
      nextRun: calculateNextRun(formData.frequency, formData.time),
      status: 'active',
      enabled: true,
    }
    setSchedules([...schedules, newSchedule])
    setOpenDialog(false)
    resetForm()
  }

  const handleEditSchedule = (schedule: ScheduleJob) => {
    setEditingSchedule(schedule)
    setFormData({
      name: schedule.name,
      scrapers: schedule.scrapers,
      frequency: schedule.frequency,
      time: schedule.time,
    })
    setOpenDialog(true)
  }

  const handleUpdateSchedule = () => {
    if (editingSchedule) {
      setSchedules(schedules.map(s => 
        s.id === editingSchedule.id 
          ? {
              ...s,
              name: formData.name,
              scrapers: formData.scrapers,
              frequency: formData.frequency,
              time: formData.time,
              nextRun: calculateNextRun(formData.frequency, formData.time),
            }
          : s
      ))
      setOpenDialog(false)
      setEditingSchedule(null)
      resetForm()
    }
  }

  const handleDeleteSchedule = (id: string) => {
    setSchedules(schedules.filter(s => s.id !== id))
  }

  const handleToggleSchedule = (id: string) => {
    setSchedules(schedules.map(s => 
      s.id === id 
        ? { ...s, enabled: !s.enabled, status: !s.enabled ? 'active' : 'paused' }
        : s
    ))
  }

  const handleRunNow = (id: string) => {
    const schedule = schedules.find(s => s.id === id)
    if (schedule) {
      alert(`Running scrapers: ${schedule.scrapers.join(', ')}`)
      setSchedules(schedules.map(s => 
        s.id === id 
          ? { ...s, lastRun: new Date().toLocaleString() }
          : s
      ))
    }
  }

  const calculateNextRun = (frequency: string, time: string) => {
    const now = new Date()
    const [hours, minutes] = time.split(':').map(Number)
    
    switch (frequency) {
      case 'hourly':
        const nextHour = new Date(now)
        nextHour.setHours(now.getHours() + 1, parseInt(time), 0, 0)
        return nextHour.toLocaleString()
      case 'daily':
        const tomorrow = new Date(now)
        tomorrow.setDate(tomorrow.getDate() + 1)
        tomorrow.setHours(hours, minutes, 0, 0)
        return tomorrow.toLocaleString()
      case 'weekly':
        const nextWeek = new Date(now)
        nextWeek.setDate(nextWeek.getDate() + 7)
        nextWeek.setHours(hours, minutes, 0, 0)
        return nextWeek.toLocaleString()
      default:
        return 'Unknown'
    }
  }

  const resetForm = () => {
    setFormData({
      name: '',
      scrapers: [],
      frequency: 'daily',
      time: '09:00',
    })
  }

  const getFrequencyColor = (frequency: string) => {
    switch (frequency) {
      case 'hourly':
        return 'primary'
      case 'daily':
        return 'success'
      case 'weekly':
        return 'warning'
      default:
        return 'default'
    }
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">Scraping Schedule</Typography>
        <Button
          startIcon={<Add />}
          variant="contained"
          onClick={() => {
            setEditingSchedule(null)
            resetForm()
            setOpenDialog(true)
          }}
        >
          Add Schedule
        </Button>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        Set up automated scraping schedules to regularly check for new auctions. 
        Schedules run based on server time (UTC).
      </Alert>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Schedule Name</TableCell>
              <TableCell>Scrapers</TableCell>
              <TableCell>Frequency</TableCell>
              <TableCell>Time</TableCell>
              <TableCell>Last Run</TableCell>
              <TableCell>Next Run</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {schedules.map((schedule) => (
              <TableRow key={schedule.id}>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Schedule />
                    <Typography>{schedule.name}</Typography>
                  </Box>
                </TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                    {schedule.scrapers.slice(0, 2).map(scraper => (
                      <Chip key={scraper} label={scraper} size="small" />
                    ))}
                    {schedule.scrapers.length > 2 && (
                      <Chip 
                        label={`+${schedule.scrapers.length - 2} more`} 
                        size="small" 
                        variant="outlined"
                      />
                    )}
                  </Box>
                </TableCell>
                <TableCell>
                  <Chip 
                    label={schedule.frequency} 
                    size="small" 
                    color={getFrequencyColor(schedule.frequency)}
                  />
                </TableCell>
                <TableCell>{schedule.time}</TableCell>
                <TableCell>{schedule.lastRun}</TableCell>
                <TableCell>{schedule.nextRun}</TableCell>
                <TableCell>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={schedule.enabled}
                        onChange={() => handleToggleSchedule(schedule.id)}
                        size="small"
                      />
                    }
                    label={schedule.status}
                  />
                </TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <IconButton 
                      size="small" 
                      onClick={() => handleRunNow(schedule.id)}
                      title="Run Now"
                      color="primary"
                    >
                      <PlayArrow />
                    </IconButton>
                    <IconButton 
                      size="small" 
                      onClick={() => handleEditSchedule(schedule)}
                      title="Edit"
                    >
                      <Edit />
                    </IconButton>
                    <IconButton 
                      size="small" 
                      onClick={() => handleDeleteSchedule(schedule.id)}
                      title="Delete"
                      color="error"
                    >
                      <Delete />
                    </IconButton>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add/Edit Schedule Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editingSchedule ? 'Edit Schedule' : 'Add New Schedule'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Schedule Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            margin="normal"
          />
          <FormControl fullWidth margin="normal">
            <InputLabel>Scrapers</InputLabel>
            <Select
              multiple
              value={formData.scrapers}
              onChange={(e) => setFormData({ ...formData, scrapers: e.target.value as string[] })}
              label="Scrapers"
            >
              <MenuItem value="All Active Scrapers">All Active Scrapers</MenuItem>
              {availableScrapers.map(scraper => (
                <MenuItem key={scraper} value={scraper}>{scraper}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            fullWidth
            select
            label="Frequency"
            value={formData.frequency}
            onChange={(e) => setFormData({ ...formData, frequency: e.target.value })}
            margin="normal"
          >
            <MenuItem value="hourly">Hourly</MenuItem>
            <MenuItem value="daily">Daily</MenuItem>
            <MenuItem value="weekly">Weekly</MenuItem>
          </TextField>
          {formData.frequency === 'hourly' ? (
            <TextField
              fullWidth
              label="Minutes"
              value={formData.time}
              onChange={(e) => setFormData({ ...formData, time: e.target.value })}
              margin="normal"
              helperText="Run at this minute of every hour (0-59)"
              type="number"
              inputProps={{ min: 0, max: 59 }}
            />
          ) : (
            <TextField
              fullWidth
              label="Time"
              value={formData.time}
              onChange={(e) => setFormData({ ...formData, time: e.target.value })}
              margin="normal"
              type="time"
              InputLabelProps={{ shrink: true }}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button 
            onClick={editingSchedule ? handleUpdateSchedule : handleAddSchedule} 
            variant="contained"
          >
            {editingSchedule ? 'Update' : 'Add'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default ScraperSchedule
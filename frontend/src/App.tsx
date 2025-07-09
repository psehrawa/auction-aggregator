import { Routes, Route } from 'react-router-dom'
import { ThemeProvider } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'

import theme from './theme'
import MainLayout from './components/layouts/MainLayout'
import HomePage from './pages/HomePage'
import SearchPage from './pages/SearchPage'
import AuctionDetailPage from './pages/AuctionDetailPage'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import DashboardPage from './pages/dashboard/DashboardPage'
import AdminDashboard from './pages/admin/AdminDashboard'

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<HomePage />} />
          <Route path="search" element={<SearchPage />} />
          <Route path="auctions/:id" element={<AuctionDetailPage />} />
          
          {/* Public routes (accessible only when not authenticated) */}
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
          
          {/* Private routes (require authentication) */}
          <Route path="dashboard" element={<DashboardPage />} />
          
          {/* Admin routes */}
          <Route path="admin" element={<AdminDashboard />} />
        </Route>
      </Routes>
    </ThemeProvider>
  )
}

export default App
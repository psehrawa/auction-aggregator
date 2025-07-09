import { Outlet } from 'react-router-dom'
import { AppBar, Toolbar, Typography, Container, Box, Button } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useAppSelector } from '@/hooks/redux'

const MainLayout = () => {
  const navigate = useNavigate()
  const { isAuthenticated, user } = useAppSelector(state => state.auth)

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ flexGrow: 1, cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            Auction Aggregator
          </Typography>
          <Button color="inherit" onClick={() => navigate('/search')}>
            Browse
          </Button>
          <Button color="inherit" onClick={() => navigate('/admin')}>
            Admin
          </Button>
          {isAuthenticated ? (
            <>
              <Button color="inherit" onClick={() => navigate('/dashboard')}>
                Dashboard
              </Button>
              <Typography variant="body2" sx={{ ml: 2 }}>
                {user?.name}
              </Typography>
            </>
          ) : (
            <>
              <Button color="inherit" onClick={() => navigate('/login')}>
                Login
              </Button>
              <Button color="inherit" onClick={() => navigate('/register')}>
                Register
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      
      <Container component="main" sx={{ flex: 1, py: 3 }}>
        <Outlet />
      </Container>
      
      <Box component="footer" sx={{ py: 3, px: 2, mt: 'auto', backgroundColor: 'background.paper' }}>
        <Container maxWidth="lg">
          <Typography variant="body2" color="text.secondary" align="center">
            © 2024 Auction Aggregator. All rights reserved.
          </Typography>
        </Container>
      </Box>
    </Box>
  )
}

export default MainLayout
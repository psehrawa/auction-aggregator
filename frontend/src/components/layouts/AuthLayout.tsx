import { Outlet } from 'react-router-dom'
import { Container, Paper, Box } from '@mui/material'

const AuthLayout = () => {
  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: '100%' }}>
          <Outlet />
        </Paper>
      </Box>
    </Container>
  )
}

export default AuthLayout
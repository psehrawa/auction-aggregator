import { Navigate, Outlet } from 'react-router-dom'
import { useAppSelector } from '@/hooks/redux'

interface PrivateRouteProps {
  allowedRoles?: string[]
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ allowedRoles }) => {
  const { isAuthenticated, user } = useAppSelector(state => state.auth)

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && user && !allowedRoles.some(role => user.roles.includes(role))) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}

export default PrivateRoute
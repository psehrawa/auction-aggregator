import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit'
import { authService } from '@/services/authService'
import { User, LoginCredentials, RegisterData } from '@/types/auth'

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('token'),
  isAuthenticated: false,
  isLoading: false,
  error: null,
}

export const login = createAsyncThunk(
  'auth/login',
  async (credentials: LoginCredentials) => {
    const response = await authService.login(credentials)
    return response
  }
)

export const register = createAsyncThunk(
  'auth/register',
  async (data: RegisterData) => {
    const response = await authService.register(data)
    return response
  }
)

export const checkAuth = createAsyncThunk(
  'auth/checkAuth',
  async () => {
    const token = localStorage.getItem('token')
    if (!token) {
      throw new Error('No token found')
    }
    const response = await authService.getCurrentUser()
    return response
  }
)

export const logout = createAsyncThunk('auth/logout', async () => {
  await authService.logout()
})

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ user: User; token: string }>
    ) => {
      state.user = action.payload.user
      state.token = action.payload.token
      state.isAuthenticated = true
      localStorage.setItem('token', action.payload.token)
    },
    clearAuth: (state) => {
      state.user = null
      state.token = null
      state.isAuthenticated = false
      state.error = null
      localStorage.removeItem('token')
    },
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload.user
        state.token = action.payload.token
        state.isAuthenticated = true
        localStorage.setItem('token', action.payload.token)
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.error.message || 'Login failed'
      })
    
    // Register
    builder
      .addCase(register.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(register.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload.user
        state.token = action.payload.token
        state.isAuthenticated = true
        localStorage.setItem('token', action.payload.token)
      })
      .addCase(register.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.error.message || 'Registration failed'
      })
    
    // Check Auth
    builder
      .addCase(checkAuth.pending, (state) => {
        state.isLoading = true
      })
      .addCase(checkAuth.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload
        state.isAuthenticated = true
      })
      .addCase(checkAuth.rejected, (state) => {
        state.isLoading = false
        state.isAuthenticated = false
        state.token = null
        localStorage.removeItem('token')
      })
    
    // Logout
    builder
      .addCase(logout.fulfilled, (state) => {
        state.user = null
        state.token = null
        state.isAuthenticated = false
        state.error = null
        localStorage.removeItem('token')
      })
  },
})

export const { setCredentials, clearAuth } = authSlice.actions
export default authSlice.reducer
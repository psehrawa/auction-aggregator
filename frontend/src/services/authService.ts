import axios from 'axios'
import { LoginCredentials, RegisterData, AuthResponse, User } from '@/types/auth'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8090'

class AuthService {
  private tokenKey = 'token'

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/api/v1/auth/login`, credentials)
    return response.data
  }

  async register(data: RegisterData): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/api/v1/auth/register`, data)
    return response.data
  }

  async getCurrentUser(): Promise<User> {
    const token = this.getToken()
    const response = await axios.get(`${API_URL}/api/v1/users/profile`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return response.data
  }

  async logout(): Promise<void> {
    localStorage.removeItem(this.tokenKey)
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey)
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token)
  }
}

export const authService = new AuthService()
export interface User {
  id: string
  email: string
  name: string
  phone?: string
  roles: string[]
  verified: boolean
  createdAt: string
  updatedAt: string
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterData {
  email: string
  password: string
  name: string
  phone?: string
  userType: 'BIDDER' | 'SELLER'
}

export interface AuthResponse {
  user: User
  token: string
}
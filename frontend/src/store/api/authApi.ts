import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const authApi = createApi({
  reducerPath: 'authApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/auth',
  }),
  endpoints: (builder) => ({
    // Add endpoints here
  }),
})

export const {} = authApi
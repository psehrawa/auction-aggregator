import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const bidApi = createApi({
  reducerPath: 'bidApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/bids',
  }),
  endpoints: (builder) => ({
    // Add endpoints here
  }),
})

export const {} = bidApi
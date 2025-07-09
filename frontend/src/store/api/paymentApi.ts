import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const paymentApi = createApi({
  reducerPath: 'paymentApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/payments',
  }),
  endpoints: (builder) => ({
    // Add endpoints here
  }),
})

export const {} = paymentApi
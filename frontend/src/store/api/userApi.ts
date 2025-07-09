import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const userApi = createApi({
  reducerPath: 'userApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/users',
  }),
  endpoints: (builder) => ({
    addToWatchlist: builder.mutation<void, string>({
      query: (auctionId) => ({
        url: `/watchlist/${auctionId}`,
        method: 'POST',
      }),
    }),
  }),
})

export const { useAddToWatchlistMutation } = userApi
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { RootState } from '..'
import { 
  Auction, 
  AuctionSearchParams, 
  AuctionCreateData, 
  AuctionUpdateData,
  PaginatedResponse 
} from '@/types/auction'

export const auctionApi = createApi({
  reducerPath: 'auctionApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/auctions',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).auth.token
      if (token) {
        headers.set('authorization', `Bearer ${token}`)
      }
      return headers
    },
  }),
  tagTypes: ['Auction', 'Bid'],
  endpoints: (builder) => ({
    getAuctions: builder.query<PaginatedResponse<Auction>, AuctionSearchParams>({
      query: (params) => ({
        url: '',
        params,
      }),
      providesTags: ['Auction'],
    }),
    
    getAuction: builder.query<Auction, string>({
      query: (id) => `/${id}`,
      providesTags: (result, error, id) => [{ type: 'Auction', id }],
    }),
    
    createAuction: builder.mutation<Auction, AuctionCreateData>({
      query: (data) => ({
        url: '',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['Auction'],
    }),
    
    updateAuction: builder.mutation<Auction, { id: string; data: AuctionUpdateData }>({
      query: ({ id, data }) => ({
        url: `/${id}`,
        method: 'PUT',
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Auction', id }],
    }),
    
    activateAuction: builder.mutation<void, string>({
      query: (id) => ({
        url: `/${id}/activate`,
        method: 'POST',
      }),
      invalidatesTags: (result, error, id) => [{ type: 'Auction', id }],
    }),
    
    cancelAuction: builder.mutation<void, { id: string; reason: string }>({
      query: ({ id, reason }) => ({
        url: `/${id}/cancel`,
        method: 'POST',
        params: { reason },
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Auction', id }],
    }),
    
    getTrendingAuctions: builder.query<Auction[], number>({
      query: (limit = 10) => ({
        url: '/trending',
        params: { limit },
      }),
      providesTags: ['Auction'],
    }),
    
    getEndingSoonAuctions: builder.query<Auction[], number>({
      query: (limit = 10) => ({
        url: '/ending-soon',
        params: { limit },
      }),
      providesTags: ['Auction'],
    }),
  }),
})

export const {
  useGetAuctionsQuery,
  useGetAuctionQuery,
  useCreateAuctionMutation,
  useUpdateAuctionMutation,
  useActivateAuctionMutation,
  useCancelAuctionMutation,
  useGetTrendingAuctionsQuery,
  useGetEndingSoonAuctionsQuery,
} = auctionApi


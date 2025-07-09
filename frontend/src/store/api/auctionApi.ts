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

// Mock data for development
export const mockAuctions: Auction[] = [
  {
    id: 'GOV-2024-001',
    title: 'Lot of 50 Dell OptiPlex Desktop Computers',
    description: '50 Dell OptiPlex 7040 desktop computers. Intel i5 processors, 8GB RAM, 256GB SSD.',
    sellerId: 'GOV-MUM',
    categoryId: 'electronics',
    status: 'ACTIVE',
    auctionType: 'STANDARD',
    startingPrice: 100000,
    currentPrice: 250000,
    bidIncrement: 5000,
    startTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
    viewCount: 234,
    watcherCount: 45,
    images: [
      { id: '1', url: 'https://picsum.photos/400/300?random=1', isPrimary: true }
    ],
    tags: ['electronics', 'computers', 'government'],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
  {
    id: 'VEH-2024-101',
    title: '2020 Honda City VX - Low Mileage',
    description: 'Honda City VX 2020, Petrol, 15,000 km, Single owner, Full service history.',
    sellerId: 'DEALER-01',
    categoryId: 'vehicles',
    status: 'ACTIVE',
    auctionType: 'STANDARD',
    startingPrice: 650000,
    currentPrice: 750000,
    bidIncrement: 10000,
    startTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
    viewCount: 512,
    watcherCount: 89,
    images: [
      { id: '2', url: 'https://picsum.photos/400/300?random=2', isPrimary: true }
    ],
    tags: ['vehicles', 'cars', 'honda'],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
  {
    id: 'GOV-2024-002',
    title: 'Office Furniture Set - 20 Desks and Chairs',
    description: 'Complete office furniture set including 20 executive desks and ergonomic chairs.',
    sellerId: 'GOV-DEL',
    categoryId: 'furniture',
    status: 'ENDING_SOON',
    auctionType: 'STANDARD',
    startingPrice: 500000,
    currentPrice: 800000,
    bidIncrement: 5000,
    startTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 6 * 60 * 60 * 1000).toISOString(),
    viewCount: 156,
    watcherCount: 23,
    images: [
      { id: '3', url: 'https://picsum.photos/400/300?random=3', isPrimary: true }
    ],
    tags: ['furniture', 'office', 'government'],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
]

// Import additional mock data
import { additionalMockAuctions } from '@/data/mockAuctions'

// Combine all mock auctions
export const allMockAuctions = [...mockAuctions, ...additionalMockAuctions]
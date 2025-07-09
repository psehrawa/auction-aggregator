import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export interface Scraper {
  id: string
  name: string
  type: string
  url: string
  status: 'active' | 'inactive' | 'running' | 'error'
  lastRun: string
  itemsFound: number
  enabled: boolean
  config: {
    selector?: string
    pagination?: boolean
    maxPages?: number
  }
}

export interface ScrapedAuction {
  id: string
  source: string
  title: string
  location: string
  startingPrice: number
  endTime: string
  category: string
  status: 'pending' | 'approved' | 'rejected'
  scrapedAt: string
  imageUrl: string
}

export interface ScraperStats {
  totalScrapers: number
  activeScrapers: number
  lastRun: string
  auctionsFound: number
  pendingReview: number
  imported: number
}

export const scraperApi = createApi({
  reducerPath: 'scraperApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/scrapers',
  }),
  tagTypes: ['Scraper', 'ScrapedAuction', 'Stats'],
  endpoints: (builder) => ({
    getScraperStats: builder.query<ScraperStats, void>({
      query: () => '/stats',
      providesTags: ['Stats'],
    }),
    getScrapers: builder.query<Scraper[], void>({
      query: () => '',
      providesTags: ['Scraper'],
    }),
    getScrapedAuctions: builder.query<ScrapedAuction[], void>({
      query: () => '/scraped-auctions',
      providesTags: ['ScrapedAuction'],
    }),
    runScraper: builder.mutation<{ status: string; message: string; jobId: string }, string>({
      query: (id) => ({
        url: `/${id}/run`,
        method: 'POST',
      }),
      invalidatesTags: ['Scraper', 'Stats'],
    }),
    approveAuction: builder.mutation<{ status: string; message: string }, string>({
      query: (id) => ({
        url: `/scraped-auctions/${id}/approve`,
        method: 'POST',
      }),
      invalidatesTags: ['ScrapedAuction', 'Stats'],
    }),
    rejectAuction: builder.mutation<{ status: string; message: string }, string>({
      query: (id) => ({
        url: `/scraped-auctions/${id}/reject`,
        method: 'POST',
      }),
      invalidatesTags: ['ScrapedAuction', 'Stats'],
    }),
    importAuctions: builder.mutation<{ imported: number; status: string; message: string }, string[]>({
      query: (auctionIds) => ({
        url: '/scraped-auctions/import',
        method: 'POST',
        body: auctionIds,
      }),
      invalidatesTags: ['ScrapedAuction', 'Stats'],
    }),
  }),
})

export const {
  useGetScraperStatsQuery,
  useGetScrapersQuery,
  useGetScrapedAuctionsQuery,
  useRunScraperMutation,
  useApproveAuctionMutation,
  useRejectAuctionMutation,
  useImportAuctionsMutation,
} = scraperApi
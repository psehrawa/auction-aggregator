export interface Auction {
  id: string
  title: string
  description: string
  sellerId: string
  categoryId: string
  status: 'DRAFT' | 'SCHEDULED' | 'ACTIVE' | 'ENDING_SOON' | 'ENDED' | 'SOLD' | 'CANCELLED'
  auctionType: 'STANDARD' | 'RESERVE' | 'ABSOLUTE' | 'SEALED_BID' | 'DUTCH' | 'PENNY'
  startingPrice: number
  reservePrice?: number
  currentPrice: number
  buyNowPrice?: number
  bidIncrement: number
  startTime: string
  endTime: string
  viewCount: number
  watcherCount: number
  images?: AuctionImage[]
  bids?: Bid[]
  tags?: string[]
  createdAt: string
  updatedAt: string
}

export interface AuctionImage {
  id: string
  url: string
  thumbnailUrl?: string
  title?: string
  isPrimary: boolean
}

export interface Bid {
  id: string
  auctionId: string
  bidderId: string
  amount: number
  bidTime: string
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'OUTBID' | 'WINNING' | 'CANCELLED'
}

export interface AuctionSearchParams {
  q?: string
  category?: string
  status?: string
  minPrice?: number
  maxPrice?: number
  page?: number
  size?: number
  sort?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { Auction } from '@/types/auction'

interface AuctionState {
  auctions: Auction[]
  selectedAuction: Auction | null
  isLoading: boolean
  error: string | null
}

const initialState: AuctionState = {
  auctions: [],
  selectedAuction: null,
  isLoading: false,
  error: null,
}

const auctionSlice = createSlice({
  name: 'auction',
  initialState,
  reducers: {
    setAuctions: (state, action: PayloadAction<Auction[]>) => {
      state.auctions = action.payload
    },
    setSelectedAuction: (state, action: PayloadAction<Auction | null>) => {
      state.selectedAuction = action.payload
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload
    },
  },
})

export const { setAuctions, setSelectedAuction, setLoading, setError } = auctionSlice.actions
export default auctionSlice.reducer
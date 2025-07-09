import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { Bid } from '@/types/auction'

interface BidState {
  bids: Bid[]
  myBids: Bid[]
  isLoading: boolean
  error: string | null
}

const initialState: BidState = {
  bids: [],
  myBids: [],
  isLoading: false,
  error: null,
}

const bidSlice = createSlice({
  name: 'bid',
  initialState,
  reducers: {
    setBids: (state, action: PayloadAction<Bid[]>) => {
      state.bids = action.payload
    },
    setMyBids: (state, action: PayloadAction<Bid[]>) => {
      state.myBids = action.payload
    },
    addBid: (state, action: PayloadAction<Bid>) => {
      state.bids.unshift(action.payload)
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload
    },
  },
})

export const { setBids, setMyBids, addBid, setLoading, setError } = bidSlice.actions
export default bidSlice.reducer
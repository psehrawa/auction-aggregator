import { configureStore } from '@reduxjs/toolkit'
import { setupListeners } from '@reduxjs/toolkit/query'

// Slices
import authReducer from './slices/authSlice'
import auctionReducer from './slices/auctionSlice'
import bidReducer from './slices/bidSlice'
import notificationReducer from './slices/notificationSlice'

// APIs
import { auctionApi } from './api/auctionApi'
import { authApi } from './api/authApi'
import { bidApi } from './api/bidApi'
import { userApi } from './api/userApi'
import { paymentApi } from './api/paymentApi'
import { scraperApi } from './api/scraperApi'

export const store = configureStore({
  reducer: {
    // Regular reducers
    auth: authReducer,
    auction: auctionReducer,
    bid: bidReducer,
    notification: notificationReducer,
    
    // RTK Query APIs
    [auctionApi.reducerPath]: auctionApi.reducer,
    [authApi.reducerPath]: authApi.reducer,
    [bidApi.reducerPath]: bidApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [paymentApi.reducerPath]: paymentApi.reducer,
    [scraperApi.reducerPath]: scraperApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['auth/setCredentials'],
      },
    }).concat(
      auctionApi.middleware,
      authApi.middleware,
      bidApi.middleware,
      userApi.middleware,
      paymentApi.middleware,
      scraperApi.middleware
    ),
})

setupListeners(store.dispatch)

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
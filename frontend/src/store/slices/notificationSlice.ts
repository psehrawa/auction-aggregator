import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface Notification {
  id: string
  type: 'info' | 'success' | 'warning' | 'error'
  message: string
  timestamp: string
}

interface NotificationState {
  notifications: Notification[]
}

const initialState: NotificationState = {
  notifications: [],
}

const notificationSlice = createSlice({
  name: 'notification',
  initialState,
  reducers: {
    addNotification: (state, action: PayloadAction<Omit<Notification, 'id' | 'timestamp'>>) => {
      const notification: Notification = {
        ...action.payload,
        id: Date.now().toString(),
        timestamp: new Date().toISOString(),
      }
      state.notifications.unshift(notification)
    },
    removeNotification: (state, action: PayloadAction<string>) => {
      state.notifications = state.notifications.filter(n => n.id !== action.payload)
    },
    clearNotifications: (state) => {
      state.notifications = []
    },
  },
})

export const { addNotification, removeNotification, clearNotifications } = notificationSlice.actions
export default notificationSlice.reducer
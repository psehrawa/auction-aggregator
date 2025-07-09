export const formatCurrency = (amount: number, currency = 'INR'): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}

export const formatDate = (date: string | Date): string => {
  return new Intl.DateTimeFormat('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(date))
}

export const formatRelativeTime = (date: string | Date): string => {
  const rtf = new Intl.RelativeTimeFormat('en', { numeric: 'auto' })
  const diff = new Date(date).getTime() - new Date().getTime()
  const days = Math.round(diff / (1000 * 60 * 60 * 24))
  
  if (Math.abs(days) < 1) {
    const hours = Math.round(diff / (1000 * 60 * 60))
    return rtf.format(hours, 'hour')
  }
  
  return rtf.format(days, 'day')
}
import { FC } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  Box,
  Chip,
  IconButton,
  Tooltip,
} from '@mui/material'
import {
  AccessTime,
  Gavel,
  Visibility,
  FavoriteBorder,
  Favorite,
} from '@mui/icons-material'
import { formatDistanceToNow } from 'date-fns'
import { Auction } from '@/types/auction'
import { useAddToWatchlistMutation } from '@/store/api/userApi'
import { formatCurrency } from '@/utils/format'

interface AuctionCardProps {
  auction: Auction
  isWatched?: boolean
}

const AuctionCard: FC<AuctionCardProps> = ({ auction, isWatched = false }) => {
  const navigate = useNavigate()
  const [addToWatchlist] = useAddToWatchlistMutation()

  const handleClick = () => {
    navigate(`/auctions/${auction.id}`)
  }

  const handleWatchlistToggle = async (e: React.MouseEvent) => {
    e.stopPropagation()
    try {
      await addToWatchlist(auction.id).unwrap()
    } catch (error) {
      console.error('Failed to update watchlist:', error)
    }
  }

  const getStatusColor = () => {
    switch (auction.status) {
      case 'ACTIVE':
        return 'success'
      case 'ENDING_SOON':
        return 'warning'
      case 'ENDED':
        return 'error'
      default:
        return 'default'
    }
  }

  const timeRemaining = () => {
    if (auction.status === 'ENDED') return 'Ended'
    try {
      return `Ends ${formatDistanceToNow(new Date((auction as any).endTime), { addSuffix: true })}`
    } catch {
      return 'Invalid date'
    }
  }

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 4,
        },
      }}
      onClick={handleClick}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="200"
          image={(auction as any).images?.[0]?.url || 'https://picsum.photos/600/400?random=1'}
          alt={auction.title}
        />
        <Box
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
            display: 'flex',
            gap: 1,
          }}
        >
          <Chip
            label={auction.status.replace('_', ' ')}
            size="small"
            color={getStatusColor()}
          />
        </Box>
        <Tooltip title={isWatched ? 'Remove from watchlist' : 'Add to watchlist'}>
          <IconButton
            sx={{
              position: 'absolute',
              bottom: 8,
              right: 8,
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 1)',
              },
            }}
            size="small"
            onClick={handleWatchlistToggle}
          >
            {isWatched ? (
              <Favorite color="error" />
            ) : (
              <FavoriteBorder />
            )}
          </IconButton>
        </Tooltip>
      </Box>

      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <Typography
          variant="h6"
          component="h3"
          gutterBottom
          sx={{
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
          }}
        >
          {auction.title}
        </Typography>

        <Box sx={{ mt: 'auto' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
            <Gavel sx={{ fontSize: 20, mr: 1, color: 'text.secondary' }} />
            <Typography variant="body2" color="text.secondary">
              {(auction as any).bidCount || 0} bids
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
            <Typography variant="h6" color="primary">
              {formatCurrency((auction as any).currentPrice || (auction as any).startingPrice)}
            </Typography>
            {auction.buyNowPrice && (
              <Chip
                label={`Buy Now: ${formatCurrency(auction.buyNowPrice)}`}
                size="small"
                color="secondary"
              />
            )}
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <AccessTime sx={{ fontSize: 16, mr: 0.5, color: 'text.secondary' }} />
              <Typography variant="caption" color="text.secondary">
                {timeRemaining()}
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Visibility sx={{ fontSize: 16, mr: 0.5, color: 'text.secondary' }} />
              <Typography variant="caption" color="text.secondary">
                {(auction as any).viewCount || 0}
              </Typography>
            </Box>
          </Box>
        </Box>
      </CardContent>
    </Card>
  )
}

export default AuctionCard
# Auction Aggregator API Documentation

## Overview

The Auction Aggregator Platform provides a comprehensive RESTful API for managing auctions, bidding, users, and payments. All API endpoints are accessed through the API Gateway at `http://localhost:8080/api/v1`.

## Authentication

Most endpoints require authentication using JWT tokens. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Base URLs

- Development: `http://localhost:8080/api/v1`
- Production: `https://api.auctionaggregator.com/v1`

## API Endpoints

### Authentication

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe",
  "phone": "+919876543210",
  "userType": "BIDDER"
}
```

### Auctions

#### List Auctions
```http
GET /auctions?page=0&size=20&sort=createdAt,desc
```

Query Parameters:
- `q` - Search query
- `category` - Category ID
- `status` - Auction status (ACTIVE, ENDING_SOON, ENDED)
- `minPrice` - Minimum price
- `maxPrice` - Maximum price
- `location` - Location filter
- `source` - Auction source (GeM, Copart, Internal)

#### Get Auction Details
```http
GET /auctions/{auctionId}
```

#### Create Auction (Seller Only)
```http
POST /auctions
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Premium Laptop",
  "description": "High-end gaming laptop",
  "categoryId": "category-123",
  "startingPrice": 50000,
  "bidIncrement": 1000,
  "startTime": "2024-01-15T10:00:00Z",
  "endTime": "2024-01-20T18:00:00Z",
  "images": [
    {
      "url": "https://example.com/laptop1.jpg",
      "isPrimary": true
    }
  ]
}
```

### Bidding

#### Place Bid
```http
POST /auctions/{auctionId}/bids
Content-Type: application/json
Authorization: Bearer <token>

{
  "amount": 55000,
  "maxAmount": 60000,  // Optional - for proxy bidding
  "source": "WEB"
}
```

#### Get Auction Bids
```http
GET /auctions/{auctionId}/bids?limit=50
```

#### Get My Bids
```http
GET /auctions/my-bids?status=ACTIVE
Authorization: Bearer <token>
```

### User Management

#### Get User Profile
```http
GET /users/profile
Authorization: Bearer <token>
```

#### Update Profile
```http
PUT /users/profile
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "John Doe",
  "phone": "+919876543210",
  "address": {
    "street": "123 Main St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zip": "400001"
  }
}
```

#### KYC Verification
```http
POST /users/kyc
Content-Type: multipart/form-data
Authorization: Bearer <token>

{
  "documentType": "AADHAAR",
  "documentNumber": "1234-5678-9012",
  "documentImage": <file>
}
```

### Watchlist

#### Add to Watchlist
```http
POST /users/watchlist/{auctionId}
Authorization: Bearer <token>
```

#### Get Watchlist
```http
GET /users/watchlist
Authorization: Bearer <token>
```

### Payments

#### Create Payment Intent
```http
POST /payments/create-intent
Content-Type: application/json
Authorization: Bearer <token>

{
  "auctionId": "auction-123",
  "amount": 55000,
  "paymentMethod": "UPI",
  "upiId": "user@paytm"
}
```

#### Confirm Payment
```http
POST /payments/confirm
Content-Type: application/json
Authorization: Bearer <token>

{
  "paymentIntentId": "pi_123456",
  "transactionId": "txn_789012"
}
```

### Categories

#### List Categories
```http
GET /categories?level=0
```

#### Get Category Tree
```http
GET /categories/tree
```

### Analytics

#### Get Price Trends
```http
GET /analytics/price-trends?categoryId=cat-123&period=30d
```

#### Get Auction Statistics
```http
GET /analytics/auctions/stats
```

## WebSocket Endpoints

### Real-time Auction Updates
```
ws://localhost:8080/ws/auctions/{auctionId}
```

Message Types:
- `BID_PLACED` - New bid placed
- `AUCTION_EXTENDED` - Auction time extended
- `AUCTION_ENDED` - Auction ended
- `PRICE_UPDATE` - Current price updated

### Notifications
```
ws://localhost:8080/ws/notifications
```

## Error Responses

All error responses follow a standard format:

```json
{
  "success": false,
  "message": "Error description",
  "errors": {
    "field": ["validation error"]
  },
  "timestamp": "2024-01-10T10:30:00Z",
  "path": "/api/v1/auctions",
  "status": 400
}
```

## Rate Limiting

API endpoints are rate-limited:
- Anonymous users: 100 requests/hour
- Authenticated users: 1000 requests/hour
- Premium users: 5000 requests/hour

## Status Codes

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `429` - Too Many Requests
- `500` - Internal Server Error

## SDK Examples

### JavaScript/TypeScript
```typescript
import { AuctionAggregatorSDK } from '@auction-aggregator/sdk';

const sdk = new AuctionAggregatorSDK({
  apiKey: 'your-api-key',
  environment: 'production'
});

// Place a bid
const bid = await sdk.auctions.placeBid('auction-123', {
  amount: 55000
});
```

### Python
```python
from auction_aggregator import Client

client = Client(api_key='your-api-key')

# Get auction details
auction = client.auctions.get('auction-123')

# Place bid
bid = client.bids.create(
    auction_id='auction-123',
    amount=55000
)
```

## Postman Collection

Download our Postman collection for easy API testing:
[Download Postman Collection](https://api.auctionaggregator.com/docs/postman-collection.json)
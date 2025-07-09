# Simple Auction Aggregator Demo

This is a simplified demo to get you started quickly without all the microservices complexity.

## Quick Start

### 1. Start PostgreSQL
```bash
# Remove any existing container
docker rm -f auction-postgres 2>/dev/null || true

# Start PostgreSQL
docker run -d \
    --name auction-postgres \
    -e POSTGRES_USER=auction_user \
    -e POSTGRES_PASSWORD=auction_pass \
    -e POSTGRES_DB=auction_db \
    -p 5433:5432 \
    postgres:15-alpine

# Wait for it to start
sleep 5

# Verify it's running
docker ps | grep auction-postgres
```

### 2. Run Frontend Only (Static Demo)

The frontend can run independently with mock data:

```bash
cd frontend
npm install
npm run dev
```

Visit http://localhost:3001 to see the UI.

### 3. Simple Backend (Optional)

If you want to run the auction service:

```bash
cd backend/auction-service

# Skip tests for quick start
mvn clean compile

# Run with simplified config
mvn spring-boot:run -Dspring.profiles.active=local
```

## What's Working in This Demo

1. **Frontend UI**
   - Home page with auction categories
   - Search functionality (UI only)
   - Auction cards display
   - Responsive design

2. **Backend (if running)**
   - REST API endpoints
   - Database connectivity
   - Basic auction CRUD operations

## Troubleshooting

### Port Issues
If ports are in use:
```bash
# Check what's using the ports
lsof -i :5433  # PostgreSQL
lsof -i :3001  # Frontend
lsof -i :8081  # Backend

# Kill processes if needed
kill -9 <PID>
```

### Database Connection Issues
```bash
# Test PostgreSQL connection
docker exec -it auction-postgres psql -U auction_user -d auction_db -c "SELECT 1"
```

### Frontend Issues
```bash
# Clear cache and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

## Next Steps

1. **Add Mock Data**: Create some sample auctions in the database
2. **Connect Frontend to Backend**: Update API endpoints in frontend
3. **Add Features**: Implement bidding, user auth, etc.
4. **Scale Up**: Add other microservices as needed

## Architecture Overview (Simplified)

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│   Browser   │────▶│   Frontend   │────▶│   Backend    │
│             │     │  (React)     │     │ (Spring Boot)│
└─────────────┘     └──────────────┘     └──────┬───────┘
                         :3001                   │ :8081
                                                 ▼
                                         ┌──────────────┐
                                         │  PostgreSQL  │
                                         └──────────────┘
                                              :5433
```

This simplified version lets you explore the codebase and understand the structure without dealing with the full microservices complexity.
# Quick Start Guide for Auction Aggregator

This guide will help you get the application running quickly.

## Prerequisites

- Java 17+
- Node.js 18+
- Docker and Docker Compose
- Maven 3.6+

## Step 1: Start Infrastructure Services

```bash
# Start only the infrastructure services (databases, cache, etc.)
./scripts/start-dev.sh
```

This will start:
- PostgreSQL on port 5433
- Redis on port 6380
- Elasticsearch on port 9201
- RabbitMQ on port 5673

## Step 2: Run the Auction Service

Since the full microservices setup requires additional configuration, let's run just the auction service:

```bash
cd backend/auction-service

# First time: Install dependencies
mvn clean install -DskipTests

# Run the service
mvn spring-boot:run -Dspring.profiles.active=local
```

The auction service will start on http://localhost:8081

## Step 3: Run the Frontend

In a new terminal:

```bash
cd frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

The frontend will start on http://localhost:3001

## Accessing the Application

- Frontend: http://localhost:3001
- Auction Service API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html

## Common Issues

### Port Conflicts
If you get port conflict errors, check the PORT_MAPPING.md file for the updated ports.

### Database Connection Issues
Make sure the PostgreSQL container is running:
```bash
docker ps | grep auction_postgres
```

### Missing Dependencies
For backend:
```bash
cd backend/auction-service
mvn clean install
```

For frontend:
```bash
cd frontend
npm install
```

## Simplified Architecture

For development, we're running a simplified version:
```
Frontend (React) -> Auction Service (Spring Boot) -> PostgreSQL/Redis
```

The full microservices architecture with API Gateway, User Service, etc. requires additional setup.

## Next Steps

1. Create test data by using the API endpoints
2. Explore the Swagger UI for API documentation
3. Check the logs if you encounter issues:
   ```bash
   # Backend logs
   tail -f backend/auction-service/logs/auction-service.log
   
   # Docker logs
   docker-compose -f docker-compose-dev.yml logs -f
   ```
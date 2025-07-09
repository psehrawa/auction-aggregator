# Port Mapping for Auction Aggregator Platform

This document lists all the ports used by the application to avoid conflicts with other services.

## Updated Port Mappings

### Frontend & Gateway
- **Frontend (React)**: `3001` (was 3000)
- **API Gateway**: `8090` (was 8080)
- **Nginx**: `9090` (was 80), `8443` (was 443)

### Backend Services
- **Eureka Server**: `8762` (was 8761)
- **Auction Service**: `8081` (unchanged - internal)
- **User Service**: Internal only
- **Payment Service**: Internal only
- **Notification Service**: Internal only
- **Analytics Service**: Internal only

### Infrastructure Services
- **PostgreSQL**: `5433` (was 5432)
- **Redis**: `6380` (was 6379)
- **Elasticsearch**: `9201` (was 9200), `9301` (was 9300)
- **RabbitMQ**: `5673` (was 5672), `15673` (was 15672)

## Quick Reference

| Service | Old Port | New Port | Purpose |
|---------|----------|----------|---------|
| Frontend | 3000 | 3001 | React development server |
| API Gateway | 8080 | 8090 | Central API endpoint |
| Nginx | 80/443 | 9090/8443 | Production web server |
| PostgreSQL | 5432 | 5433 | Database |
| Redis | 6379 | 6380 | Cache & sessions |
| Elasticsearch | 9200 | 9201 | Search engine |
| RabbitMQ | 5672/15672 | 5673/15673 | Message broker / Management UI |
| Eureka | 8761 | 8762 | Service discovery |

## Accessing the Application

After starting with `./scripts/deploy.sh`:

- **Frontend**: http://localhost:3001
- **API Gateway**: http://localhost:8090
- **API Documentation**: http://localhost:8090/swagger-ui.html
- **Eureka Dashboard**: http://localhost:8762
- **RabbitMQ Management**: http://localhost:15673 (guest/guest)

## Environment Variables

If running services individually (not in Docker), update these environment variables:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5433

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6380

# Elasticsearch
export ELASTICSEARCH_HOST=localhost
export ELASTICSEARCH_PORT=9201

# RabbitMQ
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5673

# API Gateway
export API_GATEWAY_URL=http://localhost:8090
```

## Troubleshooting

If you still encounter port conflicts:

1. Check which ports are in use:
   ```bash
   # macOS/Linux
   lsof -i :PORT_NUMBER
   
   # Windows
   netstat -ano | findstr :PORT_NUMBER
   ```

2. Stop conflicting services or choose different ports by editing:
   - `docker-compose.yml` for Docker deployments
   - `application.yml` for individual service configuration
   - `vite.config.ts` for frontend configuration

3. Remember to update all references when changing ports!
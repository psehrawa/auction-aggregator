#!/bin/bash

# Start development environment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting Auction Aggregator Development Environment${NC}"

# Stop any existing containers
echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose -f docker-compose-dev.yml down

# Start infrastructure services
echo -e "${YELLOW}Starting infrastructure services...${NC}"
docker-compose -f docker-compose-dev.yml up -d

# Wait for services to be ready
echo -e "${YELLOW}Waiting for services to be ready...${NC}"
sleep 10

# Check service health
echo -e "${YELLOW}Checking service health...${NC}"
docker-compose -f docker-compose-dev.yml ps

echo -e "${GREEN}Infrastructure services are running!${NC}"
echo -e "${GREEN}Service URLs:${NC}"
echo -e "  PostgreSQL: localhost:5433"
echo -e "  Redis: localhost:6380"
echo -e "  Elasticsearch: localhost:9201"
echo -e "  RabbitMQ: localhost:5673"
echo -e "  RabbitMQ Management: http://localhost:15673 (guest/guest)"

echo -e ""
echo -e "${YELLOW}To start the backend services:${NC}"
echo -e "  cd backend/auction-service"
echo -e "  ./mvnw spring-boot:run -Dspring.profiles.active=local"

echo -e ""
echo -e "${YELLOW}To start the frontend:${NC}"
echo -e "  cd frontend"
echo -e "  npm install"
echo -e "  npm run dev"
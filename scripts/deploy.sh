#!/bin/bash

# Deployment script for Auction Aggregator Platform

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
ENV=${1:-dev}
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env.${ENV}"

echo -e "${GREEN}Deploying Auction Aggregator Platform - Environment: ${ENV}${NC}"

# Check if environment file exists
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Environment file ${ENV_FILE} not found!${NC}"
    exit 1
fi

# Load environment variables
export $(cat $ENV_FILE | grep -v '^#' | xargs)

# Function to check if service is healthy
check_service_health() {
    local service=$1
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}Waiting for ${service} to be healthy...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose ps | grep -q "${service}.*healthy"; then
            echo -e "${GREEN}${service} is healthy!${NC}"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}${service} failed to become healthy after ${max_attempts} attempts${NC}"
    return 1
}

# Pull latest images
echo -e "${YELLOW}Pulling latest images...${NC}"
docker-compose pull

# Build services
echo -e "${YELLOW}Building services...${NC}"
docker-compose build --parallel

# Start infrastructure services first
echo -e "${YELLOW}Starting infrastructure services...${NC}"
docker-compose up -d postgres redis elasticsearch rabbitmq

# Wait for infrastructure to be ready
check_service_health "postgres"
check_service_health "redis"
check_service_health "elasticsearch"
check_service_health "rabbitmq"

# Start Eureka server
echo -e "${YELLOW}Starting Eureka server...${NC}"
docker-compose up -d eureka-server
check_service_health "eureka-server"

# Start microservices
echo -e "${YELLOW}Starting microservices...${NC}"
docker-compose up -d api-gateway auction-service user-service payment-service notification-service analytics-service

# Wait for services to register with Eureka
echo -e "${YELLOW}Waiting for services to register...${NC}"
sleep 10

# Start frontend and nginx
echo -e "${YELLOW}Starting frontend services...${NC}"
docker-compose up -d frontend nginx

# Show service status
echo -e "${GREEN}Deployment complete! Service status:${NC}"
docker-compose ps

# Show logs for any failed services
failed_services=$(docker-compose ps | grep -E "(Exit|unhealthy)" | awk '{print $1}')
if [ ! -z "$failed_services" ]; then
    echo -e "${RED}Failed services detected:${NC}"
    for service in $failed_services; do
        echo -e "${RED}Logs for $service:${NC}"
        docker-compose logs --tail=50 $service
    done
fi

echo -e "${GREEN}Application is available at:${NC}"
echo -e "  Frontend: http://localhost:3001"
echo -e "  API Gateway: http://localhost:8090"
echo -e "  Eureka Dashboard: http://localhost:8762"
echo -e "  RabbitMQ Management: http://localhost:15673"
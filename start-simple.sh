#!/bin/bash

# Simple startup script for Auction Aggregator
set -e

echo "Starting Auction Aggregator - Simplified Version"
echo "================================================"
echo ""
echo "This will start:"
echo "- PostgreSQL (port 5433)"
echo "- Frontend React app (port 3001)" 
echo ""

# Check if PostgreSQL is already running
if lsof -Pi :5433 -sTCP:LISTEN -t >/dev/null ; then
    echo "✓ PostgreSQL is already running on port 5433"
else
    echo "Starting PostgreSQL container..."
    docker run -d \
        --name auction-postgres \
        -e POSTGRES_USER=auction_user \
        -e POSTGRES_PASSWORD=auction_pass \
        -e POSTGRES_DB=auction_db \
        -p 5433:5432 \
        postgres:15-alpine
    
    echo "Waiting for PostgreSQL to start..."
    sleep 5
fi

# Start frontend
echo ""
echo "Starting frontend development server..."
echo "Please run in a new terminal:"
echo ""
echo "cd frontend"
echo "npm install"
echo "npm run dev"
echo ""
echo "The application will be available at:"
echo "- Frontend: http://localhost:3001"
echo ""
echo "For the backend, you can create a simple Spring Boot app or use the existing auction-service"
echo "with simplified configuration."
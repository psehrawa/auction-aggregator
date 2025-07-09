#!/bin/bash

echo "🔍 Auction Aggregator Status Check"
echo "================================="
echo

# Check if frontend is running
echo -n "Frontend (port 3001): "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3001 | grep -q "200"; then
    echo "✅ Running"
else
    echo "❌ Not running"
fi

# Check if backend is running
echo -n "Backend (port 8081): "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/v1/auctions | grep -q "200"; then
    echo "✅ Running"
else
    echo "❌ Not running"
fi

# Check API proxy
echo -n "API Proxy: "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3001/api/v1/auctions | grep -q "200"; then
    echo "✅ Working"
else
    echo "❌ Not working"
fi

echo
echo "📊 API Data Check:"
echo -n "Total Auctions: "
curl -s http://localhost:8081/api/v1/auctions | jq length

echo -n "Trending Auctions: "
curl -s http://localhost:8081/api/v1/auctions/trending | jq length

echo -n "Ending Soon: "
curl -s http://localhost:8081/api/v1/auctions/ending-soon | jq length

echo
echo "🌐 Access the app at: http://localhost:3001"
echo "📝 View test results: cat ui-test-results.md"
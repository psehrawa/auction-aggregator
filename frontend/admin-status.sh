#!/bin/bash

echo "🔍 Admin Portal Status Check"
echo "============================"
echo

# Check frontend
echo -n "Frontend (port 3001): "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3001 | grep -q "200"; then
    echo "✅ Running"
else
    echo "❌ Not running"
fi

# Check backend
echo -n "Backend (port 8081): "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/v1/auctions | grep -q "200"; then
    echo "✅ Running"
else
    echo "❌ Not running"
fi

# Check admin portal
echo -n "Admin Portal: "
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3001/admin | grep -q "200"; then
    echo "✅ Accessible"
else
    echo "❌ Not accessible"
fi

echo
echo "📊 Scraper Stats:"
curl -s http://localhost:8081/api/v1/scrapers/stats | jq

echo
echo "🔧 Scrapers:"
curl -s http://localhost:8081/api/v1/scrapers | jq '.[] | {name: .name, type: .type, status: .status}'

echo
echo "🌐 Access the admin portal at: http://localhost:3001/admin"
echo "📝 Features available:"
echo "   - Web Scraper Management"
echo "   - Auction Import & Review"
echo "   - Scheduling Configuration"
echo "   - Scraper Settings"
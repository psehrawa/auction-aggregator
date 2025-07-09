# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture Overview

This is a microservices-based auction aggregator platform with:
- **Frontend**: React 18 + TypeScript + Redux Toolkit + Material-UI
- **Backend**: Initially designed for microservices, currently simplified to a single Spring Boot service
- **Web Scraping**: Dual-engine system using JSoup (static HTML) and Playwright (JavaScript-rendered sites)

## Essential Commands

### Frontend Development
```bash
cd frontend
npm install              # Install dependencies
npm run dev             # Start dev server on port 3001
npm run build           # Production build
npm run lint            # Run ESLint
npm run test            # Run tests with Vitest
```

### Backend Development
```bash
cd simple-backend
mvn clean compile       # Compile Java code
mvn spring-boot:run     # Start backend on port 8081
mvn test                # Run tests
```

### Quick Start Both Services
```bash
# Terminal 1 - Backend
cd simple-backend && mvn spring-boot:run

# Terminal 2 - Frontend  
cd frontend && npm run dev
```

### Service Status Check
```bash
# Check all services
./frontend/check-status.sh

# Admin portal status
./frontend/admin-status.sh
```

## Key URLs and Ports

- Frontend: http://localhost:3001
- Backend API: http://localhost:8081
- Admin Portal: http://localhost:3001/admin
- API Gateway (Docker): http://localhost:8090
- Nginx (Docker): http://localhost:9090 (changed from 8080)

### Infrastructure Ports (Docker)
- PostgreSQL: 5433
- Redis: 6380
- Elasticsearch: 9201
- RabbitMQ: 5673 (AMQP), 15673 (Management)

## Project Structure

```
auction-aggregator/
├── frontend/                    # React TypeScript application
│   ├── src/
│   │   ├── components/         # Reusable UI components
│   │   ├── pages/             # Page components (routes)
│   │   ├── store/             # Redux store and API slices
│   │   └── types/             # TypeScript type definitions
│   └── vite.config.ts         # Vite configuration with proxy
├── simple-backend/             # Simplified Spring Boot backend
│   └── src/main/java/com/auctionaggregator/
│       ├── controller/        # REST endpoints
│       ├── service/          # Business logic & web scraping
│       └── model/            # Data models
└── backend/                   # Original microservices (not currently used)
```

## Web Scraping System

The platform includes a sophisticated web scraping system accessible via the Admin Portal:

### Key Components
1. **WebScraperService.java**: Core scraping logic with JSoup/Playwright
2. **RealScraperController.java**: REST API for scraping operations
3. **RealTimeScraper.tsx**: Admin UI for configuring and testing scrapers

### Scraping Endpoints
- `POST /api/v1/scrapers/real/scrape` - Scrape single URL
- `POST /api/v1/scrapers/real/scrape-batch` - Batch scraping
- `POST /api/v1/scrapers/real/extract-auction` - Process scraped data

### Configuration
Scrapers can be configured with:
- CSS selectors for specific elements
- JavaScript rendering toggle
- Wait times for dynamic content
- Preset configurations for common auction sites

## Important Implementation Details

### Frontend API Proxy
The Vite dev server proxies `/api` requests to the backend:
```typescript
// vite.config.ts
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
  }
}
```

### Mock Data
The frontend includes mock auction data for development. Real data comes from:
- Backend endpoints: `/api/v1/auctions/*`
- Scraped data via admin portal

### Playwright Downloads
First run of web scraping will download Playwright browsers (~300MB). This is one-time per machine.

### TypeScript Workarounds
Some auction fields use `(auction as any)` casting due to incomplete type definitions. Future work should properly type all auction fields.

## Common Issues and Solutions

1. **Port Conflicts**: Check PORT_MAPPING.md for all port assignments
2. **Backend Won't Start**: Kill process on port 8081: `lsof -ti:8081 | xargs kill -9`
3. **Scraping Errors**: Ensure target URL is complete (https://...) and selectors are valid
4. **Frontend Build Errors**: Clear node_modules and reinstall: `rm -rf node_modules && npm install`

## Testing Web Scraping

1. Go to Admin Portal: http://localhost:3001/admin
2. Click "Real-Time Scraper" tab
3. Use presets or configure manually
4. For bank auctions, use e-auction sections, not main homepages

## Git Configuration

The project uses port 9090 for Nginx (instead of 8080) to avoid conflicts. This is reflected in PORT_MAPPING.md and should be maintained in docker-compose configurations.
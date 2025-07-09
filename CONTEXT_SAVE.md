# Auction Aggregator - Context Save
Date: 2025-07-08
Time: 19:54 PST

## Current State
- **Frontend**: Running on http://localhost:3001
- **Backend**: Running on http://localhost:8081
- **Repository**: https://github.com/psehrawa/auction-aggregator

## Architecture
- Frontend: React 18 + TypeScript + Redux Toolkit + Material-UI
- Backend: Spring Boot 3.2.0 with Java 17
- Web Scraping: JSoup (static) + Playwright (dynamic)

## Key Features Implemented
1. **Auction Listing Page**: Displays auctions with cards
2. **Auction Detail Page**: Shows full auction details with source URL buttons
3. **Admin Portal**: Web scraping management with real-time scraper
4. **Source URL Feature**: Added sourceUrl and sourcePdfUrl to auction model
5. **Web Scraping**: Universal scraper that works with any website

## API Endpoints
- GET `/api/v1/auctions` - Get all auctions
- GET `/api/v1/auctions/{id}` - Get auction by ID
- POST `/api/v1/scrapers/real/scrape` - Scrape a URL
- POST `/api/v1/scrapers/real/scrape-and-import` - Scrape and import as auction
- GET `/api/v1/scrapers` - Get scraper configurations
- GET `/api/v1/scrapers/scraped-auctions` - Get scraped auctions

## Current Issues to Fix
1. Frontend is using mock data instead of real API data
2. Demo data needs to be removed
3. API integration needs to be verified
4. Ensure scraped auctions appear in the main auction list

## Mock Data Locations
- `/frontend/src/data/mockAuctions.ts` - Contains additionalMockAuctions
- `/frontend/src/pages/HomePage.tsx` - Uses mock data directly
- `/frontend/src/pages/SearchPage.tsx` - Uses mock data
- `/frontend/src/store/api/auctionApi.ts` - Has mockData fallback

## Next Steps
1. Update auctionApi.ts to properly connect to backend
2. Update HomePage to use API data
3. Remove mock data files
4. Ensure auction controller returns proper data
5. Test scraping and verify auctions appear
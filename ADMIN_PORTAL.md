# Admin Portal - Auction Aggregator

## Overview

The Admin Portal provides a comprehensive interface for managing web scrapers that automatically discover new auctions from various financial institutions including banks, NBFCs (Non-Banking Financial Companies), and government institutions.

## Access

- **URL**: http://localhost:3001/admin
- **Authentication**: Currently open access (authentication to be implemented)

## Features

### 1. Web Scraper Management

Manage and monitor web scrapers for different financial institutions:

- **Bank Scrapers**: SBI, HDFC Bank, ICICI Bank, etc.
- **NBFC Scrapers**: Bajaj Finance, Muthoot Finance, etc.
- **Government Scrapers**: Government auction portals

**Capabilities:**
- ✅ Add/Edit/Delete scrapers
- ✅ Configure scraping rules (CSS selectors, pagination)
- ✅ Enable/Disable individual scrapers
- ✅ Run scrapers manually
- ✅ Monitor scraper status (active, running, error, inactive)
- ✅ View scraping history and results

### 2. Auction Import & Review

Review and manage scraped auctions before importing to the main platform:

- **Pending Review**: New auctions requiring approval
- **Approved**: Auctions ready for import
- **Rejected**: Auctions that don't meet criteria

**Features:**
- ✅ Bulk approve/reject actions
- ✅ Preview auction details and images
- ✅ Filter by source institution
- ✅ Import approved auctions to main platform

### 3. Scheduling

Configure automated scraping schedules:

- **Frequency Options**: Hourly, Daily, Weekly
- **Schedule Types**:
  - Daily Bank Scraping (9:00 AM)
  - NBFC Weekly Scan (Mondays 2:00 PM)
  - Hourly Hot Deals Check

**Features:**
- ✅ Create custom schedules
- ✅ Assign multiple scrapers to schedules
- ✅ Enable/Disable schedules
- ✅ Run schedules manually

### 4. Settings

Configure global scraper settings:

**General Settings:**
- Auto-approve scraped auctions
- Notification preferences
- Retry attempts and timeouts
- User agent configuration

**Filter Settings:**
- Minimum/Maximum price filters
- Exclude keywords
- Category preferences

**API & Performance:**
- Rate limiting
- Concurrent requests
- Proxy configuration

**Notifications:**
- Email alerts
- Webhook integration
- Slack notifications

## Mock Data

The current implementation includes mock data for demonstration:

### Sample Scrapers:
1. **SBI Auctions** - Active, 45 items found
2. **HDFC Bank E-Auctions** - Running, 32 items found
3. **ICICI Bank Properties** - Active, 28 items found
4. **Bajaj Finance Auctions** - Error state
5. **Muthoot Finance** - Inactive

### Sample Scraped Auctions:
- Real estate properties (Mumbai, Bangalore, Pune)
- Vehicles (Honda City 2019)
- Gold jewelry
- Various price ranges: ₹2.5L to ₹1.5Cr

## API Endpoints

The admin portal uses the following backend endpoints:

```
GET  /api/v1/scrapers/stats          # Scraper statistics
GET  /api/v1/scrapers               # List all scrapers
GET  /api/v1/scrapers/scraped-auctions  # List scraped auctions
POST /api/v1/scrapers/{id}/run      # Run a specific scraper
POST /api/v1/scrapers/scraped-auctions/{id}/approve  # Approve auction
POST /api/v1/scrapers/scraped-auctions/{id}/reject   # Reject auction
POST /api/v1/scrapers/scraped-auctions/import        # Import auctions
```

## Quick Commands

```bash
# Check admin portal status
./admin-status.sh

# Access admin portal
open http://localhost:3001/admin

# View backend logs
tail -f backend.log

# View frontend logs
tail -f frontend.log
```

## Next Steps for Production

1. **Implement Real Web Scraping**:
   - Integrate Playwright/Puppeteer for JavaScript-rendered sites
   - Add JSoup for static HTML scraping
   - Implement proxy rotation

2. **Add Authentication**:
   - Admin user roles and permissions
   - Secure API endpoints
   - Session management

3. **Database Integration**:
   - Store scraper configurations
   - Persist scraped auction data
   - Track scraping history

4. **Advanced Features**:
   - Machine learning for auction categorization
   - Duplicate detection
   - Price trend analysis
   - Automated quality checks

5. **Monitoring & Alerts**:
   - Real-time scraper monitoring
   - Error notifications
   - Performance metrics
   - Success rate tracking

## Current Limitations

- Mock data only (no real scraping yet)
- No authentication
- No data persistence
- Frontend-only state management
- No real scheduling (cron jobs needed)
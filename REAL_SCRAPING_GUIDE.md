# Real Web Scraping Guide - Auction Aggregator

## Overview

The Auction Aggregator now includes a powerful, generalized web scraping system that can extract auction data from any website. The system uses both JSoup (for static HTML) and Playwright (for JavaScript-rendered sites) to ensure compatibility with all types of websites.

## Features

### 1. Real-Time Web Scraper (Admin Portal)

Access the Real-Time Scraper at: http://localhost:3001/admin

#### Key Features:
- **Universal Scraping**: Works with any website URL
- **Dual Engine Support**: 
  - JSoup for static HTML sites (faster)
  - Playwright for JavaScript-heavy sites (complete rendering)
- **Smart Data Extraction**: Automatically detects and extracts:
  - Auction titles
  - Prices (supports ₹, Rs, INR, $ formats)
  - Locations (detects Indian cities)
  - End dates/times
  - Images
  - Additional metadata
- **Preset Configurations**: Quick templates for common auction site patterns
- **Custom Selectors**: Fine-tune extraction with CSS selectors

### 2. API Endpoints

#### Scrape Single URL
```bash
POST /api/v1/scrapers/real/scrape
Content-Type: application/json

{
  "url": "https://example.com/auctions",
  "options": {
    "requiresJS": "false",
    "contentSelector": ".auction-listing",
    "titleSelector": "h2",
    "priceSelector": ".price",
    "locationSelector": ".location",
    "dateSelector": ".end-date"
  },
  "async": false
}
```

#### Batch Scraping
```bash
POST /api/v1/scrapers/real/scrape-batch
Content-Type: application/json

[
  {
    "url": "https://site1.com/auctions",
    "options": {...}
  },
  {
    "url": "https://site2.com/auctions",
    "options": {...}
  }
]
```

#### Extract Auction Data
```bash
POST /api/v1/scrapers/real/extract-auction
Content-Type: application/json

{
  "extractedData": {...},
  "images": [...],
  "url": "...",
  "scrapedAt": "..."
}
```

## Usage Examples

### Example 1: Scraping a Static Auction Site

```bash
curl -X POST http://localhost:8081/api/v1/scrapers/real/scrape \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://auction-site.com/properties",
    "options": {
      "requiresJS": "false",
      "titleSelector": ".property-title",
      "priceSelector": ".starting-bid",
      "locationSelector": ".property-address"
    }
  }'
```

### Example 2: Scraping a JavaScript-Rendered Site

```bash
curl -X POST http://localhost:8081/api/v1/scrapers/real/scrape \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://modern-auction.com/listings",
    "options": {
      "requiresJS": "true",
      "waitSelector": "[data-loaded='true']",
      "waitTime": "3000",
      "contentSelector": ".auction-grid"
    }
  }'
```

## Selector Configuration Guide

### Common Patterns

1. **Government Auction Sites**:
   ```json
   {
     "requiresJS": "false",
     "contentSelector": "table.auction-table, .auction-list",
     "titleSelector": "td:nth-child(2), .auction-title",
     "priceSelector": "td:contains('Reserve Price'), .reserve-price",
     "locationSelector": "td:contains('Location'), .property-location",
     "dateSelector": "td:contains('Auction Date'), .auction-date"
   }
   ```

2. **Bank Auction Portals**:
   ```json
   {
     "requiresJS": "true",
     "waitSelector": ".property-cards-loaded",
     "contentSelector": ".property-card, .auction-item",
     "titleSelector": ".card-title, h3",
     "priceSelector": ".price-tag, .starting-price",
     "locationSelector": ".location-info, .address",
     "dateSelector": ".auction-timer, .end-date"
   }
   ```

3. **NBFC Auction Sites**:
   ```json
   {
     "requiresJS": "false",
     "contentSelector": ".listing-container",
     "titleSelector": ".item-name",
     "priceSelector": ".bid-amount",
     "locationSelector": ".branch-location",
     "dateSelector": ".closing-date"
   }
   ```

## Data Extraction Intelligence

The scraper includes smart extraction algorithms:

### Price Detection
- Recognizes formats: ₹1,00,000, Rs. 100000, INR 1 Lakh, $1000
- Extracts numeric values from text
- Handles both starting price and current bid

### Location Detection
- Identifies major Indian cities automatically
- Extracts from common patterns: "Property in Mumbai", "Location: Delhi"
- Supports state names and area details

### Date Extraction
- Recognizes multiple date formats
- Extracts from datetime attributes
- Identifies auction end times

### Category Classification
- Automatically categorizes based on content:
  - Real Estate: property, flat, apartment, house, land
  - Vehicles: car, bike, motorcycle, truck
  - Jewelry: gold, jewelry, jewellery
  - Electronics: laptop, mobile, computer
  - Machinery: equipment, industrial

## Best Practices

1. **Start with Static Scraping**: Try `requiresJS: false` first - it's faster
2. **Use Specific Selectors**: More specific = more reliable
3. **Test Incrementally**: Start with basic extraction, then add fields
4. **Handle Pagination**: For multi-page results, scrape page by page
5. **Respect Rate Limits**: Don't overwhelm target servers

## Troubleshooting

### Common Issues:

1. **No Data Extracted**:
   - Check if site requires JavaScript (`requiresJS: true`)
   - Verify selectors match the actual HTML
   - Increase wait time for slow-loading sites

2. **Incomplete Data**:
   - Use browser DevTools to inspect element selectors
   - Try alternative selectors (class, id, data attributes)
   - Check if data is loaded dynamically

3. **Price/Date Format Issues**:
   - The extractor handles common formats automatically
   - For custom formats, use specific selectors

## Security & Ethics

- **Respect robots.txt**: Check site policies before scraping
- **Rate Limiting**: Don't make too many requests too quickly
- **User Agent**: The scraper uses standard browser user agents
- **Legal Compliance**: Ensure you have permission to scrape the target sites

## Quick Start

1. Open Admin Portal: http://localhost:3001/admin
2. Click on "Real-Time Scraper" tab
3. Enter any auction website URL
4. Select a preset or configure manually
5. Click "Start Scraping"
6. Review extracted data
7. Import to main platform if satisfied

## Example Sites to Test

For testing purposes, you can use these patterns:
- Government portals often use table layouts
- Modern sites use card-based designs
- Bank sites typically require JavaScript rendering
- Look for sites with clear auction listings

Remember to replace example URLs with actual auction websites!
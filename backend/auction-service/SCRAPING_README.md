# Web Scraping Implementation

## Overview

The auction aggregator uses Playwright for web scraping to collect auction data from multiple sources. The implementation includes:

- **Playwright-based scrapers** for dynamic content
- **Health monitoring** to track scraper performance
- **Rate limiting** to respect source websites
- **Scheduled aggregation** every 5 minutes
- **Error handling and retry mechanisms**

## Architecture

### Scraper Services

1. **RealEstateAuctionScraperService** - Scrapes real estate auctions
2. **GovAuctionScraperService** - Scrapes government surplus auctions (GeM portal)
3. **VehicleAuctionScraperService** - Scrapes vehicle auctions (Copart-style)

### Core Components

- **PlaywrightConfig** - Manages browser instances
- **AuctionAggregatorService** - Orchestrates all scrapers
- **ScraperHealthService** - Monitors scraper health and performance
- **ScraperConfiguration** - Manages scraper settings

## Configuration

Scrapers can be configured in `application.yml`:

```yaml
scraper:
  enabled: true
  thread-pool-size: 5
  
  scrapers:
    real-estate:
      enabled: true
      url: https://www.auction.com
      rate-limit:
        requests-per-minute: 30
```

## API Endpoints

### Health Monitoring

- `GET /api/v1/scraper/health` - Get health status of all scrapers
- `GET /api/v1/scraper/health/{scraperName}` - Get specific scraper health
- `POST /api/v1/scraper/sync` - Trigger manual synchronization

## Production Implementation

The current implementation uses demo data. To implement real scraping:

1. **Update Selectors**: Replace demo data generation with actual CSS selectors
2. **Handle Authentication**: Some sites may require login
3. **Implement Proxies**: To avoid IP blocking
4. **Add User Agents**: Rotate user agents for better success rates
5. **Respect robots.txt**: Always check and respect robots.txt files

### Example Real Implementation

```java
// Instead of demo data:
page.navigate(baseUrl + "/auctions");
page.waitForSelector(".auction-card");

List<ElementHandle> cards = page.querySelectorAll(".auction-card");
for (ElementHandle card : cards) {
    String title = card.querySelector(".title").innerText();
    String price = card.querySelector(".price").innerText();
    // ... extract other fields
}
```

## Rate Limiting

Each scraper has configurable rate limiting:

- `requests-per-minute`: Maximum requests per minute
- `burst-size`: Maximum concurrent requests
- `cooldown-period`: Wait time after hitting limits

## Error Handling

The system includes:

- Automatic retries with exponential backoff
- Health monitoring to disable failing scrapers
- Detailed error logging
- Graceful degradation

## Monitoring

### Health Metrics

- Success rate
- Consecutive failures
- Items scraped per run
- Run duration
- Last error message

### Unhealthy Scraper Criteria

A scraper is considered unhealthy if:
- Success rate < 70%
- More than 5 consecutive failures
- No successful run in the last hour

## Testing

Run the integration tests:

```bash
mvn test -Dtest=ScraperIntegrationTest
```

## Deployment Considerations

1. **Browser Dependencies**: Playwright requires browser binaries
2. **Memory Usage**: Headless browsers consume significant memory
3. **Network Access**: Ensure the deployment environment can access target sites
4. **Legal Compliance**: Always respect website terms of service

## Future Enhancements

1. **Machine Learning**: Automatic selector detection
2. **Distributed Scraping**: Scale across multiple nodes
3. **Visual Regression**: Detect website changes
4. **API Integration**: Prefer APIs when available
5. **Data Validation**: ML-based data quality checks
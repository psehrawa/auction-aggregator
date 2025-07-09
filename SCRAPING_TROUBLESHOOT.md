# Scraping Troubleshooting Guide

## Issue: Kotak Bank Scraping Results Show "Unknown Auction"

### Problem Analysis
You encountered this error when trying to scrape `https://www.kotak.com/en/home.html`:
```
Title: Unknown Auction
Price: ₹0
Location: N/A
End Date: N/A
Category: other
```

### Root Causes

1. **Wrong URL**: You were scraping the Kotak Bank homepage instead of their auction portal
2. **Empty Selectors**: The scraper was receiving empty CSS selectors causing Playwright errors
3. **No Auction Data**: The homepage doesn't contain auction listings

### Solutions Implemented

#### 1. Fixed Empty Selector Handling
Updated the backend to handle empty/invalid CSS selectors gracefully:
```java
if (contentSelector != null && !contentSelector.trim().isEmpty()) {
    try {
        result.content = page.locator(contentSelector).textContent();
    } catch (Exception e) {
        result.content = page.locator("body").textContent();
    }
} else {
    result.content = page.locator("body").textContent();
}
```

#### 2. Added Correct URLs for Bank Auctions
- **Kotak Bank**: `auctions.kotak.com` or `kotak.e-auctions.in`
- **SBI**: `sbiauctions.in` or bank's e-auction section
- **HDFC**: `hdfcbank.com/e-auctions` or similar
- **ICICI**: `icicibank.com/e-auction` or similar

#### 3. Added Bank-Specific Presets
New preset configurations in the admin portal:
- **Kotak Bank Auctions**: Optimized for their auction portal
- **Generic Bank Auction**: Works with most bank auction sites
- **Government Portal**: For government auction websites

## How to Use Correctly

### Step 1: Use the Right URL
❌ **Wrong**: `https://www.kotak.com/en/home.html`
✅ **Correct**: `https://auctions.kotak.com`

### Step 2: Select Appropriate Preset
1. Go to Admin Portal: http://localhost:3001/admin
2. Click "Real-Time Scraper" tab
3. Enter the correct auction URL
4. Click "Kotak Bank Auctions" preset
5. Click "Start Scraping"

### Step 3: Configure for Other Banks
For other bank auction sites:
1. Use "Generic Bank Auction" preset
2. Enable "Site requires JavaScript rendering"
3. Adjust selectors if needed:
   - Content: `.auction-listing, .property-listing`
   - Title: `h1, h2, .title, .property-name`
   - Price: `.reserve-price, .starting-price, .price`
   - Location: `.location, .address, .city`

## Common Bank Auction URLs

### Major Banks
- **State Bank of India**: `sbiauctions.in`
- **HDFC Bank**: `hdfcbank.com` → Search for "e-auction"
- **ICICI Bank**: `icicibank.com` → Look for "Auction Properties"
- **Punjab National Bank**: `pnbindia.in` → E-auction section
- **Bank of Baroda**: `bankofbaroda.in` → Auction portal
- **Canara Bank**: `canarabank.com` → E-auction

### NBFCs
- **Bajaj Finance**: `bajajfinance.in/auctions`
- **Muthoot Finance**: `muthoot.com` → Auction section
- **Mahindra Finance**: Look for property auction pages

## Selector Patterns for Bank Sites

### Common Patterns Found in Bank Auction Sites:

1. **Table-Based Layout** (Government/Old sites):
   ```css
   contentSelector: "table.auction-table"
   titleSelector: "td:nth-child(2)"
   priceSelector: "td:contains('Reserve')"
   ```

2. **Card-Based Layout** (Modern sites):
   ```css
   contentSelector: ".auction-grid, .property-cards"
   titleSelector: ".card-title, .property-name"
   priceSelector: ".price-tag, .reserve-amount"
   ```

3. **JavaScript-Heavy Sites**:
   ```css
   requiresJS: true
   waitTime: 5000
   waitSelector: ".results-loaded"
   ```

## Testing Your Configuration

### 1. Quick Test
Use the test endpoint to verify configurations:
```bash
curl -X GET http://localhost:8081/api/v1/scrapers/real/test-sites
```

### 2. Manual Test
1. Open the target auction site in browser
2. Right-click → Inspect Element
3. Look for auction listings structure
4. Test CSS selectors in browser console:
   ```javascript
   document.querySelectorAll('.auction-item').length
   ```

### 3. Gradual Configuration
Start simple and add complexity:
1. First: Just get page title and content
2. Then: Add title selector
3. Then: Add price selector
4. Finally: Add location and date selectors

## Error Resolution

### "Unexpected token while parsing selector"
- **Cause**: Empty or invalid CSS selector
- **Fix**: Ensure all selectors have valid values
- **Example**: Use `h1, h2` instead of empty string

### "No data extracted"
- **Cause**: Wrong selectors or site requires JavaScript
- **Fix**: 
  1. Enable "JavaScript rendering"
  2. Increase wait time
  3. Use browser DevTools to find correct selectors

### "Price shows ₹0"
- **Cause**: Price selector not finding price elements
- **Fix**: Look for different price patterns:
  - `.reserve-price`
  - `.starting-price`
  - `[class*="price"]`
  - Text containing "Reserve" or "Starting"

## Best Practices

1. **Always start with the official auction portal** (not homepage)
2. **Use preset configurations** as starting points
3. **Test with JavaScript rendering** for modern sites
4. **Inspect the HTML structure** before configuring selectors
5. **Start simple** and gradually add more specific selectors
6. **Be patient** - some sites load slowly

## Success Indicators

✅ **Good Results**:
- Title extracted correctly
- Price in proper format (₹X,XX,XXX)
- Location shows city/state
- Images are found
- Category auto-detected

❌ **Poor Results**:
- Title: "Unknown Auction"
- Price: "₹0"
- Location: "N/A"
- No images found

When you see poor results, adjust the configuration and try again!
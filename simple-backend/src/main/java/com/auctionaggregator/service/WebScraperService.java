package com.auctionaggregator.service;

import com.microsoft.playwright.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
public class WebScraperService {

    private static final Logger log = Logger.getLogger(WebScraperService.class.getName());

    private Playwright playwright;
    private Browser browser;

    public WebScraperService() {
        initPlaywright();
    }

    private void initPlaywright() {
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList("--no-sandbox", "--disable-setuid-sandbox"))
            );
        } catch (Exception e) {
            log.severe("Failed to initialize Playwright: " + e.getMessage());
        }
    }

    public static class ScrapedData {
        public String url;
        public String title;
        public String content;
        public Map<String, String> extractedData;
        public List<String> images;
        public LocalDateTime scrapedAt;
        public String sourceHtml;
    }

    public static class AuctionData {
        public String title;
        public String description;
        public String price;
        public String location;
        public String endDate;
        public List<String> images;
        public Map<String, String> additionalInfo;
    }

    /**
     * Scrape a website using appropriate method (static or dynamic)
     */
    public ScrapedData scrapeWebsite(String url, Map<String, String> options) {
        ScrapedData result = new ScrapedData();
        result.url = url;
        result.scrapedAt = LocalDateTime.now();
        result.extractedData = new HashMap<>();
        result.images = new ArrayList<>();

        try {
            // Check if site requires JavaScript rendering
            boolean requiresJS = options.getOrDefault("requiresJS", "false").equals("true");
            
            if (requiresJS && browser != null) {
                result = scrapeDynamicSite(url, options);
            } else {
                result = scrapeStaticSite(url, options);
            }
        } catch (Exception e) {
            log.severe("Error scraping " + url + ": " + e.getMessage());
            result.extractedData.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Scrape static HTML sites using JSoup
     */
    private ScrapedData scrapeStaticSite(String url, Map<String, String> options) throws IOException {
        ScrapedData result = new ScrapedData();
        result.url = url;
        result.scrapedAt = LocalDateTime.now();
        result.extractedData = new HashMap<>();
        result.images = new ArrayList<>();

        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .timeout(30000)
            .get();

        result.title = doc.title();
        result.sourceHtml = doc.html();

        // Extract based on selectors if provided
        String contentSelector = options.getOrDefault("contentSelector", "body");
        Elements content = doc.select(contentSelector);
        result.content = content.text();

        // Extract images
        Elements images = doc.select("img");
        for (Element img : images) {
            String imgUrl = img.absUrl("src");
            if (!imgUrl.isEmpty()) {
                result.images.add(imgUrl);
            }
        }

        // Extract auction-specific data
        AuctionData auctionData = extractAuctionData(doc, options);
        result.extractedData.put("title", auctionData.title);
        result.extractedData.put("price", auctionData.price);
        result.extractedData.put("location", auctionData.location);
        result.extractedData.put("endDate", auctionData.endDate);
        result.extractedData.putAll(auctionData.additionalInfo);

        return result;
    }

    /**
     * Scrape JavaScript-rendered sites using Playwright
     */
    private ScrapedData scrapeDynamicSite(String url, Map<String, String> options) {
        ScrapedData result = new ScrapedData();
        result.url = url;
        result.scrapedAt = LocalDateTime.now();
        result.extractedData = new HashMap<>();
        result.images = new ArrayList<>();

        try (Page page = browser.newPage()) {
            page.navigate(url);
            
            // Wait for content to load
            String waitSelector = options.getOrDefault("waitSelector", "body");
            page.waitForSelector(waitSelector, new Page.WaitForSelectorOptions()
                .setTimeout(30000));

            // Additional wait if specified
            if (options.containsKey("waitTime")) {
                Thread.sleep(Integer.parseInt(options.get("waitTime")));
            }

            result.title = page.title();
            result.sourceHtml = page.content();

            // Extract content
            String contentSelector = options.getOrDefault("contentSelector", "body");
            if (contentSelector != null && !contentSelector.trim().isEmpty()) {
                try {
                    result.content = page.locator(contentSelector).textContent();
                } catch (Exception e) {
                    result.content = page.locator("body").textContent();
                }
            } else {
                result.content = page.locator("body").textContent();
            }

            // Extract images
            List<ElementHandle> images = page.querySelectorAll("img");
            for (ElementHandle img : images) {
                String src = (String) img.getAttribute("src");
                if (src != null && !src.isEmpty()) {
                    result.images.add(src.startsWith("http") ? src : url + src);
                }
            }

            // Extract auction data using JavaScript evaluation
            Object extractedDataObj = page.evaluate("""
                () => {
                    const data = {};
                    
                    // Try to find price
                    const priceElements = document.querySelectorAll('[class*="price"], [id*="price"], [data-price]');
                    if (priceElements.length > 0) {
                        data.price = priceElements[0].textContent.trim();
                    }
                    
                    // Try to find location
                    const locationElements = document.querySelectorAll('[class*="location"], [id*="location"], [data-location]');
                    if (locationElements.length > 0) {
                        data.location = locationElements[0].textContent.trim();
                    }
                    
                    // Try to find dates
                    const dateElements = document.querySelectorAll('[class*="date"], [id*="date"], time');
                    if (dateElements.length > 0) {
                        data.endDate = dateElements[0].textContent.trim();
                    }
                    
                    return data;
                }
            """);

            if (extractedDataObj instanceof Map) {
                Map<String, Object> extractedData = (Map<String, Object>) extractedDataObj;
                extractedData.forEach((key, value) -> 
                    result.extractedData.put(key, value != null ? value.toString() : ""));
            }

        } catch (Exception e) {
            log.severe("Error in dynamic scraping: " + e.getMessage());
            result.extractedData.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Extract auction-specific data using various heuristics
     */
    private AuctionData extractAuctionData(Document doc, Map<String, String> options) {
        AuctionData auction = new AuctionData();
        auction.additionalInfo = new HashMap<>();
        auction.images = new ArrayList<>();

        // Title extraction
        auction.title = extractTitle(doc, options);

        // Price extraction
        auction.price = extractPrice(doc, options);

        // Location extraction
        auction.location = extractLocation(doc, options);

        // Date extraction
        auction.endDate = extractDate(doc, options);

        // Description extraction
        auction.description = extractDescription(doc, options);

        // Extract additional structured data
        Elements tables = doc.select("table");
        for (Element table : tables) {
            extractTableData(table, auction.additionalInfo);
        }

        // Extract from definition lists
        Elements dlElements = doc.select("dl");
        for (Element dl : dlElements) {
            extractDefinitionListData(dl, auction.additionalInfo);
        }

        return auction;
    }

    private String extractTitle(Document doc, Map<String, String> options) {
        // Try custom selector first
        if (options.containsKey("titleSelector")) {
            Elements titleElements = doc.select(options.get("titleSelector"));
            if (!titleElements.isEmpty()) {
                return titleElements.first().text();
            }
        }

        // Try common patterns
        String[] titleSelectors = {
            "h1", 
            "[class*='title']", 
            "[id*='title']",
            "[class*='heading']",
            ".product-name",
            ".item-title"
        };

        for (String selector : titleSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                return elements.first().text();
            }
        }

        return doc.title();
    }

    private String extractPrice(Document doc, Map<String, String> options) {
        // Price patterns
        Pattern pricePattern = Pattern.compile("(?:₹|Rs\\.?|INR|\\$)\\s*([0-9,]+(?:\\.[0-9]+)?)|([0-9,]+(?:\\.[0-9]+)?)\\s*(?:₹|Rs\\.?|INR|\\$)");
        
        // Try custom selector
        if (options.containsKey("priceSelector")) {
            Elements priceElements = doc.select(options.get("priceSelector"));
            if (!priceElements.isEmpty()) {
                return cleanPrice(priceElements.first().text());
            }
        }

        // Try common selectors
        String[] priceSelectors = {
            "[class*='price']",
            "[id*='price']",
            "[data-price]",
            ".amount",
            ".cost",
            "span:contains(₹)",
            "span:contains(Rs)"
        };

        for (String selector : priceSelectors) {
            Elements elements = doc.select(selector);
            for (Element element : elements) {
                String text = element.text();
                Matcher matcher = pricePattern.matcher(text);
                if (matcher.find()) {
                    return matcher.group(0);
                }
            }
        }

        // Search in all text
        Matcher matcher = pricePattern.matcher(doc.text());
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private String extractLocation(Document doc, Map<String, String> options) {
        // Try custom selector
        if (options.containsKey("locationSelector")) {
            Elements locationElements = doc.select(options.get("locationSelector"));
            if (!locationElements.isEmpty()) {
                return locationElements.first().text();
            }
        }

        // Common location selectors
        String[] locationSelectors = {
            "[class*='location']",
            "[id*='location']",
            "[class*='address']",
            "[data-location]",
            ".city",
            ".area"
        };

        for (String selector : locationSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                return elements.first().text();
            }
        }

        // Look for Indian city names
        Pattern cityPattern = Pattern.compile("\\b(Mumbai|Delhi|Bangalore|Kolkata|Chennai|Hyderabad|Pune|Ahmedabad|Surat|Jaipur|Lucknow|Kanpur|Nagpur|Indore|Thane|Bhopal|Visakhapatnam|Pimpri-Chinchwad|Patna|Vadodara|Ghaziabad|Ludhiana|Agra|Nashik|Faridabad|Meerut|Rajkot|Kalyan-Dombivli|Vasai-Virar|Varanasi)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(doc.text());
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private String extractDate(Document doc, Map<String, String> options) {
        // Try custom selector
        if (options.containsKey("dateSelector")) {
            Elements dateElements = doc.select(options.get("dateSelector"));
            if (!dateElements.isEmpty()) {
                return dateElements.first().text();
            }
        }

        // Common date selectors
        String[] dateSelectors = {
            "[class*='date']",
            "[id*='date']",
            "[class*='time']",
            "[class*='end']",
            "[class*='auction']",
            "time",
            "[datetime]"
        };

        for (String selector : dateSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                String dateText = elements.first().text();
                if (elements.first().hasAttr("datetime")) {
                    dateText = elements.first().attr("datetime");
                }
                return dateText;
            }
        }

        // Look for date patterns
        Pattern datePattern = Pattern.compile("\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4}|\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}");
        Matcher matcher = datePattern.matcher(doc.text());
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private String extractDescription(Document doc, Map<String, String> options) {
        if (options.containsKey("descriptionSelector")) {
            Elements descElements = doc.select(options.get("descriptionSelector"));
            if (!descElements.isEmpty()) {
                return descElements.first().text();
            }
        }

        String[] descSelectors = {
            "[class*='description']",
            "[id*='description']",
            "[class*='details']",
            "[class*='content']",
            ".summary",
            "article"
        };

        for (String selector : descSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                return elements.first().text().substring(0, Math.min(500, elements.first().text().length()));
            }
        }

        return "";
    }

    private void extractTableData(Element table, Map<String, String> data) {
        Elements rows = table.select("tr");
        for (Element row : rows) {
            Elements cells = row.select("td, th");
            if (cells.size() >= 2) {
                String key = cells.get(0).text().trim();
                String value = cells.get(1).text().trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    data.put(key, value);
                }
            }
        }
    }

    private void extractDefinitionListData(Element dl, Map<String, String> data) {
        Elements terms = dl.select("dt");
        Elements definitions = dl.select("dd");
        
        for (int i = 0; i < Math.min(terms.size(), definitions.size()); i++) {
            String key = terms.get(i).text().trim();
            String value = definitions.get(i).text().trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                data.put(key, value);
            }
        }
    }

    private String cleanPrice(String price) {
        return price.replaceAll("[^0-9,.]", "").trim();
    }

    public void cleanup() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
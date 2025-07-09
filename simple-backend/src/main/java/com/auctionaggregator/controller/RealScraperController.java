package com.auctionaggregator.controller;

import com.auctionaggregator.service.WebScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.net.URL;

@RestController
@RequestMapping("/api/v1/scrapers/real")
@CrossOrigin(origins = "*")
public class RealScraperController {

    private static final Logger log = Logger.getLogger(RealScraperController.class.getName());

    @Autowired
    private WebScraperService scraperService;

    // Store scraping jobs
    private final Map<String, ScrapingJob> jobs = new ConcurrentHashMap<>();

    public static class ScrapeRequest {
        private String url;
        private Map<String, String> options = new HashMap<>();
        private boolean async = false;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Map<String, String> getOptions() { return options; }
        public void setOptions(Map<String, String> options) { this.options = options; }
        public boolean isAsync() { return async; }
        public void setAsync(boolean async) { this.async = async; }
    }

    public static class ScrapingJob {
        private String jobId;
        private String url;
        private String status; // pending, running, completed, failed
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private WebScraperService.ScrapedData result;
        private String error;

        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public WebScraperService.ScrapedData getResult() { return result; }
        public void setResult(WebScraperService.ScrapedData result) { this.result = result; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class ScrapeResponse {
        private String jobId;
        private String status;
        private WebScraperService.ScrapedData data;
        private String message;

        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public WebScraperService.ScrapedData getData() { return data; }
        public void setData(WebScraperService.ScrapedData data) { this.data = data; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Scrape a single URL
     */
    @PostMapping("/scrape")
    public ResponseEntity<ScrapeResponse> scrapeUrl(@RequestBody ScrapeRequest request) {
        log.info("Received scrape request for URL: " + request.getUrl());
        
        ScrapeResponse response = new ScrapeResponse();
        String jobId = UUID.randomUUID().toString();
        response.setJobId(jobId);

        if (request.isAsync()) {
            // Async scraping
            ScrapingJob job = new ScrapingJob();
            job.setJobId(jobId);
            job.setUrl(request.getUrl());
            job.setStatus("pending");
            job.setStartTime(LocalDateTime.now());
            jobs.put(jobId, job);

            CompletableFuture.runAsync(() -> {
                job.setStatus("running");
                try {
                    WebScraperService.ScrapedData data = scraperService.scrapeWebsite(
                        request.getUrl(), 
                        request.getOptions()
                    );
                    job.setResult(data);
                    job.setStatus("completed");
                } catch (Exception e) {
                    log.severe("Scraping failed for " + request.getUrl() + ": " + e.getMessage());
                    job.setError(e.getMessage());
                    job.setStatus("failed");
                } finally {
                    job.setEndTime(LocalDateTime.now());
                }
            });

            response.setStatus("pending");
            response.setMessage("Scraping job started. Check status at /api/v1/scrapers/real/job/" + jobId);
        } else {
            // Sync scraping
            try {
                WebScraperService.ScrapedData data = scraperService.scrapeWebsite(
                    request.getUrl(), 
                    request.getOptions()
                );
                response.setData(data);
                response.setStatus("completed");
            } catch (Exception e) {
                response.setStatus("failed");
                response.setMessage(e.getMessage());
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Batch scrape multiple URLs
     */
    @PostMapping("/scrape-batch")
    public ResponseEntity<List<ScrapeResponse>> scrapeBatch(@RequestBody List<ScrapeRequest> requests) {
        List<ScrapeResponse> responses = new ArrayList<>();
        
        for (ScrapeRequest request : requests) {
            try {
                ScrapeResponse response = scrapeUrl(request).getBody();
                responses.add(response);
            } catch (Exception e) {
                ScrapeResponse errorResponse = new ScrapeResponse();
                errorResponse.setStatus("failed");
                errorResponse.setMessage(e.getMessage());
                responses.add(errorResponse);
            }
        }
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get job status
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ScrapingJob> getJobStatus(@PathVariable String jobId) {
        ScrapingJob job = jobs.get(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }

    /**
     * Get all jobs
     */
    @GetMapping("/jobs")
    public ResponseEntity<Collection<ScrapingJob>> getAllJobs() {
        return ResponseEntity.ok(jobs.values());
    }

    /**
     * Test scraping with predefined auction sites
     */
    @GetMapping("/test-sites")
    public ResponseEntity<Map<String, Object>> testAuctionSites() {
        Map<String, Object> results = new HashMap<>();
        
        // Example auction sites for testing (using generic patterns)
        Map<String, Map<String, String>> testSites = new HashMap<>();
        
        // Example configuration for a government auction site
        Map<String, String> govOptions = new HashMap<>();
        govOptions.put("requiresJS", "false");
        govOptions.put("contentSelector", ".auction-listing");
        govOptions.put("titleSelector", "h2.auction-title");
        govOptions.put("priceSelector", ".starting-price");
        govOptions.put("locationSelector", ".property-location");
        govOptions.put("dateSelector", ".auction-date");
        testSites.put("https://example-gov-auction.com", govOptions);

        // Example configuration for a bank auction site
        Map<String, String> bankOptions = new HashMap<>();
        bankOptions.put("requiresJS", "true");
        bankOptions.put("waitSelector", ".property-grid");
        bankOptions.put("contentSelector", ".property-card");
        testSites.put("https://example-bank-auction.com", bankOptions);

        results.put("message", "Test configurations ready. Replace URLs with actual auction sites.");
        results.put("configurations", testSites);
        results.put("note", "These are example configurations. Actual scraping requires real URLs and may need site-specific adjustments.");
        
        return ResponseEntity.ok(results);
    }

    /**
     * Scrape and import auction data directly
     */
    @PostMapping("/scrape-and-import")
    public ResponseEntity<Map<String, Object>> scrapeAndImport(@RequestBody ScrapeRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // First scrape the URL
            WebScraperService.ScrapedData scrapedData = scraperService.scrapeWebsite(
                request.getUrl(), 
                request.getOptions()
            );
            
            // Create auction from scraped data
            com.auctionaggregator.model.Auction auction = new com.auctionaggregator.model.Auction();
            auction.setId("AUC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            auction.setTitle(scrapedData.extractedData.getOrDefault("title", "Unknown Auction"));
            auction.setDescription(scrapedData.extractedData.getOrDefault("description", scrapedData.content));
            auction.setCategoryId(detectCategory(auction.getTitle() + " " + auction.getDescription()));
            
            // Parse price
            String priceStr = scrapedData.extractedData.get("price");
            java.math.BigDecimal price = java.math.BigDecimal.ZERO;
            if (priceStr != null) {
                try {
                    price = new java.math.BigDecimal(priceStr.replaceAll("[^0-9.]", ""));
                } catch (Exception e) {
                    price = new java.math.BigDecimal("1000000"); // Default price
                }
            }
            auction.setStartingPrice(price);
            auction.setCurrentPrice(price);
            auction.setBidIncrement(new java.math.BigDecimal("10000"));
            
            // Set times
            auction.setStartTime(LocalDateTime.now());
            auction.setEndTime(LocalDateTime.now().plusDays(7));
            auction.setStatus("ACTIVE");
            auction.setSellerId("SCRAPED-" + new URL(request.getUrl()).getHost());
            auction.setSellerName("Web Scraped Auction");
            auction.setViewCount(0);
            
            // Process images
            List<com.auctionaggregator.model.AuctionImage> images = new ArrayList<>();
            if (scrapedData.images != null && !scrapedData.images.isEmpty()) {
                for (int i = 0; i < Math.min(5, scrapedData.images.size()); i++) {
                    com.auctionaggregator.model.AuctionImage img = new com.auctionaggregator.model.AuctionImage();
                    img.setUrl(scrapedData.images.get(i));
                    img.setPrimary(i == 0);
                    images.add(img);
                }
            }
            auction.setImages(images);
            auction.setTags(extractTags(auction.getTitle(), auction.getDescription()));
            auction.setLocation(extractLocation(auction.getTitle(), auction.getDescription()));
            auction.setSourceUrl(request.getUrl()); // Set the source URL
            auction.setAuctionType("STANDARD");
            auction.setWatcherCount(0);
            auction.setCreatedAt(LocalDateTime.now());
            auction.setUpdatedAt(LocalDateTime.now());
            
            // Store the auction (in real app, this would save to database)
            response.put("auction", auction);
            response.put("status", "success");
            response.put("message", "Auction imported successfully");
            
        } catch (Exception e) {
            log.severe("Error in scrape and import: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private List<String> extractTags(String title, String description) {
        List<String> tags = new ArrayList<>();
        String combined = (title + " " + description).toLowerCase();
        
        // Location tags
        if (combined.contains("mumbai")) tags.add("Mumbai");
        if (combined.contains("delhi")) tags.add("Delhi");
        if (combined.contains("bangalore") || combined.contains("bengaluru")) tags.add("Bangalore");
        if (combined.contains("chennai")) tags.add("Chennai");
        if (combined.contains("kolkata")) tags.add("Kolkata");
        
        // Property tags
        if (combined.contains("commercial")) tags.add("Commercial");
        if (combined.contains("residential")) tags.add("Residential");
        if (combined.contains("flat") || combined.contains("apartment")) tags.add("Flat");
        if (combined.contains("plot") || combined.contains("land")) tags.add("Land");
        
        return tags;
    }
    
    private String extractLocation(String title, String description) {
        String combined = (title + " " + description).toLowerCase();
        
        if (combined.contains("mumbai")) return "Mumbai, Maharashtra";
        if (combined.contains("delhi")) return "Delhi";
        if (combined.contains("bangalore") || combined.contains("bengaluru")) return "Bangalore, Karnataka";
        if (combined.contains("chennai")) return "Chennai, Tamil Nadu";
        if (combined.contains("kolkata")) return "Kolkata, West Bengal";
        if (combined.contains("pune")) return "Pune, Maharashtra";
        if (combined.contains("hyderabad")) return "Hyderabad, Telangana";
        
        return "India";
    }

    /**
     * Extract auction data from scraped content
     */
    @PostMapping("/extract-auction")
    public ResponseEntity<Map<String, Object>> extractAuctionData(@RequestBody Map<String, Object> scrapedData) {
        Map<String, Object> auctionData = new HashMap<>();
        
        try {
            // Extract structured auction data from the scraped content
            if (scrapedData.containsKey("extractedData")) {
                Map<String, String> extracted = (Map<String, String>) scrapedData.get("extractedData");
                
                auctionData.put("title", extracted.getOrDefault("title", "Unknown Auction"));
                auctionData.put("price", parsePrice(extracted.get("price")));
                auctionData.put("location", extracted.getOrDefault("location", ""));
                auctionData.put("endDate", extracted.getOrDefault("endDate", ""));
                auctionData.put("description", extracted.getOrDefault("description", ""));
                
                // Category detection based on title/description
                String category = detectCategory(
                    extracted.getOrDefault("title", "") + " " + 
                    extracted.getOrDefault("description", "")
                );
                auctionData.put("category", category);
                
                // Image processing
                if (scrapedData.containsKey("images")) {
                    List<String> images = (List<String>) scrapedData.get("images");
                    auctionData.put("images", images.size() > 5 ? images.subList(0, 5) : images);
                }
                
                auctionData.put("sourceUrl", scrapedData.get("url"));
                auctionData.put("scrapedAt", scrapedData.get("scrapedAt"));
                auctionData.put("status", "pending_review");
            }
        } catch (Exception e) {
            log.severe("Error extracting auction data: " + e.getMessage());
            auctionData.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(auctionData);
    }

    private String parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return "0";
        }
        // Remove non-numeric characters except decimal point
        return priceStr.replaceAll("[^0-9.]", "");
    }

    private String detectCategory(String text) {
        text = text.toLowerCase();
        
        if (text.contains("property") || text.contains("flat") || text.contains("apartment") || 
            text.contains("house") || text.contains("plot") || text.contains("land")) {
            return "real-estate";
        } else if (text.contains("car") || text.contains("vehicle") || text.contains("bike") || 
                   text.contains("motorcycle") || text.contains("truck")) {
            return "vehicles";
        } else if (text.contains("gold") || text.contains("jewelry") || text.contains("jewellery")) {
            return "jewelry";
        } else if (text.contains("electronic") || text.contains("laptop") || text.contains("mobile") || 
                   text.contains("computer")) {
            return "electronics";
        } else if (text.contains("machinery") || text.contains("equipment") || text.contains("industrial")) {
            return "machinery";
        }
        
        return "other";
    }
}
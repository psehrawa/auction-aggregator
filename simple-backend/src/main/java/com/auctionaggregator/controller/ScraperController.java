package com.auctionaggregator.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/scrapers")
@CrossOrigin(origins = "*")
public class ScraperController {

    private static class ScraperConfig {
        public String id;
        public String name;
        public String type;
        public String url;
        public String status;
        public String lastRun;
        public int itemsFound;
        public boolean enabled;
        public Map<String, Object> config;
    }

    private static class ScrapedAuction {
        public String id;
        public String source;
        public String title;
        public String location;
        public double startingPrice;
        public String endTime;
        public String category;
        public String status;
        public String scrapedAt;
        public String imageUrl;
    }

    private static class ScraperStats {
        public int totalScrapers = 12;
        public int activeScrapers = 8;
        public String lastRun = "2 hours ago";
        public int auctionsFound = 156;
        public int pendingReview = 23;
        public int imported = 133;
    }

    @GetMapping("/stats")
    public ResponseEntity<ScraperStats> getStats() {
        return ResponseEntity.ok(new ScraperStats());
    }

    @GetMapping
    public ResponseEntity<List<ScraperConfig>> getScrapers() {
        List<ScraperConfig> scrapers = new ArrayList<>();
        
        // Mock scraper configurations
        scrapers.add(createScraper("1", "SBI Auctions", "bank", 
            "https://sbi.co.in/auctions", "active", "2024-01-10 14:30", 45, true));
        scrapers.add(createScraper("2", "HDFC Bank E-Auctions", "bank", 
            "https://hdfcbank.com/e-auctions", "running", "2024-01-10 15:00", 32, true));
        scrapers.add(createScraper("3", "ICICI Bank Properties", "bank", 
            "https://icicibank.com/auction-properties", "active", "2024-01-10 13:15", 28, true));
        scrapers.add(createScraper("4", "Bajaj Finance Auctions", "nbfc", 
            "https://bajajfinance.in/auctions", "error", "2024-01-10 12:00", 0, true));
        scrapers.add(createScraper("5", "Muthoot Finance", "nbfc", 
            "https://muthoot.com/gold-auctions", "inactive", "2024-01-09 18:30", 15, false));
        
        return ResponseEntity.ok(scrapers);
    }

    @GetMapping("/scraped-auctions")
    public ResponseEntity<List<ScrapedAuction>> getScrapedAuctions() {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        auctions.add(createScrapedAuction("1", "SBI Auctions", 
            "3 BHK Flat in Mumbai, Andheri West", "Mumbai, Maharashtra", 
            8500000, "2024-01-20T15:00:00", "real-estate", "pending"));
        auctions.add(createScrapedAuction("2", "HDFC Bank E-Auctions", 
            "Commercial Property in Bangalore", "Bangalore, Karnataka", 
            15000000, "2024-01-25T12:00:00", "real-estate", "pending"));
        auctions.add(createScrapedAuction("3", "ICICI Bank Properties", 
            "Agricultural Land 5 Acres", "Pune, Maharashtra", 
            4500000, "2024-01-18T10:00:00", "real-estate", "approved"));
        auctions.add(createScrapedAuction("4", "Bajaj Finance Auctions", 
            "Honda City 2019 Model", "Delhi", 
            450000, "2024-01-15T16:00:00", "vehicles", "pending"));
        auctions.add(createScrapedAuction("5", "SBI Auctions", 
            "Gold Jewelry Set - 50 grams", "Chennai, Tamil Nadu", 
            250000, "2024-01-14T11:00:00", "jewelry", "rejected"));
        
        return ResponseEntity.ok(auctions);
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<Map<String, String>> runScraper(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "started");
        response.put("message", "Scraper started successfully");
        response.put("jobId", UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scraped-auctions/{id}/approve")
    public ResponseEntity<Map<String, String>> approveAuction(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "approved");
        response.put("message", "Auction approved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scraped-auctions/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectAuction(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "rejected");
        response.put("message", "Auction rejected");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scraped-auctions/import")
    public ResponseEntity<Map<String, Object>> importAuctions(@RequestBody List<String> auctionIds) {
        Map<String, Object> response = new HashMap<>();
        response.put("imported", auctionIds.size());
        response.put("status", "success");
        response.put("message", "Auctions imported successfully");
        return ResponseEntity.ok(response);
    }

    private ScraperConfig createScraper(String id, String name, String type, 
            String url, String status, String lastRun, int itemsFound, boolean enabled) {
        ScraperConfig scraper = new ScraperConfig();
        scraper.id = id;
        scraper.name = name;
        scraper.type = type;
        scraper.url = url;
        scraper.status = status;
        scraper.lastRun = lastRun;
        scraper.itemsFound = itemsFound;
        scraper.enabled = enabled;
        scraper.config = new HashMap<>();
        scraper.config.put("selector", ".auction-item");
        scraper.config.put("pagination", true);
        scraper.config.put("maxPages", 5);
        return scraper;
    }

    private ScrapedAuction createScrapedAuction(String id, String source, String title, 
            String location, double startingPrice, String endTime, String category, String status) {
        ScrapedAuction auction = new ScrapedAuction();
        auction.id = id;
        auction.source = source;
        auction.title = title;
        auction.location = location;
        auction.startingPrice = startingPrice;
        auction.endTime = endTime;
        auction.category = category;
        auction.status = status;
        auction.scrapedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        auction.imageUrl = "https://picsum.photos/200/150?random=" + id;
        return auction;
    }
}
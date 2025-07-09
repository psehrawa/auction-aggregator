package com.auctionaggregator.auction.scraper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "scraper")
@Data
public class ScraperConfiguration {
    
    private boolean enabled = true;
    private int threadPoolSize = 5;
    private int connectionTimeout = 30000; // 30 seconds
    private int readTimeout = 60000; // 60 seconds
    private int maxRetries = 3;
    private long retryDelay = 5000; // 5 seconds
    
    private Map<String, ScraperConfig> scrapers = new HashMap<>();
    
    @Data
    public static class ScraperConfig {
        private boolean enabled = true;
        private String url;
        private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
        private int pageLoadTimeout = 30000;
        private Map<String, String> headers = new HashMap<>();
        private RateLimitConfig rateLimit = new RateLimitConfig();
    }
    
    @Data
    public static class RateLimitConfig {
        private int requestsPerMinute = 60;
        private int burstSize = 10;
        private long cooldownPeriod = 60000; // 1 minute
    }
    
    public ScraperConfig getScraperConfig(String scraperName) {
        return scrapers.getOrDefault(scraperName, new ScraperConfig());
    }
}
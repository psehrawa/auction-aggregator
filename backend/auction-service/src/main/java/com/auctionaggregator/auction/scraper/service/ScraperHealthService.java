package com.auctionaggregator.auction.scraper.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperHealthService {
    
    private final Map<String, ScraperHealth> scraperHealthMap = new ConcurrentHashMap<>();
    
    public void recordScrapingResult(String scraperName, boolean success, int itemsScraped, 
                                   long duration, String errorMessage) {
        ScraperHealth health = scraperHealthMap.computeIfAbsent(scraperName, 
            k -> ScraperHealth.builder()
                .scraperName(k)
                .lastRunTime(LocalDateTime.now())
                .build()
        );
        
        health.setLastRunTime(LocalDateTime.now());
        health.setLastRunDuration(duration);
        health.setLastRunSuccess(success);
        health.setLastItemsScraped(itemsScraped);
        health.setTotalRuns(health.getTotalRuns() + 1);
        
        if (success) {
            health.setSuccessfulRuns(health.getSuccessfulRuns() + 1);
            health.setTotalItemsScraped(health.getTotalItemsScraped() + itemsScraped);
            health.setConsecutiveFailures(0);
            health.setLastError(null);
        } else {
            health.setFailedRuns(health.getFailedRuns() + 1);
            health.setConsecutiveFailures(health.getConsecutiveFailures() + 1);
            health.setLastError(errorMessage);
        }
        
        // Calculate success rate
        double successRate = (double) health.getSuccessfulRuns() / health.getTotalRuns() * 100;
        health.setSuccessRate(successRate);
        
        // Log warning if consecutive failures exceed threshold
        if (health.getConsecutiveFailures() >= 3) {
            log.warn("Scraper {} has failed {} consecutive times. Last error: {}", 
                scraperName, health.getConsecutiveFailures(), errorMessage);
        }
    }
    
    public ScraperHealth getScraperHealth(String scraperName) {
        return scraperHealthMap.get(scraperName);
    }
    
    public Map<String, ScraperHealth> getAllScraperHealth() {
        return new ConcurrentHashMap<>(scraperHealthMap);
    }
    
    public boolean isScraperHealthy(String scraperName) {
        ScraperHealth health = scraperHealthMap.get(scraperName);
        if (health == null) {
            return true; // Assume healthy if no data
        }
        
        // Consider unhealthy if:
        // - Success rate below 70%
        // - More than 5 consecutive failures
        // - No successful run in the last hour
        return health.getSuccessRate() >= 70 
            && health.getConsecutiveFailures() < 5
            && (health.getLastRunSuccess() || 
                health.getLastRunTime().isAfter(LocalDateTime.now().minusHours(1)));
    }
    
    @Data
    @Builder
    public static class ScraperHealth {
        private String scraperName;
        private LocalDateTime lastRunTime;
        private long lastRunDuration;
        private boolean lastRunSuccess;
        private int lastItemsScraped;
        private String lastError;
        
        @Builder.Default
        private int totalRuns = 0;
        @Builder.Default
        private int successfulRuns = 0;
        @Builder.Default
        private int failedRuns = 0;
        @Builder.Default
        private int totalItemsScraped = 0;
        @Builder.Default
        private double successRate = 100.0;
        @Builder.Default
        private int consecutiveFailures = 0;
    }
}
package com.auctionaggregator.auction.controller;

import com.auctionaggregator.auction.scraper.service.AuctionAggregatorService;
import com.auctionaggregator.auction.scraper.service.ScraperHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/scraper")
@RequiredArgsConstructor
@Tag(name = "Scraper Management", description = "Endpoints for managing and monitoring scrapers")
public class ScraperHealthController {
    
    private final ScraperHealthService scraperHealthService;
    private final AuctionAggregatorService auctionAggregatorService;
    
    @GetMapping("/health")
    @Operation(summary = "Get health status of all scrapers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, ScraperHealthService.ScraperHealth>> getAllScraperHealth() {
        return ResponseEntity.ok(scraperHealthService.getAllScraperHealth());
    }
    
    @GetMapping("/health/{scraperName}")
    @Operation(summary = "Get health status of a specific scraper")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperHealthService.ScraperHealth> getScraperHealth(
            @PathVariable String scraperName) {
        ScraperHealthService.ScraperHealth health = scraperHealthService.getScraperHealth(scraperName);
        if (health == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(health);
    }
    
    @PostMapping("/sync")
    @Operation(summary = "Trigger manual synchronization of all scrapers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> triggerManualSync() {
        auctionAggregatorService.runManualSync();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Manual synchronization triggered"
        ));
    }
    
    @GetMapping("/health/{scraperName}/check")
    @Operation(summary = "Check if a scraper is healthy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkScraperHealth(
            @PathVariable String scraperName) {
        boolean isHealthy = scraperHealthService.isScraperHealthy(scraperName);
        ScraperHealthService.ScraperHealth health = scraperHealthService.getScraperHealth(scraperName);
        
        return ResponseEntity.ok(Map.of(
            "scraperName", scraperName,
            "healthy", isHealthy,
            "details", health != null ? health : "No health data available"
        ));
    }
}
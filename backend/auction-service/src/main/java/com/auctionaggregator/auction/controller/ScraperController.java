package com.auctionaggregator.auction.controller;

import com.auctionaggregator.auction.scraper.service.AuctionAggregatorService;
import com.auctionaggregator.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scraper")
@RequiredArgsConstructor
@Tag(name = "Scraper Management", description = "APIs for managing auction scrapers")
public class ScraperController {
    
    private final AuctionAggregatorService aggregatorService;
    
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually trigger auction synchronization")
    public ResponseEntity<ApiResponse<String>> triggerSync() {
        aggregatorService.runManualSync();
        return ResponseEntity.ok(ApiResponse.success("Synchronization started", "Auction sync initiated successfully"));
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get scraper status")
    public ResponseEntity<ApiResponse<String>> getStatus() {
        // In a real implementation, this would return detailed status
        return ResponseEntity.ok(ApiResponse.success("Active", "Scraper service is running"));
    }
}
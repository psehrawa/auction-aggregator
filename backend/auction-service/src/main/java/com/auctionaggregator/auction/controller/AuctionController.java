package com.auctionaggregator.auction.controller;

import com.auctionaggregator.auction.dto.*;
import com.auctionaggregator.auction.service.AuctionService;
import com.auctionaggregator.auction.service.BiddingService;
import com.auctionaggregator.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
@Tag(name = "Auction Management", description = "APIs for managing auctions")
public class AuctionController {
    
    private final AuctionService auctionService;
    private final BiddingService biddingService;
    
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a new auction")
    public ResponseEntity<ApiResponse<AuctionDTO>> createAuction(
            @Valid @RequestBody AuctionCreateDTO createDTO,
            @AuthenticationPrincipal String userId) {
        AuctionDTO auction = auctionService.createAuction(createDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(auction, "Auction created successfully"));
    }
    
    @PutMapping("/{auctionId}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update an auction")
    public ResponseEntity<ApiResponse<AuctionDTO>> updateAuction(
            @PathVariable String auctionId,
            @Valid @RequestBody AuctionUpdateDTO updateDTO,
            @AuthenticationPrincipal String userId) {
        AuctionDTO auction = auctionService.updateAuction(auctionId, updateDTO, userId);
        return ResponseEntity.ok(ApiResponse.success(auction, "Auction updated successfully"));
    }
    
    @GetMapping("/{auctionId}")
    @Operation(summary = "Get auction details")
    public ResponseEntity<ApiResponse<AuctionDTO>> getAuction(@PathVariable String auctionId) {
        AuctionDTO auction = auctionService.getAuction(auctionId);
        return ResponseEntity.ok(ApiResponse.success(auction));
    }
    
    @GetMapping
    @Operation(summary = "Search auctions")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> searchAuctions(
            @Parameter(description = "Search criteria")
            AuctionSearchDTO searchDTO,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AuctionDTO> auctions = auctionService.searchAuctions(searchDTO, pageable);
        return ResponseEntity.ok(ApiResponse.success(auctions));
    }
    
    @PostMapping("/{auctionId}/activate")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Activate an auction")
    public ResponseEntity<ApiResponse<Void>> activateAuction(
            @PathVariable String auctionId,
            @AuthenticationPrincipal String userId) {
        auctionService.activateAuction(auctionId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Auction activated successfully"));
    }
    
    @PostMapping("/{auctionId}/cancel")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Cancel an auction")
    public ResponseEntity<ApiResponse<Void>> cancelAuction(
            @PathVariable String auctionId,
            @RequestParam String reason,
            @AuthenticationPrincipal String userId) {
        auctionService.cancelAuction(auctionId, userId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Auction cancelled successfully"));
    }
    
    @PostMapping("/{auctionId}/bids")
    @PreAuthorize("hasRole('BIDDER')")
    @Operation(summary = "Place a bid on an auction")
    public ResponseEntity<ApiResponse<BidDTO>> placeBid(
            @PathVariable String auctionId,
            @Valid @RequestBody PlaceBidDTO placeBidDTO,
            @AuthenticationPrincipal String userId,
            HttpServletRequest request) {
        placeBidDTO.setAuctionId(auctionId);
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        BidDTO bid = biddingService.placeBid(placeBidDTO, userId, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(bid, "Bid placed successfully"));
    }
    
    @GetMapping("/{auctionId}/bids")
    @Operation(summary = "Get bids for an auction")
    public ResponseEntity<ApiResponse<List<BidDTO>>> getAuctionBids(
            @PathVariable String auctionId,
            @RequestParam(defaultValue = "50") int limit) {
        List<BidDTO> bids = biddingService.getAuctionBids(auctionId, limit);
        return ResponseEntity.ok(ApiResponse.success(bids));
    }
    
    @GetMapping("/{auctionId}/next-bid")
    @Operation(summary = "Get the minimum next bid amount")
    public ResponseEntity<ApiResponse<NextBidDTO>> getNextBidAmount(@PathVariable String auctionId) {
        BigDecimal nextBid = biddingService.getNextMinimumBid(auctionId);
        NextBidDTO response = NextBidDTO.builder()
            .auctionId(auctionId)
            .minimumBidAmount(nextBid)
            .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/bids/{bidId}/cancel")
    @PreAuthorize("hasRole('BIDDER')")
    @Operation(summary = "Cancel a bid")
    public ResponseEntity<ApiResponse<Void>> cancelBid(
            @PathVariable String bidId,
            @RequestParam String reason,
            @AuthenticationPrincipal String userId) {
        biddingService.cancelBid(bidId, userId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Bid cancelled successfully"));
    }
    
    @GetMapping("/my-bids")
    @PreAuthorize("hasRole('BIDDER')")
    @Operation(summary = "Get current user's bids")
    public ResponseEntity<ApiResponse<List<BidDTO>>> getMyBids(
            @RequestParam(required = false) Bid.BidStatus status,
            @AuthenticationPrincipal String userId) {
        List<BidDTO> bids = biddingService.getUserBids(userId, status);
        return ResponseEntity.ok(ApiResponse.success(bids));
    }
    
    @GetMapping("/trending")
    @Operation(summary = "Get trending auctions")
    public ResponseEntity<ApiResponse<List<AuctionDTO>>> getTrendingAuctions(
            @RequestParam(defaultValue = "10") int limit) {
        List<AuctionDTO> auctions = auctionService.getTrendingAuctions(limit);
        return ResponseEntity.ok(ApiResponse.success(auctions));
    }
    
    @GetMapping("/ending-soon")
    @Operation(summary = "Get auctions ending soon")
    public ResponseEntity<ApiResponse<List<AuctionDTO>>> getEndingSoonAuctions(
            @RequestParam(defaultValue = "10") int limit) {
        List<AuctionDTO> auctions = auctionService.getEndingSoonAuctions(limit);
        return ResponseEntity.ok(ApiResponse.success(auctions));
    }
}
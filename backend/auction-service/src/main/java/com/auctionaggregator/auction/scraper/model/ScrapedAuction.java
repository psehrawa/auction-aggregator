package com.auctionaggregator.auction.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedAuction {
    private String externalId;
    private String title;
    private String description;
    private String category;
    private BigDecimal currentPrice;
    private BigDecimal startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private List<String> imageUrls;
    private String sourceUrl;
    private String source;
    private Integer bidCount;
    private String sellerName;
    private AuctionStatus status;
    
    public enum AuctionStatus {
        UPCOMING,
        ACTIVE,
        ENDING_SOON,
        ENDED
    }
}
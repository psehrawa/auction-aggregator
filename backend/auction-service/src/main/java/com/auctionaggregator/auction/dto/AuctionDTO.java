package com.auctionaggregator.auction.dto;

import com.auctionaggregator.auction.entity.Auction;
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
public class AuctionDTO {
    private String id;
    private String title;
    private String description;
    private String sellerId;
    private String categoryId;
    private String categoryName;
    private Auction.AuctionStatus status;
    private Auction.AuctionType auctionType;
    private BigDecimal startingPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private BigDecimal buyNowPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime actualEndTime;
    private Boolean autoExtend;
    private Integer autoExtendMinutes;
    private Integer viewCount;
    private Integer watcherCount;
    private Integer bidCount;
    private String winnerId;
    private BigDecimal winningBid;
    private String source;
    private List<String> imageUrls;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
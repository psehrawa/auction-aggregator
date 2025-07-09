package com.auctionaggregator.auction.entity;

import com.auctionaggregator.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids", indexes = {
    @Index(name = "idx_auction_id", columnList = "auction_id"),
    @Index(name = "idx_bidder_id", columnList = "bidder_id"),
    @Index(name = "idx_bid_time", columnList = "bid_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Bid extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
    
    @Column(nullable = false)
    private String bidderId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal maxAmount; // For proxy bidding
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BidType bidType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BidStatus status;
    
    @Column(nullable = false)
    private LocalDateTime bidTime;
    
    private String ipAddress;
    
    private String userAgent;
    
    private String deviceId;
    
    @Column(nullable = false)
    private Boolean isProxyBid = false;
    
    private String parentBidId; // For proxy bid chains
    
    private String cancellationReason;
    
    private LocalDateTime cancellationTime;
    
    @Column(nullable = false)
    private Boolean isWinningBid = false;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BidSource source;
    
    public enum BidType {
        MANUAL,
        PROXY,
        SNIPE,
        AUTOBID,
        BUY_NOW
    }
    
    public enum BidStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        OUTBID,
        WINNING,
        CANCELLED,
        EXPIRED
    }
    
    public enum BidSource {
        WEB,
        MOBILE_APP,
        API,
        PHONE,
        IN_PERSON
    }
}
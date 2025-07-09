package com.auctionaggregator.auction.entity;

import com.auctionaggregator.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auctions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Auction extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String sellerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal startingPrice;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal reservePrice;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal buyNowPrice;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal bidIncrement;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    private LocalDateTime actualEndTime;
    
    @Column(nullable = false)
    private Boolean autoExtend = false;
    
    private Integer autoExtendMinutes = 5;
    
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Bid> bids = new ArrayList<>();
    
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImage> images = new ArrayList<>();
    
    @OneToOne(mappedBy = "auction", cascade = CascadeType.ALL)
    private AuctionDetails details;
    
    @Column(nullable = false)
    private Integer viewCount = 0;
    
    @Column(nullable = false)
    private Integer watcherCount = 0;
    
    private String winnerId;
    
    private BigDecimal winningBid;
    
    @Column(nullable = false)
    private String source; // GeM, Copart, Internal, etc.
    
    private String externalId; // ID from external source
    
    private String externalUrl;
    
    @ElementCollection
    @CollectionTable(name = "auction_tags", joinColumns = @JoinColumn(name = "auction_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<AuctionHistory> history = new ArrayList<>();
    
    public enum AuctionStatus {
        DRAFT,
        SCHEDULED,
        ACTIVE,
        ENDING_SOON,
        ENDED,
        SOLD,
        CANCELLED,
        SUSPENDED
    }
    
    public enum AuctionType {
        STANDARD,
        RESERVE,
        ABSOLUTE,
        SEALED_BID,
        DUTCH,
        PENNY
    }
}
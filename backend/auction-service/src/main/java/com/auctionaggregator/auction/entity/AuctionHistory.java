package com.auctionaggregator.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    
    @Column(nullable = false)
    private String actionBy;
    
    @Column(columnDefinition = "TEXT")
    private String actionDetails;
    
    @Column(columnDefinition = "jsonb")
    private String oldValue;
    
    @Column(columnDefinition = "jsonb")
    private String newValue;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum ActionType {
        CREATED,
        UPDATED,
        ACTIVATED,
        SUSPENDED,
        CANCELLED,
        ENDED,
        EXTENDED,
        BID_PLACED,
        BID_CANCELLED,
        WINNER_SELECTED
    }
}
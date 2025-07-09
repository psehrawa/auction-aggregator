package com.auctionaggregator.auction.entity;

import com.auctionaggregator.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "auction_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuctionDetails extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false, unique = true)
    private Auction auction;
    
    @Column(length = 50)
    private String condition;
    
    @Column(length = 255)
    private String locationCity;
    
    @Column(length = 255)
    private String locationState;
    
    @Column(length = 100)
    private String locationCountry;
    
    @Column(length = 20)
    private String locationZip;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(nullable = false)
    private Boolean shippingAvailable = true;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal shippingCost;
    
    @Column(columnDefinition = "TEXT")
    private String shippingInfo;
    
    @Column(nullable = false)
    private Boolean inspectionAvailable = false;
    
    @Column(columnDefinition = "TEXT")
    private String inspectionDates;
    
    @Column(columnDefinition = "TEXT")
    private String warrantyInfo;
    
    @Column(columnDefinition = "TEXT")
    private String returnPolicy;
    
    @Column(columnDefinition = "jsonb")
    private String additionalInfo;
}
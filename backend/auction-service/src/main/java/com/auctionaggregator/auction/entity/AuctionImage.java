package com.auctionaggregator.auction.entity;

import com.auctionaggregator.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuctionImage extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
    
    @Column(nullable = false, length = 500)
    private String url;
    
    @Column(length = 500)
    private String thumbnailUrl;
    
    @Column(length = 255)
    private String title;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @Column(nullable = false)
    private Boolean isPrimary = false;
    
    private Integer width;
    
    private Integer height;
    
    private Long sizeBytes;
}
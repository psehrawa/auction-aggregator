package com.auctionaggregator.auction.dto;

import com.auctionaggregator.auction.entity.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionSearchDTO {
    private String query;
    private String categoryId;
    private Auction.AuctionStatus status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String location;
    private String source;
    private String[] tags;
}
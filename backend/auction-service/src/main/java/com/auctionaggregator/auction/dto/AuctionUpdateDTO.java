package com.auctionaggregator.auction.dto;

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
public class AuctionUpdateDTO {
    private String title;
    private String description;
    private String categoryId;
    private BigDecimal reservePrice;
    private BigDecimal buyNowPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean autoExtend;
    private Integer autoExtendMinutes;
    private List<String> tags;
}
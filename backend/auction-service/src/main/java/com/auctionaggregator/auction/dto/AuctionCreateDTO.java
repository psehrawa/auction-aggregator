package com.auctionaggregator.auction.dto;

import com.auctionaggregator.auction.entity.Auction;
import jakarta.validation.constraints.*;
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
public class AuctionCreateDTO {
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Auction type is required")
    private Auction.AuctionType auctionType;
    
    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be at least 0.01")
    private BigDecimal startingPrice;
    
    private BigDecimal reservePrice;
    
    private BigDecimal buyNowPrice;
    
    @NotNull(message = "Bid increment is required")
    @DecimalMin(value = "0.01", message = "Bid increment must be at least 0.01")
    private BigDecimal bidIncrement;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private Boolean autoExtend = false;
    
    @Min(value = 1, message = "Auto extend minutes must be at least 1")
    @Max(value = 60, message = "Auto extend minutes must not exceed 60")
    private Integer autoExtendMinutes = 5;
    
    private List<ImageDTO> images;
    
    private List<String> tags;
    
    private AuctionDetailsDTO details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDTO {
        @NotBlank(message = "Image URL is required")
        private String url;
        private String thumbnailUrl;
        private String title;
        private Boolean isPrimary = false;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuctionDetailsDTO {
        private String condition;
        private String locationCity;
        private String locationState;
        private String locationCountry;
        private String locationZip;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Boolean shippingAvailable;
        private BigDecimal shippingCost;
        private String shippingInfo;
        private Boolean inspectionAvailable;
        private String inspectionDates;
        private String warrantyInfo;
        private String returnPolicy;
    }
}
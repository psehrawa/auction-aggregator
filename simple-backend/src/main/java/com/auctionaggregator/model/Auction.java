package com.auctionaggregator.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Auction {
    private String id;
    private String title;
    private String description;
    private String categoryId;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String sellerId;
    private String sellerName;
    private int viewCount;
    private List<AuctionImage> images;
    private List<String> tags;
    private String location;
    private String sourceUrl;
    private String sourcePdfUrl;
    private String auctionType;
    private int watcherCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Auction() {}

    public Auction(String id, String title, String description, String categoryId,
                   BigDecimal startingPrice, BigDecimal currentPrice, BigDecimal bidIncrement,
                   LocalDateTime startTime, LocalDateTime endTime, String status,
                   String sellerId, String sellerName, int viewCount,
                   List<AuctionImage> images, List<String> tags, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.bidIncrement = bidIncrement;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.viewCount = viewCount;
        this.images = images;
        this.tags = tags;
        this.location = location;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getBidIncrement() { return bidIncrement; }
    public void setBidIncrement(BigDecimal bidIncrement) { this.bidIncrement = bidIncrement; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public List<AuctionImage> getImages() { return images; }
    public void setImages(List<AuctionImage> images) { this.images = images; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    
    public String getSourcePdfUrl() { return sourcePdfUrl; }
    public void setSourcePdfUrl(String sourcePdfUrl) { this.sourcePdfUrl = sourcePdfUrl; }
    
    public String getAuctionType() { return auctionType; }
    public void setAuctionType(String auctionType) { this.auctionType = auctionType; }
    
    public int getWatcherCount() { return watcherCount; }
    public void setWatcherCount(int watcherCount) { this.watcherCount = watcherCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
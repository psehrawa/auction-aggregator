package com.auctionaggregator.model;

public class AuctionImage {
    private String url;
    private boolean isPrimary;

    public AuctionImage() {}

    public AuctionImage(String url, boolean isPrimary) {
        this.url = url;
        this.isPrimary = isPrimary;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
}
package com.auctionaggregator.auction.scraper.service;

import com.auctionaggregator.auction.scraper.model.ScrapedAuction;
import java.util.List;

public interface AuctionScraperService {
    List<ScrapedAuction> scrapeAuctions();
    String getSourceName();
    boolean isEnabled();
}
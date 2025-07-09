package com.auctionaggregator.auction.service;

import com.auctionaggregator.auction.entity.Auction;
import com.auctionaggregator.auction.entity.AuctionHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionHistoryService {
    
    public void recordHistory(Auction auction, AuctionHistory.ActionType actionType, 
                            String actionBy, String details) {
        log.info("Recording history for auction {} - Action: {} by {}", 
                auction.getId(), actionType, actionBy);
        // TODO: Implement history recording
    }
}
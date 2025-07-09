package com.auctionaggregator.auction.service;

import com.auctionaggregator.auction.dto.BidDTO;
import com.auctionaggregator.auction.dto.PlaceBidDTO;
import com.auctionaggregator.auction.entity.Auction;
import com.auctionaggregator.auction.entity.Bid;
import com.auctionaggregator.auction.exception.BidException;
import com.auctionaggregator.auction.mapper.BidMapper;
import com.auctionaggregator.auction.repository.AuctionRepository;
import com.auctionaggregator.auction.repository.BidRepository;
import com.auctionaggregator.auction.websocket.BidWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BiddingService {
    
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final BidMapper bidMapper;
    private final BidWebSocketHandler webSocketHandler;
    private final AuctionNotificationService notificationService;
    private final BidValidationService validationService;
    private final ProxyBiddingService proxyBiddingService;
    
    private final ConcurrentHashMap<String, Lock> auctionLocks = new ConcurrentHashMap<>();
    
    public BidDTO placeBid(PlaceBidDTO placeBidDTO, String bidderId, String ipAddress, String userAgent) {
        String auctionId = placeBidDTO.getAuctionId();
        Lock lock = auctionLocks.computeIfAbsent(auctionId, k -> new ReentrantLock());
        
        lock.lock();
        try {
            return processBid(placeBidDTO, bidderId, ipAddress, userAgent);
        } finally {
            lock.unlock();
            auctionLocks.remove(auctionId);
        }
    }
    
    private BidDTO processBid(PlaceBidDTO placeBidDTO, String bidderId, String ipAddress, String userAgent) {
        log.info("Processing bid for auction: {} by bidder: {} amount: {}", 
            placeBidDTO.getAuctionId(), bidderId, placeBidDTO.getAmount());
        
        Auction auction = auctionRepository.findById(placeBidDTO.getAuctionId())
            .orElseThrow(() -> new BidException("Auction not found"));
        
        validateBid(auction, placeBidDTO, bidderId);
        
        Bid bid = createBid(auction, placeBidDTO, bidderId, ipAddress, userAgent);
        
        updateAuctionState(auction, bid);
        
        bid = bidRepository.save(bid);
        
        processProxyBids(auction, bid);
        
        notifyBidPlaced(auction, bid);
        
        return bidMapper.toDTO(bid);
    }
    
    private void validateBid(Auction auction, PlaceBidDTO placeBidDTO, String bidderId) {
        if (auction.getStatus() != Auction.AuctionStatus.ACTIVE && 
            auction.getStatus() != Auction.AuctionStatus.ENDING_SOON) {
            throw new BidException("Auction is not active");
        }
        
        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new BidException("Auction has ended");
        }
        
        if (auction.getSellerId().equals(bidderId)) {
            throw new BidException("Sellers cannot bid on their own auctions");
        }
        
        BigDecimal minimumBid = calculateMinimumBid(auction);
        if (placeBidDTO.getAmount().compareTo(minimumBid) < 0) {
            throw new BidException("Bid amount must be at least " + minimumBid);
        }
        
        validationService.validateBidderEligibility(bidderId, auction);
        validationService.validateBidLimits(bidderId, auction, placeBidDTO.getAmount());
    }
    
    private BigDecimal calculateMinimumBid(Auction auction) {
        BigDecimal currentPrice = auction.getCurrentPrice() != null ? 
            auction.getCurrentPrice() : auction.getStartingPrice();
        return currentPrice.add(auction.getBidIncrement());
    }
    
    private Bid createBid(Auction auction, PlaceBidDTO placeBidDTO, String bidderId, 
                         String ipAddress, String userAgent) {
        Bid bid = Bid.builder()
            .auction(auction)
            .bidderId(bidderId)
            .amount(placeBidDTO.getAmount())
            .maxAmount(placeBidDTO.getMaxAmount())
            .bidType(determineBidType(placeBidDTO))
            .status(Bid.BidStatus.ACCEPTED)
            .bidTime(LocalDateTime.now())
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .deviceId(placeBidDTO.getDeviceId())
            .isProxyBid(placeBidDTO.getMaxAmount() != null)
            .source(placeBidDTO.getSource() != null ? placeBidDTO.getSource() : Bid.BidSource.WEB)
            .build();
        
        return bid;
    }
    
    private Bid.BidType determineBidType(PlaceBidDTO placeBidDTO) {
        if (placeBidDTO.getMaxAmount() != null) {
            return Bid.BidType.PROXY;
        }
        
        if (placeBidDTO.getIsSnipeBid() != null && placeBidDTO.getIsSnipeBid()) {
            return Bid.BidType.SNIPE;
        }
        
        return Bid.BidType.MANUAL;
    }
    
    private void updateAuctionState(Auction auction, Bid bid) {
        auction.setCurrentPrice(bid.getAmount());
        
        outbidPreviousBids(auction, bid);
        
        if (bid.getAmount().compareTo(auction.getBuyNowPrice()) >= 0 && 
            auction.getBuyNowPrice() != null) {
            endAuctionWithBuyNow(auction, bid);
        }
    }
    
    private void outbidPreviousBids(Auction auction, Bid newBid) {
        auction.getBids().stream()
            .filter(bid -> bid.getStatus() == Bid.BidStatus.ACCEPTED)
            .filter(bid -> !bid.getId().equals(newBid.getId()))
            .forEach(bid -> {
                bid.setStatus(Bid.BidStatus.OUTBID);
                bid.setIsWinningBid(false);
            });
    }
    
    private void endAuctionWithBuyNow(Auction auction, Bid bid) {
        auction.setStatus(Auction.AuctionStatus.ENDED);
        auction.setActualEndTime(LocalDateTime.now());
        auction.setWinnerId(bid.getBidderId());
        auction.setWinningBid(bid.getAmount());
        
        bid.setStatus(Bid.BidStatus.WINNING);
        bid.setIsWinningBid(true);
        bid.setBidType(Bid.BidType.BUY_NOW);
    }
    
    private void processProxyBids(Auction auction, Bid newBid) {
        if (newBid.getMaxAmount() != null) {
            proxyBiddingService.registerProxyBid(auction, newBid);
        }
        
        proxyBiddingService.processProxyBidsForAuction(auction.getId());
    }
    
    private void notifyBidPlaced(Auction auction, Bid bid) {
        webSocketHandler.broadcastBidUpdate(auction.getId(), bid);
        notificationService.notifyBidPlaced(auction, bid);
        notificationService.notifyOutbidUsers(auction, bid);
    }
    
    public List<BidDTO> getAuctionBids(String auctionId, int limit) {
        List<Bid> bids = bidRepository.findTopByAuctionIdOrderByBidTimeDesc(auctionId, limit);
        return bidMapper.toDTOList(bids);
    }
    
    public List<BidDTO> getUserBids(String userId, Bid.BidStatus status) {
        List<Bid> bids;
        if (status != null) {
            bids = bidRepository.findByBidderIdAndStatus(userId, status);
        } else {
            bids = bidRepository.findByBidderId(userId);
        }
        return bidMapper.toDTOList(bids);
    }
    
    public void cancelBid(String bidId, String userId, String reason) {
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new BidException("Bid not found"));
        
        if (!bid.getBidderId().equals(userId)) {
            throw new SecurityException("User not authorized to cancel this bid");
        }
        
        if (bid.getStatus() != Bid.BidStatus.ACCEPTED) {
            throw new BidException("Only active bids can be cancelled");
        }
        
        if (bid.getIsWinningBid() && bid.getAuction().getStatus() == Auction.AuctionStatus.ENDED) {
            throw new BidException("Cannot cancel winning bid on ended auction");
        }
        
        bid.setStatus(Bid.BidStatus.CANCELLED);
        bid.setCancellationReason(reason);
        bid.setCancellationTime(LocalDateTime.now());
        
        bidRepository.save(bid);
        
        recalculateAuctionPrice(bid.getAuction());
        
        notificationService.notifyBidCancelled(bid.getAuction(), bid);
    }
    
    private void recalculateAuctionPrice(Auction auction) {
        bidRepository.findTopByAuctionIdAndStatusOrderByAmountDesc(
            auction.getId(), Bid.BidStatus.ACCEPTED
        ).ifPresentOrElse(
            highestBid -> auction.setCurrentPrice(highestBid.getAmount()),
            () -> auction.setCurrentPrice(auction.getStartingPrice())
        );
        
        auctionRepository.save(auction);
    }
    
    public BigDecimal getNextMinimumBid(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new BidException("Auction not found"));
        
        return calculateMinimumBid(auction);
    }
}
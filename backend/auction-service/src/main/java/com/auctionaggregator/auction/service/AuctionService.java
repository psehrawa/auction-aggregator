package com.auctionaggregator.auction.service;

import com.auctionaggregator.auction.dto.AuctionCreateDTO;
import com.auctionaggregator.auction.dto.AuctionDTO;
import com.auctionaggregator.auction.dto.AuctionSearchDTO;
import com.auctionaggregator.auction.dto.AuctionUpdateDTO;
import com.auctionaggregator.auction.entity.Auction;
import com.auctionaggregator.auction.entity.AuctionHistory;
import com.auctionaggregator.auction.exception.AuctionNotFoundException;
import com.auctionaggregator.auction.mapper.AuctionMapper;
import com.auctionaggregator.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    
    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final AuctionHistoryService historyService;
    private final AuctionNotificationService notificationService;
    private final AuctionSearchService searchService;
    
    public AuctionDTO createAuction(AuctionCreateDTO createDTO, String sellerId) {
        log.info("Creating new auction for seller: {}", sellerId);
        
        Auction auction = auctionMapper.toEntity(createDTO);
        auction.setSellerId(sellerId);
        auction.setStatus(Auction.AuctionStatus.DRAFT);
        auction.setCurrentPrice(createDTO.getStartingPrice());
        
        validateAuction(auction);
        
        auction = auctionRepository.save(auction);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.CREATED, sellerId, "Auction created");
        searchService.indexAuction(auction);
        
        log.info("Auction created with ID: {}", auction.getId());
        return auctionMapper.toDTO(auction);
    }
    
    public AuctionDTO updateAuction(String auctionId, AuctionUpdateDTO updateDTO, String userId) {
        log.info("Updating auction: {} by user: {}", auctionId, userId);
        
        Auction auction = getAuctionById(auctionId);
        
        validateUpdatePermissions(auction, userId);
        validateAuctionUpdate(auction, updateDTO);
        
        auctionMapper.updateEntity(updateDTO, auction);
        auction = auctionRepository.save(auction);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.UPDATED, userId, "Auction updated");
        searchService.updateAuctionIndex(auction);
        
        return auctionMapper.toDTO(auction);
    }
    
    public AuctionDTO getAuction(String auctionId) {
        Auction auction = getAuctionById(auctionId);
        incrementViewCount(auction);
        return auctionMapper.toDTO(auction);
    }
    
    public Page<AuctionDTO> searchAuctions(AuctionSearchDTO searchDTO, Pageable pageable) {
        Specification<Auction> spec = buildSearchSpecification(searchDTO);
        Page<Auction> auctions = auctionRepository.findAll(spec, pageable);
        return auctions.map(auctionMapper::toDTO);
    }
    
    public void activateAuction(String auctionId, String userId) {
        log.info("Activating auction: {} by user: {}", auctionId, userId);
        
        Auction auction = getAuctionById(auctionId);
        validateActivation(auction, userId);
        
        auction.setStatus(Auction.AuctionStatus.ACTIVE);
        auctionRepository.save(auction);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.ACTIVATED, userId, "Auction activated");
        notificationService.notifyAuctionStarted(auction);
    }
    
    public void cancelAuction(String auctionId, String userId, String reason) {
        log.info("Cancelling auction: {} by user: {} with reason: {}", auctionId, userId, reason);
        
        Auction auction = getAuctionById(auctionId);
        validateCancellation(auction, userId);
        
        auction.setStatus(Auction.AuctionStatus.CANCELLED);
        auctionRepository.save(auction);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.CANCELLED, userId, reason);
        notificationService.notifyAuctionCancelled(auction, reason);
    }
    
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void processEndingAuctions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endingSoon = now.plusMinutes(30);
        
        List<Auction> endingSoonAuctions = auctionRepository.findByStatusAndEndTimeBetween(
            Auction.AuctionStatus.ACTIVE, now, endingSoon
        );
        
        endingSoonAuctions.forEach(auction -> {
            if (auction.getStatus() != Auction.AuctionStatus.ENDING_SOON) {
                auction.setStatus(Auction.AuctionStatus.ENDING_SOON);
                auctionRepository.save(auction);
                notificationService.notifyAuctionEndingSoon(auction);
            }
        });
        
        List<Auction> endedAuctions = auctionRepository.findByStatusAndEndTimeBefore(
            Auction.AuctionStatus.ACTIVE, now
        );
        
        endedAuctions.forEach(this::endAuction);
    }
    
    private void endAuction(Auction auction) {
        log.info("Ending auction: {}", auction.getId());
        
        if (shouldExtendAuction(auction)) {
            extendAuction(auction);
            return;
        }
        
        auction.setStatus(Auction.AuctionStatus.ENDED);
        auction.setActualEndTime(LocalDateTime.now());
        
        determineWinner(auction);
        
        auctionRepository.save(auction);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.ENDED, "SYSTEM", "Auction ended");
        notificationService.notifyAuctionEnded(auction);
    }
    
    private boolean shouldExtendAuction(Auction auction) {
        if (!auction.getAutoExtend()) {
            return false;
        }
        
        LocalDateTime lastBidTime = auction.getBids().isEmpty() ? 
            auction.getStartTime() : 
            auction.getBids().get(0).getBidTime();
            
        LocalDateTime extensionThreshold = auction.getEndTime().minusMinutes(auction.getAutoExtendMinutes());
        
        return lastBidTime.isAfter(extensionThreshold);
    }
    
    private void extendAuction(Auction auction) {
        LocalDateTime newEndTime = auction.getEndTime().plusMinutes(auction.getAutoExtendMinutes());
        auction.setEndTime(newEndTime);
        
        log.info("Extended auction {} end time to {}", auction.getId(), newEndTime);
        
        historyService.recordHistory(auction, AuctionHistory.ActionType.EXTENDED, "SYSTEM", 
            "Auction auto-extended by " + auction.getAutoExtendMinutes() + " minutes");
        notificationService.notifyAuctionExtended(auction);
    }
    
    private void determineWinner(Auction auction) {
        auction.getBids().stream()
            .filter(bid -> bid.getStatus() == Bid.BidStatus.ACCEPTED)
            .findFirst()
            .ifPresent(winningBid -> {
                auction.setWinnerId(winningBid.getBidderId());
                auction.setWinningBid(winningBid.getAmount());
                auction.setStatus(Auction.AuctionStatus.SOLD);
                
                winningBid.setStatus(Bid.BidStatus.WINNING);
                winningBid.setIsWinningBid(true);
            });
    }
    
    private Auction getAuctionById(String auctionId) {
        return auctionRepository.findById(auctionId)
            .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
    }
    
    private void incrementViewCount(Auction auction) {
        auction.setViewCount(auction.getViewCount() + 1);
        auctionRepository.save(auction);
    }
    
    private void validateAuction(Auction auction) {
        if (auction.getStartTime().isAfter(auction.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        if (auction.getReservePrice() != null && 
            auction.getReservePrice().compareTo(auction.getStartingPrice()) < 0) {
            throw new IllegalArgumentException("Reserve price must be greater than starting price");
        }
        
        if (auction.getBuyNowPrice() != null && 
            auction.getBuyNowPrice().compareTo(auction.getStartingPrice()) <= 0) {
            throw new IllegalArgumentException("Buy now price must be greater than starting price");
        }
    }
    
    private void validateUpdatePermissions(Auction auction, String userId) {
        if (!auction.getSellerId().equals(userId) && !isAdmin(userId)) {
            throw new SecurityException("User not authorized to update this auction");
        }
        
        if (auction.getStatus() != Auction.AuctionStatus.DRAFT && 
            auction.getStatus() != Auction.AuctionStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot update active or ended auctions");
        }
    }
    
    private void validateAuctionUpdate(Auction auction, AuctionUpdateDTO updateDTO) {
        // Implement validation logic for updates
    }
    
    private void validateActivation(Auction auction, String userId) {
        if (!auction.getSellerId().equals(userId) && !isAdmin(userId)) {
            throw new SecurityException("User not authorized to activate this auction");
        }
        
        if (auction.getStatus() != Auction.AuctionStatus.DRAFT && 
            auction.getStatus() != Auction.AuctionStatus.SCHEDULED) {
            throw new IllegalStateException("Auction is not in a valid state for activation");
        }
    }
    
    private void validateCancellation(Auction auction, String userId) {
        if (!auction.getSellerId().equals(userId) && !isAdmin(userId)) {
            throw new SecurityException("User not authorized to cancel this auction");
        }
        
        if (auction.getStatus() == Auction.AuctionStatus.ENDED || 
            auction.getStatus() == Auction.AuctionStatus.SOLD) {
            throw new IllegalStateException("Cannot cancel ended or sold auctions");
        }
    }
    
    private boolean isAdmin(String userId) {
        // TODO: Implement admin check
        return false;
    }
    
    private Specification<Auction> buildSearchSpecification(AuctionSearchDTO searchDTO) {
        // TODO: Implement search specification builder
        return Specification.where(null);
    }
    
    public List<AuctionDTO> getTrendingAuctions(int limit) {
        List<Auction> auctions = auctionRepository.findTrendingAuctions(Auction.AuctionStatus.ACTIVE)
            .stream()
            .limit(limit)
            .toList();
        return auctions.stream().map(auctionMapper::toDTO).toList();
    }
    
    public List<AuctionDTO> getEndingSoonAuctions(int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endingSoon = now.plusHours(24);
        
        List<Auction> auctions = auctionRepository.findByStatusAndEndTimeBetween(
            Auction.AuctionStatus.ACTIVE, now, endingSoon
        ).stream().limit(limit).toList();
        
        return auctions.stream().map(auctionMapper::toDTO).toList();
    }
}
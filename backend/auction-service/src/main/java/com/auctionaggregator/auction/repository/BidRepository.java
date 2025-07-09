package com.auctionaggregator.auction.repository;

import com.auctionaggregator.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, String> {
    
    List<Bid> findByAuctionIdOrderByBidTimeDesc(String auctionId);
    
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.bidTime DESC LIMIT :limit")
    List<Bid> findTopByAuctionIdOrderByBidTimeDesc(@Param("auctionId") String auctionId, @Param("limit") int limit);
    
    List<Bid> findByBidderId(String bidderId);
    
    List<Bid> findByBidderIdAndStatus(String bidderId, Bid.BidStatus status);
    
    Optional<Bid> findTopByAuctionIdAndStatusOrderByAmountDesc(String auctionId, Bid.BidStatus status);
    
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auction.id = :auctionId AND b.status = :status")
    long countByAuctionIdAndStatus(@Param("auctionId") String auctionId, @Param("status") Bid.BidStatus status);
}
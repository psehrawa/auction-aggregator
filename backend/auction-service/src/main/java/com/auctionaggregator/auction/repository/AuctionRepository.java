package com.auctionaggregator.auction.repository;

import com.auctionaggregator.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, String>, JpaSpecificationExecutor<Auction> {
    
    List<Auction> findByStatus(Auction.AuctionStatus status);
    
    List<Auction> findByStatusAndEndTimeBetween(
        Auction.AuctionStatus status, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );
    
    List<Auction> findByStatusAndEndTimeBefore(
        Auction.AuctionStatus status, 
        LocalDateTime time
    );
    
    @Query("SELECT a FROM Auction a WHERE a.status = :status ORDER BY a.viewCount DESC")
    List<Auction> findTrendingAuctions(@Param("status") Auction.AuctionStatus status);
    
    @Query("SELECT a FROM Auction a WHERE a.sellerId = :sellerId ORDER BY a.createdAt DESC")
    List<Auction> findBySellerId(@Param("sellerId") String sellerId);
    
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = :status")
    long countByStatus(@Param("status") Auction.AuctionStatus status);
    
    Optional<Auction> findBySourceAndExternalId(String source, String externalId);
}
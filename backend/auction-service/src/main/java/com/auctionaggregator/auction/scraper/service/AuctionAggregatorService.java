package com.auctionaggregator.auction.scraper.service;

import com.auctionaggregator.auction.entity.Auction;
import com.auctionaggregator.auction.entity.AuctionImage;
import com.auctionaggregator.auction.repository.AuctionRepository;
import com.auctionaggregator.auction.scraper.model.ScrapedAuction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionAggregatorService {
    
    private final List<AuctionScraperService> scraperServices;
    private final AuctionRepository auctionRepository;
    private final ScraperHealthService scraperHealthService;
    
    @Scheduled(fixedDelay = 300000, initialDelay = 10000) // Run every 5 minutes
    public void aggregateAuctions() {
        log.info("Starting auction aggregation process");
        
        AtomicInteger totalScraped = new AtomicInteger(0);
        AtomicInteger totalSaved = new AtomicInteger(0);
        
        scraperServices.parallelStream()
            .filter(AuctionScraperService::isEnabled)
            .forEach(scraper -> {
                String scraperName = scraper.getSourceName();
                long startTime = System.currentTimeMillis();
                boolean success = false;
                String errorMessage = null;
                int itemsScraped = 0;
                
                try {
                    // Check if scraper is healthy before running
                    if (!scraperHealthService.isScraperHealthy(scraperName)) {
                        log.warn("Skipping unhealthy scraper: {}", scraperName);
                        return;
                    }
                    
                    log.info("Scraping auctions from: {}", scraperName);
                    List<ScrapedAuction> scrapedAuctions = scraper.scrapeAuctions();
                    itemsScraped = scrapedAuctions.size();
                    totalScraped.addAndGet(itemsScraped);
                    
                    AtomicInteger savedCount = new AtomicInteger(0);
                    scrapedAuctions.forEach(scrapedAuction -> {
                        try {
                            if (saveOrUpdateAuction(scrapedAuction)) {
                                savedCount.incrementAndGet();
                                totalSaved.incrementAndGet();
                            }
                        } catch (Exception e) {
                            log.error("Error saving auction: {}", scrapedAuction.getExternalId(), e);
                        }
                    });
                    
                    success = true;
                    log.info("Scraper {} completed: {} items scraped, {} saved", 
                            scraperName, itemsScraped, savedCount.get());
                    
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.error("Error in scraper: {}", scraperName, e);
                } finally {
                    long duration = System.currentTimeMillis() - startTime;
                    scraperHealthService.recordScrapingResult(
                        scraperName, success, itemsScraped, duration, errorMessage
                    );
                }
            });
        
        log.info("Aggregation complete. Total scraped: {}, Total saved/updated: {}", 
                totalScraped.get(), totalSaved.get());
    }
    
    private boolean saveOrUpdateAuction(ScrapedAuction scrapedAuction) {
        Optional<Auction> existingAuction = auctionRepository
            .findBySourceAndExternalId(scrapedAuction.getSource(), scrapedAuction.getExternalId());
        
        Auction auction;
        if (existingAuction.isPresent()) {
            auction = existingAuction.get();
            updateAuctionFromScraped(auction, scrapedAuction);
        } else {
            auction = createAuctionFromScraped(scrapedAuction);
        }
        
        auctionRepository.save(auction);
        return true;
    }
    
    private Auction createAuctionFromScraped(ScrapedAuction scraped) {
        Auction auction = Auction.builder()
            .title(scraped.getTitle())
            .description(scraped.getDescription())
            .source(scraped.getSource())
            .externalId(scraped.getExternalId())
            .externalUrl(scraped.getSourceUrl())
            .startingPrice(scraped.getStartingPrice())
            .currentPrice(scraped.getCurrentPrice() != null ? scraped.getCurrentPrice() : scraped.getStartingPrice())
            .bidIncrement(calculateBidIncrement(scraped.getStartingPrice()))
            .startTime(scraped.getStartTime())
            .endTime(scraped.getEndTime())
            .status(mapStatus(scraped.getStatus()))
            .auctionType(Auction.AuctionType.STANDARD)
            .sellerId("EXTERNAL_" + scraped.getSource())
            .viewCount(0)
            .watcherCount(0)
            .build();
        
        // Add images
        if (scraped.getImageUrls() != null && !scraped.getImageUrls().isEmpty()) {
            List<AuctionImage> images = new ArrayList<>();
            for (int i = 0; i < scraped.getImageUrls().size(); i++) {
                AuctionImage image = AuctionImage.builder()
                    .auction(auction)
                    .url(scraped.getImageUrls().get(i))
                    .displayOrder(i)
                    .isPrimary(i == 0)
                    .build();
                images.add(image);
            }
            auction.setImages(images);
        }
        
        // Add tags
        auction.setTags(generateTags(scraped));
        
        return auction;
    }
    
    private void updateAuctionFromScraped(Auction auction, ScrapedAuction scraped) {
        auction.setCurrentPrice(scraped.getCurrentPrice());
        auction.setStatus(mapStatus(scraped.getStatus()));
        
        // Update bid count if available
        if (scraped.getBidCount() != null && scraped.getBidCount() > 0) {
            // This is a simplified update - in real implementation, 
            // you'd sync actual bids
            auction.setViewCount(auction.getViewCount() + scraped.getBidCount());
        }
    }
    
    private Auction.AuctionStatus mapStatus(ScrapedAuction.AuctionStatus scrapedStatus) {
        return switch (scrapedStatus) {
            case UPCOMING -> Auction.AuctionStatus.SCHEDULED;
            case ACTIVE -> Auction.AuctionStatus.ACTIVE;
            case ENDING_SOON -> Auction.AuctionStatus.ENDING_SOON;
            case ENDED -> Auction.AuctionStatus.ENDED;
        };
    }
    
    private BigDecimal calculateBidIncrement(BigDecimal startingPrice) {
        if (startingPrice.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("50");
        } else if (startingPrice.compareTo(new BigDecimal("10000")) < 0) {
            return new BigDecimal("100");
        } else if (startingPrice.compareTo(new BigDecimal("100000")) < 0) {
            return new BigDecimal("1000");
        } else {
            return new BigDecimal("5000");
        }
    }
    
    private List<String> generateTags(ScrapedAuction scraped) {
        List<String> tags = new ArrayList<>();
        
        if (scraped.getCategory() != null) {
            tags.add(scraped.getCategory().toLowerCase());
        }
        
        if (scraped.getLocation() != null) {
            String[] locationParts = scraped.getLocation().split(",");
            for (String part : locationParts) {
                tags.add(part.trim().toLowerCase());
            }
        }
        
        // Add source as tag
        tags.add(scraped.getSource().toLowerCase().replace(" ", "-"));
        
        return tags;
    }
    
    public void runManualSync() {
        log.info("Running manual sync");
        aggregateAuctions();
    }
}
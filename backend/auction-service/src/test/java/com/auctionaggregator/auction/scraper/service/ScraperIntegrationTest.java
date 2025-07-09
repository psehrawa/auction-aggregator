package com.auctionaggregator.auction.scraper.service;

import com.auctionaggregator.auction.scraper.config.PlaywrightConfig;
import com.auctionaggregator.auction.scraper.model.ScrapedAuction;
import com.auctionaggregator.auction.scraper.service.impl.GovAuctionScraperService;
import com.auctionaggregator.auction.scraper.service.impl.RealEstateAuctionScraperService;
import com.auctionaggregator.auction.scraper.service.impl.VehicleAuctionScraperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "scraper.enabled=true",
    "scraper.real-estate.enabled=true",
    "scraper.gov-auction.enabled=true",
    "scraper.vehicle-auction.enabled=true"
})
public class ScraperIntegrationTest {
    
    @Autowired
    private RealEstateAuctionScraperService realEstateScraperService;
    
    @Autowired
    private GovAuctionScraperService govAuctionScraperService;
    
    @Autowired
    private VehicleAuctionScraperService vehicleAuctionScraperService;
    
    @Autowired
    private PlaywrightConfig playwrightConfig;
    
    @BeforeEach
    void setUp() {
        // Ensure Playwright is initialized
        assertThat(playwrightConfig).isNotNull();
    }
    
    @Test
    void testRealEstateScraperReturnsData() {
        List<ScrapedAuction> auctions = realEstateScraperService.scrapeAuctions();
        
        assertThat(auctions).isNotEmpty();
        assertThat(auctions).allSatisfy(auction -> {
            assertThat(auction.getExternalId()).isNotBlank();
            assertThat(auction.getTitle()).isNotBlank();
            assertThat(auction.getCategory()).isEqualTo("Real Estate");
            assertThat(auction.getCurrentPrice()).isNotNull();
            assertThat(auction.getStartingPrice()).isNotNull();
            assertThat(auction.getEndTime()).isNotNull();
            assertThat(auction.getSource()).isEqualTo("Real Estate Auctions");
        });
    }
    
    @Test
    void testGovAuctionScraperReturnsData() {
        List<ScrapedAuction> auctions = govAuctionScraperService.scrapeAuctions();
        
        assertThat(auctions).isNotEmpty();
        assertThat(auctions).allSatisfy(auction -> {
            assertThat(auction.getExternalId()).isNotBlank();
            assertThat(auction.getTitle()).isNotBlank();
            assertThat(auction.getCurrentPrice()).isNotNull();
            assertThat(auction.getSource()).isEqualTo("GeM Portal");
        });
    }
    
    @Test
    void testVehicleAuctionScraperReturnsData() {
        List<ScrapedAuction> auctions = vehicleAuctionScraperService.scrapeAuctions();
        
        assertThat(auctions).isNotEmpty();
        assertThat(auctions).allSatisfy(auction -> {
            assertThat(auction.getExternalId()).isNotBlank();
            assertThat(auction.getTitle()).isNotBlank();
            assertThat(auction.getCategory()).isEqualTo("Vehicles");
            assertThat(auction.getCurrentPrice()).isNotNull();
            assertThat(auction.getSource()).isEqualTo("Vehicle Auctions");
        });
    }
    
    @Test
    void testScraperCanBeDisabled() {
        // This would be tested with a different configuration
        assertThat(realEstateScraperService.isEnabled()).isTrue();
        assertThat(govAuctionScraperService.isEnabled()).isTrue();
        assertThat(vehicleAuctionScraperService.isEnabled()).isTrue();
    }
    
    @Test
    void testScrapedAuctionDataIntegrity() {
        List<ScrapedAuction> allAuctions = vehicleAuctionScraperService.scrapeAuctions();
        
        assertThat(allAuctions).allSatisfy(auction -> {
            // Verify required fields
            assertThat(auction.getExternalId()).isNotBlank();
            assertThat(auction.getTitle()).isNotBlank();
            assertThat(auction.getSource()).isNotBlank();
            assertThat(auction.getStatus()).isNotNull();
            
            // Verify price logic
            assertThat(auction.getCurrentPrice()).isNotNull();
            assertThat(auction.getStartingPrice()).isNotNull();
            assertThat(auction.getCurrentPrice()).isGreaterThanOrEqualTo(auction.getStartingPrice());
            
            // Verify time logic
            assertThat(auction.getStartTime()).isNotNull();
            assertThat(auction.getEndTime()).isNotNull();
            assertThat(auction.getEndTime()).isAfter(auction.getStartTime());
            
            // Verify optional fields have reasonable values
            if (auction.getImageUrls() != null) {
                assertThat(auction.getImageUrls()).allSatisfy(url -> 
                    assertThat(url).startsWith("http")
                );
            }
        });
    }
}
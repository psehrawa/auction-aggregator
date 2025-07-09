package com.auctionaggregator.auction.scraper.service.impl;

import com.auctionaggregator.auction.scraper.config.PlaywrightConfig;
import com.auctionaggregator.auction.scraper.model.ScrapedAuction;
import com.auctionaggregator.auction.scraper.service.AuctionScraperService;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleAuctionScraperService implements AuctionScraperService {
    
    private final PlaywrightConfig playwrightConfig;
    
    @Value("${scraper.vehicle-auction.enabled:true}")
    private boolean enabled;
    
    @Value("${scraper.vehicle-auction.url:https://www.copart.com}")
    private String baseUrl;
    
    private static final Pattern PRICE_PATTERN = Pattern.compile("[$₹]?\\s*([0-9,]+(?:\\.[0-9]{2})?)");
    private static final Pattern MILEAGE_PATTERN = Pattern.compile("([0-9,]+)\\s*(km|miles?|mi)", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
    };
    
    @Override
    public List<ScrapedAuction> scrapeAuctions() {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        if (!enabled) {
            log.info("Vehicle auction scraper is disabled");
            return auctions;
        }
        
        try (BrowserContext context = playwrightConfig.createContext()) {
            Page page = context.newPage();
            
            log.info("Scraping vehicle auctions...");
            
            // In production, this would scrape actual vehicle auction sites
            auctions.addAll(scrapeCopartAuctions(page));
            auctions.addAll(scrapeLocalVehicleAuctions(page));
            
        } catch (Exception e) {
            log.error("Error scraping vehicle auctions", e);
        }
        
        log.info("Scraped {} vehicle auctions", auctions.size());
        return auctions;
    }
    
    private List<ScrapedAuction> scrapeCopartAuctions(Page page) {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        try {
            // Production scraping example:
            /*
            page.navigate(baseUrl + "/todaysAuction/");
            page.waitForSelector(".lot-card", new Page.WaitForSelectorOptions().setTimeout(10000));
            
            List<ElementHandle> lotCards = page.querySelectorAll(".lot-card");
            for (ElementHandle card : lotCards) {
                try {
                    String lotNumber = card.querySelector(".lot-number").innerText();
                    String title = card.querySelector(".lot-title").innerText();
                    String currentBidText = card.querySelector(".current-bid").innerText();
                    String auctionDateText = card.querySelector(".auction-date").innerText();
                    String location = card.querySelector(".lot-location").innerText();
                    String mileageText = card.querySelector(".lot-odometer").innerText();
                    
                    // Extract vehicle details
                    ElementHandle detailsLink = card.querySelector("a.lot-link");
                    String detailsUrl = detailsLink.getAttribute("href");
                    
                    ScrapedAuction auction = ScrapedAuction.builder()
                        .externalId(lotNumber)
                        .title(title)
                        .currentPrice(parsePrice(currentBidText))
                        .endTime(parseDateTime(auctionDateText))
                        .location(location)
                        .sourceUrl(baseUrl + detailsUrl)
                        .source("Copart")
                        .build();
                        
                    // Add mileage to description if found
                    Matcher mileageMatcher = MILEAGE_PATTERN.matcher(mileageText);
                    if (mileageMatcher.find()) {
                        auction.setDescription("Mileage: " + mileageMatcher.group());
                    }
                    
                    auctions.add(auction);
                } catch (Exception e) {
                    log.warn("Error parsing vehicle lot card", e);
                }
            }
            */
            
            // Demo data representing actual Copart auctions
            auctions.add(createVehicleAuction("43215678", 
                "2019 Toyota Camry LE - Salvage Title",
                "Front End Damage, Runs and Drives, 35,420 km, Silver exterior, Grey interior",
                new BigDecimal("8500"), 
                new BigDecimal("12500"),
                "Mumbai - Copart Yard",
                "Salvage"));
                
            auctions.add(createVehicleAuction("43215679", 
                "2020 Honda Accord Sport - Clean Title",
                "Minor Damage, 28,000 km, Black exterior, Black leather interior, Sunroof",
                new BigDecimal("15000"), 
                new BigDecimal("18500"),
                "Delhi NCR - Copart Facility",
                "Clean"));
                
            auctions.add(createVehicleAuction("43215680", 
                "2018 Maruti Suzuki Vitara Brezza VDi",
                "Flood Damage, 42,000 km, White exterior, Does not start",
                new BigDecimal("3500"), 
                new BigDecimal("5200"),
                "Chennai - Copart Location",
                "Salvage"));
                
        } catch (Exception e) {
            log.error("Error scraping Copart auctions", e);
        }
        
        return auctions;
    }
    
    private List<ScrapedAuction> scrapeLocalVehicleAuctions(Page page) {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        try {
            // Demo data for local vehicle auctions
            auctions.add(createVehicleAuction("LOCAL-2024-201", 
                "2021 Hyundai Creta SX(O) Diesel AT",
                "Single Owner, Full Service History, 18,500 km, Pearl White, Warranty till 2025",
                new BigDecimal("1400000"), 
                new BigDecimal("1525000"),
                "Bangalore - Premium Cars",
                "Used"));
                
            auctions.add(createVehicleAuction("LOCAL-2024-202", 
                "2017 BMW 3 Series 320d Luxury Line",
                "Second Owner, 68,000 km, Mineral Grey, Full BMW service history, New tyres",
                new BigDecimal("1850000"), 
                new BigDecimal("2100000"),
                "Pune - Luxury Auto Exchange",
                "Certified Pre-Owned"));
                
            auctions.add(createVehicleAuction("LOCAL-2024-203", 
                "Fleet Sale: 10 Maruti Suzuki Dzire VXI",
                "2019-2020 models, Average 80,000 km, Company maintained, Selling as lot",
                new BigDecimal("3500000"), 
                new BigDecimal("3850000"),
                "Gurgaon - Fleet Auctions",
                "Fleet"));
                
        } catch (Exception e) {
            log.error("Error scraping local vehicle auctions", e);
        }
        
        return auctions;
    }
    
    private ScrapedAuction createVehicleAuction(String id, String title, String description,
                                               BigDecimal startingPrice, BigDecimal currentPrice,
                                               String location, String condition) {
        return ScrapedAuction.builder()
            .externalId(id)
            .title(title)
            .description(description + " | Condition: " + condition)
            .category("Vehicles")
            .startingPrice(startingPrice)
            .currentPrice(currentPrice)
            .location(location)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusDays(3))
            .imageUrls(List.of(
                "https://picsum.photos/600/400?random=" + id.hashCode(),
                "https://picsum.photos/600/400?random=" + (id.hashCode() + 1),
                "https://picsum.photos/600/400?random=" + (id.hashCode() + 2)
            ))
            .sourceUrl(baseUrl + "/lot/" + id)
            .source("Vehicle Auctions")
            .bidCount((int)(Math.random() * 30 + 5))
            .sellerName("Certified Dealer")
            .status(ScrapedAuction.AuctionStatus.ACTIVE)
            .tags(List.of(condition, "Vehicles", extractVehicleType(title)))
            .build();
    }
    
    private String extractVehicleType(String title) {
        String titleLower = title.toLowerCase();
        if (titleLower.contains("car") || titleLower.contains("sedan") || 
            titleLower.contains("suv") || titleLower.contains("hatchback")) {
            return "Cars";
        } else if (titleLower.contains("bike") || titleLower.contains("motorcycle") || 
                   titleLower.contains("scooter")) {
            return "Two Wheelers";
        } else if (titleLower.contains("truck") || titleLower.contains("commercial")) {
            return "Commercial";
        }
        return "Other Vehicles";
    }
    
    private BigDecimal parsePrice(String priceText) {
        if (priceText == null) return BigDecimal.ZERO;
        
        Matcher matcher = PRICE_PATTERN.matcher(priceText);
        if (matcher.find()) {
            String cleanPrice = matcher.group(1).replaceAll(",", "");
            try {
                return new BigDecimal(cleanPrice);
            } catch (NumberFormatException e) {
                log.warn("Could not parse price: {}", priceText);
            }
        }
        return BigDecimal.ZERO;
    }
    
    private LocalDateTime parseDateTime(String dateText) {
        if (dateText == null) return LocalDateTime.now().plusDays(7);
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateText, formatter);
            } catch (Exception e) {
                // Try next formatter
            }
        }
        
        log.warn("Could not parse date: {}", dateText);
        return LocalDateTime.now().plusDays(7);
    }
    
    @Override
    public String getSourceName() {
        return "Vehicle Auctions";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
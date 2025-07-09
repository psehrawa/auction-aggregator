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
public class GovAuctionScraperService implements AuctionScraperService {
    
    private final PlaywrightConfig playwrightConfig;
    
    @Value("${scraper.gov-auction.enabled:true}")
    private boolean enabled;
    
    @Value("${scraper.gov-auction.url:https://gem.gov.in}")
    private String baseUrl;
    
    private static final Pattern PRICE_PATTERN = Pattern.compile("₹?\\s*([0-9,]+(?:\\.[0-9]{2})?)");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    
    @Override
    public List<ScrapedAuction> scrapeAuctions() {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        if (!enabled) {
            log.info("Government auction scraper is disabled");
            return auctions;
        }
        
        try (BrowserContext context = playwrightConfig.createContext()) {
            Page page = context.newPage();
            
            log.info("Scraping government surplus auctions from GeM portal...");
            
            // Since we can't scrape real GeM portal in demo, we'll use realistic patterns
            // In production, this would navigate to actual pages
            auctions.addAll(scrapeGeMBids(page));
            auctions.addAll(scrapeDirectPurchase(page));
            
        } catch (Exception e) {
            log.error("Error scraping government auctions", e);
        }
        
        log.info("Scraped {} government auctions", auctions.size());
        return auctions;
    }
    
    private List<ScrapedAuction> scrapeGeMBids(Page page) {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        try {
            // Production scraping example:
            /*
            page.navigate(baseUrl + "/view-bids");
            page.waitForSelector(".bid-card", new Page.WaitForSelectorOptions().setTimeout(10000));
            
            List<ElementHandle> bidCards = page.querySelectorAll(".bid-card");
            for (ElementHandle card : bidCards) {
                String bidId = card.querySelector(".bid-number").innerText();
                String title = card.querySelector(".bid-title").innerText();
                String priceText = card.querySelector(".bid-value").innerText();
                String endDateText = card.querySelector(".bid-end-date").innerText();
                
                ScrapedAuction auction = ScrapedAuction.builder()
                    .externalId(bidId)
                    .title(title)
                    .currentPrice(parsePrice(priceText))
                    .endTime(parseDateTime(endDateText))
                    .source("GeM Bids")
                    .build();
                    
                auctions.add(auction);
            }
            */
            
            // Demo data representing actual GeM bids
            auctions.add(createGeMBid("GEM/2024/B/4521001", 
                "Procurement of Desktop Computers - 100 Units",
                "Supply of Desktop Computers with Intel i5 11th Gen, 8GB RAM, 512GB SSD",
                new BigDecimal("4500000"), 
                new BigDecimal("4200000"),
                "IT Equipment"));
                
            auctions.add(createGeMBid("GEM/2024/B/4521002", 
                "Annual Maintenance Contract for CCTV Systems",
                "AMC for 500 CCTV cameras across government buildings in Delhi NCR",
                new BigDecimal("1200000"), 
                new BigDecimal("1150000"),
                "Services"));
                
            auctions.add(createGeMBid("GEM/2024/B/4521003", 
                "Supply of Office Stationery Items",
                "Annual supply contract for stationery items including papers, pens, files, etc.",
                new BigDecimal("800000"), 
                new BigDecimal("750000"),
                "Office Supplies"));
                
        } catch (Exception e) {
            log.error("Error scraping GeM bids", e);
        }
        
        return auctions;
    }
    
    private List<ScrapedAuction> scrapeDirectPurchase(Page page) {
        List<ScrapedAuction> auctions = new ArrayList<>();
        
        try {
            // Demo data for direct purchase items
            auctions.add(createGeMBid("GEM/2024/DP/4521004", 
                "Medical Equipment - Ventilators (10 Units)",
                "High-end ventilators for government hospitals. CE certified, with warranty.",
                new BigDecimal("8000000"), 
                new BigDecimal("7500000"),
                "Medical Equipment"));
                
            auctions.add(createGeMBid("GEM/2024/DP/4521005", 
                "Solar Power Equipment - 100KW System",
                "Complete solar power system including panels, inverters, and installation",
                new BigDecimal("5500000"), 
                new BigDecimal("5200000"),
                "Renewable Energy"));
                
        } catch (Exception e) {
            log.error("Error scraping direct purchase items", e);
        }
        
        return auctions;
    }
    
    private ScrapedAuction createGeMBid(String id, String title, String description,
                                       BigDecimal startingPrice, BigDecimal currentPrice,
                                       String category) {
        return ScrapedAuction.builder()
            .externalId(id)
            .title(title)
            .description(description)
            .category(category)
            .startingPrice(startingPrice)
            .currentPrice(currentPrice)
            .location("New Delhi")
            .startTime(LocalDateTime.now().minusDays(3))
            .endTime(LocalDateTime.now().plusDays(4))
            .imageUrls(List.of(
                "https://picsum.photos/600/400?random=" + id.hashCode()
            ))
            .sourceUrl(baseUrl + "/product-detail/" + id)
            .source("GeM Portal")
            .bidCount((int)(Math.random() * 25 + 5))
            .sellerName("Government e-Marketplace")
            .status(ScrapedAuction.AuctionStatus.ACTIVE)
            .build();
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
        
        try {
            return LocalDateTime.parse(dateText, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Could not parse date: {}", dateText);
            return LocalDateTime.now().plusDays(7);
        }
    }
    
    @Override
    public String getSourceName() {
        return "GeM Portal";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
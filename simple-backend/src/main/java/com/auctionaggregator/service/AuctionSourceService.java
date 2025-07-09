package com.auctionaggregator.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuctionSourceService {
    
    private static final Map<String, SourceInfo> AUCTION_SOURCES = new HashMap<>();
    
    static {
        // Government sources
        AUCTION_SOURCES.put("GOV-Electronics", new SourceInfo(
            "https://gem.gov.in/",
            "https://gem.gov.in/resources/pdf/gem-brochure.pdf",
            "Government e-Marketplace"
        ));
        
        // Bank sources - Real estate
        AUCTION_SOURCES.put("BANK-RealEstate-SBI", new SourceInfo(
            "https://www.sbi.co.in/web/sbi-in-the-news/auction-notices",
            "https://www.sbi.co.in/documents/16012/25493050/Auction_Notice_Properties.pdf",
            "SBI Auction Notices"
        ));
        
        AUCTION_SOURCES.put("BANK-RealEstate-HDFC", new SourceInfo(
            "https://www.hdfcbank.com/personal/resources/rates-charges/auction-notices",
            null,
            "HDFC Bank Auctions"
        ));
        
        AUCTION_SOURCES.put("BANK-RealEstate-ICICI", new SourceInfo(
            "https://www.icicibank.com/managed-assets/docs/about-us/notice-auction.page",
            "https://www.icicibank.com/managed-assets/docs/about-us/notice-auction.pdf",
            "ICICI Bank Property Auctions"
        ));
        
        // Vehicle auctions
        AUCTION_SOURCES.put("VEHICLE-Bank", new SourceInfo(
            "https://www.bankeauctions.com/",
            null,
            "Bank eAuctions Portal"
        ));
        
        AUCTION_SOURCES.put("VEHICLE-MSTC", new SourceInfo(
            "https://www.mstcecommerce.com/auctionhome/index.jsp",
            null,
            "MSTC Vehicle Auctions"
        ));
        
        // Industrial auctions
        AUCTION_SOURCES.put("INDUSTRIAL-MSTC", new SourceInfo(
            "https://www.mstcecommerce.com/auctionhome/psu/index.jsp",
            null,
            "MSTC Industrial Auctions"
        ));
        
        // Furniture/Office equipment
        AUCTION_SOURCES.put("FURNITURE-DGS", new SourceInfo(
            "https://eprocure.gov.in/cppp/",
            null,
            "Central Public Procurement Portal"
        ));
        
        // Electronics/IT equipment
        AUCTION_SOURCES.put("ELECTRONICS-GeM", new SourceInfo(
            "https://mkp.gem.gov.in/computers-desktop/search",
            "https://assets-bg.gem.gov.in/resources/pdf/GeMBuyersManual.pdf",
            "GeM - Computers & Electronics"
        ));
    }
    
    public SourceInfo getSourceForAuction(String categoryId, String sellerId) {
        // Try specific combination first
        String key = categoryId.toUpperCase() + "-" + sellerId;
        if (AUCTION_SOURCES.containsKey(key)) {
            return AUCTION_SOURCES.get(key);
        }
        
        // Try category-based defaults
        switch (categoryId.toLowerCase()) {
            case "electronics":
                if (sellerId.contains("GOV")) {
                    return AUCTION_SOURCES.get("GOV-Electronics");
                }
                return AUCTION_SOURCES.get("ELECTRONICS-GeM");
                
            case "real-estate":
            case "realestate":
            case "property":
                if (sellerId.contains("SBI")) {
                    return AUCTION_SOURCES.get("BANK-RealEstate-SBI");
                } else if (sellerId.contains("HDFC")) {
                    return AUCTION_SOURCES.get("BANK-RealEstate-HDFC");
                } else if (sellerId.contains("ICICI")) {
                    return AUCTION_SOURCES.get("BANK-RealEstate-ICICI");
                }
                return AUCTION_SOURCES.get("BANK-RealEstate-SBI");
                
            case "vehicles":
            case "vehicle":
                if (sellerId.contains("MSTC")) {
                    return AUCTION_SOURCES.get("VEHICLE-MSTC");
                }
                return AUCTION_SOURCES.get("VEHICLE-Bank");
                
            case "industrial":
            case "machinery":
                return AUCTION_SOURCES.get("INDUSTRIAL-MSTC");
                
            case "furniture":
            case "office":
                return AUCTION_SOURCES.get("FURNITURE-DGS");
                
            default:
                return AUCTION_SOURCES.get("GOV-Electronics");
        }
    }
    
    public static class SourceInfo {
        public final String url;
        public final String pdfUrl;
        public final String sourceName;
        
        public SourceInfo(String url, String pdfUrl, String sourceName) {
            this.url = url;
            this.pdfUrl = pdfUrl;
            this.sourceName = sourceName;
        }
    }
}
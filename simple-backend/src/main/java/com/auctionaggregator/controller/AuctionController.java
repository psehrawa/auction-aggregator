package com.auctionaggregator.controller;

import com.auctionaggregator.model.Auction;
import com.auctionaggregator.model.AuctionImage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    private final List<Auction> mockAuctions;

    public AuctionController() {
        this.mockAuctions = createMockAuctions();
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(mockAuctions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable String id) {
        Optional<Auction> auction = mockAuctions.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
        
        if (auction.isPresent()) {
            return ResponseEntity.ok(auction.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Auction>> searchAuctions(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        List<Auction> filtered = mockAuctions.stream()
                .filter(auction -> {
                    if (q != null && !auction.getTitle().toLowerCase().contains(q.toLowerCase())) {
                        return false;
                    }
                    if (category != null && !auction.getCategoryId().equalsIgnoreCase(category)) {
                        return false;
                    }
                    if (status != null && !auction.getStatus().equalsIgnoreCase(status)) {
                        return false;
                    }
                    return true;
                })
                .toList();
        
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Auction>> getTrendingAuctions() {
        List<Auction> trending = mockAuctions.stream()
                .filter(a -> "ACTIVE".equals(a.getStatus()))
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .limit(12)
                .toList();
        return ResponseEntity.ok(trending);
    }

    @GetMapping("/ending-soon")
    public ResponseEntity<List<Auction>> getEndingSoonAuctions() {
        List<Auction> endingSoon = mockAuctions.stream()
                .filter(a -> "ENDING_SOON".equals(a.getStatus()))
                .toList();
        return ResponseEntity.ok(endingSoon);
    }

    private List<Auction> createMockAuctions() {
        return Arrays.asList(
            new Auction("1", "Dell OptiPlex 7040 Desktop Computer", 
                "Refurbished Dell OptiPlex with Intel i5 processor, 8GB RAM, 256GB SSD", 
                "electronics", new BigDecimal("15000"), new BigDecimal("18500"), new BigDecimal("500"),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(3), "ACTIVE",
                "seller1", "TechDeals Mumbai", 125,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=1", true)),
                Arrays.asList("electronics", "computer", "refurbished"), "Mumbai, Maharashtra"),

            new Auction("2", "2019 Maruti Suzuki Swift VXI", 
                "Well maintained Swift with 32,000 km, single owner, full service history", 
                "vehicles", new BigDecimal("450000"), new BigDecimal("485000"), new BigDecimal("5000"),
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(6), "ENDING_SOON",
                "seller2", "CarMax Delhi", 89,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=2", true)),
                Arrays.asList("vehicles", "car", "hatchback"), "New Delhi"),

            new Auction("3", "Office Furniture Set - 20 Desks & Chairs", 
                "Complete office furniture lot from government office renovation", 
                "furniture", new BigDecimal("75000"), new BigDecimal("95000"), new BigDecimal("2000"),
                LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(4), "ACTIVE",
                "seller3", "Government Surplus", 156,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=3", true)),
                Arrays.asList("furniture", "office", "government"), "Bangalore, Karnataka"),

            new Auction("4", "Industrial Generator 100 KVA", 
                "Kirloskar diesel generator, 2018 model, excellent condition", 
                "industrial", new BigDecimal("350000"), new BigDecimal("385000"), new BigDecimal("10000"),
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), "ACTIVE",
                "seller4", "Industrial Auctions", 67,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=4", true)),
                Arrays.asList("industrial", "generator", "machinery"), "Chennai, Tamil Nadu"),

            new Auction("5", "Samsung 55 inch 4K Smart TV", 
                "Brand new Samsung QLED TV, sealed box with full warranty", 
                "electronics", new BigDecimal("55000"), new BigDecimal("52000"), new BigDecimal("1000"),
                LocalDateTime.now().minusHours(8), LocalDateTime.now().plusHours(16), "ENDING_SOON",
                "seller5", "ElectroMart", 203,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=5", true)),
                Arrays.asList("electronics", "tv", "samsung"), "Hyderabad, Telangana"),

            new Auction("6", "Vintage Teak Wood Dining Set", 
                "Antique teak dining table with 6 chairs, excellent craftsmanship", 
                "furniture", new BigDecimal("45000"), new BigDecimal("58000"), new BigDecimal("2000"),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(5), "ACTIVE",
                "seller6", "Antique Collections", 78,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=6", true)),
                Arrays.asList("furniture", "antique", "teak"), "Kolkata, West Bengal"),

            new Auction("7", "MacBook Pro 13 inch M1", 
                "Like new MacBook Pro with M1 chip, 256GB storage, minimal usage", 
                "electronics", new BigDecimal("85000"), new BigDecimal("92000"), new BigDecimal("2000"),
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3), "ACTIVE",
                "seller7", "Apple Store Reseller", 145,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=7", true)),
                Arrays.asList("electronics", "laptop", "apple"), "Pune, Maharashtra"),

            new Auction("8", "Royal Enfield Classic 350", 
                "2021 model Royal Enfield, 8,500 km, gunmetal grey color", 
                "vehicles", new BigDecimal("140000"), new BigDecimal("148000"), new BigDecimal("2000"),
                LocalDateTime.now().minusHours(4), LocalDateTime.now().plusDays(1), "ACTIVE",
                "seller8", "Bike Bazaar", 91,
                Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=8", true)),
                Arrays.asList("vehicles", "motorcycle", "royal-enfield"), "Jaipur, Rajasthan")
        );
    }
}
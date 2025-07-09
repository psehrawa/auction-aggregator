package com.auctionaggregator.controller;

import com.auctionaggregator.model.Auction;
import com.auctionaggregator.model.AuctionImage;
import com.auctionaggregator.service.AuctionSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    @Autowired
    private AuctionSourceService sourceService;

    private final List<Auction> mockAuctions;

    public AuctionController() {
        this.mockAuctions = new ArrayList<>();
    }
    
    @jakarta.annotation.PostConstruct
    public void init() {
        this.mockAuctions.addAll(createMockAuctions());
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
        List<Auction> auctions = new ArrayList<>();
        
        Auction a1 = new Auction();
        a1.setId("1");
        a1.setTitle("Dell OptiPlex 7040 Desktop Computer");
        a1.setDescription("Refurbished Dell OptiPlex with Intel i5 processor, 8GB RAM, 256GB SSD");
        a1.setCategoryId("electronics");
        a1.setStartingPrice(new BigDecimal("15000"));
        a1.setCurrentPrice(new BigDecimal("18500"));
        a1.setBidIncrement(new BigDecimal("500"));
        a1.setStartTime(LocalDateTime.now().minusDays(2));
        a1.setEndTime(LocalDateTime.now().plusDays(3));
        a1.setStatus("ACTIVE");
        a1.setSellerId("GOV-MUM");
        a1.setSellerName("Government e-Marketplace Mumbai");
        a1.setViewCount(125);
        a1.setWatcherCount(12);
        a1.setAuctionType("STANDARD");
        a1.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=1", true)));
        a1.setTags(Arrays.asList("electronics", "computer", "refurbished"));
        a1.setLocation("Mumbai, Maharashtra");
        a1.setCreatedAt(LocalDateTime.now().minusDays(5));
        a1.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        // Add source URLs
        AuctionSourceService.SourceInfo source = sourceService.getSourceForAuction(a1.getCategoryId(), a1.getSellerId());
        a1.setSourceUrl(source.url);
        a1.setSourcePdfUrl(source.pdfUrl);
        
        auctions.add(a1);

        
        // Create remaining auctions
        for (int i = 2; i <= 8; i++) {
            Auction auction = new Auction();
            auction.setId(String.valueOf(i));
            auction.setStatus("ACTIVE");
            auction.setAuctionType("STANDARD");
            auction.setCreatedAt(LocalDateTime.now().minusDays(10 - i));
            auction.setUpdatedAt(LocalDateTime.now().minusDays(i % 3));
            
            switch (i) {
                case 2:
                    auction.setTitle("2019 Maruti Suzuki Swift VXI");
                    auction.setDescription("Well maintained Swift with 32,000 km, single owner, full service history");
                    auction.setCategoryId("vehicles");
                    auction.setStartingPrice(new BigDecimal("450000"));
                    auction.setCurrentPrice(new BigDecimal("485000"));
                    auction.setBidIncrement(new BigDecimal("5000"));
                    auction.setStartTime(LocalDateTime.now().minusDays(1));
                    auction.setEndTime(LocalDateTime.now().plusHours(6));
                    auction.setStatus("ENDING_SOON");
                    auction.setSellerId("MSTC-VEH");
                    auction.setSellerName("MSTC Vehicle Auctions");
                    auction.setViewCount(89);
                    auction.setWatcherCount(15);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=2", true)));
                    auction.setTags(Arrays.asList("vehicles", "car", "hatchback"));
                    auction.setLocation("New Delhi");
                    break;
                case 3:
                    auction.setTitle("Office Furniture Set - 20 Desks & Chairs");
                    auction.setDescription("Complete office furniture lot from government office renovation");
                    auction.setCategoryId("furniture");
                    auction.setStartingPrice(new BigDecimal("75000"));
                    auction.setCurrentPrice(new BigDecimal("95000"));
                    auction.setBidIncrement(new BigDecimal("2000"));
                    auction.setStartTime(LocalDateTime.now().minusDays(3));
                    auction.setEndTime(LocalDateTime.now().plusDays(4));
                    auction.setSellerId("GOV-BLR");
                    auction.setSellerName("Central Government Surplus");
                    auction.setViewCount(156);
                    auction.setWatcherCount(23);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=3", true)));
                    auction.setTags(Arrays.asList("furniture", "office", "government"));
                    auction.setLocation("Bangalore, Karnataka");
                    break;
                case 4:
                    auction.setTitle("Industrial Generator 100 KVA");
                    auction.setDescription("Kirloskar diesel generator, 2018 model, excellent condition");
                    auction.setCategoryId("industrial");
                    auction.setStartingPrice(new BigDecimal("350000"));
                    auction.setCurrentPrice(new BigDecimal("385000"));
                    auction.setBidIncrement(new BigDecimal("10000"));
                    auction.setStartTime(LocalDateTime.now().minusDays(1));
                    auction.setEndTime(LocalDateTime.now().plusDays(2));
                    auction.setSellerId("seller4");
                    auction.setSellerName("Industrial Auctions");
                    auction.setViewCount(67);
                    auction.setWatcherCount(8);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=4", true)));
                    auction.setTags(Arrays.asList("industrial", "generator", "machinery"));
                    auction.setLocation("Chennai, Tamil Nadu");
                    break;
                case 5:
                    auction.setTitle("Samsung 55 inch 4K Smart TV");
                    auction.setDescription("Brand new Samsung QLED TV, sealed box with full warranty");
                    auction.setCategoryId("electronics");
                    auction.setStartingPrice(new BigDecimal("55000"));
                    auction.setCurrentPrice(new BigDecimal("52000"));
                    auction.setBidIncrement(new BigDecimal("1000"));
                    auction.setStartTime(LocalDateTime.now().minusHours(8));
                    auction.setEndTime(LocalDateTime.now().plusHours(16));
                    auction.setStatus("ENDING_SOON");
                    auction.setSellerId("seller5");
                    auction.setSellerName("ElectroMart");
                    auction.setViewCount(203);
                    auction.setWatcherCount(45);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=5", true)));
                    auction.setTags(Arrays.asList("electronics", "tv", "samsung"));
                    auction.setLocation("Hyderabad, Telangana");
                    break;
                case 6:
                    auction.setTitle("Vintage Teak Wood Dining Set");
                    auction.setDescription("Antique teak dining table with 6 chairs, excellent craftsmanship");
                    auction.setCategoryId("furniture");
                    auction.setStartingPrice(new BigDecimal("45000"));
                    auction.setCurrentPrice(new BigDecimal("58000"));
                    auction.setBidIncrement(new BigDecimal("2000"));
                    auction.setStartTime(LocalDateTime.now().minusDays(2));
                    auction.setEndTime(LocalDateTime.now().plusDays(5));
                    auction.setSellerId("seller6");
                    auction.setSellerName("Antique Collections");
                    auction.setViewCount(78);
                    auction.setWatcherCount(12);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=6", true)));
                    auction.setTags(Arrays.asList("furniture", "antique", "teak"));
                    auction.setLocation("Kolkata, West Bengal");
                    break;
                case 7:
                    auction.setTitle("MacBook Pro 13 inch M1");
                    auction.setDescription("Like new MacBook Pro with M1 chip, 256GB storage, minimal usage");
                    auction.setCategoryId("electronics");
                    auction.setStartingPrice(new BigDecimal("85000"));
                    auction.setCurrentPrice(new BigDecimal("92000"));
                    auction.setBidIncrement(new BigDecimal("2000"));
                    auction.setStartTime(LocalDateTime.now().minusDays(1));
                    auction.setEndTime(LocalDateTime.now().plusDays(3));
                    auction.setSellerId("seller7");
                    auction.setSellerName("Apple Store Reseller");
                    auction.setViewCount(145);
                    auction.setWatcherCount(34);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=7", true)));
                    auction.setTags(Arrays.asList("electronics", "laptop", "apple"));
                    auction.setLocation("Pune, Maharashtra");
                    break;
                case 8:
                    auction.setTitle("Royal Enfield Classic 350");
                    auction.setDescription("2021 model Royal Enfield, 8,500 km, gunmetal grey color");
                    auction.setCategoryId("vehicles");
                    auction.setStartingPrice(new BigDecimal("140000"));
                    auction.setCurrentPrice(new BigDecimal("148000"));
                    auction.setBidIncrement(new BigDecimal("2000"));
                    auction.setStartTime(LocalDateTime.now().minusHours(4));
                    auction.setEndTime(LocalDateTime.now().plusDays(1));
                    auction.setSellerId("seller8");
                    auction.setSellerName("Bike Bazaar");
                    auction.setViewCount(91);
                    auction.setWatcherCount(19);
                    auction.setImages(Arrays.asList(new AuctionImage("https://picsum.photos/600/400?random=8", true)));
                    auction.setTags(Arrays.asList("vehicles", "motorcycle", "royal-enfield"));
                    auction.setLocation("Jaipur, Rajasthan");
                    break;
            }
            
            // Add source URLs for each auction
            AuctionSourceService.SourceInfo auctionSource = sourceService.getSourceForAuction(
                auction.getCategoryId(), 
                auction.getSellerId()
            );
            auction.setSourceUrl(auctionSource.url);
            auction.setSourcePdfUrl(auctionSource.pdfUrl);
            
            auctions.add(auction);
        }
        
        return auctions;
    }
}
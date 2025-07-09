package com.auctionaggregator.auction.mapper;

import com.auctionaggregator.auction.dto.AuctionCreateDTO;
import com.auctionaggregator.auction.dto.AuctionDTO;
import com.auctionaggregator.auction.dto.AuctionUpdateDTO;
import com.auctionaggregator.auction.entity.Auction;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuctionMapper {
    
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "bidCount", expression = "java(auction.getBids() != null ? auction.getBids().size() : 0)")
    @Mapping(target = "imageUrls", expression = "java(mapImageUrls(auction))")
    AuctionDTO toDTO(Auction auction);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bids", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "history", ignore = true)
    Auction toEntity(AuctionCreateDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(AuctionUpdateDTO dto, @MappingTarget Auction auction);
    
    default List<String> mapImageUrls(Auction auction) {
        if (auction.getImages() == null || auction.getImages().isEmpty()) {
            return List.of();
        }
        return auction.getImages().stream()
            .map(img -> img.getUrl())
            .collect(Collectors.toList());
    }
}
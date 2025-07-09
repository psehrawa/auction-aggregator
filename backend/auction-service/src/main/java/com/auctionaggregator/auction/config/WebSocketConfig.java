package com.auctionaggregator.auction.config;

import com.auctionaggregator.auction.websocket.AuctionWebSocketHandler;
import com.auctionaggregator.auction.websocket.BidWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final AuctionWebSocketHandler auctionWebSocketHandler;
    private final BidWebSocketHandler bidWebSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(auctionWebSocketHandler, "/ws/auctions/{auctionId}")
                .setAllowedOrigins("*")
                .withSockJS();
        
        registry.addHandler(bidWebSocketHandler, "/ws/bids")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
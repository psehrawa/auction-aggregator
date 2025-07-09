package com.auctionaggregator.auction.websocket;

import com.auctionaggregator.auction.dto.BidUpdateMessage;
import com.auctionaggregator.auction.entity.Bid;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
@Slf4j
public class BidWebSocketHandler extends TextWebSocketHandler {
    
    private final ObjectMapper objectMapper;
    
    // Map of auctionId to set of WebSocket sessions
    private final Map<String, Set<WebSocketSession>> auctionSessions = new ConcurrentHashMap<>();
    
    // Map of sessionId to auctionId for cleanup
    private final Map<String, String> sessionAuctions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String auctionId = extractAuctionId(session);
        if (auctionId != null) {
            auctionSessions.computeIfAbsent(auctionId, k -> new CopyOnWriteArraySet<>()).add(session);
            sessionAuctions.put(session.getId(), auctionId);
            log.info("WebSocket connection established for auction: {} session: {}", auctionId, session.getId());
            
            // Send initial connection confirmation
            sendMessage(session, BidUpdateMessage.builder()
                .type("CONNECTION")
                .message("Connected to auction " + auctionId)
                .build());
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String auctionId = sessionAuctions.remove(session.getId());
        if (auctionId != null) {
            Set<WebSocketSession> sessions = auctionSessions.get(auctionId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    auctionSessions.remove(auctionId);
                }
            }
        }
        log.info("WebSocket connection closed for session: {} status: {}", session.getId(), status);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages if needed (e.g., heartbeat, subscription changes)
        log.debug("Received message: {} from session: {}", message.getPayload(), session.getId());
    }
    
    public void broadcastBidUpdate(String auctionId, Bid bid) {
        BidUpdateMessage message = BidUpdateMessage.builder()
            .type("BID_PLACED")
            .auctionId(auctionId)
            .bidId(bid.getId())
            .bidderId(maskBidderId(bid.getBidderId()))
            .amount(bid.getAmount())
            .timestamp(bid.getBidTime())
            .build();
        
        broadcastToAuction(auctionId, message);
    }
    
    public void broadcastAuctionUpdate(String auctionId, String updateType, Object data) {
        BidUpdateMessage message = BidUpdateMessage.builder()
            .type(updateType)
            .auctionId(auctionId)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
        
        broadcastToAuction(auctionId, message);
    }
    
    private void broadcastToAuction(String auctionId, BidUpdateMessage message) {
        Set<WebSocketSession> sessions = auctionSessions.get(auctionId);
        if (sessions != null && !sessions.isEmpty()) {
            sessions.parallelStream().forEach(session -> {
                if (session.isOpen()) {
                    try {
                        sendMessage(session, message);
                    } catch (Exception e) {
                        log.error("Error sending message to session: {}", session.getId(), e);
                    }
                }
            });
            log.info("Broadcasted {} update to {} sessions for auction: {}", 
                message.getType(), sessions.size(), auctionId);
        }
    }
    
    private void sendMessage(WebSocketSession session, Object message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }
    
    private String extractAuctionId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return null;
    }
    
    private String maskBidderId(String bidderId) {
        if (bidderId == null || bidderId.length() < 8) {
            return "****";
        }
        return bidderId.substring(0, 4) + "****";
    }
    
    public int getActiveConnectionsCount(String auctionId) {
        Set<WebSocketSession> sessions = auctionSessions.get(auctionId);
        return sessions != null ? sessions.size() : 0;
    }
    
    public int getTotalActiveConnections() {
        return sessionAuctions.size();
    }
}
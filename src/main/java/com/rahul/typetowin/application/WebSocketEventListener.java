package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.ChatMessage;
import com.rahul.typetowin.application.dto.GameRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomService roomService;

    // Map to track which player is in which room
    private final ConcurrentHashMap<String, String> sessionToRoom = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToPlayer = new ConcurrentHashMap<>();
    // Track kicked players to avoid duplicate messages
    private final ConcurrentHashMap<String, Boolean> kickedPlayers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        String roomId = sessionToRoom.get(sessionId);
        String playerId = sessionToPlayer.get(sessionId);
        
        if (roomId != null && playerId != null) {
            // Check if this player was kicked - if so, don't send leave message
            boolean wasKicked = kickedPlayers.remove(playerId);
            
            if (!wasKicked) {
                GameRoom room = roomService.getRoom(roomId);
                if (room != null) {
                    String playerName = room.getPlayerById(playerId) != null ? 
                        room.getPlayerById(playerId).getName() : "Unknown";
                    
                    roomService.removePlayerFromRoom(roomId, playerId);
                    
                    // Send notification to remaining players
                    ChatMessage leaveMessage = new ChatMessage(
                        playerId, 
                        playerName, 
                        playerName + " left the room", 
                        ChatMessage.MessageType.PLAYER_LEFT
                    );
                    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", leaveMessage);
                    
                    // Update player list
                    GameRoom updatedRoom = roomService.getRoom(roomId);
                    if (updatedRoom != null) {
                        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/players", updatedRoom.getPlayers());
                    }
                }
            }
            
            // Clean up session tracking
            sessionToRoom.remove(sessionId);
            sessionToPlayer.remove(sessionId);
        }
    }

    public void trackPlayerSession(String sessionId, String roomId, String playerId) {
        sessionToRoom.put(sessionId, roomId);
        sessionToPlayer.put(sessionId, playerId);
    }
    
    public void markPlayerAsKicked(String playerId) {
        kickedPlayers.put(playerId, true);
    }
}
package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class MultiPlayerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @Autowired
    private GameTimerService gameTimerService;

    @PostMapping("/api/room/create")
    @ResponseBody
    public GameRoom createRoom(@RequestBody RoomRequest request) {
        GameRoom room = roomService.createRoom(request.getPlayerName());
        
        // Send notification to room
        ChatMessage joinMessage = new ChatMessage(
            room.getOwnerId(), 
            request.getPlayerName(), 
            request.getPlayerName() + " created the room", 
            ChatMessage.MessageType.PLAYER_JOINED
        );
        messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId() + "/chat", joinMessage);
        
        return room;
    }

    @PostMapping("/api/room/join")
    @ResponseBody
    public GameRoom joinRoom(@RequestBody RoomRequest request) {
        GameRoom room = roomService.joinRoom(request.getRoomId(), request.getPlayerName());
        
        // Get the newly added player
        Player newPlayer = room.getPlayers().stream()
            .filter(p -> p.getName().equals(request.getPlayerName()))
            .reduce((first, second) -> second) // Get the last one (newly added)
            .orElse(null);
        
        if (newPlayer != null) {
            // Send room update to all players
            messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId() + "/players", room.getPlayers());
            
            // Send chat notification
            ChatMessage joinMessage = new ChatMessage(
                newPlayer.getId(), 
                request.getPlayerName(), 
                request.getPlayerName() + " joined the room", 
                ChatMessage.MessageType.PLAYER_JOINED
            );
            messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId() + "/chat", joinMessage);
        }
        
        return room;
    }

    @MessageMapping("/room/{roomId}/chat")
    @SendTo("/topic/room/{roomId}/chat")
    public ChatMessage sendChatMessage(@DestinationVariable String roomId, ChatMessage message) {
        message.setType(ChatMessage.MessageType.CHAT);
        return message;
    }

    @MessageMapping("/room/{roomId}/track")
    public void trackSession(@DestinationVariable String roomId, SessionTrackRequest request,
                           org.springframework.messaging.simp.SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        webSocketEventListener.trackPlayerSession(sessionId, roomId, request.getPlayerId());
    }

    @MessageMapping("/room/{roomId}/start")
    public void startGame(@DestinationVariable String roomId, RoomRequest request) {
        try {
            GameRoom room = roomService.getRoom(roomId);
            if (room != null) {
                // Find player by name (since we're using name as identifier from frontend)
                Player player = room.getPlayers().stream()
                    .filter(p -> p.getName().equals(request.getPlayerName()))
                    .findFirst()
                    .orElse(null);
                    
                if (player != null && player.isOwner()) {
                    roomService.startGame(roomId, player.getId());
                    
                    // Send game start notification
                    ChatMessage startMessage = new ChatMessage(
                        player.getId(), 
                        player.getName(), 
                        "Game starting in 3 seconds...", 
                        ChatMessage.MessageType.GAME_STARTED
                    );
                    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", startMessage);
                    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/gameStart", room);
                    
                    // Start countdown timer
                    gameTimerService.startCountdown(roomId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error starting game: " + e.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/kick")
    public void kickPlayer(@DestinationVariable String roomId, KickRequest request) {
        try {
            GameRoom room = roomService.getRoom(roomId);
            if (room != null) {
                // Find owner by name
                Player owner = room.getPlayers().stream()
                    .filter(p -> p.getName().equals(request.getOwnerName()))
                    .findFirst()
                    .orElse(null);
                    
                if (owner != null && owner.isOwner()) {
                    // Find player to kick and get their name before removal
                    Player playerToKick = room.getPlayerById(request.getPlayerIdToKick());
                    if (playerToKick != null) {
                        String kickedPlayerName = playerToKick.getName();
                        
                        // Mark player as kicked to prevent duplicate disconnect message
                        webSocketEventListener.markPlayerAsKicked(request.getPlayerIdToKick());
                        
                        roomService.kickPlayer(roomId, owner.getId(), request.getPlayerIdToKick());
                        
                        // Send updated player list
                        GameRoom updatedRoom = roomService.getRoom(roomId);
                        if (updatedRoom != null) {
                            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/players", updatedRoom.getPlayers());
                        }
                        
                        // Send chat notification
                        ChatMessage kickMessage = new ChatMessage(
                            owner.getId(), 
                            owner.getName(), 
                            kickedPlayerName + " was kicked from the room", 
                            ChatMessage.MessageType.PLAYER_LEFT
                        );
                        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", kickMessage);
                        
                        // Notify the kicked player specifically
                        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/kicked/" + request.getPlayerIdToKick(), "You have been kicked from the room");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error kicking player: " + e.getMessage());
        }
    }

    @GetMapping("/api/room/{roomId}")
    @ResponseBody
    public GameRoom getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }
}
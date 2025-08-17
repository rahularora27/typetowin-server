package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.GameRoom;
import com.rahul.typetowin.application.dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    
    @Autowired
    private QuoteService quoteService;

    public GameRoom createRoom(String playerName) {
        String roomId = generateRoomId();
        String playerId = UUID.randomUUID().toString();
        
        Player owner = new Player(playerId, playerName, true);
        GameRoom room = new GameRoom(roomId, playerId);
        room.addPlayer(owner);
        
        rooms.put(roomId, room);
        return room;
    }

    public GameRoom joinRoom(String roomId, String playerName) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        
        if (room.isGameStarted()) {
            throw new RuntimeException("Game already started");
        }

        String playerId = UUID.randomUUID().toString();
        Player player = new Player(playerId, playerName, false);
        room.addPlayer(player);
        
        return room;
    }

    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void removePlayerFromRoom(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room != null) {
            room.removePlayer(playerId);
            
            // If room becomes empty, remove it
            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomId);
            }
            // If owner leaves, assign new owner or remove room
            else if (room.getOwnerId().equals(playerId)) {
                if (!room.getPlayers().isEmpty()) {
                    Player newOwner = room.getPlayers().get(0);
                    newOwner.setOwner(true);
                    room.setOwnerId(newOwner.getId());
                }
            }
        }
    }

    public void startGame(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        
        if (!room.getOwnerId().equals(playerId)) {
            throw new RuntimeException("Only room owner can start the game");
        }
        
        // Generate a quote for the game
        String quote = quoteService.getRandomQuote(50).getText();
        room.setQuote(quote);
        room.setGameStarted(true);
    }

    public void kickPlayer(String roomId, String ownerId, String playerIdToKick) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        
        if (!room.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Only room owner can kick players");
        }
        
        if (ownerId.equals(playerIdToKick)) {
            throw new RuntimeException("Room owner cannot kick themselves");
        }
        
        Player playerToKick = room.getPlayerById(playerIdToKick);
        if (playerToKick == null) {
            throw new RuntimeException("Player not found in room");
        }
        
        room.removePlayer(playerIdToKick);
    }

    private String generateRoomId() {
        // Generate a short 6-character room ID
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
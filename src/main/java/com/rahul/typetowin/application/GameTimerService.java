package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.GameRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GameTimerService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final ConcurrentHashMap<String, ScheduledFuture<?>> countdownTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> gameTimerTasks = new ConcurrentHashMap<>();
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private RoomService roomService;
    
    @PostConstruct
    private void init() {
        // Set up circular dependency
        roomService.setGameTimerService(this);
    }
    
    public void startCountdown(String roomId) {
        // Cancel any existing countdown for this room
        cancelCountdown(roomId);
        
        GameRoom room = roomService.getRoom(roomId);
        if (room == null) return;
        
        ScheduledFuture<?> countdownTask = scheduler.scheduleAtFixedRate(() -> {
            GameRoom currentRoom = roomService.getRoom(roomId);
            if (currentRoom == null) {
                cancelCountdown(roomId);
                return;
            }
            
            int currentTime = currentRoom.getCountdownTime();
            
            // Broadcast current countdown time
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/countdown", currentTime);
            
            if (currentTime <= 0) {
                // Countdown finished, start the game
                roomService.updateCountdown(roomId, 0);
                cancelCountdown(roomId);
                
                // Set game time to the configured duration
                roomService.updateGameTimer(roomId, currentRoom.getGameDuration());
                startGameTimer(roomId);
                
                // Notify game actually started
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/gameStarted", currentRoom);
                return;
            }
            
            // Decrement countdown
            roomService.updateCountdown(roomId, currentTime - 1);
            
        }, 0, 1, TimeUnit.SECONDS);
        
        countdownTasks.put(roomId, countdownTask);
    }
    
    public void startGameTimer(String roomId) {
        // Cancel any existing timer for this room
        cancelGameTimer(roomId);
        
        GameRoom room = roomService.getRoom(roomId);
        if (room == null) return;
        
        ScheduledFuture<?> timerTask = scheduler.scheduleAtFixedRate(() -> {
            GameRoom currentRoom = roomService.getRoom(roomId);
            if (currentRoom == null) {
                cancelGameTimer(roomId);
                return;
            }
            
            int currentTime = currentRoom.getGameTime();
            
            // Broadcast current game time
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/gameTimer", currentTime);
            
            if (currentTime <= 0) {
                // Game finished
                roomService.updateGameTimer(roomId, 0);
                cancelGameTimer(roomId);
                
                // Notify game ended
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/gameEnded", currentRoom);
                return;
            }
            
            // Decrement timer
            roomService.updateGameTimer(roomId, currentTime - 1);
            
        }, 0, 1, TimeUnit.SECONDS);
        
        gameTimerTasks.put(roomId, timerTask);
    }
    
    public void cancelCountdown(String roomId) {
        ScheduledFuture<?> task = countdownTasks.remove(roomId);
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }
    
    public void cancelGameTimer(String roomId) {
        ScheduledFuture<?> task = gameTimerTasks.remove(roomId);
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }
    
    public void cancelAllTimers(String roomId) {
        cancelCountdown(roomId);
        cancelGameTimer(roomId);
    }
}
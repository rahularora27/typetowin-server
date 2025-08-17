package com.rahul.typetowin.application.dto;

import java.time.LocalDateTime;

public class ChatMessage {
    private String playerId;
    private String playerName;
    private String message;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT, PLAYER_JOINED, PLAYER_LEFT, GAME_STARTED
    }

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(String playerId, String playerName, String message, MessageType type) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
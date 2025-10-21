package com.rahul.typetowin.application.dto;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private String roomId;
    private List<Player> players;
    private String ownerId;
    private boolean gameStarted;
    private String quote;
    private boolean countdownActive;
    private int countdownTime;
    private boolean gameActive;
    private int gameTime;

    public GameRoom() {
        this.players = new ArrayList<>();
        this.gameStarted = false;
    }

    public GameRoom(String roomId, String ownerId) {
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.players = new ArrayList<>();
        this.gameStarted = false;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(String playerId) {
        this.players.removeIf(player -> player.getId().equals(playerId));
    }

    public Player getPlayerById(String playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public boolean isCountdownActive() {
        return countdownActive;
    }

    public void setCountdownActive(boolean countdownActive) {
        this.countdownActive = countdownActive;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }
}

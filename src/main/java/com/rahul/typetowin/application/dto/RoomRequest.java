package com.rahul.typetowin.application.dto;

public class RoomRequest {
    private String playerName;
    private String roomId;

    public RoomRequest() {}

    public RoomRequest(String playerName, String roomId) {
        this.playerName = playerName;
        this.roomId = roomId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
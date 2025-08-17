package com.rahul.typetowin.application.dto;

public class KickRequest {
    private String ownerName;
    private String playerIdToKick;

    public KickRequest() {}

    public KickRequest(String ownerName, String playerIdToKick) {
        this.ownerName = ownerName;
        this.playerIdToKick = playerIdToKick;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPlayerIdToKick() {
        return playerIdToKick;
    }

    public void setPlayerIdToKick(String playerIdToKick) {
        this.playerIdToKick = playerIdToKick;
    }
}
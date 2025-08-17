package com.rahul.typetowin.application.dto;

public class SessionTrackRequest {
    private String playerId;

    public SessionTrackRequest() {}

    public SessionTrackRequest(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
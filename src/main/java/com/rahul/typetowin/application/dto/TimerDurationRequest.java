package com.rahul.typetowin.application.dto;

public class TimerDurationRequest {
    private String playerName;
    private int duration;

    public TimerDurationRequest() {
    }

    public TimerDurationRequest(String playerName, int duration) {
        this.playerName = playerName;
        this.duration = duration;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
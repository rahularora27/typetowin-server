package com.rahul.typetowin.application.dto;

public class GameResult {
    private String sessionId;
    private String quote;
    private int correctChars;
    private int incorrectChars;
    private int timer;

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getCorrectChars() {
        return correctChars;
    }

    public void setCorrectChars(int correctChars) {
        this.correctChars = correctChars;
    }

    public int getIncorrectChars() {
        return incorrectChars;
    }

    public void setIncorrectChars(int incorrectChars) {
        this.incorrectChars = incorrectChars;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}

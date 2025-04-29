package com.rahul.typetowin.application.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game_results")
public class GameResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "correct_chars")
    private int correctChars;
    @Column(name = "incorrect_chars")
    private int incorrectChars;
    @Column(name = "timer")
    private int timer;

    private Instant createdAt = Instant.now();

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

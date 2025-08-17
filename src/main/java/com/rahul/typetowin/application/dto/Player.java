package com.rahul.typetowin.application.dto;

public class Player {
    private String id;
    private String name;
    private boolean isOwner;

    public Player() {}

    public Player(String id, String name, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.isOwner = isOwner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
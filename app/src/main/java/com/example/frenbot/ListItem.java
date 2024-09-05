package com.example.frenbot;

public class ListItem {
    String userId;
    String name;
    String profilePic;

    public ListItem(String userId, String name, String profilePic) {
        this.userId = userId;
        this.name = name;
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return userId;
    }
}

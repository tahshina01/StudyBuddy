package com.example.frenbot;

public class MessageItem {
    String message;
    String timeStamp;
    String uuid;

    public MessageItem(String message, String timeStamp, String uuid) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.uuid = uuid;
    }
}

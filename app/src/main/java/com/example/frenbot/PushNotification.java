package com.example.frenbot;

public class PushNotification {
    private NotificationData data;
    private String to;

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public PushNotification(NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }
}

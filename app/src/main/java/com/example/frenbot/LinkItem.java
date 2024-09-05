package com.example.frenbot;

public class LinkItem {
    private String title;
    private String link;
    private String linkId;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getLinkId() {
        return linkId;
    }

    public LinkItem(String title, String link, String linkId) {
        this.title = title;
        this.link = link;
        this.linkId = linkId;
    }
}

package com.example.frenbot;

public class eventmodel {
    String title;
    String time;
    String place;
    String uuid;
    String desc;
    String note;


    public eventmodel(String title, String time, String place, String uuid, String desc, String note) {
        this.title = title;
        this.time = time;
        this.place = place;
        this.uuid = uuid;
        this.desc = desc;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }


}

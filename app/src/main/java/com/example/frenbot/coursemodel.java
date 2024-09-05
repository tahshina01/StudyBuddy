package com.example.frenbot;

public class coursemodel {
    String course;
    String id;
    String instructor;
    String uuid;
    String desc;
    boolean archive;
    String sharedBy;

    public coursemodel(String course, String id, String instructor, String uuid, String desc, boolean archive, String sharedBy) {
        this.course = course;
        this.id = id;
        this.instructor = instructor;
        this.uuid = uuid;
        this.desc = desc;
        this.archive = archive;
        this.sharedBy = sharedBy;
    }

    public coursemodel(String course, String id, String instructor, String uuid, String desc, boolean archive) {
        this.course = course;
        this.id = id;
        this.instructor = instructor;
        this.uuid = uuid;
        this.desc = desc;
        this.archive = archive;
    }

    public String getinstructor() {
        return instructor;
    }

    public String getid() {
        return id;
    }

    public String getcourse() {
        return course;
    }

    public String getDesc() {
        return desc;
    }
    public String getUuid() {
        return uuid;
    }

    public boolean getArchive() {
        return archive;
    }
}

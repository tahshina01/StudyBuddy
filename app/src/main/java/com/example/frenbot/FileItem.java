package com.example.frenbot;

public class FileItem {
    private String title;
    private String downloadUri;
    private String uuid;
    private String fileType;

    public String getTitle() {
        return title;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFileType() {
        return fileType;
    }

    public FileItem(String title, String downloadUri, String uuid, String fileType) {
        this.title = title;
        this.downloadUri = downloadUri;
        this.uuid = uuid;
        this.fileType = fileType;
    }
}

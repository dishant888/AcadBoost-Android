package com.example.acadboost.Model;

public class VideoModel {

    private String ID,title,objectURL;

    public VideoModel(String ID, String title, String objectURL) {
        this.ID = ID;
        this.title = title;
        this.objectURL = objectURL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectURL() {
        return objectURL;
    }

    public void setObjectURL(String objectURL) {
        this.objectURL = objectURL;
    }
}

package com.example.greenaura;

public class Resource {
    private String title;
    private String description;
    private String photoUrl;


    public Resource(String title, String description, String photoUrl) {
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}


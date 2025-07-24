package com.example.greenaura;

public class RecyclingGuide {
    private String binType;
    private int imageResId;

    public RecyclingGuide(String binType, int imageResId) {
        this.binType = binType;
        this.imageResId = imageResId;
    }

    public String getBinType() {
        return binType;
    }

    public void setBinType(String binType) {
        this.binType = binType;
    }

    public int getImageResId() {
        return imageResId;
    }
}


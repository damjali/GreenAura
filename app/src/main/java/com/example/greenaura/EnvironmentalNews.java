package com.example.greenaura;

public class EnvironmentalNews {
    private String imageUrl;
    private Class<?> newsActivityClass;

    public EnvironmentalNews(String imageUrl, Class<?> newsActivityClass) {
        this.imageUrl = imageUrl;
        this.newsActivityClass = newsActivityClass;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Class<?> getNewsActivityClass() {
        return newsActivityClass;
    }
}

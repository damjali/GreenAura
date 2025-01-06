package com.example.greenaura;

import java.util.Map;

public class Resource {
    private String ResourceId;
    private String ResourceHeader;
    private String ResourceDescription;
    private String ResourcePhoto;
    private String ResourceLink;
    private String ResourcePostDate;
    private int ResourceUpvote;
    private Map<String, Boolean> UserUpvotes;

    public Resource(String resourceId, String title, String description, String photoUrl, String link, String date, int likes, Map<String, Boolean> userUpvotes) {
        this.ResourceHeader = title;
        this.ResourceDescription = description;
        this.ResourcePhoto = photoUrl;
        this.ResourceLink = link;
        this.ResourcePostDate = date;
        this.ResourceUpvote = likes;
        this.ResourceId = resourceId;
        this.UserUpvotes = userUpvotes;
    }

    public Map<String, Boolean> getUserUpvotes() {
        return UserUpvotes;
    }

    public void setUserUpvotes(Map<String, Boolean> userUpvotes) {
        UserUpvotes = userUpvotes;
    }

    public String getResourceId() {
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public int getResourceUpvote() {
        return ResourceUpvote;
    }

    public void setResourceUpvote(int resourceUpvote) {
        ResourceUpvote = resourceUpvote;
    }

    public String getResourcePhoto() {
        return ResourcePhoto;
    }

    public String getResourceHeader() {
        return ResourceHeader;
    }

    public void setResourceHeader(String resourceHeader) {
        ResourceHeader = resourceHeader;
    }

    public String getResourceDescription() {
        return ResourceDescription;
    }

    public void setResourceDescription(String resourceDescription) {
        ResourceDescription = resourceDescription;
    }

    public void setResourcePhoto(String resourcePhoto) {
        ResourcePhoto = resourcePhoto;
    }

    public String getResourceLink() {
        return ResourceLink;
    }

    public void setResourceLink(String resourceLink) {
        ResourceLink = resourceLink;
    }

    public String getResourcePostDate() {
        return ResourcePostDate;
    }

    public void setResourcePostDate(String resourcePostDate) {
        ResourcePostDate = resourcePostDate;
    }
}


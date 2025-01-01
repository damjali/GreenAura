package com.example.greenaura;

public class Resource {
    private String ResourceHeader;
    private String ResourceDescription;
    private String ResourcePhoto;
    private String ResourceLink;
    private String ResourcePostDate;
    private int ResourceUpvote;

    public Resource(String title, String description, String photoUrl, String link, String date, int likes) {
        this.ResourceHeader = title;
        this.ResourceDescription = description;
        this.ResourcePhoto = photoUrl;
        this.ResourceLink = link;
        this.ResourcePostDate = date;
        this.ResourceUpvote = likes;
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


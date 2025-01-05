package com.example.greenaura;

public class Upvote {
    private String userId;

    // Default constructor required for Firestore
    public Upvote() {}

    // Constructor to initialize the object
    public Upvote(String userId) {
        this.userId = userId;
    }

    // Getter for Firestore to serialize the data
    public String getUserId() {
        return userId;
    }

    // Setter for Firestore to deserialize the data
    public void setUserId(String userId) {
        this.userId = userId;
    }
}

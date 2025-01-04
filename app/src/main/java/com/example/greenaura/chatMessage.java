package com.example.greenaura;

public class chatMessage {
    private String message;
    private boolean isUserMessage;

    public chatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}

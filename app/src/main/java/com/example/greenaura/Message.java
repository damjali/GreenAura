package com.example.greenaura;

public class Message {
    private final String text;
    private final boolean isBot;

    public Message(String text, boolean isBot) {
        this.text = text;
        this.isBot = isBot;
    }

    public String getText() {
        return text;
    }

    public boolean isBot() {
        return isBot;
    }
}

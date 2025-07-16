package com.msgapp.localchat;

public class Message {
    private String username;
    private String content;
    private long timestamp;
    private String messageId;

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.messageId = generateMessageId();
    }

    private String generateMessageId() {
        return username + "_" + timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
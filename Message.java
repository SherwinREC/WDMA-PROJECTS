package com.petadoption.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private int petId;
    private String content;
    private Timestamp sentAt;
    
    // Join fields
    private String senderUsername;
    private String petName;

    public Message() {}

    public Message(int senderId, int receiverId, int petId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.petId = petId;
        this.content = content;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
}

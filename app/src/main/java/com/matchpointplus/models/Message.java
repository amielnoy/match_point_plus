package com.matchpointplus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Message {
    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("is_sent_by_me")
    private boolean isSentByMe;

    @SerializedName("receiver_id")
    private String receiverId;

    @SerializedName("created_at")
    private String createdAt; // Supabase returns ISO string

    public Message() {
    }

    public Message(String text, boolean isSentByMe, String receiverId) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.receiverId = receiverId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isSentByMe() { return isSentByMe; }
    public void setSentByMe(boolean sentByMe) { isSentByMe = sentByMe; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

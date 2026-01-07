package com.matchpointplus.models;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Message {
    @SerializedName("id")
    public String id;

    @SerializedName("text")
    public String text;

    @SerializedName("is_sent_by_me")
    public boolean isSentByMe;

    @SerializedName("created_at")
    public long timestamp;

    @SerializedName("receiver_id")
    public String receiverId;

    public Message() {
        this.id = UUID.randomUUID().toString();
    }

    public Message(String text, boolean isSentByMe, String receiverId) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.receiverId = receiverId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public boolean isSentByMe() { return isSentByMe; }
    public long getTimestamp() { return timestamp; }
    public String getReceiverId() { return receiverId; }
}

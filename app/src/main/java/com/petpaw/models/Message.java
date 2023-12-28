package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message implements FirebaseDoc {
    private String uid;
    private String senderId;
    private String receiverId;
    private String content;
    private Date timeStamp;

    public Message(String uid, String senderId, String receiverId, String content, Date timeStamp) {
        this.uid = uid;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("senderId", senderId);
        doc.put("receiverId", receiverId);
        doc.put("content", content);
        doc.put("timeStamp", timeStamp);
        return doc;
    }
}

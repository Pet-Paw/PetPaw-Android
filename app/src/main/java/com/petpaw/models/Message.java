package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message implements FirebaseDoc {
    public static final String MESSAGES = "messages";
    private String uid;
    private String senderId;
    private String content;
    private Date sentAt;

    public Message() {}

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("content", content);
        doc.put("sentAt", sentAt);
        doc.put("senderId", senderId);
        return doc;
    }


}

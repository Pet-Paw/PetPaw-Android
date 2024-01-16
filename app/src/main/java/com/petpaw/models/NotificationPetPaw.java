package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationPetPaw implements FirebaseDoc {
    private String content;
    private String from;
    private String to;
    private Date createdDate;
    private boolean isNew;

    private String title;
    public NotificationPetPaw() {
    }

    public NotificationPetPaw(String title, String content, String from, String to, Date createdDate, boolean isNew) {
        this.title = title;
        this.content = content;
        this.from = from;
        this.to = to;
        this.createdDate = createdDate;
        this.isNew = isNew;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("content", content);
        doc.put("from", from);
        doc.put("to", to);
        doc.put("createdDate", createdDate);
        doc.put("isNew", isNew);
        return doc;
    }
}

package com.petpaw.models;

import com.google.firebase.Firebase;
import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.Map;

public class User implements FirebaseDoc {
    private String uid;
    private String name;
    private String imageUrl;

    public User(String uid, String name, String imageUrl) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("imageUrl", imageUrl);
        return doc;
    }
}

package com.petpaw.models;

import com.google.firebase.Firebase;
import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.Map;

public class User implements FirebaseDoc {
    public static final String USERS = "users";
    private String uid;
    private String name;
    private String imageURL;

    public User(String uid, String name, String imageUrl) {
        this.uid = uid;
        this.name = name;
        this.imageURL = imageUrl;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("imageURL", imageURL);
        return doc;
    }
}

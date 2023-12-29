package com.petpaw.models;

import com.google.firebase.Firebase;
import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.Map;

public class User implements FirebaseDoc {
    public static final String USERS = "users";
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String imageURL;

    public User() {
    }

    public User(String uid, String username, String email, String phoneNumber, String address) {
        this.uid= uid;
        this.name = username;
        this.email = email;
        this.phone = phoneNumber;
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("imageURL", imageURL);
        return doc;
    }
}

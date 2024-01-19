package com.petpaw.models;

import com.google.firebase.Firebase;
import com.google.type.LatLng;
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
    private String physicalDeviceToken;
    private LatLng location;

    private String country;
    private String banReason;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public User() {
    }

    public User(String uid, String username, String email, String phoneNumber, String address,
                String imageURL,
                String physicalDeviceToken) {
        this.uid= uid;
        this.name = username;
        this.email = email;
        this.phone = phoneNumber;
        this.address = address;
        this.imageURL = imageURL;
        this.physicalDeviceToken = physicalDeviceToken;
        this.banReason = null;
    }

    public User(String uid, String username, String email, String phoneNumber, String address, String physicalDeviceToken) {
        this.uid= uid;
        this.name = username;
        this.email = email;
        this.phone = phoneNumber;
        this.address = address;
        this.physicalDeviceToken = physicalDeviceToken;
        this.banReason = null;
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


    public String getPhysicalDeviceToken() {
        return physicalDeviceToken;
    }

    public void setPhysicalDeviceToken(String physicalDeviceToken) {
        this.physicalDeviceToken = physicalDeviceToken;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("imageURL", imageURL);
        return doc;
    }


}

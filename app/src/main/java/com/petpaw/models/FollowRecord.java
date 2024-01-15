package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.Map;

public class FollowRecord implements FirebaseDoc {
    private String uid;
    private String followerUid;
    private String followingUid;

    public FollowRecord() {
    }

//    public FollowRecord(String followerUid, String followingUid) {
//        this.followerUid = followerUid;
//        this.followingUid = followingUid;
//    }

    public FollowRecord(String uid, String followerUid, String followingUid) {
        this.uid = uid;
        this.followerUid = followerUid;
        this.followingUid = followingUid;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("followerUid", followerUid);
        doc.put("followingUid", followingUid);
        doc.put("uid", uid);
        return doc;
    }

    public String getFollowerUid() {
        return followerUid;
    }

    public void setFollowerUid(String followerUid) {
        this.followerUid = followerUid;
    }

    public String getFollowingUid() {
        return followingUid;
    }

    public void setFollowingUid(String followingUid) {
        this.followingUid = followingUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

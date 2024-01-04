package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.Map;

public class FollowRecord implements FirebaseDoc {
    private String followerUid;
    private String followingUid;

    public FollowRecord() {
    }

    public FollowRecord(String followerUid, String followingUid) {
        this.followerUid = followerUid;
        this.followingUid = followingUid;
    }

    @Override
    public Map<String, Object> toDoc() {
        return null;
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
}

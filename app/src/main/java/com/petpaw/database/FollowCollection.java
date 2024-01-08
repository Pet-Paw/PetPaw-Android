package com.petpaw.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowCollection {
    public interface Callback {
        void onCallback(List<FollowRecord> followRecords);
    }

    private static FollowCollection instance;

    private FirebaseFirestore firebaseFirestore;

    private CollectionReference followsCollectionReference;
    private static final String COLLECTION_NAME = "follows";

    public static FollowCollection newInstance() {
        if (instance == null) {
            instance = new FollowCollection();
        }
        return instance;
    }

    private FollowCollection() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        followsCollectionReference = firebaseFirestore.collection(COLLECTION_NAME);
    }

    public void addFollowing(User currentUser, User followingUser ) {
        followsCollectionReference.add(
                new FollowRecord(currentUser.getUid(), followingUser.getUid())
        );
    }

    public void addFollower(User currentUser, User followerUser ) {
        followsCollectionReference.add(
                new FollowRecord(followerUser.getUid(), currentUser.getUid())
        );
    }

    public List<User> getAllFollowings(User currentUser, Callback callback) {
        List<User> followings = new ArrayList<>();
        followsCollectionReference.whereEqualTo("followerUid", currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);

                for (FollowRecord followRecord : followRecords) {
                    String followingUid = followRecord.getFollowingUid();
                    UserCollection.newInstance().getUser(followingUid,
                            new UserCollection.Callback() {
                                @Override
                                public void onCallback(List<User> users) {

                                }
                                @Override
                                public void onCallBack(User user) {
                                    followings.add(user);
                                }
                            }
                    );
                }
                callback.onCallback(followRecords);
            }
        });
        return followings;
    }

    public List<User> getAllFollowers(User currentUser, Callback callback) {
        List<User> followers = new ArrayList<>();
        followsCollectionReference.whereEqualTo("followingUid", currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);

                for (FollowRecord followRecord : followRecords) {
                    String followerUid = followRecord.getFollowerUid();
                    UserCollection.newInstance().getUser(followerUid,
                            new UserCollection.Callback() {
                                @Override
                                public void onCallback(List<User> users) {

                                }
                                @Override
                                public void onCallBack(User user) {
                                    followers.add(user);
                                }
                            }
                    );
                }
                callback.onCallback(followRecords);
            }
        });
        return followers;
    }

}

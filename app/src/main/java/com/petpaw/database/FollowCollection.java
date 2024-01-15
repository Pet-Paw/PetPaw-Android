package com.petpaw.database;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FollowCollection {
    public interface Callback {
        void onCallback(List<FollowRecord> followRecords);
        void  onCallBackGetUsers(List<User> users);

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

    public void addFollowing(String currentUserUid, String followingUserUid) {
        FollowRecord followRecord = new FollowRecord("", currentUserUid, followingUserUid);

        followsCollectionReference.add(followRecord.toDoc())
            .addOnSuccessListener(
            documentReference -> {
                String uid = documentReference.getId();
                followRecord.setUid(uid);
                followsCollectionReference.document(uid).set(followRecord);
            }
        );
    }

    public void removeFollowing(String currentUserUid, String followingUserUid) {
        CompletableFuture<String> future = new CompletableFuture<>();
        followsCollectionReference.whereEqualTo("followerUid", currentUserUid)
                .whereEqualTo("followingUid", followingUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);
                        if (followRecords.size() > 0) {
                            FollowRecord followRecord = followRecords.get(0);
                            followsCollectionReference.document(followRecord.getUid()).delete();
                            future.complete(followRecord.getUid());
                        } else {
                            future.complete(null);
                        }

                        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                                future);
                        allOf.thenRun(() -> {
                            String followRecordId = future.getNow(null);
                            if (followRecordId != null) {
                                Toast.makeText(null, "Follow record id: " + followRecordId, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }


    public void getAllFollowings(User currentUser, Callback callback) {
        List<User> followings = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        followsCollectionReference.whereEqualTo("followerUid", currentUser.getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);

                    for (FollowRecord followRecord : followRecords) {
                        String followingUid = followRecord.getFollowingUid();

                        CompletableFuture<Void> future = new CompletableFuture<>();
                        futures.add(future);

                        UserCollection.newInstance().getUser(followingUid,
                            new UserCollection.Callback() {
                                @Override
                                public void onCallback(List<User> users) {
                                    // Not used in this case
                                }
                                @Override
                                public void onCallBack(User user) {
                                    if (user != null) {
                                        followings.add(user);
                                    }
                                    future.complete(null);
                                }
                            });
                    }
                    CompletableFuture<Void> allOf = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]));

                    allOf.thenRun(() -> callback.onCallBackGetUsers(followings));
                }
            });
    }


    public void getAllFollowers(User currentUser, Callback callback) {
        List<User> followings = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        followsCollectionReference.whereEqualTo("followingUid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);

                        for (FollowRecord followRecord : followRecords) {
                            String followerUid = followRecord.getFollowerUid();

                            CompletableFuture<Void> future = new CompletableFuture<>();
                            futures.add(future);

                            UserCollection.newInstance().getUser(followerUid,
                                    new UserCollection.Callback() {
                                        @Override
                                        public void onCallback(List<User> users) {
                                            // Not used in this case
                                        }
                                        @Override
                                        public void onCallBack(User user) {
                                            if (user != null) {
                                                followings.add(user);
                                            }
                                            future.complete(null);
                                        }
                                    });
                        }
                        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                                futures.toArray(new CompletableFuture[0]));

                        allOf.thenRun(() -> callback.onCallBackGetUsers(followings));
                    }
                });
    }

}

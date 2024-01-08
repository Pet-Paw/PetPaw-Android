package com.petpaw.database;

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

//    public void  getAllFollowings(User currentUser, Callback callback) {
//        List<User> followings = new ArrayList<>();
//        followsCollectionReference.whereEqualTo("followerUid", currentUser.getUid()).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<FollowRecord> followRecords = task.getResult().toObjects(FollowRecord.class);
//
//                for (FollowRecord followRecord : followRecords) {
//                    String followingUid = followRecord.getFollowingUid();
//                    UserCollection.newInstance().getUser(followingUid,
//                            new UserCollection.Callback() {
//                                @Override
//                                public void onCallback(List<User> users) {
//
//                                }
//                                @Override
//                                public void onCallBack(User user) {
//                                    if (user != null)
//                                        followings.add(user);
//                                }
//                            }
//                    );
//                }
//                callback.onCallBackGetUsers(followings);
//            }
//        });
//    }

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

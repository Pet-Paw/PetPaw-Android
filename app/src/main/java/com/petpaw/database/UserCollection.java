package com.petpaw.database;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserCollection {
    public interface Callback {
        void onCallback(List<User> users);

        void onCallBack(User user);
    }
    private static final String COLLECTION_NAME = "users";
    private static UserCollection instance;
    private FirebaseFirestore firebaseFirestore;
    protected CollectionReference usersCollectionReference;


    public static final ExecutorService databaseWriteExecutor =
            Executors.newSingleThreadExecutor();

    public static UserCollection newInstance() {
        if (instance == null) {
            instance = new UserCollection();
        }
        return instance;
    }

    private UserCollection() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionReference = firebaseFirestore.collection(COLLECTION_NAME);
    }
    public void createUser(User user) {
        usersCollectionReference.document(user.getUid()).set(user);
    }

    public void getUser(String uid, Callback callback) {
        usersCollectionReference.document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                callback.onCallBack(user);

            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }
}

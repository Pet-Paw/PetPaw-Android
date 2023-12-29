package com.petpaw.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserCollection {
    public interface Callback {
        void onCallback(List<User> users);
    }
    private static final String COLLECTION_NAME = "users";
    private static UserCollection instance;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersCollectionReference;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

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
        usersCollectionReference.add(user);
    }
}

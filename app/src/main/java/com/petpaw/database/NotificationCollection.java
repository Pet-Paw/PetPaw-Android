package com.petpaw.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.NotificationPetPaw;

import java.util.Collections;
import java.util.List;

public class NotificationCollection {
    public interface Callback {
        void onCallback(List<NotificationPetPaw> notifications);
    }

    private static final String COLLECTION_NAME = "Notifications";
    private static NotificationCollection instance;
    private FirebaseFirestore firebaseFirestore;
    protected CollectionReference notificationsCollectionReference;

    public static NotificationCollection newInstance() {
        if (instance == null) {
            instance = new NotificationCollection();
        }
        return instance;
    }

    private NotificationCollection() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        notificationsCollectionReference = firebaseFirestore.collection(COLLECTION_NAME);
    }

    public void createNotification(NotificationPetPaw notification) {
        notificationsCollectionReference.add(notification);

    }

    public void getAccountNotification(String uid, Callback callback) {
        notificationsCollectionReference.whereEqualTo("to", uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<NotificationPetPaw> notifications = task.getResult().toObjects(NotificationPetPaw.class);
                sortNewCurrentNotification(notifications);
                callback.onCallback(notifications);
            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }

    public void getTotalNewNotification(String uid, Callback callback) {
        notificationsCollectionReference.whereEqualTo("to", uid).whereEqualTo("new", true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<NotificationPetPaw> notifications = task.getResult().toObjects(NotificationPetPaw.class);
                callback.onCallback(notifications);


            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }

    private void sortNewCurrentNotification(List<NotificationPetPaw> notifications) {
        notifications.sort((o1, o2) -> {
            if (o1.getCreatedDate() == null || o2.getCreatedDate() == null)
                return 0;
            return o2.getCreatedDate().compareTo(o1.getCreatedDate());
        });
    }
}

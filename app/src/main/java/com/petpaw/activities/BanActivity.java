package com.petpaw.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.petpaw.R;

public class BanActivity extends AppCompatActivity {
    ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);

        TextView banReason = findViewById(R.id.ban_reason);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("users");
        DocumentReference docRef = usersRef.document(uid);

//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document != null && document.exists()) {
//                    String banReasonText = (String) document.get("banReason");
//                    banReason.setText(banReasonText);
//                }
//            }
//        });

        Button btnBack = findViewById(R.id.ban_log_out);
        btnBack.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(BanActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        listener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "get failed with " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    String banReasonText = (String) value.get("banReason");
                    banReason.setText(banReasonText);


                    if (banReasonText == null || banReasonText.isEmpty()) {
                        // banReason is null
                        Intent intent = new Intent(BanActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.remove();
    }
}
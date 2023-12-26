package com.petpaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.Post;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //create a new sample post instant and add it to firestore
        // Create a sample Post
        Post post = new Post();
        post.setTitle("My Pet");
        post.setContent("My pet did something cute today!");
        post.setAuthor("John Doe");
        post.setPetId("pet123");

        // Test connection to firebase ----------------------------------------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("CreatePostActivity", "Adding post to Firestore");
//        db.collection("Posts")
//                .add(post)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show();
//                    Log.d("CreatePostActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("CreatePostActivity", "Error adding document", e);
//                });

        DocumentReference docRef;
        docRef = db.collection("Posts").document("IzktKwdA0H1mKdswX0r0");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "Document data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "Get failed with ", task.getException());
                }
            }
        });





    }
}
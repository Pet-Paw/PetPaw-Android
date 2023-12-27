package com.petpaw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import static io.grpc.Context.LazyStorage.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.models.Post;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petpaw.databinding.ActivityCreatePostBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity {
    ActivityCreatePostBinding binding;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);


        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createPostSelectImageButton.setOnClickListener(v -> {
            selectImage();
        });

        binding.createPostUploadButton.setOnClickListener(v -> {
            //upload image to firebase storage --------------------------------------------
//            uploadImage();

            //create a new sample post instant and add it to firestore
            // Create a sample Post
            Post post = new Post();
            post.setTitle("My Pet");
            post.setContent("My pet did something cute today!");
            post.setAuthorId("John Doe");
            post.setPetId("pet123");

            // Test connection to firebase ----------------------------------------
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("CreatePostActivity", "Adding post to Firestore");
            db.collection("Posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show();
                        Log.d("CreatePostActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CreatePostActivity", "Error adding document", e);
                    });
        });



//        DocumentReference docRef;
//        docRef = db.collection("Posts").document("IzktKwdA0H1mKdswX0r0");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d("TAG", "Document data: " + document.getData());
//                    } else {
//                        Log.d("TAG", "No such document");
//                    }
//                } else {
//                    Log.d("TAG", "Get failed with ", task.getException());
//                }
//            }
//        });
    }


    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);

    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        //Change the time to VN time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.TAIWAN);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        binding.createPostImageView.setImageURI(null);
                        Toast.makeText(CreatePostActivity.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(CreatePostActivity.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            binding.createPostImageView.setImageURI(imageUri);


        }
    }

}
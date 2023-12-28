package com.petpaw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import static io.grpc.Context.LazyStorage.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.models.Post;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petpaw.databinding.ActivityCreatePostBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity {
    ActivityCreatePostBinding binding;
    private Uri imageUri;
    private StorageReference storageReference;
    private StorageReference getImageStorageReference;
    ProgressDialog progressDialog;

//    *** The imageId is also the post id ***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

//        Bind the select button to the selectImage function
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createPostSelectImageButton.setOnClickListener(v -> {
            selectImage();
        });

// ------------Check if the intent has a postId, if it does, then it is an edit post -------------------
        if(postId != null) {
            binding.createPostUploadButton.setVisibility(View.GONE);
            Log.d("CreatePostActivity", "PostId: " + postId);

//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            DocumentReference docRef;
//            docRef = db.collection("Posts").document(postId);
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            Log.d("TAG", "Document data: " + document.getData());
//
//                        } else {
//                            Log.d("TAG", "No such document");
//                        }
//                    } else {
//                        Log.d("TAG", "Get failed with ", task.getException());
//                    }
//                }
//            });

            //get the image from storage and set it to the image view
            getImageStorageReference = FirebaseStorage.getInstance().getReference().child("postImages/"+postId);

            try {
                final File localFile = File.createTempFile(postId, "jpg");
                getImageStorageReference.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                binding.createPostImageView.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("CreatePostActivity", "Error getting image from storage", e);
                            }
                        }
                );
            }catch (Exception e){
                Log.e("CreatePostActivity", "Error creating temp file", e);
            }



        }else { //if it does not have a postId, then it is a new post ------------------------------
            Log.d("CreatePostActivity", "PostId is null");
            binding.createPostButtonLinearLayout.setVisibility(View.GONE);
            binding.createPostUploadButton.setOnClickListener(v -> {
                //upload image to firebase storage --------------------------------------------

                //create a new sample post instant and add it to firestore
                // Create a sample Post
                Post post = new Post();
                post.setDateModified(new Date());
                post.setContent(binding.createPostDescriptionEditText.getText().toString().trim());
                post.setAuthorId("John Doe");
                post.setPetId("pet123");

                // Test connection to firebase ----------------------------------------
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d("CreatePostActivity", "Adding post to Firestore");
                db.collection("Posts")
                        .add(post)
                        .addOnSuccessListener(documentReference -> {
                            uploadImage(documentReference.getId());

                            Toast.makeText(this, "Post Successfully Created", Toast.LENGTH_SHORT).show();
                            Log.d("CreatePostActivity", "DocumentSnapshot written with ID: " + documentReference.getId());

//                        ******** Remember to add the post id to the user data also ********

                        })
                        .addOnFailureListener(e -> {
                            Log.e("CreatePostActivity", "Error adding document", e);
                        });
            });
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    private void uploadImage(String postId) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        //Change the time to VN time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.TAIWAN);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("postImages/"+postId);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.createPostImageView.setImageURI(null);
                        binding.createPostImageView.setVisibility(View.GONE);
                        binding.createPostDescriptionEditText.setText("");
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("CreatePostActivity", "Image uploaded successfully");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("CreatePostActivity", "Error uploading image", e);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.createPostImageView.setImageURI(imageUri);
            binding.createPostImageView.setVisibility(View.VISIBLE);
        }
    }

}
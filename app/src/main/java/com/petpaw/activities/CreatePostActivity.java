package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import static io.grpc.Context.LazyStorage.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.petpaw.R;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.models.Post;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    ActivityCreatePostBinding binding;

    private Uri imageUri;
//    Uri uploadImageUri;
    private StorageReference storageReference;
//    private StorageReference getImageStorageReference;
    private DocumentReference postDocRef;
    private boolean isSelectNewImage = false;

    ProgressDialog progressDialog;

//    *** The imageId is also the post id ***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        Log.d("CreatePostActivity", "Current User Id: " + currentUserId);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

//        Bind the select button to the selectImage function
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

// ------------Check if the intent has a postId, if it does, then it is an edit post -----------------
        if(postId != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Fetching Post Detail....");
            progressDialog.show();

            binding.createPostUploadButton.setVisibility(View.GONE);
            Log.d("CreatePostActivity", "PostId: " + postId);

            binding.createPostSelectImageButton.setOnClickListener(v -> {
                selectImage();
                isSelectNewImage = true;
            });

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference postRef;
            postRef = db.collection("Posts").document(postId);
            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            postDocRef = postRef;

//                           ----------- Load image with the URL then the description -----------
                            String imageUrl = document.getString("imageURL");
//                            Log.d("CreatePostActivity", "Image URL Open New: " + imageUrl);
                            Picasso.get()
                                    .load(imageUrl)
                                    .tag(System.currentTimeMillis())
                                    .into(binding.createPostImageView, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            binding.createPostImageView.setVisibility(View.VISIBLE);
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            Log.d("TAG", "Load image successfully");
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("TAG", "Load image failed");
                                        }
                                    });

                            binding.createPostDescriptionEditText.setText(document.getString("content"));

//                            ------- Check valid input --------
                            binding.createPostSaveButton.setOnClickListener(v -> {
                                boolean isValid = true;
                                List<String> tags = new ArrayList<>();

                                String description = binding.createPostDescriptionEditText.getText().toString();

                                if(!description.isEmpty()){
                                    String[] parts = description.split("#");
                                    for(int i=1; i<parts.length; i++) {
                                        String part = parts[i];
                                        if(!part.isEmpty() && part.charAt(0) != '#') {
                                            tags.add(part);
                                        }
                                    }
                                    Log.d("CreatePostActivity", "Tags: " + tags.toString());
                                }

//                            -------- update if valid input --------

                                if(isValid) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("content", description);
                                    data.put("tags", tags);
                                    data.put("dateModified", new Date());
                                    data.put("modified", true);

                                    postRef.update(data)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                                if(isSelectNewImage) {
                                                    uploadImage(postId,true);
                                                } else {
                                                    finish();
                                                }
                                            })      .addOnFailureListener(e->{
                                                Log.e("CreatePostActivity", "Error updating document", e);
                                            });
                                }
                            });

                            storageReference = FirebaseStorage.getInstance().getReference("postImages/"+postId);

                            binding.createPostDeleteButton.setOnClickListener(v -> {
                                postRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                storageReference.delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "Image deleted from storage!");
                                                            }
                                                        });

                                                Log.d("TAG", "Document deleted from Firestore!");
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.w("TAG", "Failed to delete image and/or document", exception);
                                            }
                                        });
                            });

                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this, "Post Detail not found", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "No document found");
                            finish();
                        }
                    } else {
                        Log.d("TAG", "Get failed with ", task.getException());
                    }
                }
            });

        }
        else { //----------if it does not have a postId, then it is creating a new post --------------
            binding.createPostSelectImageButton.setOnClickListener(v -> {
                selectImage();
            });

            Log.d("CreatePostActivity", "PostId is null");
            binding.createPostButtonLinearLayout.setVisibility(View.GONE);
            binding.createPostUploadButton.setOnClickListener(v -> {
                boolean isValid = true;
                List<String> tags = new ArrayList<>();

                String description = binding.createPostDescriptionEditText.getText().toString();

                if(!description.isEmpty()){
                    String[] parts = description.split("#");
                    for(int i=1; i<parts.length; i++) {
                        String part = parts[i];
                        if(!part.isEmpty() && part.charAt(0) != '#') {
                            tags.add(part);
                        }
                    }
                    Log.d("CreatePostActivity", "Tags: " + tags.toString());
                }

                if(isValid){
                    Post post = new Post();
                    post.setDateModified(new Date());
                    post.setContent(description);
                    post.setAuthorId(currentUserId);
//                    **** Still haven't had correct petId ****
                    post.setPetId("pet123");
                    post.setTags(tags);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Log.d("CreatePostActivity", "Adding post to Firestore");
                    db.collection("Posts")
                            .add(post)
                            .addOnSuccessListener(documentReference -> {
                                String id = documentReference.getId();
                                DocumentReference postRef = db.collection("Posts").document(id);
                                postRef.update("postId", id)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                        })      .addOnFailureListener(e->{
                                    Log.e("CreatePostActivity", "Error updating document", e);
                                });

                                postDocRef = documentReference;
                                uploadImage(id, false);

                                Toast.makeText(this, "Post Successfully Created", Toast.LENGTH_SHORT).show();

//                        ******** Need to update relevant class Later on ********
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error creating post", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }

        binding.createPostBackButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    private void uploadImage(String postId, boolean isEditImage) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading to Database....");
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
                        // Get download URL after upload completes
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("CreatePostActivity", "Image URL: " + uri);

                                    if(isEditImage){
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        postDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreatePostActivity", "Update image path successfully");

                                                    finish();
//                                                    startActivity(getIntent());

//                                                    recreate();

                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreatePostActivity", "Error updating document", e);
                                                });
                                        finish();
                                    }else{
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        postDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreatePostActivity", "DocumentSnapshot successfully updated!");
                                                finish();
                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreatePostActivity", "Error updating document", e);
                                                });
                                    }
                                });
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
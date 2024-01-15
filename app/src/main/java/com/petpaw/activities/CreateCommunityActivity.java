package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.petpaw.R;
import com.petpaw.databinding.ActivityCreateCommunityBinding;
import com.petpaw.databinding.ActivityCreatePostBinding;
import com.petpaw.models.Community;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateCommunityActivity extends AppCompatActivity {
    ActivityCreateCommunityBinding binding;
    ProgressDialog progressDialog;
    private StorageReference storageReference;
    Boolean defaultPic;
    private DocumentReference comDocRef;

    private Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onStart(){
        binding.backBtn.setOnClickListener(v -> finish());
        binding.selectImageBtn.setOnClickListener(v -> selectImage());
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/petpaw-1d5a6.appspot.com/o/communityImages%2Fdefault_community_picture.jpg?alt=media&token=02941e55-1875-4eb3-8e9e-35d11ef657df")
                .tag(System.currentTimeMillis())
                .into(binding.previewImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "Load image successfully");
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e("TAG", "Load image failed");
                            }
                        });
        binding.createCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.communityNameEditText.getText().length() != 0){
                    Community com = new Community(binding.communityNameEditText.getText().toString(), binding.descriptionEditText.getText().toString(), auth.getCurrentUser().getUid(), "https://firebasestorage.googleapis.com/v0/b/petpaw-1d5a6.appspot.com/o/communityImages%2Fdefault_community_picture.jpg?alt=media&token=02941e55-1875-4eb3-8e9e-35d11ef657df", new ArrayList<>());
                    db.collection("Communities")
                            .add(com.toDoc())
                            .addOnSuccessListener(documentReference -> {
                                String id = documentReference.getId();
//                        comDocRef = documentReference;
//                        comDocRef.update("id", id);
//                        db.collection("users").document(auth.getCurrentUser().getUid())
//                                .update("pets", FieldValue.arrayUnion(id));
                                if(!defaultPic) {
                                    uploadImage(id, false);
                                } else {
                                    finish();
                                }

                                Toast.makeText(getBaseContext(), "Community Successfully Created", Toast.LENGTH_SHORT).show();
//
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getBaseContext(), "Error Creating Community", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        super.onStart();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
        defaultPic = false;
    }

    private void uploadImage(String petId, boolean isEditImage) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading to Database....");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("communityImages/" + petId);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.previewImageView.setImageURI(null);
                        binding.communityNameEditText.setText("");
                        binding.descriptionEditText.setText("");
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("CreateCommunityActivity", "Image uploaded successfully");
                        // Get download URL after upload completes
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("CreateCommunityActivity", "Image URL: " + uri);

                                    if(isEditImage){
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        comDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreateCommunityActivity", "Update image path successfully");

                                                    finish();
//                                                    startActivity(getIntent());

//                                                    recreate();

                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreateCommunityActivity", "Error updating document", e);
                                                });
                                        finish();
                                    }else{
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        comDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CreateCommunityActivity", "DocumentSnapshot successfully updated!");
                                                    finish();
                                                })      .addOnFailureListener(e->{
                                                    Log.e("CreateCommunityActivity", "Error updating document", e);
                                                });
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("CreateCommunityActivity", "Error uploading image", e);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.previewImageView.setImageURI(imageUri);
        }
    }
}
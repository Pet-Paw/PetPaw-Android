package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.petpaw.R;
import com.petpaw.databinding.ActivityAddPetBinding;
import com.petpaw.models.Pet;
import com.petpaw.models.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPetActivity extends AppCompatActivity {

    ActivityAddPetBinding binding;

    private Uri imageUri;

    private StorageReference storageReference;

    private DocumentReference petDocRef;
    private String ownerId;

    private Boolean defaultPic = true;
    ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.selectImageBtn.setOnClickListener(v -> selectImage());
        

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            ownerId = currentUser.getUid();
        }

        binding.addPetBtn.setOnClickListener(v -> {
            boolean isValid = true;
            //check validation

            if(binding.petNameEditText.getText().length() != 0 && binding.petBreedEditText.getText().length() != 0 && binding.petWeightEditText.getText().length() != 0 && binding.petAgeEditText.getText().length() != 0){
                Pet pet = new Pet(ownerId, "", binding.petNameEditText.getText().toString(), Integer.parseInt(binding.petAgeEditText.getText().toString()), binding.petBreedEditText.getText().toString(), Integer.parseInt(binding.petWeightEditText.getText().toString()), "https://firebasestorage.googleapis.com/v0/b/petpaw-1d5a6.appspot.com/o/petImages%2Fdefault_pet_avatar.png?alt=media&token=d2be6754-a0e0-43a4-a4a1-c20ae8873c3f", new ArrayList<>());

                Log.d("AddPetActivity", "Adding pet to Firestore");
                db.collection("Pets")
                        .add(pet.toDoc())
                        .addOnSuccessListener(documentReference -> {
                            String id = documentReference.getId();
                            petDocRef = documentReference;
                            petDocRef.update("id", id);
                            db.collection("users").document(auth.getCurrentUser().getUid())
                                            .update("pets", FieldValue.arrayUnion(id));
                            if(!defaultPic) {
                                uploadImage(id, false);
                            } else {
                                finish();
                            }

                            Toast.makeText(getBaseContext(), "Pet Successfully Added", Toast.LENGTH_SHORT).show();
//
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getBaseContext(), "Error Adding Pet", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "You must fill in all pet information!", Toast.LENGTH_SHORT).show();
            }
        });

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

        storageReference = FirebaseStorage.getInstance().getReference("petImages/" + petId);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.previewPetImageView.setImageURI(null);
                        binding.previewPetImageView.setVisibility(View.GONE);
                        binding.petAgeEditText.setText("");
                        binding.petNameEditText.setText("");
                        binding.petBreedEditText.setText("");
                        binding.petWeightEditText.setText("");
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("AddPetActivity", "Image uploaded successfully");
                        // Get download URL after upload completes
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("AddPetActivity", "Image URL: " + uri);

                                    if(isEditImage){
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        petDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("AddPetActivity", "Update image path successfully");

                                                    finish();
//                                                    startActivity(getIntent());

//                                                    recreate();

                                                })      .addOnFailureListener(e->{
                                                    Log.e("AddPetActivity", "Error updating document", e);
                                                });
                                        finish();
                                    }else{
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageURL", uri.toString());
                                        petDocRef.update(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("AddPetActivity", "DocumentSnapshot successfully updated!");
                                                    finish();
                                                })      .addOnFailureListener(e->{
                                                    Log.e("AddPetActivity", "Error updating document", e);
                                                });
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("AddPetActivity", "Error uploading image", e);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();

            binding.previewPetImageView.setImageURI(imageUri);
            binding.previewPetImageView.setVisibility(View.VISIBLE);
        }
    }
}
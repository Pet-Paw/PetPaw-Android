package com.petpaw.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petpaw.R;
import com.petpaw.databinding.ActivityEditPetProfileBinding;
import com.petpaw.databinding.ActivityPetProfileBinding;
import com.squareup.picasso.Picasso;

public class EditPetProfileActivity extends AppCompatActivity {
    private ActivityEditPetProfileBinding binding;
    private Uri petAvatarImageUri;

    private String petId, avatarUrl, name, age, breed, weight;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet_profile);

        binding = ActivityEditPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.petName.setVisibility(View.VISIBLE);
        binding.petAge.setVisibility(View.VISIBLE);
        binding.petBreed.setVisibility(View.VISIBLE);
        binding.petWeight.setVisibility(View.VISIBLE);

        binding.editNameText.setVisibility(View.INVISIBLE);
        binding.editAgeText.setVisibility(View.INVISIBLE);
        binding.editBreedText.setVisibility(View.INVISIBLE);
        binding.editWeightText.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");
        avatarUrl = intent.getStringExtra("petAvatar");
        name = intent.getStringExtra("petName");
        age = intent.getStringExtra("petAge");
        breed = intent.getStringExtra("petBreed");
        weight = intent.getStringExtra("petWeight");

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.get().load(avatarUrl).into(binding.petAvatar);
        } else {
            binding.petAvatar.setImageResource(R.drawable.default_pet_avatar);
        }

        binding.petName.setText(name);
        binding.editNameText.setText(name);
        binding.petAge.setText(age);
        binding.editAgeText.setText(age);
        binding.petBreed.setText(breed);
        binding.editBreedText.setText(breed);
        binding.petWeight.setText(weight);
        binding.editWeightText.setText(weight);

        binding.editAvatarBtn.setOnClickListener(v -> {
            selectImage();
        });
        binding.editInformationBtn.setOnClickListener(v -> {
            binding.petName.setVisibility(View.INVISIBLE);
            binding.petAge.setVisibility(View.INVISIBLE);
            binding.petBreed.setVisibility(View.INVISIBLE);
            binding.petWeight.setVisibility(View.INVISIBLE);

            binding.editNameText.setVisibility(View.VISIBLE);
            binding.editAgeText.setVisibility(View.VISIBLE);
            binding.editBreedText.setVisibility(View.VISIBLE);
            binding.editWeightText.setVisibility(View.VISIBLE);
        });

        binding.saveBtn.setOnClickListener(v-> {
            savePetInfo();
            finish();
        });

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void savePetInfo() {
        String updatedName = binding.editNameText.getText().toString();
        String updatedAge = binding.editAgeText.getText().toString();
        String updatedBreed = binding.editBreedText.getText().toString();
        String updatedWeight = binding.editWeightText.getText().toString();

        if (updatedName.equals(name) && updatedAge.equals(age) && updatedBreed.equals(breed)
                && updatedWeight.equals(weight) && (petAvatarImageUri == null)) {
            finish();
        }else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Updating Pet Profile...");
            progressDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Update Firestore
            db.collection("Pets").document(petId).update("name", updatedName,
                            "age", Integer.parseInt(updatedAge),
                            "breed", updatedBreed,
                            "weight", Integer.parseInt(updatedWeight))
                    .addOnSuccessListener(aVoida->{
                        // Update Storage if avatar is changed
                        if (petAvatarImageUri != null) {
                            StorageReference avatarRef = FirebaseStorage.getInstance().getReference("petImages/" + petId);
                            avatarRef.putFile(petAvatarImageUri)
                                    .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String avatarUrl = uri.toString();
                                        db.collection("Pets").document(petId).update("imageURL", avatarUrl)
                                                .addOnSuccessListener(aVoid -> {
                                                    //setResult(Activity.RESULT_OK);
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    finish();
                                                });
                                    }));
                        }else{
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null && data.getData() != null) {
            petAvatarImageUri = data.getData();
            binding.petAvatar.setImageURI(petAvatarImageUri);
        }
    }
}
package com.petpaw.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.petpaw.R;
import com.petpaw.databinding.ActivityEditPetProfileBinding;
import com.petpaw.databinding.ActivityPetProfileBinding;
import com.squareup.picasso.Picasso;

public class EditPetProfileActivity extends AppCompatActivity {
    private ActivityEditPetProfileBinding binding;
    private Uri petAvatarImageUri;



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
        String petAvatarURL = intent.getStringExtra("avatarURL");
        String petName = intent.getStringExtra("name");
        String petAge = intent.getStringExtra("age");
        String petBreed = intent.getStringExtra("breed");
        String petWeight = intent.getStringExtra("weight");

        if (petAvatarURL != null) {
            Picasso.get().load(petAvatarURL).into(binding.petAvatar);
        }
        binding.petName.setText(petName);
        binding.editNameText.setText(petName);
        binding.petAge.setText(petAge);
        binding.editAgeText.setText(petAge);
        binding.petBreed.setText(petBreed);
        binding.editBreedText.setText(petBreed);
        binding.petWeight.setText(petWeight);
        binding.editWeightText.setText(petWeight);

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
            //savePetInfo();
            //setResult(Activity.RESULT_OK);
            //finish();
        });

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            petAvatarImageUri = data.getData();
            binding.petAvatar.setImageURI(petAvatarImageUri);
        }
    }
}
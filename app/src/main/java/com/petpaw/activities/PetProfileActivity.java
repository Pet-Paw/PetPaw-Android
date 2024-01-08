package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.petpaw.R;
import com.petpaw.databinding.ActivityEditProfileBinding;
import com.petpaw.databinding.ActivityPetProfileBinding;
import com.petpaw.databinding.FragmentProfileBinding;

public class PetProfileActivity extends AppCompatActivity {
    private ActivityPetProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.displayImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.displayImages.setClickable(false);
                binding.displayImages.setTypeface(null, Typeface.BOLD);
                binding.displayInfo.setClickable(true);
                binding.displayInfo.setTypeface(null, Typeface.NORMAL);
                binding.petImagesGridView.setVisibility(View.VISIBLE);
                binding.petInfoLayout.setVisibility(View.GONE);
            }
        });

        binding.displayInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.displayInfo.setClickable(false);
                binding.displayInfo.setTypeface(null, Typeface.BOLD);
                binding.displayImages.setClickable(true);
                binding.displayImages.setTypeface(null, Typeface.NORMAL);
                binding.petInfoLayout.setVisibility(View.VISIBLE);
                binding.petImagesGridView.setVisibility(View.GONE);
            }
        });

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetProfileActivity.this, EditPetProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}
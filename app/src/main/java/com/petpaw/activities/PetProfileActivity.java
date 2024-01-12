package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.databinding.ActivityEditProfileBinding;
import com.petpaw.databinding.ActivityPetProfileBinding;
import com.petpaw.databinding.FragmentProfileBinding;
import com.petpaw.models.Pet;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

public class PetProfileActivity extends AppCompatActivity {
    private ActivityPetProfileBinding binding;
    private Pet pet;
    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");

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
                intent.putExtra("petAvatar", pet.getImageURL());
                intent.putExtra("petName", pet.getName());
                intent.putExtra("petAge", String.valueOf(pet.getAge()));
                intent.putExtra("petBreed", pet.getBreed());
                intent.putExtra("petWeight", String.valueOf(pet.getWeight()));
                startActivity(intent);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        displayPetInfo();

    }

    private void displayPetInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Pets");
        Query query = usersCollection.whereEqualTo("id", petId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        pet = documentSnapshot.toObject(Pet.class);
                        Log.d("Pet Profile", "image URL: " + pet.getImageURL());
                        if (pet.getImageURL() != null && !(pet.getImageURL().isEmpty())) {
                            Picasso.get()
                                    .load(pet.getImageURL())
                                    .tag(System.currentTimeMillis())
                                    .into(binding.petAvatar, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("Pet Profile", "Load avatar successfully at " + System.currentTimeMillis());
                                        }
                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("Pet Profile", "Load image failed");
                                        }
                                    });
                        } else {
                            binding.petAvatar.setImageResource(R.drawable.cat_placeholder);
                        }
                        binding.petName.setText(pet.getName());
                        binding.petAge.setText(String.valueOf(pet.getAge()));
                        binding.petWeight.setText(String.valueOf(pet.getWeight()));
                        binding.petBreed.setText(pet.getBreed());
                        break;
                    }
                } else {
                    Log.e("Pet Profile", "Error getting pet data: ", task.getException());
                }
            }
        });
    }

}
package com.petpaw.fragments.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.EditPetProfileActivity;
import com.petpaw.activities.EmptyActivity;
import com.petpaw.adapters.PetGridAdapter;
import com.petpaw.databinding.FragmentPetProfileBinding;
import com.petpaw.databinding.FragmentProfileBinding;
import com.petpaw.models.Pet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PetProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentPetProfileBinding binding;
    private Pet pet;
    private String petId, ownerId;

    public PetProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param petId Parameter 1.
     * @param ownerId Parameter 2.
     * @return A new instance of fragment PetProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetProfileFragment newInstance(String petId, String ownerId) {
        PetProfileFragment fragment = new PetProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, petId);
        args.putString(ARG_PARAM2, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            petId = getArguments().getString(ARG_PARAM1);
            ownerId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetProfileBinding.inflate(inflater, container, false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser.getUid().equals(ownerId)) {
            // Current user is the owner
            binding.editBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            // Current user is not the owner
            binding.editBtn.setVisibility(View.GONE);
            binding.deleteBtn.setVisibility(View.GONE);
        }

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
                Intent intent = new Intent(requireContext(), EditPetProfileActivity.class);
                intent.putExtra("petId", pet.getId());
                intent.putExtra("petAvatar", pet.getImageURL() != null ? pet.getImageURL() : "");
                intent.putExtra("petName", pet.getName());
                intent.putExtra("petAge", String.valueOf(pet.getAge()));
                intent.putExtra("petBreed", pet.getBreed());
                intent.putExtra("petWeight", String.valueOf(pet.getWeight()));
                startActivity(intent);
            }
        });

        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before deleting
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Pet")
                        .setMessage("Are you sure you want to delete this pet?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePet();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    getActivity().findViewById(R.id.overlay_pet_fragment).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.profileFragmentLayout).setVisibility(View.VISIBLE);
                }
            }
        });

        displayPetImages();

        return binding.getRoot();
    }

    private void deletePet() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pets").document(petId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted
                    Toast.makeText(getContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to profile fragment
                    if (isAdded() && getActivity() != null) {
                        getActivity().findViewById(R.id.overlay_pet_fragment).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.profileFragmentLayout).setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getContext(), EmptyActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failures
                    Toast.makeText(getContext(), "Error deleting pet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ProfileFragment", "onResume");
        displayPetInfo();
    }

    private void displayPetImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsCollection = db.collection("Posts");
        Query query = postsCollection.whereArrayContains("petIdList", petId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> imageUrls = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageUrl = document.getString("imageURL");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrls.add(imageUrl);
                        }
                    }
                    binding.petImagesGridView.setAdapter(new PetGridAdapter(getContext(), imageUrls));
                } else {
                    Log.e("Pet Profile", "Error getting images: ", task.getException());
                }
            }
        });
    }


    private void displayPetInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsCollection = db.collection("Pets");
        Query query = petsCollection.whereEqualTo("id", petId);
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
                            binding.petAvatar.setImageResource(R.drawable.default_pet_avatar);
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
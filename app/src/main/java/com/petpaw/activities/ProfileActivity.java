package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.petpaw.R;
import com.petpaw.databinding.ActivityProfileBinding;
import com.petpaw.databinding.ActivitySignInBinding;
import com.petpaw.models.User;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding profileBinding;
    private User user;
    private String uid;
    private ImageView avatar;
    private TextView name, location, postNum, followerNum, followingNum, displayPosts, displayPets;
    private Button editBtn, addPetBtn;
    private GridView postsGridView;
    private RecyclerView petsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        avatar = profileBinding.avatar;
        name = profileBinding.name;
        location = profileBinding.location;
        postNum = profileBinding.postNum;
        followerNum = profileBinding.followerNum;
        followingNum = profileBinding.followingNum;
        displayPosts = profileBinding.displayPosts;
        displayPets = profileBinding.displayPets;
        editBtn = profileBinding.editBtn;
        addPetBtn = profileBinding.addPetBtn;
        postsGridView = profileBinding.postGridView;
        petsRecyclerView = profileBinding.petsRecyclerView;

        displayUserInfo();

        displayPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPosts.setClickable(false);
                displayPosts.setTypeface(null, Typeface.BOLD);
                displayPets.setClickable(true);
                displayPets.setTypeface(null, Typeface.NORMAL);
                postsGridView.setVisibility(View.VISIBLE);
                petsRecyclerView.setVisibility(View.GONE);
            }
        });

        displayPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPets.setClickable(false);
                displayPets.setTypeface(null, Typeface.BOLD);
                displayPosts.setClickable(true);
                displayPosts.setTypeface(null, Typeface.NORMAL);
                petsRecyclerView.setVisibility(View.VISIBLE);
                postsGridView.setVisibility(View.GONE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("avatarURL", user.getImageURL());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("address", user.getAddress());
                intent.putExtra("phone", user.getPhone());
                startActivity(intent);
                finish();
            }
        });

    }

    private void displayUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = firebaseFirestore.collection("users");
            Query query = usersCollection.whereEqualTo("uid", uid);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    name.setText(user.getName());
                                    location.setText(user.getAddress());
                                    break;
                                }
                            }
                        } else {
                            Log.e("ProfileActivity", "No user data found for UID: " + uid);
                        }
                    } else {
                        Log.e("ProfileActivity", "Error getting user data: ", task.getException());
                    }
                }
            });
        }
    }
}
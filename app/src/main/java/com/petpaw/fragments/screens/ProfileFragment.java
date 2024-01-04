package com.petpaw.fragments.screens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.EditProfileActivity;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int EDIT_PROFILE_REQUEST_CODE = 100;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private User user;
    private String uid;
    private ImageView avatar;
    private TextView name, location, postNum, followerNum, followingNum, displayPosts, displayPets;
    private Button editBtn, addPetBtn;
    private RecyclerView postsRecyclerView, petsRecyclerView;
    private List<Post> userPostList = new ArrayList<>();
    private List<User> userFollowingList = new ArrayList<>();
    private List<User> userFollowerList = new ArrayList<>();


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }
        displayUserInfo();
        getUserPosts();
        getUserFollowers();
        getUserFollowings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avatar = view.findViewById(R.id.profileAvatarImage);
        name = view.findViewById(R.id.profileName);
        location = view.findViewById(R.id.profileLocation);
        postNum = view.findViewById(R.id.profilePostNum);
        followerNum = view.findViewById(R.id.profileFollowerNum);
        followingNum = view.findViewById(R.id.profileFollowingNum);
        displayPosts = view.findViewById(R.id.profileDisplayPostsText);
        displayPets = view.findViewById(R.id.profileDisplayPetsText);
        editBtn = view.findViewById(R.id.profileEditBtn);
        addPetBtn = view.findViewById(R.id.profileAddPetBtn);
        postsRecyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        petsRecyclerView = view.findViewById(R.id.profilePetsRecyclerView);

        displayPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPosts.setClickable(false);
                displayPosts.setTypeface(null, Typeface.BOLD);
                displayPets.setClickable(true);
                displayPets.setTypeface(null, Typeface.NORMAL);
                postsRecyclerView.setVisibility(View.VISIBLE);
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
                postsRecyclerView.setVisibility(View.GONE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                intent.putExtra("avatarURL", user.getImageURL());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("address", user.getAddress());
                intent.putExtra("phone", user.getPhone());
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Activity activity = getActivity();
            if (activity != null) {
                ProfileFragment fragment = (ProfileFragment) ((FragmentActivity) activity).getSupportFragmentManager()
                        .findFragmentByTag("ProfileFragment");
                if (fragment != null) {
                    fragment.displayUserInfo();
                }
            }
        }
    }


    @Override
    public void onResume() {
        Log.d("TAG", "onResume: ");
        super.onResume();
        displayUserInfo();
    }

    private void displayUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users"); // Get reference to users collection
        Query query = usersCollection.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                                Picasso.get().load(user.getImageURL()).into(avatar);
                            } else {
                                avatar.setImageResource(R.drawable.default_avatar);
                            }
                            name.setText(user.getName());
                            location.setText(user.getAddress());
                            break;
                        }
                    }
                } else {
                    Log.e("ProfileFragment", "Error getting user data: ", task.getException());
                }
            }
        });
    }

    private void getUserPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
        Query query = postsRef.whereEqualTo("authorId", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userPostList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = document.toObject(Post.class);
                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId());
                        userPostList.add(postTemp);
                    }
                    // Check if the userPostList is empty and log
                    if (userPostList.isEmpty()) {
                        Log.d("ProfileFragment", "No posts found for the user.");
                    }

                    postNum.setText(userPostList.size() + "");
                    postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    postsRecyclerView.setAdapter(new PostListAdapter(requireContext(), userPostList));
                } else {
                    Log.e("ProfileFragment", "Error getting user posts: ", task.getException());
                }
            }
        });
    }

    private void getUserFollowers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference followsRef = db.collection("follows"); // Get reference to follows collection
        Query query = followsRef.whereEqualTo("followingUid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int followerCount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        followerCount++;
                    }
                    followerNum.setText(followerCount + "");

                } else {
                    Log.e("ProfileFragment", "Error getting user followers: ", task.getException());
                }
            }
        });
    }

    private void getUserFollowings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference followsRef = db.collection("follows"); // Get reference to follows collection
        Query query = followsRef.whereEqualTo("followerUid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int followingCount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        followingCount++;
                    }
                    followingNum.setText(followingCount + "");

                } else {
                    Log.e("ProfileFragment", "Error getting user followers: ", task.getException());
                }
            }
        });
    }
}
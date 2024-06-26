package com.petpaw.fragments.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.CreateCommunityActivity;
import com.petpaw.activities.CreatePostActivity;
import com.petpaw.activities.MainActivity;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewRandom;

    private Context context;
    private String mParam1;
    private String mParam2;
    private String uid;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.homePostRecyclerView);
        recyclerViewRandom = view.findViewById(R.id.homePostRandomRecyclerView);
//        getPosts();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        ImageView createPostBtn = view.findViewById(R.id.homeCreatePostImageView);
//        Button comBtn = view.findViewById(R.id.comBtn);
        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
//                intent.putExtra("communityId", "RWNRXeWkMpRlb20yV7z8");
                startActivity(intent);
            }
        });
/*
        comBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateCommunityActivity.class);
                startActivity(intent);
            }
        });

 */

        return view;
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume: ");
        super.onResume();
        getPosts();
    }

    private void getPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> followingList = new ArrayList<>();

        CollectionReference followingRef = db.collection("follows");
        Query followingQuery = followingRef.whereEqualTo("followerUid", uid);
        followingQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                Log.d("TAG", "onComplete: " + task.getResult().getDocuments());
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        followingList.add(document.getString("followingUid"));
                    }
                    followingList.add(uid); //add your own posts to the feed

//                    ----------------- if the user is not following anyone -----------------
                    if (followingList.size() == 0) {
//                        all posts displayed in the home fragment in DESCENDING order
                        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
                        Query query = postsRef.whereEqualTo("communityId", null).orderBy("communityId").orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) { // Get all documents in the Posts collection
                                if (task.isSuccessful()) {
                                    postList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Post post = document.toObject(Post.class);
                                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                                        postList.add(postTemp);
                                    }
                                    if (context != null) {
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(new PostListAdapter(getContext(), postList));
                                    }
                                    Log.d("TAG", "onComplete: " + postList);
                                }else{
                                    Log.d("TAG", "ERROR: " + task.getException());
                                }
                            }
                        });
                    }else{
                        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
                        Query query = postsRef.whereIn("authorId", followingList).whereEqualTo("communityId", null).orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) { // Get all documents in the Posts collection
                                if (task.isSuccessful()) {
                                    postList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Post post = document.toObject(Post.class);
                                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                                        postList.add(postTemp);
                                    }
                                    if (context != null) {
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(new PostListAdapter(getContext(), postList));
                                    }
                                    Log.d("TAG", "postList: " + postList);
                                }else{
                                    Log.d("TAG", "ERROR: " + task.getException());
                                }
                            }
                        });

                        List<Post> randomPostList = new ArrayList<>();
                        Query queryRandom = postsRef.whereNotIn("authorId", followingList).whereEqualTo("communityId", null).orderBy("authorId")
                                .orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
                        queryRandom.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) { // Get all documents in the Posts collection
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Post post = document.toObject(Post.class);
                                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                                        randomPostList.add(postTemp);
                                    }
                                    if (context != null) {
                                        recyclerViewRandom.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerViewRandom.setAdapter(new PostListAdapter(getContext(), randomPostList));
                                    }
                                    Log.d("randomPostList", "randomPostList: " + randomPostList);
                                }else{
                                    Log.d("TAG", "ERROR: " + task.getException());
                                }
                            }
                        });
                    }

                } else {
                    Log.d("TAG", "ERROR: " + task.getException());
                }
            }
            });




//        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
//        Query query = postsRef.whereEqualTo("communityId", null).orderBy("communityId").orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    postList.clear();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Post post = document.toObject(Post.class);
//                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
//                        postList.add(postTemp);
//                    }
//                    if (context != null) {
//                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                        recyclerView.setAdapter(new PostListAdapter(requireContext(), postList));
//                    }
//                    Log.d("TAG", "onComplete: " + postList);
//                }else{
//                    Log.d("TAG", "ERROR: " + task.getException());
//                }
//            }
//        });
    }
}
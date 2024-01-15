package com.petpaw.fragments.screens;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.auth.User;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.adapters.UserListAdapter;
import com.petpaw.databinding.FragmentSearchBinding;

import com.petpaw.R;
import com.petpaw.models.Community;
import com.petpaw.models.Post;
import com.petpaw.models.User;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentSearchBinding binding;
    private List<Post> postList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Community> communityList = new ArrayList<>();

    private boolean isPost = true;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        setUnderline(binding.searchFragmentPostTextView);

        binding.searchFragmentPostTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnderline(binding.searchFragmentPostTextView);
                removeUnderline(binding.searchFragmentUserTextView);

//                binding.searchFragmentPostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                binding.searchFragmentPostRecyclerView.setAdapter(new PostListAdapter(requireContext(), postList));
                binding.searchFragmentPostRecyclerView.setVisibility(View.VISIBLE);

                //binding.searchFragmentUserRecyclerView.setAdapter(null);
                binding.searchFragmentUserRecyclerView.setVisibility(View.GONE);

                getPosts("");

            }
        });

        binding.searchFragmentUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUnderline(binding.searchFragmentPostTextView);
                setUnderline(binding.searchFragmentUserTextView);

                //binding.searchFragmentPostRecyclerView.setAdapter(null);
                binding.searchFragmentPostRecyclerView.setVisibility(View.GONE);

                binding.searchFragmentUserRecyclerView.setVisibility(View.VISIBLE);
                getUsers("");
            }
        });

        binding.searchFragmentSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //if user search for post
                if(isPost){
                    getPosts(s);
                }else {
                    getUsers(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //if user search for post
                if(isPost){
                    if(s.isEmpty()){
                        getPosts("");
                    }
                }else {
                    if(s.isEmpty()){
                        getUsers("");
                    }
                }

                return false;
            }
        });

        return binding.getRoot();
    }

    public void setUnderline(TextView textView) {
        String text = textView.getText().toString();
        binding.searchFragmentSearchBar.setQuery("", false);
        if(text.equals("POST")){
            isPost = true;
        } else {
            isPost = false;
        }
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public void removeUnderline(TextView textView) {
        textView.setText(textView.getText().toString());
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume: ");
        super.onResume();
        getPosts("");
    }

    private void getPosts(String searchValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("Posts"); // Get a reference to the Posts collection
        Query query = postsRef.orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = document.toObject(Post.class);
                        if(searchValue.equals("")){
                            Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList());
                            postList.add(postTemp);
                        } else {
                            if(post.getContent().toLowerCase().contains(searchValue.toLowerCase())){
                                Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList());
                                postList.add(postTemp);
                            }
                        }
                    }
                    if (context != null) {
                        binding.searchFragmentPostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.searchFragmentPostRecyclerView.setAdapter(new PostListAdapter(requireContext(), postList));
                    }
                }
            }
        });
    }

    private void getUsers(String searchValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.orderBy("name", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userList.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if(searchValue.equals("")){
                            User userTemp = new User(user.getUid(), user.getName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getImageURL());
                            userList.add(userTemp);
                        } else {
                            if(user.getName().toLowerCase().contains(searchValue.toLowerCase())){
                                User userTemp = new User(user.getUid(), user.getName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getImageURL());
                                userList.add(userTemp);
                            }
                        }
                    }
                    if (context != null) {
                        binding.searchFragmentUserRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.searchFragmentUserRecyclerView.setAdapter(new UserListAdapter(requireContext(), userList));
                    }
                }
            }
        });
    }
}
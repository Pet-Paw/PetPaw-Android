package com.petpaw.fragments.screens;

import static com.petpaw.fragments.screens.ProfileFragment.PREVIOUS_FRAGMENT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.CreatePostActivity;
import com.petpaw.activities.EmptyActivity;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.databinding.FragmentCommunityDetailBinding;
import com.petpaw.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentCommunityDetailBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String communityId;
    private Context context;
    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private String mParam2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public CommunityDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param communityId Parameter 1.
     * @param previousFragment Parameter 2.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityDetailFragment newInstance(String communityId, int previousFragment) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, communityId);
        args.putInt(PREVIOUS_FRAGMENT, previousFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            communityId = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume: ");
        super.onResume();
        getPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityDetailBinding.inflate(inflater, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference communitiesRef = db.collection("Communities"); // Get a reference to the Communities collection

//        --------------------- Setting the Community Detail --------------------------
        db.collection("Communities")
                .document(communityId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
//                            ----------------- bind Name and Description -----------------
                            binding.communityDetailName.setText(document.get("name").toString());
                            binding.communityDetailDescription.setText(document.get("description").toString());

//                            ------------------- community Image --------------------


//                            ------------------- button Visibility --------------------
                            String ownerId = document.get("owner").toString();
                            List<String> membersList = (List<String>) document.get("members");

                            if(ownerId.equals(userId)){
                                binding.deleteCommunityBtn.setVisibility(View.VISIBLE);
                                binding.leaveCommunityBtn.setVisibility(View.GONE);
                                binding.createPostCommunityBtn.setVisibility(View.VISIBLE);
                                binding.chatBtn.setVisibility(View.VISIBLE);
                            } else if(membersList.contains(userId)){
                                binding.deleteCommunityBtn.setVisibility(View.GONE);
                                binding.leaveCommunityBtn.setVisibility(View.VISIBLE);
                                binding.createPostCommunityBtn.setVisibility(View.VISIBLE);
                                binding.chatBtn.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.deleteCommunityBtn.setVisibility(View.GONE);
                                binding.leaveCommunityBtn.setVisibility(View.GONE);
                                binding.createPostCommunityBtn.setVisibility(View.GONE);

                            }

                        }
                    }
                });

//        ----------------------- Create Post Button --------------------------
        binding.createPostCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CreatePostActivity.class);
                intent.putExtra("communityId", communityId);
                startActivity(intent);
            }
        });
        getPosts();

//        ----------------------- Delete Community Button --------------------------
        binding.deleteCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Communities").document(communityId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(requireContext(), "Community Deleted", Toast.LENGTH_SHORT).show();
//                                not including deleting the posts belongs to the community

//                                go back to previous fragment
                                getActivity().onBackPressed();

//                                trigger the onResume of the previous fragment
                                Intent intent = new Intent(requireContext(), EmptyActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });

//        ----------------------- Leave Community Button --------------------------
        binding.leaveCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Communities").document(communityId)
                        .update("members", FieldValue.arrayRemove(userId))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(requireContext(), "Community Left", Toast.LENGTH_SHORT).show();
//                                go back to previous fragment
                                getActivity().onBackPressed();

//                                trigger the onResume of the previous fragment
                                Intent intent = new Intent(requireContext(), EmptyActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });

//        ----------------------- Back Button --------------------------
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getArguments() != null;
                int previousFragment = getArguments().getInt(PREVIOUS_FRAGMENT);
                Log.d("ProfileFragment", "previousFragment = " + previousFragment);
                String owner_uid = getArguments().getString("owner_uid");
                if (previousFragment == R.id.communityFragment) {
                    getActivity().onBackPressed();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        return binding.getRoot();
    }

    private void getPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("Posts");
        Query query = postsRef.whereEqualTo("communityId", communityId).orderBy("communityId").orderBy("dateModified", Query.Direction.DESCENDING); // Order documents by dateModified in ascending order
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = document.toObject(Post.class);
                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                        postList.add(postTemp);
                    }

                    if (context != null) {
                        binding.communityDetailPostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.communityDetailPostRecyclerView.setAdapter(new PostListAdapter(requireContext(), postList));
                    }
                }else{
                    Log.d("TAG", "ERROR: " + task.getException());
                }
            }
        });
    }
}
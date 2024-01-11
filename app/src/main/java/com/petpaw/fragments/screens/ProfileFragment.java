package com.petpaw.fragments.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.petpaw.R;
import com.petpaw.activities.AddPetActivity;
import com.petpaw.activities.EditProfileActivity;
import com.petpaw.adapters.PetListAdapter;
import com.petpaw.adapters.PostListAdapter;
import com.petpaw.adapters.UserFollowingAdapter;
import com.petpaw.adapters.UserFollowsAdapter;
import com.petpaw.adapters.UserListAdapter;
import com.petpaw.databinding.FragmentMessagesBinding;
import com.petpaw.databinding.FragmentProfileBinding;
import com.petpaw.models.Pet;
import com.petpaw.models.Post;
import com.petpaw.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private static final String OWNER_UID = "ownerUid";

    public static final String PREVIOUS_FRAGMENT = "previousFragment";

    // TODO: Rename and change types of parameters
    private FragmentProfileBinding binding;
    private String mParam1;
    private String mParam2;
    private User user;
    private String uid;
    private List<Post> userPostList = new ArrayList<>();
    private List<Pet> userPetList = new ArrayList<>();
    private List<User> userFollowingList = new ArrayList<>();
    private List<User> userFollowerList = new ArrayList<>();

    static final String USER_ID = "userId";

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String userId, int previousFragment, String ownerUid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        args.putInt(PREVIOUS_FRAGMENT, previousFragment);
        args.putString(OWNER_UID, ownerUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
         */
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        } else {
            // No UID passed, so default to the current user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                uid = currentUser.getUid();
            }
            mParam2 = null;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser.getUid().equals(uid)) {
            // Current user's profile
            binding.editBtn.setVisibility(View.VISIBLE);
            binding.addPetBtn.setVisibility(View.VISIBLE);
            binding.settingsBtn.setVisibility(View.VISIBLE);
            binding.backBtn.setVisibility(View.INVISIBLE);
            binding.followBtn.setVisibility(View.GONE);
            binding.messageBtn.setVisibility(View.GONE);
        } else {
            // Another user's profile
            binding.editBtn.setVisibility(View.GONE);
            binding.addPetBtn.setVisibility(View.GONE);
            binding.followBtn.setVisibility(View.VISIBLE);
            binding.messageBtn.setVisibility(View.VISIBLE);
            binding.backBtn.setVisibility(View.VISIBLE);
            binding.settingsBtn.setVisibility(View.INVISIBLE);
        }
        binding.displayPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.displayPosts.setClickable(false);
                binding.displayPosts.setTypeface(null, Typeface.BOLD);
                binding.displayPets.setClickable(true);
                binding.displayPets.setTypeface(null, Typeface.NORMAL);
                binding.postsRecyclerView.setVisibility(View.VISIBLE);
                binding.petsRecyclerView.setVisibility(View.GONE);
            }
        });

        binding.displayPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.displayPets.setClickable(false);
                binding.displayPets.setTypeface(null, Typeface.BOLD);
                binding.displayPosts.setClickable(true);
                binding.displayPosts.setTypeface(null, Typeface.NORMAL);
                binding.petsRecyclerView.setVisibility(View.VISIBLE);
                binding.postsRecyclerView.setVisibility(View.GONE);
            }
        });

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                intent.putExtra("avatarURL", user.getImageURL());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("address", user.getAddress());
                intent.putExtra("phone", user.getPhone());
                startActivity(intent);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isAdded()) {
//                    FrameLayout overlayContainer = getActivity().findViewById(R.id.overlay_fragment_container);
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNav);
                    int selectedItemId = bottomNav.getSelectedItemId();
//
//                    overlayContainer.setVisibility(View.GONE);
//
//                    // Restore visibility of the underlying layout
//                    if (selectedItemId == R.id.searchFragment) {
//                        getActivity().findViewById(R.id.searchLayout).setVisibility(View.VISIBLE);
//                    } else if (selectedItemId == R.id.profileFragment) {
//                        getActivity().findViewById(R.id.userFollowingFragment).setVisibility(View.VISIBLE);
//                    }
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    assert getArguments() != null;
                    int previousFragment = getArguments().getInt(PREVIOUS_FRAGMENT);
                    Log.d("ProfileFragment", "previousFragment = " + previousFragment);
                    String owner_uid = getArguments().getString(OWNER_UID);
                    if (previousFragment == R.id.userFollowingFragment) {
                        Toast.makeText(requireContext(), "previousFragment = " + previousFragment, Toast.LENGTH_SHORT).show();
                        fragmentTransaction.replace(R.id.profileFragmentLayout, UserFollowingFragment.newInstance(owner_uid)).commitNow();
                    } else if (previousFragment == R.id.useFollowerFragment) {
                        fragmentTransaction.replace(R.id.profileFragmentLayout, UserFollowersFragment.newInstance(owner_uid)).commitNow();
                    } else if (previousFragment == R.id.searchFragment || selectedItemId == R.id.searchFragment) {
//                        fragmentTransaction.replace(R.id.sear, new SearchFragment()).commitNow();
                        getActivity().onBackPressed();

                    } else {
                        fragmentTransaction.replace(R.id.profileFragmentLayout, new ProfileFragment()).commitNow();

                    }


                }

//            }
        });

        binding.addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddPetActivity.class);
                startActivity(intent);
            }
        });
        TabLayout tabLayout = binding.tabLayout;
        tabLayout.selectTab(null);
        ViewPager2 viewPager = binding.viewPager;


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // passing uid to viewpager
                Bundle bundle = new Bundle();
                bundle.putString(USER_ID, uid);

                viewPager.setAdapter(new UserFollowsAdapter(requireActivity(), uid));
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.setVisibility(View.VISIBLE);
                binding.profileLayout.setVisibility(View.GONE);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing when a tab is unselected

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing when a tab is reselected
            }
        });

        return binding.getRoot();
    }
    /*
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
     */


    @Override
    public void onResume() {
        super.onResume();
        Log.d("ProfileFragment", "onResume");
        displayUserInfo();
        getUserPosts();
        getUserFollowers();
        getUserFollowings();
        getUserPets();
    }

    private void displayUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users"); // Get reference to users collection
        Query query = usersCollection.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("ProfileFragment", "task.getResult().size() = " + task.getResult().size());

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        user = documentSnapshot.toObject(User.class);
                        Log.d("ProfileFragment", "image URL: " + user.getImageURL());
                        if (user.getImageURL() != null && !(user.getImageURL().isEmpty())) {
                            Picasso.get()
                                    .load(user.getImageURL())
                                    .tag(System.currentTimeMillis())
                                    .into(binding.profileAvatar, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("TAG", "Load avatar successfully at " + System.currentTimeMillis());
                                        }
                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("TAG", "Load image failed");
                                        }
                                    });
                        } else {
                            binding.profileAvatar.setImageResource(R.drawable.default_avatar);
                        }
                        binding.name.setText(user.getName());
                        binding.location.setText(user.getAddress());
                        break;
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
                        Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList());
                        userPostList.add(postTemp);
                    }
                    // Check if the userPostList is empty and log
                    if (userPostList.isEmpty()) {
                        Log.d("ProfileFragment", "No posts found for the user.");
                    }

                    binding.postNum.setText(userPostList.size() + "");
                    if(context != null) {
                        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.postsRecyclerView.setAdapter(new PostListAdapter(requireContext(), userPostList));
                    }
                } else {
                    Log.e("ProfileFragment", "Error getting user posts: ", task.getException());
                }
            }
        });

    }

    private void getUserPets() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("Pets");
        Query query = postsRef.whereEqualTo("ownerId", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userPetList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Pet pet = document.toObject(Pet.class);
                        userPetList.add(pet);
                    }
                    // Check if the userPostList is empty and log
                    if (userPetList.isEmpty()) {
                        Log.d("ProfileFragment", "No pets found for the user.");
                    }
                    if(context != null) {
                        binding.petsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.petsRecyclerView.setAdapter(new PetListAdapter(requireContext(), userPetList));
                    }
                } else {
                    Log.e("ProfileFragment", "Error getting user pets: ", task.getException());
                }
            }
        });
        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getCurrentUser().getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {
                                db.collection("Pets")
                                        .whereIn(FieldPath.documentId(), (List<String>) doc.get("pets"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    userPetList.clear();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d("ProfileFragment", document.getId() + " => " + document.getData());
                                                        userPetList.add(document.toObject(Pet.class));
                                                        binding.petsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                                        binding.petsRecyclerView.setAdapter(new PetListAdapter(requireContext(), userPetList));
                                                    }
                                                } else {
                                                    Log.d("ProfileFragment", "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        });

         */
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
                    binding.followerNum.setText(followerCount + "");

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
                    binding.followingNum.setText(followingCount + "");

                } else {
                    Log.e("ProfileFragment", "Error getting user followers: ", task.getException());
                }
            }
        });
    }
}
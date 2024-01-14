package com.petpaw.fragments.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.R;
import com.petpaw.activities.MainActivity;
import com.petpaw.adapters.UserFollowingAdapter;
import com.petpaw.database.FollowCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.FragmentUserFollowingBinding;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFollowingFragment extends Fragment {


    private static final String TAG = "UserFollowingFragment";
    private FragmentUserFollowingBinding userFollowingBinding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private List<FollowRecord> followRecords = new ArrayList<>();
    private User currentUser;

    private User followingUser;
    private FollowCollection followCollection = FollowCollection.newInstance();

    private RecyclerView recyclerView;
    private UserFollowingAdapter userFollowingAdapter;
    private UserCollection userCollection = UserCollection.newInstance();

    private List<User> followingUsers = new ArrayList<>();


    public UserFollowingFragment() {
        // Required empty public constructor
    }


    public static UserFollowingFragment newInstance(String userId) {
        UserFollowingFragment fragment = new UserFollowingFragment();
        Bundle args = new Bundle();
        args.putString(ProfileFragment.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userFollowingBinding = FragmentUserFollowingBinding.inflate(inflater, container, false);

        recyclerView = userFollowingBinding.followingRecyclerView;

        TextView msgNoFollowing = userFollowingBinding.msgNoFollowing;
        msgNoFollowing.setVisibility(TextView.INVISIBLE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert getArguments() != null;
        String userId = getArguments().getString(ProfileFragment.USER_ID);

        if (userId != null) {
//            getCurrentUserAndFollowingUser(firebaseUser.getUid(), "JhmTNRM4bjbykb92yOxAGoaIMH92");
            getUser(userId, new UserCollection.Callback() {
                @Override
                public void onCallback(List<User> users) {

                }

                @Override
                public void onCallBack(User user) {
                    currentUser = user;
                    followCollection.getAllFollowings(currentUser, new FollowCollection.Callback() {
                        @Override
                        public void onCallback(List<FollowRecord> res) {
                            followRecords = res;
                        }

                        @Override
                        public void onCallBackGetUsers(List<User> users) {
                            followingUsers = users;
                            userFollowingAdapter = new UserFollowingAdapter(true, userId);
                            userFollowingAdapter.setUsers(followingUsers);
                            recyclerView.setAdapter(userFollowingAdapter);

                            if (followingUsers.size() == 0) {
                                msgNoFollowing.setVisibility(TextView.VISIBLE);
                            }
                        }
                    });


                }
            });
        }

        userFollowingBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(userId, 0, "");
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFragmentLayout, profileFragment)
                        .commit();
            }
        });

        SearchView searchBar = userFollowingBinding.searchBar;
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        // Inflate the layout for this fragment
        return userFollowingBinding.getRoot();
    }

    private void getCurrentUserAndFollowingUser(String currentUserUid, String followingUserUid) {
        getUser(currentUserUid, new UserCollection.Callback() {
            @Override
            public void onCallback(List<User> users) {
                // This method is not used for retrieving a list of users
            }

            @Override
            public void onCallBack(User user) {
                currentUser = user;
                Log.d(TAG, "Current User: " + currentUser);

                // Now that we have the current user, let's get the following user
                getFollowingUser(followingUserUid, new UserCollection.Callback() {
                    @Override
                    public void onCallback(List<User> users) {
                        // This method is not used for retrieving a list of users
                    }

                    @Override
                    public void onCallBack(User user) {
                        followingUser = user;

                        // Check if both users are not null before proceeding
                        if (currentUser != null && followingUser != null) {
                            // Perform your actions, e.g., add followers and following
                            followCollection.addFollowing(currentUser, followingUser);
                            followCollection.addFollower(currentUser, followingUser);

                            // You can perform additional actions here
                        } else {
                            // Handle the case where one or both users are not found
                            Log.e(TAG, "Error: One or both users not found.");
                        }
                    }
                });
            }
        });
    }

    private void getUser(String uid, UserCollection.Callback callback) {
        userCollection.getUser(uid, callback);
    }

    private void getFollowingUser(String uid, UserCollection.Callback callback) {
        userCollection.getUser(uid, callback);
    }

    private void filter(String text) {

        if (!text.isEmpty()) {
            List<User> filteredList = new ArrayList<>();

            for (User user : followingUsers) {
                if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            userFollowingAdapter.filter(filteredList);
        } else {
            userFollowingAdapter.filter(followingUsers);
        }
    }
}
package com.petpaw.fragments.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.petpaw.adapters.UserFollowingAdapter;
import com.petpaw.database.FollowCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.FragmentUserFollowersBinding;
import com.petpaw.databinding.FragmentUserFollowingBinding;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFollowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFollowersFragment extends Fragment {

    private static final String TAG = "UserFollowingFragment";
    private FragmentUserFollowersBinding userFollowersBinding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private List<FollowRecord> followRecords = new ArrayList<>();
    private User currentUser;

    private User followingUser;

    private FollowCollection followCollection = FollowCollection.newInstance();

    private RecyclerView recyclerView;
    private UserFollowingAdapter userFollowingAdapter;
    private UserCollection userCollection = UserCollection.newInstance();

    private List<User> followerUsers = new ArrayList<>();


    public UserFollowersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFollowersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFollowersFragment newInstance(String param1, String param2) {
        UserFollowersFragment fragment = new UserFollowersFragment();
        Bundle args = new Bundle();
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
        userFollowersBinding = FragmentUserFollowersBinding.inflate(inflater, container, false);

        recyclerView = userFollowersBinding.followingRecyclerView;

        TextView msgNoFollowing = userFollowersBinding.msgNoFollowing;
        msgNoFollowing.setVisibility(TextView.INVISIBLE);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
//            getCurrentUserAndFollowingUser(firebaseUser.getUid(), "JhmTNRM4bjbykb92yOxAGoaIMH92");
            getUser(firebaseUser.getUid(), new UserCollection.Callback() {
                @Override
                public void onCallback(List<User> users) {

                }

                @Override
                public void onCallBack(User user) {
                    currentUser = user;
                    followCollection.getAllFollowers(currentUser, new FollowCollection.Callback() {
                        @Override
                        public void onCallback(List<FollowRecord> res) {
                            followRecords = res;
                        }

                        @Override
                        public void onCallBackGetUsers(List<User> users) {
                            followerUsers = users;
                            userFollowingAdapter = new UserFollowingAdapter();
                            userFollowingAdapter.setUsers(followerUsers);
                            recyclerView.setAdapter(userFollowingAdapter);

                            if (followerUsers.size() == 0) {
                                msgNoFollowing.setVisibility(TextView.VISIBLE);
                            }
                        }
                    });
                }
            });
        }

        userFollowersBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNav);
                bottomNavigationView.setSelectedItemId(R.id.profileFragment);

            }
        });

        SearchView searchBar = userFollowersBinding.searchBar;
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
        return userFollowersBinding.getRoot();
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

            for (User user : followerUsers) {
                if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            userFollowingAdapter.filter(filteredList);
        } else {
            userFollowingAdapter.filter(followerUsers);
        }
    }
}
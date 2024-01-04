package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.R;
import com.petpaw.adapters.UserFollowingAdapter;
import com.petpaw.database.FollowCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivityUserFollowingBinding;
import com.petpaw.models.FollowRecord;
import com.petpaw.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingActivity extends AppCompatActivity {
    private static final String TAG = "UserFollowingActivity";
    private ActivityUserFollowingBinding userFollowingBinding;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFollowingBinding = ActivityUserFollowingBinding.inflate(getLayoutInflater());
        setContentView(userFollowingBinding.getRoot());
        recyclerView = findViewById(R.id.followingRecyclerView);

        TextView msgNoFollowing = userFollowingBinding.msgNoFollowing;
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
                  followCollection.getAllFollowings(currentUser, new FollowCollection.Callback() {
                        @Override
                        public void onCallback(List<FollowRecord> res) {
                             followRecords = res;
                        }

                       @Override
                       public void onCallBackGetUsers(List<User> users) {
                            followingUsers = users;
                            userFollowingAdapter = new UserFollowingAdapter();
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





}
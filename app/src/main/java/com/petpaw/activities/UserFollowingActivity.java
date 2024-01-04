package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.R;
import com.petpaw.database.FollowCollection;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivityUserFollowingBinding;
import com.petpaw.models.User;

import java.util.List;

public class UserFollowingActivity extends AppCompatActivity {
    private static final String TAG = "UserFollowingActivity";
    private ActivityUserFollowingBinding userFollowingBinding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private User currentUser;

    private User followingUser;
    private FollowCollection followCollection = FollowCollection.newInstance();

    private UserCollection userCollection = UserCollection.newInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_following);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            getCurrentUserAndFollowingUser(firebaseUser.getUid(), "3LFFf4l3w3Dv9Ikjuw9H");
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
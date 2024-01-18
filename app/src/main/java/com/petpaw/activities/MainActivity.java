package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.petpaw.R;
import com.petpaw.clients.NotiSender;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.fragments.screens.SideNavFragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserCollection userCollection = UserCollection.newInstance();
    ActivityMainBinding mBinding;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        userCollection.updateDeviceToken(firebaseUser.getUid(), token);

                        // Log and toast
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "token: "+  token);
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.toolbarFragment, SideNavFragment.newInstance())
                    .commit();
        }

        
        setupUI();
    }

    private void setupUI() {
        setupBottomNav();

    }

    @SuppressLint("NonConstantResourceId")
    private void setupBottomNav() {
//        mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__home);
//        mBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int itemId = item.getItemId();
//
//                if (itemId == R.id.menu_bottom_nav_item__home) {
//                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__home);
//                    return true;
//                } else if (itemId == R.id.menu_bottom_nav_item__search) {
//                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__search);
//                    return true;
//                } else if (itemId == R.id.menu_bottom_nav_item__notifications) {
//                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__notifications);
//                    return true;
//                } else if (itemId == R.id.menu_bottom_nav_item__messages) {
//                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__notifications);
//                    return true;
//                } else if (itemId == R.id.menu_bottom_nav_item__profile) {
//                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__profile);
//                    return true;
//                }
//
//                return false;
//            }
//        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(mBinding.fr.getId());
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(mBinding.bottomNav, navController);


//        mBinding.bottomNav.setSelectedItemId(R.id.homeFragment);
        mBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FrameLayout userProfileContainer = findViewById(R.id.overlay_profile_fragment);
                FrameLayout petProfileContainer = findViewById(R.id.overlay_pet_fragment);
                FrameLayout communityContainer = findViewById(R.id.overlay_community_fragment);
                FrameLayout notificationsContainer = findViewById(R.id.overlay_notification_fragment);
                mBinding.fr.setVisibility(View.VISIBLE);

                // If the overlay container is visible, hide it when navigating to a different tab
                if (userProfileContainer.getVisibility() == View.VISIBLE) {
                    userProfileContainer.setVisibility(View.GONE);
                }
                if (petProfileContainer.getVisibility() == View.VISIBLE) {
                    petProfileContainer.setVisibility(View.GONE);
                }
                if (communityContainer.getVisibility() == View.VISIBLE) {
                    communityContainer.setVisibility(View.GONE);
                }
                if (notificationsContainer.getVisibility() == View.VISIBLE) {
                    notificationsContainer.setVisibility(View.GONE);
                }

                SideNavFragment sideNavFragment = (SideNavFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.toolbarFragment);

                if (sideNavFragment != null) {
                    NavigationView sideNav = sideNavFragment.mNavigationView;
                    sideNav.setCheckedItem(item.getItemId());
                }

                return NavigationUI.onNavDestinationSelected(item, navController, false)
                        || MainActivity.super.onOptionsItemSelected(item);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FrameLayout userProfileContainer = findViewById(R.id.overlay_profile_fragment);
        FrameLayout petProfileContainer = findViewById(R.id.overlay_pet_fragment);
        FrameLayout communityContainer = findViewById(R.id.overlay_community_fragment);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        int selectedItemId = bottomNav.getSelectedItemId();

        if (petProfileContainer.getVisibility() == View.VISIBLE) {
            petProfileContainer.setVisibility(View.GONE);
            // Restore visibility of the underlying layout
            findViewById(R.id.profileFragmentLayout).setVisibility(View.VISIBLE);
        }
        else if (communityContainer.getVisibility() == View.VISIBLE) {
            communityContainer.setVisibility(View.GONE);

            // Restore visibility of the underlying layout
            if (selectedItemId == R.id.searchFragment) {
                findViewById(R.id.searchLayout).setVisibility(View.VISIBLE);
            } else if (selectedItemId == R.id.communityFragment) {
                findViewById(R.id.communityLayout).setVisibility(View.VISIBLE);
            }
        }
        else if (userProfileContainer.getVisibility() == View.VISIBLE) {
            userProfileContainer.setVisibility(View.GONE);

            // Restore visibility of the underlying layout
            if (selectedItemId == R.id.searchFragment) {
                findViewById(R.id.searchLayout).setVisibility(View.VISIBLE);
            } else if (selectedItemId == R.id.profileFragment) {
                findViewById(R.id.userFollowingFragment).setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.petpaw.R;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.fragments.screens.SideNavFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;

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
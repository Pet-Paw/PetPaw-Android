package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationBarView;
import com.petpaw.PostDetailActivity;
import com.petpaw.R;
import com.petpaw.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        
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

        mBinding.bottomNav.setSelectedItemId(R.id.homeFragment);
        mBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return NavigationUI.onNavDestinationSelected(item, navController, false)
                        || MainActivity.super.onOptionsItemSelected(item);
            }
        });
    }
}
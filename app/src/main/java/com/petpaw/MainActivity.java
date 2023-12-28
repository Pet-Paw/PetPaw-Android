package com.petpaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationBarView;
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

    @Override
    public void onStart(){
        super.onStart();
        Button postDetailBtn = findViewById(R.id.postDetailBtn);
        postDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
                startActivity(intent);
            }
        });

        Button createPostBtn = findViewById(R.id.createPostBtn);

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupUI() {
        setupBottomNav();

    }

    @SuppressLint("NonConstantResourceId")
    private void setupBottomNav() {
        mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__home);
        mBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_bottom_nav_item__home) {
                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__home);
                    return true;
                } else if (itemId == R.id.menu_bottom_nav_item__search) {
                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__search);
                    return true;
                } else if (itemId == R.id.menu_bottom_nav_item__notifications) {
                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__notifications);
                    return true;
                } else if (itemId == R.id.menu_bottom_nav_item__messages) {
                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__notifications);
                    return true;
                } else if (itemId == R.id.menu_bottom_nav_item__profile) {
                    mBinding.bottomNav.setSelectedItemId(R.id.menu_bottom_nav_item__profile);
                    return true;
                }

                return false;
            }
        });
    }

    private void moveToMessagesScreen() {

    }
}
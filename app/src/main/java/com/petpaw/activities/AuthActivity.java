package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.petpaw.R;
import com.petpaw.databinding.ActivityAuthBinding;
import com.petpaw.fragments.SignUpFormFragment;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding mBinding;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupData();
        setupUI();
    }

    private void setupData() {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
    }

    private void setupUI() {
        mFragmentTransaction
                .setReorderingAllowed(true)
                .replace(mBinding.authFragment.getId(), SignUpFormFragment.class, null)
                .commit();
    }
}
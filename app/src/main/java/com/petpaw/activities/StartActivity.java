package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding startBinding;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBinding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(startBinding.getRoot());

        Button startBtn = startBinding.startBtn;
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }
}
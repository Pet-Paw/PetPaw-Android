package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBindings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petpaw.MainActivity;
import com.petpaw.R;
import com.petpaw.databinding.ActivityMainBinding;
import com.petpaw.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding startBinding;
    FirebaseUser firebaseUser;
    @Override
    protected void onStart() {
        super.onStart();
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
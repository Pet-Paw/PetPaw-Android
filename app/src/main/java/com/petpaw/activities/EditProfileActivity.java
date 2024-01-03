package com.petpaw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.petpaw.R;
import com.petpaw.databinding.ActivityEditProfileBinding;
import com.petpaw.databinding.ActivityProfileBinding;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding editProfileBinding;

    private ImageView avatar;
    private TextView name, email, phoneNum, location, password;
    private Button editInformationBtn, editLocationBtn, editPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editProfileBinding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(editProfileBinding.getRoot());

        avatar = editProfileBinding.avatar;
        name = editProfileBinding.name;
        email = editProfileBinding.email;
        phoneNum = editProfileBinding.phoneNum;
        location = editProfileBinding.address;
        password = editProfileBinding.password;
        editInformationBtn = editProfileBinding.editInformationBtn;
        editLocationBtn = editProfileBinding.editLocationBtn;
        editPasswordBtn = editProfileBinding.editPasswordBtn;

        Intent intent = getIntent();
        String avatarURL = intent.getStringExtra("avatarURL");
        String userName = intent.getStringExtra("name");
        String userEmail = intent.getStringExtra("email");
        String userAddress = intent.getStringExtra("address");
        String userPhone = intent.getStringExtra("phone");

        name.setText(userName);
        email.setText(userEmail);
        location.setText(userAddress);
        phoneNum.setText(userPhone);

    }


}
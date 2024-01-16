package com.petpaw.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.petpaw.R;
import com.petpaw.database.UserCollection;
import com.petpaw.databinding.ActivitySignUpBinding;
import com.petpaw.models.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private ActivitySignUpBinding signUpBinding;
    private FirebaseAuth auth;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private UserCollection userCollection = UserCollection.newInstance();

    private String phoneInSignUpByPhone;

    private String usernameInSignUpByPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.w(TAG, "FirebaseAuthInvalidCredentialsException", e);
                    Toast.makeText(SignUpActivity.this, "Please input a phone numbers are written in the format [+][country code][subscriber number including area code]", Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.w(TAG, "FirebaseTooManyRequestsException", e);
                    Toast.makeText(SignUpActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // open dialog to input otp
                Dialog dialog = new Dialog(SignUpActivity.this);
                dialog.setContentView(R.layout.otp_form);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.show();

                PinView pinView = dialog.findViewById(R.id.pinview);
                Button btn_submit_otp = dialog.findViewById(R.id.btnConfirmOtp);

                btn_submit_otp.setOnClickListener(v -> {
                    String otp = pinView.getText().toString();
                    if (otp.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        verifyCode(otp);
                    }
                });
//
//
//                // test verify code
//                verifyCode("123456");
            }
        };

        EditText username = signUpBinding.username;
        EditText password = signUpBinding.password;
        EditText address = signUpBinding.address;
        Button signUpBtn = signUpBinding.btnSignUp;
        TextView directToLogin = signUpBinding.directToLogin;
        EditText emailOrPhone = signUpBinding.emailOrPhone;

        auth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(v -> {
            String txt_username = username.getText().toString();
            String txt_emailOrPhone = emailOrPhone.getText().toString();
            String txt_password = password.getText().toString();
            String txt_address = address.getText().toString();

            if (txt_username.isEmpty() || txt_emailOrPhone.isEmpty() || txt_password.isEmpty() || txt_address.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                register(txt_username, txt_emailOrPhone,  txt_address, txt_password);
            }
        });

        directToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void register(String username, String emailOrPhone, String address, String password) {
        boolean isEmail = isEmail(emailOrPhone);
        if (isEmail) {
            auth.createUserWithEmailAndPassword(emailOrPhone, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();
                    createUser(userId, username, emailOrPhone, "", address);
                } else {
                    Toast.makeText(SignUpActivity.this, "Email is already existed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            phoneInSignUpByPhone = emailOrPhone;
            usernameInSignUpByPhone = username;


            startPhoneNumberVerification(emailOrPhone);

        }
    }


    private boolean isEmail(String emailOrPhone) {
        return emailOrPhone.contains("@");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    Toast.makeText(SignUpActivity.this, "Signup successfully", Toast.LENGTH_SHORT).show();

                    createUser(auth.getCurrentUser().getUid(), usernameInSignUpByPhone, "", phoneInSignUpByPhone, "");

                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void verifyCode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Code);
        signInWithPhoneAuthCredential(credential);
    }

    private void createUser(String userId, String username, String emailOrPhone, String phoneNumber, String address) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            Toast.makeText(SignUpActivity.this, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                        }
                        String physicalDeviceToken = task.getResult();
                        User newUser = new User(userId, username, emailOrPhone, "",  address, physicalDeviceToken);
                        userCollection.createUser(newUser);

                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SignUpActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
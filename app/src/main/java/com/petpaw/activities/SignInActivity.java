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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.petpaw.R;
import com.petpaw.databinding.ActivitySignInBinding;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    private ActivitySignInBinding signInBinding;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private String otp;
    boolean isLoginByPhone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());

        auth = FirebaseAuth.getInstance();

        EditText emailOrPhone = signInBinding.email;
        EditText password = signInBinding.password;
        TextView signUpDirect = signInBinding.signUpDirect;
        Button btn_login = signInBinding.btnLogin;
        Button btn_login_by_phone = signInBinding.btnLoginByPhone;

        auth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                final String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    Log.d(TAG, "onVerificationCompleted: code: " + code);
//                    verifyCode(code);
                }
//                    signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.w(TAG, "FirebaseAuthInvalidCredentialsException", e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.w(TAG, "FirebaseTooManyRequestsException", e);
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

                // test verify code
            }
        };


        btn_login.setOnClickListener(v -> {
            String txt_email = emailOrPhone.getText().toString();
            String txt_password = password.getText().toString();

            if (!isLoginByPhone) {
                if (txt_email.isEmpty() || txt_password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                String phone =  "+84" + emailOrPhone.getText().toString();
                startPhoneNumberVerification(phone);


                // open dialog to input otp
                Dialog dialog = new Dialog(SignInActivity.this);
                dialog.setContentView(R.layout.otp_form);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                if (!phone.isEmpty()) {
                    dialog.show();
                }

                PinView pinView = dialog.findViewById(R.id.pinview);
                Button btn_submit_otp = dialog.findViewById(R.id.btnConfirmOtp);

                btn_submit_otp.setOnClickListener(task -> {
                    otp = pinView.getText().toString();

                    if (otp.isEmpty()) {
                        Toast.makeText(SignInActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        verifyCode(otp);
                    }
                });
            }
        });

        btn_login_by_phone.setOnClickListener(v -> {
            // hidden field password
            password.setVisibility(EditText.INVISIBLE);
            emailOrPhone.setHint("Enter Phone number");
            isLoginByPhone = true;

            btn_login_by_phone.setVisibility(Button.INVISIBLE);
        });


        signUpDirect.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    Toast.makeText(SignInActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "OTP message is expired", Toast.LENGTH_SHORT).show();
                    }

                    // otp is expired

                    Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
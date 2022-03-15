package com.app.jomnaik.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jomnaik.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Email verification screen..
public class VerificationActivity extends AppCompatActivity {

    Button btnVerify;
    TextView tvResend;
    FirebaseAuth mAuth;
    int counter = 0;
    public  static boolean emailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //All the Views of screen and firebase initialization..
        mAuth = FirebaseAuth.getInstance();

        btnVerify = findViewById(R.id.verify_button);
        tvResend = findViewById(R.id.tvResend);

        //Resend email if not received..
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInAndVerify(AuthenticationActivity.email , AuthenticationActivity.password);
            }
        });
    }

    //Checks user verify email using link or not
    private void SignInAndVerify(String email, String password) {
        if(checkEmailIsVerified()) {
            emailVerified = true;
            Toast.makeText(getApplicationContext(), "Email Verified", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        } else {
            emailVerified = false;
            Toast.makeText(getApplicationContext(), "Email Not Verified!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        }
    }

    //Check email verification status method...
    private boolean checkEmailIsVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            return true;
        }else {
            return false;
        }
    }
}

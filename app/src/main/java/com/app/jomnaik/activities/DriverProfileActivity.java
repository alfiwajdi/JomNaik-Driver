package com.app.jomnaik.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.jomnaik.R;
import com.app.jomnaik.models.DriverModelCLass;
import com.app.jomnaik.models.RatingModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//User Profile Screen..
public class DriverProfileActivity extends BaseActivity {

    ImageView imgEdit;
    EditText edtName, edtPhone,edtAddress,  edtEmail, editPassword;
    Spinner spnGender;
    Button btnUpdate, btnUpdatePass;
    TextView tvEmailStatus, tvRated;
    String userId, userName,bikeName, userPhone, password, gender, email;
    DatabaseReference databaseReference;
    int count = 0, counter=0;
    float totalRating=0;
    boolean emailVerified, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        //Get user data from user screen..

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName = DriverActivity.fullName;
        userPhone = DriverActivity.phone;
        password = DriverActivity.password;
        bikeName = DriverActivity.bikeName;
        gender = DriverActivity.gender;
        email = DriverActivity.email;
        password = DriverActivity.password;

        //Firebase and screen views initialization..

        databaseReference = FirebaseDatabase.getInstance().getReference("DriversData");

        imgEdit = findViewById(R.id.imgEdit);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        editPassword = findViewById(R.id.editPassword);
        spnGender = findViewById(R.id.spnGender);
        tvEmailStatus = findViewById(R.id.tvEmailStatus);
        tvRated = findViewById(R.id.tvRated);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);

        edtName.setText(userName);
        edtPhone.setText(userPhone);
        edtAddress.setText(bikeName);
        if(gender.equals("Male")){
            spnGender.setSelection(1);
        }else if(gender.equals("Female")){
            spnGender.setSelection(2);
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        edtEmail.setText(firebaseUser.getEmail());
        editPassword.setText(password);

        edtName.setEnabled(false);
        edtPhone.setEnabled(false);
        edtAddress.setEnabled(false);
        edtEmail.setEnabled(false);
        editPassword.setEnabled(false);

        tvEmailStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkEmailIsVerified()){
                    sendVerificationEmail();
                }
            }
        });

        //Edit sign clicks code
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    edtName.setEnabled(true);
                    edtName.requestFocus();
                    edtPhone.setEnabled(true);
                    edtAddress.setEnabled(true);
                }else {
                    edtName.setEnabled(false);
                    edtPhone.setEnabled(false);
                    edtAddress.setEnabled(false);
                }
            }
        });
        //Update button click code
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edtName.getText().toString().trim();
                userPhone = edtPhone.getText().toString().trim();
                bikeName = edtAddress.getText().toString().trim();
                gender = spnGender.getSelectedItem().toString();

                //Validations to all data fields..
                if(TextUtils.isEmpty(userName)){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userPhone)){
                    edtPhone.setError("Required!");
                    edtPhone.requestFocus();
                    return;
                }
                if(userPhone.length()>10 || userPhone.length()<10){
                    edtPhone.setError("Enter valid phone number!!");
                    edtPhone.requestFocus();
                }
                if(TextUtils.isEmpty(bikeName)){
                    edtAddress.setError("Required!");
                    edtAddress.requestFocus();
                    return;
                }
                if(gender.equals("Select Gender")){
                    Toast.makeText(getApplicationContext(), "Please select gender!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateAccount();
            }
        });

        //Send update password email code..
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        calculateRating();
    }

    private void calculateRating() {
        showProgressDialog("Calculating Rating..");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DriverRatings").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalRating = 0;
                counter = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    RatingModelClass model = snapshot1.getValue(RatingModelClass.class);
                    totalRating = totalRating + model.getRating();
                    counter = counter + 1;
                }
                if(counter>0){
                    float rating = (totalRating / counter);

                    if(rating>0 && rating<=1){
                        tvRated.setText("Very Bad Driver");
                    }else if(rating>1 && rating<=2){
                        tvRated.setText("Bad Driver");
                    }else if(rating>2. || rating<=3){
                        tvRated.setText("Good Driver");
                    }else if(rating>3 || rating<=4){
                        tvRated.setText("Very Good Driver");
                    }else if(rating==4 || rating<=5){
                        tvRated.setText("Excellent Driver");
                    }else if(rating==0){
                        tvRated.setText("Not Rated Yet");
                    }
                    hideProgressDialog();

                }else {
                    Toast.makeText(getApplicationContext(), "You didn't rated yet!", Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }

    //Update account details function..
    private void updateAccount() {
        DriverModelCLass model = new DriverModelCLass(userId,userName,email,userPhone,bikeName,
                password,DriverActivity.token,DriverActivity.userType, DriverActivity.latitude,DriverActivity.longitude,
                gender,DriverActivity.status,emailVerified);
        databaseReference.child(userId).setValue(model);
        edtName.setEnabled(false);
        edtPhone.setEnabled(false);
        edtAddress.setEnabled(false);
        editPassword.setEnabled(false);

        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!checkEmailIsVerified()){
            tvEmailStatus.setText("Email not Verified, CLick to verify");
            emailVerified = false;
        }else {
            tvEmailStatus.setText("Email Verified");
            emailVerified = true;
        }
    }

    //Verification method...
    private boolean checkEmailIsVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            return true;
        }else {
            return false;
        }
    }

    private void sendVerificationEmail() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Verification email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

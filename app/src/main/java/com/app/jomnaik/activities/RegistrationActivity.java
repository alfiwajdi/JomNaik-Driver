package com.app.jomnaik.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.jomnaik.R;
import com.app.jomnaik.models.DriverModelCLass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Sign up form screen fro getting name, address etc from user
public class RegistrationActivity extends BaseActivity {

    EditText edtName, edtPhone, edtMotorcycle;
    Button btnRegister;
    String fullName, phone, address, token, gender;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    LocationListener locationListener;
    Spinner spnGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Firebase database and authentication initialization...

        mAuth = FirebaseAuth.getInstance();

        //Get device token for sending notifications using fcm..
        SharedPreferences e = getSharedPreferences("token",MODE_PRIVATE);
        token = e.getString("id","null");

        //All the Views of screen and firebase initialization..
        edtName = findViewById(R.id.edtName);
        spnGender = findViewById(R.id.spnGender);
        edtPhone = findViewById(R.id.edtPhone);
        edtMotorcycle = findViewById(R.id.edtMotorcycle);
        btnRegister = findViewById(R.id.btnRegister);

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {

            }
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = edtName.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();
                address = edtMotorcycle.getText().toString().trim();
                gender = spnGender.getSelectedItem().toString();

                if(TextUtils.isEmpty(fullName)){
                   edtName.setError("Required!");
                   edtName.requestFocus();
                   return;
                }
                if(TextUtils.isEmpty(phone)){
                    edtPhone.setError("Required!");
                    edtPhone.requestFocus();
                    return;
                }
                if(phone.length()>10 || phone.length()<10){
                    edtPhone.setError("Enter valid phone number!!");
                    edtPhone.requestFocus();
                }
                if(TextUtils.isEmpty(address)){
                    edtMotorcycle.setError("Required!");
                    edtMotorcycle.requestFocus();
                    return;
                }

                if(gender.equals("Select Gender")){
                    Toast.makeText(getApplicationContext(), "Please select gender!", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccount();
            }
        });
    }

    //This method store all the data fileds of user to firebase...
    private void createAccount(){
        getUserLocation();
    }

    private void getUserLocation() {

        showProgressDialog("Getting Your Current Location..");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DriversData");
                DriverModelCLass model = new DriverModelCLass(userId,fullName,AuthenticationActivity.email,phone,address,
                        AuthenticationActivity.password,token,"Driver", location.getLatitude(),location.getLongitude(),gender,true,VerificationActivity.emailVerified);
                databaseReference.child(userId).setValue(model);
                Toast.makeText(getApplicationContext(), "Your details saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }};

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
                }
            }
        }
    }
}

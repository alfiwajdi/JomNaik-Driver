package com.app.jomnaik.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.app.jomnaik.R;
import com.app.jomnaik.helpers.MyFirebaseInstanceService;
import com.app.jomnaik.models.BookingModelClass;
import com.app.jomnaik.models.DriverModelCLass;
import com.app.jomnaik.models.RatingModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Locale;

//Driver Home Page Screen Activity File.
public class DriverActivity extends BaseActivity {

    Button btnViewHistory,btnRatings, btnProfile,btnAboutApp, btnSignOut;
    TextView tvName;
    Switch swAvailable;
    String userId="";
    DatabaseReference databaseReference, dbRef;
    public static String fullName, phone, bikeName, token, userType, email, password, gender;
    public static double latitude=0, longitude=0;
    public static boolean emailVerified, status;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alertDialog, alertDialog2;
    public static BookingModelClass model2;
    float totalRating = 0;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);


        //Firebase realtime database initialization for driver.
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("DriversData").child(userId);
        dbRef = FirebaseDatabase.getInstance().getReference("BookingRequests");

        //Views initialization of driver screen.
        tvName = findViewById(R.id.tvName);
        swAvailable = findViewById(R.id.swAvailable);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnRatings = findViewById(R.id.btnRatings);
        btnProfile = findViewById(R.id.btnProfile);
        btnAboutApp = findViewById(R.id.btnAboutApp);
        btnSignOut = findViewById(R.id.btnSignOut);

        swAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    databaseReference.child("available").setValue(true);
                }else {
                    databaseReference.child("available").setValue(false);
                }
            }
        });
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailVerified){
                    Intent intent = new Intent(getApplicationContext(), ViewHistoryActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Please verify your email first from profile!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailVerified){
                    Intent intent = new Intent(getApplicationContext(), ViewRatingActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Please verify your email first from profile!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //View Profile button code
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverProfileActivity.class);
                startActivity(intent);
            }
        });

        btnAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutAppActivity.class);
                startActivity(intent);
            }
        });
        //Sign Out button code
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
                builder.setTitle("Confirmation?");
                builder.setMessage("Are you sure to sign out?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //databaseReference.child("token").setValue("null");
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), DriverLoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

//                databaseReference.child("latitude").setValue(latitude);
//                databaseReference.child("longitude").setValue(longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }};

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener);
            }
        }

        if(!checkEmailIsVerified()){
            emailVerified = false;
            tvName.setText("Email not verified");
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("Alert!");
            builder.setMessage("Please verify your email from profile screen for using app properly!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            emailVerified = true;
        }

        checkLocationEnabled();
    }

    private void checkLocationEnabled() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(DriverActivity.this)
                    .setMessage("Location/Gps not enabled, Please enable to use app functions properly")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    //Getting current logged in driver data from firebase realtime database.
    private void loadUserData() {

        showProgressDialog("Preparing app functions..");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DriverModelCLass model = snapshot.getValue(DriverModelCLass.class);
                fullName = model.getFullName();
                bikeName = model.getAddress();
                phone = model.getPhone();
                password = model.getPassword();
                email = model.getEmail();
                userType = model.getUserType();
                token = model.getToken();
                latitude = model.getLatitude();
                longitude = model.getLongitude();
                gender = model.getGender();
                status = model.isAvailable();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                tvName.setText("Driver : "+fullName);

                if(status){
                    swAvailable.setChecked(true);
                }else {
                    swAvailable.setChecked(false);
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { hideProgressDialog();}});
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadUserData();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    BookingModelClass model = snapshot1.getValue(BookingModelClass.class);
                    if(userId.equals(model.getDriverId()) && model.getStatus().equals("Pending")){
                        showRideRequestDialog(model);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showRideRequestDialog(BookingModelClass model) {
        try {
            AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(DriverActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
            dailogBuilder.setView(dialogView);

            final TextView tvGender = dialogView.findViewById(R.id.tvGender);
            final EditText edtPickUp = dialogView.findViewById(R.id.edtPickUp);
            final EditText edtDropOff = dialogView.findViewById(R.id.edtDropOff);
            final Button btnAccept = dialogView.findViewById(R.id.btnAccept);
            final Button btnReject = dialogView.findViewById(R.id.btnReject);

            tvGender.setText("Rider Gender : "+model.getRiderGender());

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(model.getDropOffLati(), model.getDropOffLongi(), 1);
                List<Address> listAddresses2 = geocoder.getFromLocation(model.getPickUpLati(), model.getPickUpLongi(), 1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    String address = "";

                    if (listAddresses.get(0).getAddressLine(0) != null) {
                        address += listAddresses.get(0).getAddressLine(0);
                        edtDropOff.setText(address);
                        edtDropOff.setEnabled(false);
                    }else {
                        if (listAddresses.get(0).getThoroughfare() != null) {
                            address += listAddresses.get(0).getThoroughfare() + ", ";
                        }
                        if (listAddresses.get(0).getLocality() != null) {
                            address += listAddresses.get(0).getLocality() + ", ";
                        }
                        if (listAddresses.get(0).getAdminArea() != null) {
                            address += listAddresses.get(0).getAdminArea()+", ";
                        }
                        if (listAddresses.get(0).getCountryName() != null) {
                            address += listAddresses.get(0).getCountryName();
                            edtDropOff.setText(address);
                            edtDropOff.setEnabled(false);
                        }
                    }
                }

                if (listAddresses2 != null && listAddresses2.size() > 0) {
                    String address = "";

                    if (listAddresses2.get(0).getAddressLine(0) != null) {
                        address += listAddresses2.get(0).getAddressLine(0);
                        edtPickUp.setText(address);
                        edtPickUp.setEnabled(false);
                    }else {
                        if (listAddresses2.get(0).getThoroughfare() != null) {
                            address += listAddresses2.get(0).getThoroughfare() + ", ";
                        }
                        if (listAddresses2.get(0).getLocality() != null) {
                            address += listAddresses2.get(0).getLocality() + ", ";
                        }
                        if (listAddresses2.get(0).getAdminArea() != null) {
                            address += listAddresses2.get(0).getAdminArea()+", ";
                        }
                        if (listAddresses2.get(0).getCountryName() != null) {
                            address += listAddresses2.get(0).getCountryName();
                            edtPickUp.setText(address);
                            edtPickUp.setEnabled(false);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookingModelClass modelClass = new BookingModelClass(model.getId(),model.getRiderId(),model.getRiderName(),model.getRiderPhone(),model.getRiderGender(),
                            bikeName,model.getDriverId(),model.getDriverName(),model.getDriverPhone(),model.getPickUpLati(),model.getPickUpLongi(),model.getDropOffLati(),
                            model.getDropOffLongi(),"Accept");
                    dbRef.child(model.getId()).setValue(modelClass);
                    alertDialog.dismiss();

                    new MyFirebaseInstanceService().sendMessageSingle(DriverActivity.this, model.getRiderToken(), "Driver Response", "Driver accept your trip", null);
                    databaseReference.child("available").setValue(false);
                    model2 = model;
                    startActivity(new Intent(getApplicationContext(), ViewRideActivity.class));
                    finish();
                }
            });
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookingModelClass modelClass = new BookingModelClass(model.getId(),model.getRiderId(),model.getRiderName(),model.getRiderPhone(),model.getRiderGender(),
                            model.getRiderToken(),model.getDriverId(),model.getDriverName(),model.getDriverPhone(),model.getPickUpLati(),model.getPickUpLongi(),
                            model.getDropOffLati(),model.getDropOffLongi(),"Reject");
                    dbRef.child(model.getId()).setValue(modelClass);

                    new MyFirebaseInstanceService().sendMessageSingle(DriverActivity.this, model.getRiderToken(), "Driver Response", "Driver reject your trip", null);
                    alertDialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), DriverActivity.class));
                    finish();
                }
            });
            alertDialog = dailogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
            e.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }
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

                    showRatingDialog(rating);
                }else {
                    Toast.makeText(getApplicationContext(), "You didn't rated yet!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }

    private void showRatingDialog(float rating) {
        hideProgressDialog();
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DriverActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_layout2, null);
            dialogBuilder.setView(dialogView);

            final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
            final TextView tvRating = dialogView.findViewById(R.id.tvRating);
            final Button btnClose = dialogView.findViewById(R.id.btnClose);

            ratingBar.setActivated(false);
            ratingBar.setIsIndicator(true);
            ratingBar.setRating(rating);

            if(rating==0.5 || rating==1){
                tvRating.setText("Very Bad");
            }else if(rating==1.5 || rating==2){
                tvRating.setText("Bad");
            }else if(rating==2.5 || rating==3){
                tvRating.setText("Good");
            }else if(rating==3.5 || rating==4){
                tvRating.setText("Very Good");
            }else if(rating==4.5 || rating==5){
                tvRating.setText("Excellent");
            }else if(rating==0){
                tvRating.setText("");
            }

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog2.dismiss();
                }
            });
            alertDialog2 = dialogBuilder.create();
            alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog2.setCanceledOnTouchOutside(false);
            alertDialog2.show();
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
            e.printStackTrace();
            hideProgressDialog();
        }catch (Exception ex){
            ex.printStackTrace();
            hideProgressDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    private boolean checkEmailIsVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            return true;
        }else {
            return false;
        }
    }
}
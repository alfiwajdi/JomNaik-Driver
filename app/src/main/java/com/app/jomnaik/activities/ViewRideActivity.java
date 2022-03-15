package com.app.jomnaik.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jomnaik.R;
import com.app.jomnaik.models.BookingModelClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class ViewRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap mMap;
    MapView mapView;
    LocationManager locationManager;
    LocationListener locationListener;
    String userId;
    Context context = ViewRideActivity.this;
    DatabaseReference databaseReference;
    Location location1, location2;
    Button btnArrived, btnStartTrip, btnEndTrip;
    EditText edtPickUp, edtDropOff;
    TextView tvRider;
    ImageView imgPhone, tvWeatherPickUp, tvWeatherDropOff;
    BookingModelClass model;
    public static int REQUEST_PHONE_CALL = 2;
    boolean mGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
                }
            }
        }
        if (requestCode == REQUEST_PHONE_CALL && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                mGranted = true;
            } else {
                Toast.makeText(getApplicationContext(), "Please Allow permission to make call", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ride_request);

        model = DriverActivity.model2;
        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("BookingRequests");

        mapView = findViewById(R.id.mapView);
        tvRider = findViewById(R.id.tvRider);
        tvWeatherPickUp = findViewById(R.id.tvWeatherPickUp);
        tvWeatherDropOff = findViewById(R.id.tvWeatherDropOff);
        imgPhone = findViewById(R.id.imgPhone);
        edtPickUp = findViewById(R.id.edtPickUp);
        edtDropOff = findViewById(R.id.edtDropOff);
        btnArrived = findViewById(R.id.btnArrived);
        btnStartTrip = findViewById(R.id.btnStartTrip);
        btnEndTrip = findViewById(R.id.btnEndTrip);

        tvRider.setText(model.getRiderName());

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

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        location1 = new Location("Location1");
        location2 = new Location("Location2");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("BookingRequests");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    BookingModelClass bookingModel = snapshot1.getValue(BookingModelClass.class);
                    if(userId.equals(bookingModel.getDriverId()) && model.getId().equals(bookingModel.getId()) && model.getStatus().equals("Cancelled")){
                        Toast.makeText(getApplicationContext(), "Ride is cancelled by rider!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});

        tvWeatherPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                intent.putExtra("lati",model.getPickUpLati());
                intent.putExtra("longi",model.getPickUpLongi());
                startActivity(intent);
            }
        });
        tvWeatherDropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                intent.putExtra("lati",model.getDropOffLati());
                intent.putExtra("longi",model.getDropOffLongi());
                startActivity(intent);
            }
        });
        imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mGranted) {
                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            return;
                        }
                    }
                }
                String phone = model.getRiderPhone();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel: +6"+phone));
                startActivity(intent);
            }
        });
        btnArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(model.getId()).child("status").setValue("Arrived");
                btnArrived.setVisibility(View.GONE);
                btnStartTrip.setVisibility(View.VISIBLE);

//                location2.setLatitude(model.getPickUpLati());
//                location2.setLongitude(model.getPickUpLongi());
//
//                distance = location1.distanceTo(location2);
//
//                if(distance<=1){
//                    databaseReference.child(model.getId()).child("status").setValue("Arrived");
//                    btnArrived.setVisibility(View.GONE);
//                    btnStartTrip.setVisibility(View.VISIBLE);
//                }else {
//                    Toast.makeText(getApplicationContext(), "Your location doesn't matched with pick up location!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(model.getId()).child("status").setValue("Started");
                btnStartTrip.setVisibility(View.GONE);
                btnEndTrip.setVisibility(View.VISIBLE);
            }
        });
        btnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(model.getId()).child("status").setValue("Completed");
                btnEndTrip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Trip Completed", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewRideActivity.this);
                builder.setTitle("Great");
                builder.setMessage("Thanks for your volunteer work, Please keep doing your great work!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), DriverActivity.class));
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

//                location2.setLatitude(model.getDropOffLati());
//                location2.setLongitude(model.getDropOffLongi());
//
//                distance = location1.distanceTo(location2);
//
//                if(distance<=1){
//                    databaseReference.child(model.getId()).child("status").setValue("Completed");
//                    btnEndTrip.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Trip Completed", Toast.LENGTH_SHORT).show();
//                    finish();
//                }else {
//                    Toast.makeText(getApplicationContext(), "Your location doesn't matched with drop-off location!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        LatLng userLocation2 = new LatLng(model.getPickUpLati(), model.getPickUpLongi());
        mMap.addMarker(new MarkerOptions().position(userLocation2).title("Pick Up Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation2,10));

        LatLng userLocation = new LatLng(model.getDropOffLati(), model.getDropOffLongi());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Drop-Off").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));

        mMap.addPolyline(
                new PolylineOptions()
                        .add(userLocation)
                        .add(userLocation2)
                        .width(5f)
                        .color(Color.RED)
        );
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));

                location1.setLatitude(location.getLatitude());
                location1.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }
            @Override
            public void onProviderEnabled(String s) { }
            @Override
            public void onProviderDisabled(String s) { }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        hideProgressDialog();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private ProgressDialog mProgressDialog;

    //This function show progress dialog on screen with user custom message.
    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    //This function hide progress dialog from screen.
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRideActivity.this);
        builder.setTitle("Confirmation?");
        builder.setMessage("Are you sure to cancel the ride?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("BookingRequests").child(model.getId());
                dbRef.child("status").setValue("Cancelled");
                startActivity(new Intent(getApplicationContext(), DriverActivity.class));
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
}
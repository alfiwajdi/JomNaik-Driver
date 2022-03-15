package com.app.jomnaik.models;

import android.content.Intent;

import java.util.List;

public class DriverModelCLass {
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String token;
    private String userType;
    private double latitude;
    private double longitude;
    private String gender;
    private boolean available;
    private boolean emailVerified;

    public DriverModelCLass() { }

    public DriverModelCLass(String userId, String fullName, String email, String phone, String address,String password, String token,
                            String userType, double latitude, double longitude, String gender, boolean available, boolean emailVerified) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.token = token;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gender = gender;
        this.available = available;
        this.emailVerified = emailVerified;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getUserType() {
        return userType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGender() {
        return gender;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}

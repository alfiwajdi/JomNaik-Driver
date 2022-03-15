package com.app.jomnaik.models;

import android.location.Location;

public class BookingModelClass {
    private String id;
    private String riderId;
    private String riderName;
    private String riderPhone;
    private String riderGender;
    private String riderToken;
    private String driverId;
    private String driverName;
    private String driverPhone;
    private double pickUpLati;
    private double pickUpLongi;
    private double dropOffLati;
    private double dropOffLongi;
    private String status;

    public BookingModelClass() { }


    public BookingModelClass(String id, String riderId, String riderName, String riderPhone, String riderGender, String riderToken, String driverId, String driverName, String driverPhone,
                             double pickUpLati, double pickUpLongi, double dropOffLati, double dropOffLongi, String status) {
        this.id = id;
        this.riderId = riderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.riderGender = riderGender;
        this.riderToken = riderToken;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.pickUpLati = pickUpLati;
        this.pickUpLongi = pickUpLongi;
        this.dropOffLati = dropOffLati;
        this.dropOffLongi = dropOffLongi;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public String getRiderGender() {
        return riderGender;
    }

    public String getRiderToken() {
        return riderToken;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public double getPickUpLati() {
        return pickUpLati;
    }

    public double getPickUpLongi() {
        return pickUpLongi;
    }

    public double getDropOffLati() {
        return dropOffLati;
    }

    public double getDropOffLongi() {
        return dropOffLongi;
    }

    public String getStatus() {
        return status;
    }
}

package com.app.jomnaik.models;

public class RatingModelClass {
    private String id;
    private float rating;
    private String riderId;
    private String riderName;
    private String driverId;
    private String bookingId;

    public RatingModelClass() { }

    public RatingModelClass(String id, float rating, String riderId, String riderName, String driverId, String bookingId) {
        this.id = id;
        this.rating = rating;
        this.riderId = riderId;
        this.riderName = riderName;
        this.driverId = driverId;
        this.bookingId = bookingId;
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getBookingId() {
        return bookingId;
    }
}

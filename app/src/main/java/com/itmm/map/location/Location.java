package com.itmm.map.location;

/**
 * Created by Дмитрий on 11/12/2016.
 */

public class Location {

    double longitude;
    double latitude;
    String title;

    public Location() {

    }

    public Location (double longitude, double latitude, String title) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

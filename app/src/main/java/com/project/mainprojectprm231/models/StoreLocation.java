package com.project.mainprojectprm231.models;
import java.io.Serializable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class StoreLocation implements Serializable{
    @SerializedName("locationId")
    private int id;
    private double latitude;
    private double longitude;
    private String address;

    public StoreLocation(int id, double latitude, double longitude, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NonNull
    @Override
    public String toString() {
        return address; // Để hiển thị trong Spinner
    }
}
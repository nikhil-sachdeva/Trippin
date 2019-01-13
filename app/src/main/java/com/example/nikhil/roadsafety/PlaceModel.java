package com.example.nikhil.roadsafety;

public class PlaceModel {
    private double latitude;
    private double longitude;
    private String placeName;
    private String vicinity;

    public PlaceModel(double latitude, double longitude, String placeName, String vicinity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.vicinity = vicinity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}

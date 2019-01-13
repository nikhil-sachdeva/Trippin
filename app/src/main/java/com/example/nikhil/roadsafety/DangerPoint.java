package com.example.nikhil.roadsafety;

import com.mmi.util.GeoPoint;

public class DangerPoint {

    private GeoPoint geoPoint;
    private double dangerIndex;

    public DangerPoint(GeoPoint geoPoint, double dangerIndex) {
        this.geoPoint = geoPoint;
        this.dangerIndex = dangerIndex;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public double getDangerIndex() {
        return dangerIndex;
    }

    public void setDangerIndex(double dangerIndex) {
        this.dangerIndex = dangerIndex;
    }
}

package com.example.nikhil.roadsafety;

import com.mmi.util.GeoPoint;

import java.util.ArrayList;

public class RouteGeopoints {

    public static enum Color{
        DANGEROUS,
        MODERATE,
        SAFE
    }

    private static final double MODERATE_LEVEL = 4.5;
    private static final double DANGEROUS_LEVEL = 7.5;

    private ArrayList<GeoPoint> route;
    Color color;

    public RouteGeopoints(ArrayList<GeoPoint> route, Color color ) {
        this.route = route;
        this.color = color;

    }

    public ArrayList<GeoPoint> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<GeoPoint> route) {
        this.route = route;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

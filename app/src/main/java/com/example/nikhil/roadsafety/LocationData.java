package com.example.nikhil.roadsafety;

public class LocationData {

    private String name;
    private double distance;
    private double lat;
    private double lang;
    private double score;

    private String zone;


    public LocationData(String name, double distance, double lat, double lang, double score, String zone) {
        this.name = name;
        this.distance = distance;
        this.lat = lat;
        this.lang = lang;

        this.score = score;
        this.zone = zone;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public double getScore() {
        return score;
    }

    public String getZone() {
        return zone;
    }

    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }
}

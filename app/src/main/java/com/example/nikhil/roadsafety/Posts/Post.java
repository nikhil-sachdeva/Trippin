package com.example.nikhil.roadsafety.Posts;

import java.util.HashMap;
import java.util.Map;

public class Post {

    String caption;
    String name;
    String location;
    double longitude,latitude;
    String imgURI;
    String resolved;




    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("caption", caption);
        result.put("imgURI",imgURI);
        result.put("location",location);
        result.put("name",name);
        result.put("resolved",resolved);

        return result;
    }

    public String getImgURI() {
        return imgURI;
    }

    public void setImgURI(String imgURI) {
        this.imgURI = imgURI;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolved() {
        return resolved;
    }

    public void setResolved(String resolved) {
        this.resolved = resolved;
    }
}

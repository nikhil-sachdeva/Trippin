package com.example.nikhil.roadsafety;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.util.GeoPoint;

public class MapsActivity extends AppCompatActivity {

    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();


        mMapView = ((MapmyIndiaMapView) findViewById(R.id.route_map)).getMapView();
        mMapView.setCenter( new GeoPoint((intent.getDoubleExtra("from_lat", 0) +intent.getDoubleExtra("to_lat", 0))/2,
                (intent.getDoubleExtra("from_lang", 0) +intent.getDoubleExtra("to_lang", 0))/2));

        GeoPoint from = new GeoPoint(intent.getDoubleExtra("from_lat", 0), intent.getDoubleExtra("from_lang", 0));
        GeoPoint to = new GeoPoint(intent.getDoubleExtra("to_lat", 0), intent.getDoubleExtra("to_lang", 0));

        addMarker(from);
        addMarker(to);
        RoutesTask findRoutesTask = new RoutesTask(MapsActivity.this, mMapView);
        findRoutesTask.execute(from, to);
    }

    private void addMarker(GeoPoint geoPoint){
        BasicInfoWindow infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);
        Marker marker = new Marker(mMapView);
        marker.setTitle("Title");
        marker.setDescription("description");
        marker.setSubDescription("subdescription");
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(infoWindow);
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
    }

}

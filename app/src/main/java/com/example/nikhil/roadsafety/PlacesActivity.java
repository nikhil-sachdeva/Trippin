package com.example.nikhil.roadsafety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;

public class PlacesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mapView = ((MapmyIndiaMapView) findViewById(R.id.places_map)).getMapView();

        Bundle bundle = new Bundle();
        Log.i("MyLogs", "Lat in PlacesActivity: "+getIntent().getDoubleExtra("LATITUDE", 0)+
                getIntent().getStringExtra("PLACE"));
        bundle.putDouble("LATITUDE", getIntent().getDoubleExtra("LATITUDE", 0));
        bundle.putDouble("LONGITUDE", getIntent().getDoubleExtra("LONGITUDE", 0));
        bundle.putString("PLACE", getIntent().getStringExtra("PLACE"));

        recyclerView = findViewById(R.id.places_recycler);

        PlacesTask placesTask = new PlacesTask(PlacesActivity.this, mapView, recyclerView);
        placesTask.execute(bundle);

    }
}

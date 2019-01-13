package com.example.nikhil.roadsafety;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DangerIndexActivity extends AppCompatActivity {

    private static final String TAG = "MyLogs";
    AccidentDatabaseHelper dbHelper;
    Location myLocation;
    LocationAdapter adapter;
    RecyclerView recyclerView;
    TextView indexView;
    TextView constituencyLabel;
    TextView constituencyIdx;
    ImageView currentLocation, otherLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_index);

        recyclerView = findViewById(R.id.recycler);
        indexView = findViewById(R.id.index);
        constituencyLabel = findViewById(R.id.label);
        constituencyIdx = findViewById(R.id.label_index);
        currentLocation = findViewById(R.id.current_location);
        otherLocation = findViewById(R.id.autocomplete_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        dbHelper = new AccidentDatabaseHelper(getApplicationContext());
        myLocation = new Location("");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(DangerIndexActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat;
                            double lang;
                            if(location!=null) {
                                lat = location.getLatitude();
                                lang = location.getLongitude();
                            }
                            else {
                                lat = 28.54717020;
                                lang = 77.1980858;
                            }

                            myLocation.setLatitude(lat);
                            myLocation.setLongitude(lang);

                            Log.d(TAG, "onSuccess: "+lang+","+lat);
                            try {
                                getDatabase();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.i("Messages", "IOException in main()");
                                Log.i("Messages", e.toString());
                            }

                            ArrayList<LocationData> list = getClosest();
                            String zone = list.get(0).getZone();
                            int count = dbHelper.getZonewiseCount(zone);
                            float odds = (float) count/(145-count);
                            double index = average(list)*odds*10;
                            double zoneIndex = dbHelper.getZoneIndex(zone);
                            Log.i("Messages", "Zone index: "+zone+ "::"+dbHelper.getZoneIndex(zone));

                            Log.i("Messages", "Particular zone index: "+"Count: "+count+": "+ average(list)*odds);
                            dbHelper.dropTable();



                            indexView.setText(new DecimalFormat("##.##").format(index)+"");
                            indexView.setText(new DecimalFormat("##.##").format(index)+"");
                            if(index<=4){
                                Log.i("MyLogs", "Index less than 4");
                                indexView.setTextColor(getResources().getColor(R.color.safeColor));
                            }
                            if(index>4 && index<=7){
                                indexView.setTextColor(getResources().getColor(R.color.moderateColor));
                            }
                            if(index>7 && index<=10){
                                indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                            }
                            constituencyLabel.setText(getConstituency(list.get(0).getZone())+": ");
                            constituencyIdx.setText(new DecimalFormat("##.##").format(zoneIndex)+"");
                            if(zoneIndex<=4){
                                constituencyIdx.setTextColor(getResources().getColor(R.color.safeColor));
                            }
                            if(zoneIndex>4 && zoneIndex<=7){
                                constituencyIdx.setTextColor(getResources().getColor(R.color.moderateColor));
                            }
                            if(zoneIndex>7 && zoneIndex<=10){
                                indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                            }

                            adapter = new LocationAdapter(list, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


                        }

                    }
                });



        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(DangerIndexActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double lat;
                                    double lang;
                                    if(location!=null) {
                                        lat = location.getLatitude();
                                        lang = location.getLongitude();
                                    }
                                    else {
                                        lat = 28.54717020;
                                        lang = 77.1980858;
                                    }

                                    myLocation.setLatitude(lat);
                                    myLocation.setLongitude(lang);

                                    Log.d(TAG, "onSuccess: "+lang+","+lat);
                                    try {
                                        getDatabase();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.i("Messages", "IOException in main()");
                                        Log.i("Messages", e.toString());
                                    }

                                    ArrayList<LocationData> list = getClosest();
                                    String zone = list.get(0).getZone();
                                    int count = dbHelper.getZonewiseCount(zone);
                                    float odds = (float) count/(145-count);
                                    double index = average(list)*odds*10;
                                    double zoneIndex = dbHelper.getZoneIndex(zone);
                                    Log.i("Messages", "Zone index: "+zone+ "::"+dbHelper.getZoneIndex(zone));

                                    Log.i("Messages", "Particular zone index: "+"Count: "+count+": "+ average(list)*odds);
                                    dbHelper.dropTable();
                                    Log.i("MyLogs", index+"");

                                    indexView.setText(new DecimalFormat("##.##").format(index)+"");
                                    if(index<=4){
                                        Log.i("MyLogs", "Index less than 4");
                                        indexView.setTextColor(getResources().getColor(R.color.safeColor));
                                    }
                                    if(index>4 && index<=7){
                                        indexView.setTextColor(getResources().getColor(R.color.moderateColor));
                                    }
                                    if(index>7 && index<=10){
                                        indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                                    }
                                    constituencyLabel.setText(getConstituency(list.get(0).getZone())+": ");
                                    constituencyIdx.setText(new DecimalFormat("##.##").format(zoneIndex)+"");
                                    if(zoneIndex<=4){
                                        constituencyIdx.setTextColor(getResources().getColor(R.color.safeColor));
                                    }
                                    if(zoneIndex>4 && zoneIndex<=7){
                                        constituencyIdx.setTextColor(getResources().getColor(R.color.moderateColor));
                                    }
                                    if(zoneIndex>7 && zoneIndex<=10){
                                        indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                                    }

                                    adapter = new LocationAdapter(list, v.getContext());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));


                                }

                            }
                        });

            }
        });

        otherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlaceAutocompleteActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });


        Log.d(TAG, "onCreateView: "+myLocation.getLatitude()+myLocation.getLongitude());
    }

    private void getDatabase() throws IOException {

        InputStream is = getApplicationContext().getAssets().open("AccidentProneAreas.csv");
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line = "";

        int count = 0;
        while ((line = buffer.readLine()) != null) {
            if(count==0){
                count++;
                continue;
            }

            String[] str = line.split(",");
            String name = str[1];
            double lat = Double.parseDouble(str[2]);
            double lang = Double.parseDouble(str[3]);
            int simpleAccidents = Integer.parseInt(str[4]);
            int fatalAccidents = Integer.parseInt(str[5]);
            double dangerPoints = Double.parseDouble(str[6]);
            String zone = str[7];
            double dangerIndex = Double.parseDouble(str[8]);
            double distance = getDistance(lat, lang);
            dbHelper.insertData(name,lat,lang,simpleAccidents,fatalAccidents,dangerPoints,zone,dangerIndex,distance);
        }
    }

    private double getDistance(double lat, double lang){
        Location targetLocation = new Location("");
        targetLocation.setLatitude(lat);
        targetLocation.setLongitude(lang);
        return myLocation.distanceTo(targetLocation)/1000;
    }

    private ArrayList<LocationData> getClosest(){
        ArrayList<LocationData> list= new ArrayList<>();
        Cursor cr = dbHelper.getAll();

        int count = 0;
        while (cr.moveToNext() && count<5){
            list.add(new LocationData(cr.getString(cr.getColumnIndex("name")),
                    cr.getDouble(cr.getColumnIndex("distance")),
                    cr.getDouble(cr.getColumnIndex("lat")),
                    cr.getDouble(cr.getColumnIndex("lang")),
                    cr.getDouble(cr.getColumnIndex("danger_idx")),
                    cr.getString(cr.getColumnIndex("zone"))));
            count++;
            Log.i("Messages", count+". "+
                    cr.getDouble(cr.getColumnIndex("distance")));
        }
        cr.close();
        return list;
    }

    private double average(ArrayList<LocationData> data){
        double avg = 0;
        for(LocationData location : data){
            avg+=location.getScore();
        }
        avg/=data.size();
        return avg;
    }

    private String getConstituency(String label) {
        switch (label){
            case "new": return "NEW DELHI";
            case "south": return "SOUTH DELHI";
            case "northeast": return "NORTH-EAST DELHI";
            case "northwest": return "NORTH-WEST DELHI";
            case "east": return "EAST DELHI";
            case "west": return "WEST DELHI";
            case "chandnichowk": return "CHANDNI CHOWK";
            default: return "Unknown Constituency";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                double lat = intentBundle.getDouble("LATITUDE");
                double lang = intentBundle.getDouble("LONGITUDE");
                myLocation.setLatitude(lat);
                myLocation.setLongitude(lang);
                Log.d(TAG, "onSuccess: "+lang+","+lat);
                try {
                    getDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("Messages", "IOException in main()");
                    Log.i("Messages", e.toString());
                }

                ArrayList<LocationData> list = getClosest();
                String zone = list.get(0).getZone();
                int count = dbHelper.getZonewiseCount(zone);
                float odds = (float) count/(145-count);
                double index = average(list)*odds*10;
                double zoneIndex = dbHelper.getZoneIndex(zone);
                Log.i("Messages", "Zone index: "+zone+ "::"+dbHelper.getZoneIndex(zone));

                Log.i("Messages", "Particular zone index: "+"Count: "+count+": "+ average(list)*odds);
                dbHelper.dropTable();

                indexView.setText(new DecimalFormat("##.##").format(index)+"");
                if(index<=4){
                    Log.i("MyLogs", "Index less than 4");
                    indexView.setTextColor(getResources().getColor(R.color.safeColor));
                }
                if(index>4 && index<=7){
                    indexView.setTextColor(getResources().getColor(R.color.moderateColor));
                }
                if(index>7 && index<=10){
                    indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                }
                constituencyLabel.setText(getConstituency(list.get(0).getZone())+": ");
                constituencyIdx.setText(new DecimalFormat("##.##").format(zoneIndex)+"");
                if(zoneIndex<=4){
                    constituencyIdx.setTextColor(getResources().getColor(R.color.safeColor));
                }
                if(zoneIndex>4 && zoneIndex<=7){
                    constituencyIdx.setTextColor(getResources().getColor(R.color.moderateColor));
                }
                if(zoneIndex>7 && zoneIndex<=10){
                    indexView.setTextColor(getResources().getColor(R.color.dangerColor));
                }

                adapter = new LocationAdapter(list, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }
        }
    }
}

package com.example.nikhil.roadsafety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class HelpActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;


    double latitude, longitude;
    protected PlaceDetectionClient placeDetectionClient;
    private static final int LOC_REQ_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

    }

    public void emerCALL(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void emergencySMS(String number, Context context) {

        Log.i("MyLogs", "sendSMS called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(HelpActivity.this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        Log.i("MyLogs", "onSuccess: ");
                        if(location!=null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        else {
                            latitude = 28.54717020;
                            longitude = 77.1980858;
                        }
                        Log.i("MyLogs", latitude + "," + longitude);
                    }
                });

        String message = "Help me, I'm in distress. My current location: " + "https://www.google.com/maps/@"+latitude + "," + longitude;
        try {
            SmsManager.getDefault().sendTextMessage(number, null,
                    message, null, null);
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new
                    AlertDialog.Builder(context);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
            Log.i("MyLogs", "emergencySMS: Sms not sent " + e.toString());
        }
    }

    public void sendSMS(View view) {
        ContactDatabaseHelper myDB = new ContactDatabaseHelper(view.getContext());
        Cursor cr = myDB.getNumbers();
        while (cr.moveToNext()) {
            String allowedNumber = cr.getString(cr.getColumnIndexOrThrow("contact_no"));
            Log.i("MyLogs", allowedNumber);
            emergencySMS(allowedNumber, view.getContext());
        }
    }

    public void call(View view) {
        ContactDatabaseHelper myDB = new ContactDatabaseHelper(view.getContext());
        Cursor cr = myDB.getNumbers();
        if(cr.moveToFirst()){
            String allowedNumber = cr.getString(cr.getColumnIndexOrThrow("contact_no"));
            emerCALL(allowedNumber);

        }
    }

    public void police(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(HelpActivity.this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        Log.i("MyLogs", "onSuccess: ");
                        if(location!=null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        else {
                            latitude = 28.54717020;
                            longitude = 77.1980858;
                        }



                        Log.i("MyLogs", "onSuccess: "+latitude+","+longitude);
                        Intent intent = new Intent(HelpActivity.this, PlacesActivity.class);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDE", longitude);
                        intent.putExtra("PLACE", "police");
                        startActivity(intent);
                    }
                });



    }

    public void hospitals(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(HelpActivity.this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        Log.i("MyLogs", "onSuccess: ");
                        if(location!=null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        else {
                            latitude = 28.54717020;
                            longitude = 77.1980858;
                        }
                        Log.i("MyLogs", latitude + "," + longitude);

                        Intent intent = new Intent(HelpActivity.this, PlacesActivity.class);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDE", longitude);
                        intent.putExtra("PLACE", "hospital");
                        startActivity(intent);
                    }
                });


    }
}

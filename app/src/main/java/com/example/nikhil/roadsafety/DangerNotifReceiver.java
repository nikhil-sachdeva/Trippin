package com.example.nikhil.roadsafety;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mmi.util.GeoPoint;

import java.io.IOException;
import java.util.Locale;

public class DangerNotifReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        RoutesTask routesTask = new RoutesTask(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        double lat, lang;
                        if (location != null) {
                            lat = location.getLatitude();
                            lang = location.getLongitude();
                        }
                        else {
                            lat = 28.7324;
                            lang = 77.1442;
                        }
                            String name = null;
                            Geocoder gcd = new Geocoder(context, Locale.getDefault());
                            try {
                                name = gcd.getFromLocation(lat, lang, 1).get(0).getLocality();
                                Toast.makeText(context, "" + name + lang + lat, Toast.LENGTH_SHORT).show();
                                Log.d("Real", "onSuccess: " + lang + lat);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("Real", "onSuccess: " + e.toString());
                            }
                            RoutesTask routesTask = new RoutesTask(context);
                            try {
                                double MyIndex = routesTask.getDangerIndex(new GeoPoint(lang, lat));
                                Log.i("MyLogs", "MyIndex: "+MyIndex);

                                if(MyIndex>=5){
                                    Log.i("MyLogs", "MyIndex is greater");
                                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);



                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                            context)
                                            // Set Icon
                                            .setSmallIcon(R.drawable.add)
                                            // Set Ticker Message
                                            .setTicker("Ticker")
                                            // Set Title
                                            .setContentTitle("Helllooooooo")
                                            // Set Text
                                            .setContentText("Testttt")
                                            // Add an Action Button below Notification
                                            .addAction(R.drawable.check, "Action Button", pIntent)
                                            // Set PendingIntent into Notification
                                            .setContentIntent(pIntent)
                                            // Dismiss Notification
                                            .setAutoCancel(true);

                                    // Create Notification Manager
                                    NotificationManager notificationmanager = (NotificationManager) context
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                                    // Build Notification with Notification Manager
                                    notificationmanager.notify(0, builder.build());

                                }
                                Log.d("Real", "onCreateView: dangerIndex" + routesTask.getDangerIndex(new GeoPoint(lang, lat)));
                            } catch (IOException e) {
                                Log.d("Real", e.toString());
                                e.printStackTrace();
                            }


                        }

                    });

    }
}

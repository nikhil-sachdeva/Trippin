package com.example.nikhil.roadsafety;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nikhil.roadsafety.Posts.CreatePost;
import com.example.nikhil.roadsafety.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mmi.util.GeoPoint;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class DrivingModeFragment extends Fragment {
    int i=0;
    TextView myIndex,DI;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    private FusedLocationProviderClient mFusedLocationClient;
    ToggleButton isDndOn;
    TextView label;
    FloatingActionButton fab;
    BroadcastReceiver incomingCallReceiver;
    public static ArrayList<String> incomingCallList;

    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.driving_fragment,container,false);
        getRequiredPermissions();
        fab = view.findViewById(R.id.floatingActionButton);
        incomingCallReceiver = new IncomingCallReceiver();
        isDndOn = view.findViewById(R.id.dndtoggle);
        label = view.findViewById(R.id.label);
        myIndex=view.findViewById(R.id.my_index);
        DI=view.findViewById(R.id.di);
        incomingCallList = new ArrayList<>();
        DI.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddContacts();
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        isDndOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }
        });
        isDndOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    getRequiredPermissions();
                    IntentFilter filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
                    if(getActivity()!=null) {
                        getActivity().registerReceiver(incomingCallReceiver, filter);
                        Log.i("Messages", "getActivity() is not null");
                    } else {
                        Log.i("Messages", "getActivity() is null");
                    }
                    label.setText("Driving mode enabled.");

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double lat = location.getLatitude();
                                        double lang = location.getLongitude();
                                        String name = null;
                                        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                                        try {
                                            name = gcd.getFromLocation(lat, lang, 1).get(0).getLocality();
                                            Toast.makeText(getActivity(), ""+name+lang+lat, Toast.LENGTH_SHORT).show();
                                            Log.d("Real", "onSuccess: "+lang+lat);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d("Real", "onSuccess: "+e.toString());
                                        }
                                        RoutesTask routesTask = new RoutesTask(getContext());
                                        try {
                                            double MyIndex = routesTask.getDangerIndex(new GeoPoint(lang,lat));
                                            startTimer(MyIndex);
                                            Log.d("Real", "onCreateView: dangerIndex"+routesTask.getDangerIndex(new GeoPoint(lang,lat)));
                                        } catch (IOException e) {
                                            Log.d("Real",e.toString());
                                            e.printStackTrace();
                                        }


                                    }

                                }
                            });


                } else if (!isChecked) {
                    stopTimer();
                    Objects.requireNonNull(getActivity()).unregisterReceiver(incomingCallReceiver);
                    sendAvailableSMS();
                    label.setText("Driving mode disabled.");
                }
            }
        });


        return view;

    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
            DI.setVisibility(View.INVISIBLE);
            myIndex.setVisibility(View.INVISIBLE);
        }
    }


    private void startTimer(final double index){
        mTimer1 = new Timer();

        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        if(DI.getVisibility()==View.INVISIBLE){
                            DI.setVisibility(View.VISIBLE);
                        }
                        Log.d("timer", "run: "+i);
                        i+=1;
                        myIndex.setText(""+Double.toString(index).substring(0,5));
                        if(index<=4){
                            myIndex.setTextColor(getResources().getColor(R.color.safeColor));
                        }
                        if(index>4 && index<=7){
                            myIndex.setTextColor(getResources().getColor(R.color.moderateColor));
                        }
                        if(index>7 && index<=10){
                            myIndex.setTextColor(getResources().getColor(R.color.dangerColor));
                        }
                    }
                });

            }
        };

        mTimer1.schedule(mTt1, 5000);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    isDndOn.setChecked(false);
                    Toast.makeText(getContext(), "DND services cannot function without the required permissions.", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }


    public void onClickAddContacts() {
        Intent intent = new Intent(getContext(), ContactsActivity.class);
        startActivity(intent);

    }

    private void getRequiredPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                    getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED ||
                    getContext().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    private void sendAvailableSMS() {
        if(incomingCallList.size()>0) {
            for(String number: incomingCallList) {
                sendReplySMS(number, getContext());
            }
        }
        incomingCallList.clear();
    }

    private void sendReplySMS(String number, Context context){
        DrivingModeFragment.incomingCallList.add(number);
        String message = "Hi, I'm available to take calls now.";
        try {
            SmsManager.getDefault().sendTextMessage(number, null,
                    message, null, null);
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new
                    AlertDialog.Builder(context);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }
}


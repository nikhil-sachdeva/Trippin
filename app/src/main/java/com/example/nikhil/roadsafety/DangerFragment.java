package com.example.nikhil.roadsafety;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DangerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DangerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DangerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LocationManager locationManager;
    LocationListener locationListener;

    Context context;
    TextView Speed;
    TextView danger;
    TextView mySpeed;
    TextView myDanger;
    double curTime = 0;
    double oldLat = 0.0;
    double oldLon = 0.0;

    public double calculationBydistance(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

    private void getspeed(Location location) {
        double newTime = System.currentTimeMillis();
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        if (location.hasSpeed()) {
            float speed = location.getSpeed();
            Log.i("speed", String.valueOf(speed*3.6));
            //Toast.makeText(getApplication(),"SPEED : "+String.valueOf(speed)+"m/s",Toast.LENGTH_SHORT).show();
            Speed.setText(String.valueOf(speed*3.6) + "km/hr");
        } else {
            double distance = calculationBydistance(newLat, newLon, oldLat, oldLon);
            double timeDifferent = newTime - curTime;
            double speed = distance / timeDifferent;
            curTime = newTime;
            oldLat = newLat;
            oldLon = newLon;
            Speed.setText(String.valueOf(speed*3600) + "km/hr");
            //Toast.makeText(getApplication(),"SPEED 2 : "+String.valueOf(speed)+"m/s",Toast.LENGTH_SHORT).show();
        }
        Speed.setTextColor(this.getResources().getColor(R.color.speedColor));
        Speed.setTextSize(30);
    }

    private OnFragmentInteractionListener mListener;

    public DangerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DangerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DangerFragment newInstance(String param1, String param2) {
        DangerFragment fragment = new DangerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        locationManager = (LocationManager) container.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getspeed(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //if user has already given permissions, we can ask for some new permissions.
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        View view = inflater.inflate(R.layout.fragment_danger, container, false);
        Speed = view.findViewById(R.id.speedTextView);
        danger = view.findViewById(R.id.textView2);
        mySpeed = view.findViewById(R.id.textView3);
        myDanger = view.findViewById(R.id.MyDangerTextView);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

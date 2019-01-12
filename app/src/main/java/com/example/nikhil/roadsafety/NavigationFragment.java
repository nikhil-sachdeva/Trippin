package com.example.nikhil.roadsafety;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mmi.LicenceManager;
import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.layers.PathOverlay;
import com.mmi.util.GeoPoint;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mMapView;
    FloatingActionButton startTrip;

    private final int TO_AUTOCOMPLETE = 101;
    private final int FROM_AUTOCOMPLETE = 102;
    private final int AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE = 1001;
    GeoPoint to, from;
    EditText fromEditText, toEditText;

    private OnFragmentInteractionListener mListener;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String param1, String param2) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LicenceManager.getInstance().setRestAPIKey(getString(R.string.mmi_rest_api_key));
        LicenceManager.getInstance().setMapSDKKey(getString(R.string.mmi_sdk_key));
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

        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.map)).getMapView();

        toEditText = view.findViewById(R.id.to_edittext);
        fromEditText = view.findViewById(R.id.from_edittext);
        startTrip = view.findViewById(R.id.startTrip);
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("to_lat", to.getLatitude());
                intent.putExtra("to_lang", to.getLongitude());
                intent.putExtra("from_lat", from.getLatitude());
                intent.putExtra("from_lang", from.getLongitude());
                startActivity(intent);

            }
        });
        toEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAutocomplete(TO_AUTOCOMPLETE);
            }
        });

        fromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAutocomplete(FROM_AUTOCOMPLETE);
            }
        });

        GeoPoint home = new GeoPoint(28.6129, 77.2295);

        addMarker(new GeoPoint(home.getLatitude()-0.0001, home.getLatitude()-0.00001));

//        mMapView.setCenter(new GeoPoint(home.getLatitude()-0.0001, home.getLatitude()-0.0001));
        mMapView.setZoom(14);
        addMarker(home);


//        GeoPoint dtu = new GeoPoint(28.7501, 77.1177);

//        addMarker(home);
//        addMarker(dtu);
//        RoutesTask findRoutesTask = new RoutesTask(view.getContext(), mMapView);
//        findRoutesTask.execute(home, dtu)



        return view;
//        return inflater.inflate(R.layout.fragment_navigation, container, false);
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

    private void addPolyline(GeoPoint from, GeoPoint to) {
        ArrayList<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(from);
        geoPoints.add(new GeoPoint(from.getLatitude()+0.0001, from.getLongitude()+0.0001));

        PathOverlay pathOverlay = new PathOverlay(getActivity());
        pathOverlay.setColor(getResources().getColor(R.color.baseColor));
        pathOverlay.setWidth(10);
        pathOverlay.setPoints(geoPoints);
        mMapView.getOverlays().add(pathOverlay);
        mMapView.invalidate();
    }

    public void callAutocomplete(int code) {
        Intent intent = new Intent(getContext(), PlaceAutocompleteActivity.class);
        intent.putExtra("code", code);
        startActivityForResult(intent, AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                double lat = intentBundle.getDouble("LATITUDE");
                double lang = intentBundle.getDouble("LONGITUDE");
                String placeName = intentBundle.getString("NAME");
                int code = intentBundle.getInt("CODE");

                if (code == TO_AUTOCOMPLETE){
                    to = new GeoPoint(lat, lang);
                    toEditText.setText(placeName);
                }

                if (code == FROM_AUTOCOMPLETE){
                    from = new GeoPoint(lat, lang);
                    fromEditText.setText(placeName);
                }
            }
        }
    }
}


package com.example.nikhil.roadsafety;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mmi.MapView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.util.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PlacesTask extends AsyncTask<Bundle, Void, ArrayList<PlaceModel>> {


    private ProgressDialog progressDialog;
    private Context context;
    private MapView mMapView;
    RecyclerView recyclerView;


    public PlacesTask(Context context, MapView mMapView, RecyclerView recyclerView) {
        this.context = context;
        this.mMapView = mMapView;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching routes, please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    @Override
    protected ArrayList<PlaceModel> doInBackground(Bundle... bundles) {

        HttpURLConnection urlConn;
        BufferedReader bufferedReader = null;

        double lat = bundles[0].getDouble("LATITUDE"), lang = bundles[0].getDouble("LONGITUDE");
        String place = bundles[0].getString("PLACE");

        String Url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + lat + "," + lang +
                "&rankby=distance&types=" + place + "&sensor=false&key=" + context.getString(R.string.autocomplete_api_key);

        Log.i("MyLogs", Url);

        try {
            Log.i("MyLogs", "Entered try block");
            URL url = new URL(Url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            StringBuilder inline = new StringBuilder();

            if (urlConn.getResponseCode() != 200)
                Log.i("MyLogs", urlConn.getResponseCode() + ": " + urlConn.getResponseMessage());
            else {
                Scanner sc = new Scanner(url.openStream());

                while (sc.hasNext()) {
                    inline.append(sc.nextLine());
                }
                sc.close();

            }
            JSONObject jsonObject = new JSONObject(inline.toString());

            ArrayList<PlaceModel> places = new ArrayList<>();
            JSONArray results = jsonObject.getJSONArray("results");
            Log.i("MyLogs", results.toString());


            for (int i = 0; i < 5 && i < results.length(); i++) {
                JSONObject location = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                String name = results.getJSONObject(i).getString("name");
                String vicinity = results.getJSONObject(i).getString("vicinity");

                Log.i("MyLogs", location.toString()+name+vicinity);

                places.add(new PlaceModel(location.getDouble("lat"),
                        location.getDouble("lng"),
                        results.getJSONObject(i).getString("name"),
                        results.getJSONObject(i).getString("vicinity")));
            }

            return places;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("MyLogs", "Returning null");
        return null;

    }

    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placesList) {
        super.onPostExecute(placesList);

        progressDialog.dismiss();
        for(PlaceModel place: placesList){
            addMarker(new GeoPoint(place.getLatitude(), place.getLongitude()));
        }
        PlacesAdapter adapter = new PlacesAdapter(placesList, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
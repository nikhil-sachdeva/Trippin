package com.example.nikhil.roadsafety;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.mmi.MapView;
import com.mmi.layers.PathOverlay;
import com.mmi.util.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class RoutesTask extends AsyncTask<GeoPoint, Void, ArrayList<ArrayList<RouteGeopoints>>> {


    private ProgressDialog progressDialog;
    private Context context;
    private MapView mMapView;
    private AccidentDatabaseHelper dbHelper;
    private static final String BASE_URL = "https://apis.mapmyindia.com/advancedmaps/v1/";
    private static final int CLIENT_ERROR_CODE = 400;
    private static final int SERVER_ERROR_CODE = 100;
    private static final int SUCCESS_CODE = 200;


    public RoutesTask(Context context, MapView mMapView) {
        this.context = context;
        this.mMapView = mMapView;
    }
    public RoutesTask(Context context){
        this.context=context;
    }
    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching routes, please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();



    }

    @Override
    protected ArrayList<ArrayList<RouteGeopoints>> doInBackground(GeoPoint... geoPoints) {
        HttpURLConnection urlConn;
        BufferedReader bufferedReader = null;

        String Url = BASE_URL + context.getString(R.string.mmi_rest_api_key) + "/route?"
                + "start=" + geoPoints[0].getLatitude() + "," + geoPoints[0].getLongitude()
                + "&destination=" + geoPoints[1].getLatitude() + "," + geoPoints[1].getLongitude()
                + "&alternatives=true&with_advices=1";

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
            Log.i("MyLogs", jsonObject.toString());

            if (jsonObject != null) {
                try {
                    if (jsonObject.getInt("responseCode") == SUCCESS_CODE) {

                        JSONArray alternatives = jsonObject.getJSONObject("results").getJSONArray("alternatives");

                        ArrayList<ArrayList<RouteGeopoints>> allRoutes = new ArrayList<>();

                        for (int i = 0; i < alternatives.length(); i++) {
                            Log.i("MyLogs", "i = "+ i);
                            JSONObject alternative = (JSONObject) alternatives.get(i);
                            ArrayList<GeoPoint> route = decodeEncodedRoute(alternative.getString("pts"));

                            ArrayList<RouteGeopoints> routePoints = getColoredRoutes(route);
                            allRoutes.add(routePoints);
                            Log.i("MyLogs", "All routes length: "+ allRoutes.size());

                        }



                        JSONArray trips = jsonObject.getJSONObject("results").getJSONArray("trips");

                        for (int i = 0; i < trips.length(); i++) {

                            JSONObject trip = (JSONObject) trips.get(i);
                            ArrayList<GeoPoint> route = decodeEncodedRoute(trip.getString("pts"));
//                            addPolyline(route);

                            ArrayList<RouteGeopoints> routePoints = getColoredRoutes(route);

                            allRoutes.add(routePoints);
                            Log.i("MyLogs", "All routes length: "+ allRoutes.size());


                        }

                        return allRoutes;

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
        e.printStackTrace();
    }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("MyLogs", "Returning null");
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<RouteGeopoints>> allRoutes) {
        super.onPostExecute(allRoutes);

        progressDialog.dismiss();

        for(ArrayList<RouteGeopoints> singleRoute: allRoutes){
            for(RouteGeopoints subRoute: singleRoute){
                addPolyline(subRoute);
            }
        }



    }

    public double getDangerIndex(GeoPoint geoPoint) throws IOException {

        dbHelper = new AccidentDatabaseHelper(context);
        try {
            getDatabase(geoPoint);
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
        return index;
    }

    private void getDatabase(GeoPoint geoPoint) throws IOException {

        InputStream is = context.getAssets().open("AccidentProneAreas.csv");
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
            double distance = getDistance(geoPoint, lat, lang);
            dbHelper.insertData(name,lat,lang,simpleAccidents,fatalAccidents,dangerPoints,zone,dangerIndex,distance);
        }
    }

     double getDistance(GeoPoint geoPoint, double lat, double lang){
        Location targetLocation = new Location("");
        targetLocation.setLatitude(lat);
        targetLocation.setLongitude(lang);
        Location startLocation = new Location("");
        startLocation.setLatitude(geoPoint.getLatitude());
        startLocation.setLongitude(geoPoint.getLongitude());
        return startLocation.distanceTo(targetLocation)/1000;
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


    private ArrayList<RouteGeopoints> getColoredRoutes(ArrayList<GeoPoint> route) throws IOException {
        ArrayList<RouteGeopoints> routePoints = new ArrayList<>();

        DangerPoint start = new DangerPoint(route.get(0), getDangerIndex(route.get(0)));
        DangerPoint end;

        if(route.size()<10)
            end = new DangerPoint(route.get(route.size()), getDangerIndex(route.get(route.size())));
        else
            end = new DangerPoint(route.get(10), getDangerIndex(route.get(10)));
        ArrayList<GeoPoint> routeSubPoints = new ArrayList<>();

        int j = 0;
        while (j < route.size()) {

            if(j%10 == 0) {
                Log.i("MyLogs", "Alternative j = " + j);
                Log.i("MyLogs", "Start: " + start.getGeoPoint().toString());
                Log.i("MyLogs", "End: " + end.getGeoPoint().toString());


                if (j == 0) {
                    routeSubPoints.add(start.getGeoPoint());
                } else {
                    int endIndex;
                    if(j+10 < route.size())
                        endIndex = j+10;
                    else
                        endIndex = route.size()-1;

                    start = new DangerPoint(route.get(j), getDangerIndex(route.get(j)));
                    Double endDangerIndex = getDangerIndex(route.get(endIndex));
                    Log.i("DangerIndex", endDangerIndex+" Latlang: "+start.getGeoPoint().getLatitude()+", "+start.getGeoPoint().getLongitude());


                    end = new DangerPoint(route.get(endIndex), endDangerIndex);

                    RouteGeopoints.Color endColor = getColor(end.getDangerIndex());
                    RouteGeopoints.Color startColor = getColor(start.getDangerIndex());

                    if (endColor != startColor) {
                        Log.i("MyLogs", "Route points length: "+ j+": "+routePoints.size());
                        routePoints.add(new RouteGeopoints(routeSubPoints, startColor));
                        routeSubPoints = new ArrayList<>();
                        routeSubPoints.add(route.get(j-1));
                        routeSubPoints.add(route.get(j));
//                                          routeSubPoints.add(end.getGeoPoint());
                    }
                }
            }
            Log.i("MyLogs", "Route sub points length: "+ routeSubPoints.size());
            routeSubPoints.add(route.get(j));

            j++;
            Log.i("MyLogs", "Ending while");
        }
        if(!routeSubPoints.isEmpty()){
            Log.i("MyLogs", "Route subpoints is not empty");
            routePoints.add(new RouteGeopoints(routeSubPoints,
                    getColor(getDangerIndex(routeSubPoints.get(0)))));
        }

        return routePoints;
    }

    private RouteGeopoints.Color getColor(double dangerIndex) {
        if (dangerIndex <= 4.5)
            return RouteGeopoints.Color.SAFE;
        else if (dangerIndex <= 7.5)
            return RouteGeopoints.Color.MODERATE;
        else
            return RouteGeopoints.Color.DANGEROUS;
    }

    private ArrayList<GeoPoint> decodeEncodedRoute(String encodedRoute) {
        ArrayList<GeoPoint> route = new ArrayList<>();
        double latitude = 0, longitude = 0;
        int index = 0;
        while (index < encodedRoute.length()) {
            int b, shift = 0, result = 0;
            do {
                b = ((int) encodedRoute.charAt(index++)) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            double dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latitude += dlat;
            shift = 0;
            result = 0;

            do {
                b = ((int) encodedRoute.charAt(index++)) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            double dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            longitude += dlng;

            route.add(new GeoPoint(latitude / 1e6, longitude / 1e6));
        }

        return route;

    }

    private void  addSimplePolyline(ArrayList<GeoPoint> route, String ETA, String distance){
        PathOverlay pathOverlay = new PathOverlay(context);
        pathOverlay.setWidth(15);
        pathOverlay.setPoints(route);
        mMapView.getOverlays().add(pathOverlay);
        mMapView.invalidate();
    }
    private void addPolyline(RouteGeopoints geoPoints) {

        PathOverlay pathOverlay = new PathOverlay(context);
        RouteGeopoints.Color color = geoPoints.getColor();
        switch (color){
            case DANGEROUS: pathOverlay.setColor(context.getResources().getColor(R.color.dangerColor));
                break;
            case SAFE: pathOverlay.setColor(context.getResources().getColor(R.color.safeColor));
                break;
            case MODERATE: pathOverlay.setColor(context.getResources().getColor(R.color.moderateColor));
                break;
            default: pathOverlay.setColor(context.getResources().getColor(R.color.safeColor));
                break;

        }

        pathOverlay.setWidth(15);
        pathOverlay.setPoints(geoPoints.getRoute());
        mMapView.getOverlays().add(pathOverlay);
        mMapView.invalidate();
    }

}

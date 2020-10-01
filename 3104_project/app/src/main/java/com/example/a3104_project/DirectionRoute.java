package com.example.a3104_project;


import android.app.Application;
import android.graphics.Color;
import android.location.Location;
import android.widget.Toast;

import com.example.a3104_project.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectionRoute extends Application {

    LatLng firstMile;
    LatLng lastMile;
    String key;
    public DirectionRoute(LatLng firstMile, LatLng lastMile, String key){
        this.firstMile=firstMile;
        this.lastMile=lastMile;
        this.key=key;
    }
    public PolylineOptions generateRoute(){
        PolylineOptions firstmileroute = getDirectionsURLwithMode(firstMile,lastMile,"WALKING");
        firstmileroute.width(5).color(Color.RED);
        return firstmileroute;
    }


    private PolylineOptions getDirectionsURLwithMode(LatLng from, LatLng to,String modeselection){
        String origin = "origin=" + from.latitude + "," + from.longitude+"&";
        String dest = "destination=" + to.latitude + "," + to.longitude+"&";
        String key = "key=" +this.key;
        String sensor = "sensor=false"+"&";
        String mode = "mode="+modeselection+"&";
        String alternative = "alternatives=true&";
        String avoid = "avoid=highways"+"&";
        String params = origin.concat(dest).concat(avoid).concat(mode).concat(alternative).concat(key);
        // return "https://maps.googleapis.com/maps/api/directions/json?".concat(params);
        return buildPolylinefromDirectionsURL("https://maps.googleapis.com/maps/api/directions/json?".concat(params));
    }
    //returns a polylineOption
    private PolylineOptions buildPolylinefromDirectionsURL(String url) {
        PolylineOptions polylineoption = new PolylineOptions();
        try {
            URL url2 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                //string to Json Object
                String response =responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                polylineoption = buildPolyLine(jsonObject);
                //addPolyLine(pointlist);

            } finally {
                connection.disconnect();
            }
        }catch (IOException | JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return polylineoption;
    }

    private PolylineOptions buildPolyLine(JSONObject jsonObject) throws JSONException {
        List<LatLng> locationlist = new ArrayList<LatLng>();
        JSONArray routes = jsonObject.getJSONArray("routes");
        JSONObject zero = routes.getJSONObject(0);
        JSONArray legs = zero.getJSONArray("legs");
        JSONObject zerotwo = legs.getJSONObject(0);
        JSONArray steps = zerotwo.getJSONArray("steps");
        for (int i=0; i<steps.length();i++){
            JSONObject obj = steps.getJSONObject(i);
            JSONObject end_location= obj.getJSONObject("end_location");
            JSONObject start_location= obj.getJSONObject("start_location");
            //String PT = obj.getString("NUMBER");
            //String title = "PT: " + PT;
            if(i==0){
                Double StartLAT = start_location.getDouble("lat");
                Double StartLNG = start_location.getDouble("lng");
                LatLng position = new LatLng(StartLAT, StartLNG);
                locationlist.add(position);
            }
            Double StartLAT = end_location.getDouble("lat");
            Double StartLNG = end_location.getDouble("lng");
            LatLng position = new LatLng(StartLAT, StartLNG);
            locationlist.add(position);


        }
        return buildPolyLineOptionfromListofLatLng(locationlist);
        //return locationlist;

    }
    private PolylineOptions buildPolyLineOptionfromListofLatLng(List<LatLng> input){
        PolylineOptions polylineOptions = new PolylineOptions();
        // Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(input);
        polylineOptions.width(5).color(Color.RED);
        return polylineOptions;
    }


    //Unused Methods below.

    private Places getClosesTransit(String url, LatLng Origin) {
        Places nearestplace = new Places();
        try {
            URL url2 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                //string to Json Object
                String response =responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                //JSONObject results = jsonObject.getJSONArray("results");
                for (int i =0; i <results.length(); i++){
                    if(i!=0){break;}
                    else {
                        JSONObject index = results.getJSONObject(i);
                        JSONObject geometry = index.getJSONObject("geometry");
                        String nama = index.getString("name");
                        JSONObject location = geometry.getJSONObject("location");
                        double latit = location.getDouble("lat");
                        double lng = location.getDouble("lng");
                        //JSONObject icon = index.getJSONObject("icon");
                        String iconurl = index.getString("icon");
                        double distance = getDistance(Origin,new LatLng(latit,lng));
                        nearestplace = new Places(nama, latit, lng, iconurl,distance);
                        //placeslist.add(new Places(nama, latit, lng, iconurl));
                    }
                }


            } finally {
                connection.disconnect();
            }
        }catch (IOException | JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return nearestplace;
    }

    private List<Places> getListofLocationsNearby(String url) {
        List<Places> placeslist = new ArrayList<Places>();
        try {
            URL url2 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                //string to Json Object
                String response =responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                //JSONObject results = jsonObject.getJSONArray("results");
                for (int i =0; i <results.length(); i++){
                    JSONObject index = results.getJSONObject(i);
                    JSONObject geometry = index.getJSONObject("geometry");
                    String nama = index.getString("name");
                    JSONObject location = geometry.getJSONObject("location");
                    double latit = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    //JSONObject icon = index.getJSONObject("icon");
                    String iconurl = index.getString("icon");
                    placeslist.add(new Places(nama,latit,lng,iconurl));
                }


            } finally {
                connection.disconnect();
            }
        }catch (IOException | JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return placeslist;
    }
    private String generateNearbyPlacesURL(LatLng current,String transporttype){
        String location = "location=" + current.latitude + "," + current.longitude+"&";
        String radius = "radius" + "=1500"+"&";
        String type = "type="+ transporttype + "&";
        String key = "key=" +  this.key;
        String params = location.concat(radius).concat(type).concat(key);
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?".concat(params);
    }
    private List<String> buildNearbyPlacesURL(LatLng current){
        List<String> types = new ArrayList<>();
        List<String> URLS = new ArrayList<>();
        types.add("bus_station");
        types.add("subway_station");
        for (int i=0; i < types.size(); i++){
            URLS.add(generateNearbyPlacesURL(current,types.get(i)));
        }
        return URLS;
    }
    private double rad(double input) {
        return input * Math.PI / 180;
    };

    private double getDistance (LatLng p1, LatLng p2) {
        double  R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p2.latitude - p1.latitude);
        double dLong = rad(p2.longitude - p1.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // returns the distance in meter
    };
}

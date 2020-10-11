package edu.singaporetech.ict3104.project;


import android.app.Application;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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





}

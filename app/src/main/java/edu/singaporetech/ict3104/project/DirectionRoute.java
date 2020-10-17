package edu.singaporetech.ict3104.project;


import android.app.Application;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

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
    String mode;
    List<LocationSteps> location_step_list;
    public DirectionRoute(LatLng firstMile, LatLng lastMile, String key){
        this.firstMile=firstMile;
        this.lastMile=lastMile;
        this.key=key;
    }
    public PolylineOptions generateRoute(){
        PolylineOptions firstmileroute = getDirectionsURLwithMode(firstMile,lastMile,"WALKING");
        firstmileroute.width(10).color(Color.RED).geodesic(true);
        return firstmileroute;
    }
    public List<LocationSteps> getLocationStepList(){
        return location_step_list;
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
    //GETTING THE JSON FROM INPUT STREAM
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
    //DO SMT TO THE JSON RESULT
    private PolylineOptions buildPolyLine(JSONObject jsonObject) throws JSONException {
        location_step_list = new ArrayList<>();
        JSONArray routes = jsonObject.getJSONArray("routes");
        JSONObject zero = routes.getJSONObject(0);
        JSONArray legs = zero.getJSONArray("legs");
        JSONObject zerotwo = legs.getJSONObject(0);
        JSONArray steps = zerotwo.getJSONArray("steps");
        for (int i=0; i<steps.length();i++){
            JSONObject obj = steps.getJSONObject(i);
            JSONObject start_location= obj.getJSONObject("start_location");
            JSONObject end_location= obj.getJSONObject("end_location");
            LatLng start_loc=new LatLng(start_location.getDouble("lat"),start_location.getDouble("lng"));
            LatLng end_loc=new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng"));

            location_step_list.add(new LocationSteps(
                    obj.getJSONObject("duration").getInt("value"),
                    start_loc,
                    end_loc,
                    obj.getJSONObject("distance").getInt("value"),
                    obj.getString("html_instructions"),
                    obj.getJSONObject("polyline").getString("points")));

        }
        return buildPolyLineOptionfromListofLatLng(location_step_list);

    }
    private PolylineOptions buildPolyLineOptionfromListofLatLng(List<LocationSteps> input){
        PolylineOptions polylineOptions = new PolylineOptions();
        for(int i=0; i <input.size(); i++){
            LocationSteps t = input.get(i);
            polylineOptions.addAll(PolyUtil.decode(t.getPolyline()));
        }
        polylineOptions.width(10).color(Color.RED);
        return polylineOptions;
    }





}

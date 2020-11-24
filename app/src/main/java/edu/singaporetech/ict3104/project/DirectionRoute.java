package edu.singaporetech.ict3104.project;


import android.app.Application;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    List<List<LocationSteps>> location_step_list;
    List<List<PolylineOptions>> wholeList;
    public DirectionRoute(LatLng firstMile, LatLng lastMile, String key){
        this.firstMile=firstMile;
        this.lastMile=lastMile;
        this.key=key;
        this.getAlternateRoute();
    }
    public List<List<PolylineOptions>> getRouteList(){
        return wholeList;
    }
    public List<List<LocationSteps>> getLocationStepList(){
        return location_step_list;
    }
    public void getAlternateRoute(){
        getListOfPolyLineFromAlternative("walking");
    }

    private void getListOfPolyLineFromAlternative(String modeselection){
        String origin = "origin=" + firstMile.latitude + "," + firstMile.longitude+"&";
        String dest = "destination=" + lastMile.latitude + "," + lastMile.longitude+"&";
        String key = "key=" +this.key;
        String mode = "mode="+modeselection+"&";
        String alternative = "alternatives=true&";

//        String avoid = "avoid=highways"+"&";
        String params = origin.concat(dest).concat(mode).concat(alternative).concat(key);
        // return "https://maps.googleapis.com/maps/api/directions/json?".concat(params);
        buildListofPolylinefromDirectionsURL("https://maps.googleapis.com/maps/api/directions/json?".concat(params));
    }
    private void buildListofPolylinefromDirectionsURL(String url) {
        location_step_list=new ArrayList<>();
        wholeList= new ArrayList<>();
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
                JSONArray routes = jsonObject.getJSONArray("routes");
                for(int i=0; i< routes.length();i++){
                    List<PolylineOptions> listpolyline = new ArrayList<>();
                    List<LocationSteps> listlocationstep = new ArrayList<>();
                    JSONObject routeobject = routes.getJSONObject(i);
                    JSONObject zero = routes.getJSONObject(i);
                    JSONArray legs = zero.getJSONArray("legs");
                    JSONObject zerotwo = legs.getJSONObject(0);
                    JSONArray steps = zerotwo.getJSONArray("steps");
                    for(int j=0; j<steps.length();j++){
                        PolylineOptions polylineoption = new PolylineOptions();
                        String poly = steps.getJSONObject(j).getJSONObject("polyline").getString("points");
                        polylineoption.addAll(PolyUtil.decode(poly));
                        listpolyline.add(polylineoption);

                        JSONObject obj = steps.getJSONObject(j);
                        JSONObject start_location= obj.getJSONObject("start_location");
                        JSONObject end_location= obj.getJSONObject("end_location");
                        LatLng start_loc=new LatLng(start_location.getDouble("lat"),start_location.getDouble("lng"));
                        LatLng end_loc=new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng"));
                        String html = obj.getString("html_instructions");
                        listlocationstep.add(new LocationSteps(obj.getJSONObject("duration").getInt("value"),start_loc,end_loc,obj.getJSONObject("distance").getInt("value"),obj.getString("html_instructions"),obj.getJSONObject("polyline").getString("points")));

                    }
                    location_step_list.add(listlocationstep);
                    wholeList.add(listpolyline);
                }

            } finally {
                connection.disconnect();
            }
        }catch (IOException | JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //return wholeList;
    }

//    private PolylineOptions buildPolyLineOptionfromListofLatLng(List<LocationSteps> input){
//        PolylineOptions polylineOptions = new PolylineOptions();
//        for(int i=0; i <input.size(); i++){
//            LocationSteps t = input.get(i);
//            polylineOptions.addAll(PolyUtil.decode(t.getPolyline()));
//        }
//        polylineOptions.width(10).color(Color.RED);
//        return polylineOptions;
//    }





}

package com.example.a3104_project;

import android.app.Application;
import android.location.Location;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

public class Places{

    String name;
    double latitude;
    double longitude;
    String imgurl;
    Location location;
    Double distance;
    public Places(){

    }
    public Places(String name,double lat, double lng,String imgurl,double distance) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lng;
        this.imgurl = imgurl;
        this.location = new Location(this.name);
        this.location.setLongitude(this.longitude);
        this.location.setLatitude(this.latitude);
        this.distance= distance;
    }
    public Places(String name,double lat, double lng,String imgurl) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lng;
        this.imgurl = imgurl;
        this.location = new Location(this.name);
        this.location.setLongitude(this.longitude);
        this.location.setLatitude(this.latitude);
    }
    public String getName ( )
    {
        return name;
    }

    public void setName (String studentName)
    {
        name = studentName;
    }

    public double getLatitude ( )
    {
        return latitude;
    }

    public void setLatitude(double input)
    {
        latitude = input;
    }
    public double getLongitude( )
    {
        return longitude;
    }

    public void setLongitude(double input)
    {
        longitude = input;
    }
    public String getImageUrl(){return imgurl;    }
    public void setImageUrl(String input){this.imgurl =input;    }
    public Location getLocation() { return this.location;}
    public void setDistance(double input) { this.distance=input;}
    public double getDistance(){return this.distance;}


}

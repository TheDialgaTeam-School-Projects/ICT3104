package edu.singaporetech.ict3104.project;

import android.location.Location;

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

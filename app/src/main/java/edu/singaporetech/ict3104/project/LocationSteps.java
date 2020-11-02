package edu.singaporetech.ict3104.project;

import com.google.android.gms.maps.model.LatLng;


public class LocationSteps {
    double distanceinmeters;
    int duration;
    LatLng start_location,end_location;
    String html_instructions;
    String polyline;

    public LocationSteps(Integer duration,LatLng start_location,LatLng end_location,Integer distance,String html_instructions,String polyline) {
        this.duration=duration;
        this.start_location = start_location;
        this.end_location = end_location;
        this.distanceinmeters= ConvertFeettometers(distance);
        this.html_instructions=html_instructions;
        this.polyline=polyline;
    }

    public double ConvertFeettometers(Integer feet){
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round((feet/3.2808) * scale) / scale;
    }
    public Integer getDuration(){
        return this.duration;
    }
    public double getDistanceinmeters(){
        return this.distanceinmeters;
    }
    public LatLng getStart_location(){
        return this.start_location;
    }
    public LatLng getEnd_location(){
        return this.end_location;
    }
    public String getHtml_instructions(){
        return this.html_instructions;
    }
    public String getPolyline(){
        return this.polyline;
    }
}

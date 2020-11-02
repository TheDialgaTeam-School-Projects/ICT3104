package edu.singaporetech.ict3104.project;

import android.location.Location;

public class PlaceOfInterest {
    String featurename;
    double latitude;
    double longitude;
    String tag;


    public PlaceOfInterest(String featurename,double lat, double lng,String tag) {
        this.featurename = featurename;
        this.latitude = lat;
        this.longitude = lng;
        this.tag = tag;
    }
    public String getFeaturename ( )
    {
        return featurename;
    }
    public Double getLatitude ( )
    {
        return latitude;
    }
    public Double getLongitude ( )    {       return longitude;    }
    public String getTag ( )
    {
        return tag;
    }
}

package edu.singaporetech.ict3104.project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;

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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 15.0f;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    MarkerOptions markerOptions;
    Marker myMarker;
    List<Marker> listofmarker;
    Polyline polyline;
    boolean locationPermissionGranted = false;
    Location lastKnownLocation;
    LatLng selectedLocation;
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Button btnReset, btnOpenARNavigation,btnStartRoute;
    LocationManager mLocationManager;
    MapView mMapView;
    Handler mHandler;
    Runnable runnable;
    DirectionRoute R1;

    public MapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       //return inflater.inflate(R.layout.fragment_map, container, false);    }
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(lastKnownLocation!=null && R1!=null){
//                    bdccGeoDistanceAlgorithm ba = new bdccGeoDistanceAlgorithm(lastKnownLocation,R1.getLocationlist());
//                    if(ba.bdccGeoDistanceCheckWithRadius(70)){
////                        Log.d("test","You are getting too far");
//                        Toast.makeText(getActivity(), "You are getting too far", Toast.LENGTH_SHORT).show();
//
//                    }
                    Log.d("test","test");
                }
                new Handler().postDelayed(runnable,5000);
            }
        };
        mHandler = new Handler();
        btnOpenARNavigation = rootView.findViewById(R.id.btnOpenARNavigation);
        btnReset = rootView.findViewById(R.id.btnReset);
        btnStartRoute = rootView.findViewById(R.id.btnStartRoute);
        setStartJourneyButton(true);
        btnStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startJourney();
            }
        });
        btnOpenARNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAR();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMarker!=null){
                    mMap.clear();
                }
                clearPath();
                setStartJourneyButton(false);
                CreateMarkers(populateListofNearbyPlaces());
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        mHandler.postDelayed(runnable,5000);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPolicy();
    }
    private void startAR(){
        if(selectedLocation==null){
            //REJ
        }else{

        }
    }

    private void clearPath() {
        if (polyline != null)
            polyline.remove();
        polyline = null;
    }

    private void setPolicy() {
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        getLocationPermission();
        final List<Places> placeslist = populateListofNearbyPlaces();
        CreateMarkers(placeslist);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                new AlertDialog.Builder(getContext()).setTitle("Set Destination?").setMessage("Do you really want to travel here?").setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getActivity(), "Destination Set!", Toast.LENGTH_SHORT).show();
                                clearPath();
                                mMap.clear();
                                selectedLocation=new LatLng(arg0.latitude,arg0.longitude);
                                markerOptions = new MarkerOptions().position(selectedLocation).title("Selected Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station));
                                mMap.addMarker(markerOptions);
                                setStartJourneyButton(true);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


            }
        });
    }
    private void CreateMarkers(List <Places> description){

        listofmarker  = new ArrayList<Marker>();
        for (int i =0; i<description.size(); i++){
            Places t = description.get(i);
            LatLng sl = new LatLng(t.getLatitude(), t.getLongitude());
            MarkerOptions tmarkerOptions = new MarkerOptions().position(sl).title(t.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station));
            listofmarker.add(mMap.addMarker(tmarkerOptions));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        for (int i =0; i<listofmarker.size(); i++){
            if (marker.equals(listofmarker.get(i))){
                Marker t =listofmarker.get(i);
                clearPath();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(t.getPosition()).title(t.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station)));
                selectedLocation=marker.getPosition();
                setStartJourneyButton(true);
            }

        }
        return true;
    }

    private List<Places> populateListofNearbyPlaces() {
        getLocationPermission();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        LatLng mycurrent = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        List<String> TYPE = new ArrayList<>();
        TYPE.add("bus_station");
        TYPE.add("subway_station");
        TYPE.add("transit_station");
        List<Places> placeslist = new ArrayList<Places>();
        for (int i = 0; i < TYPE.size(); i++) {
            placeslist.addAll(buildNearbyPlacesURL(mycurrent, TYPE.get(i)));
        }
        return placeslist;
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
                String response = responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                //JSONObject results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject index = results.getJSONObject(i);
                    JSONObject geometry = index.getJSONObject("geometry");
                    String nama = index.getString("name");
                    JSONObject location = geometry.getJSONObject("location");
                    double latit = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    //JSONObject icon = index.getJSONObject("icon");
                    String iconurl = index.getString("icon");
                    placeslist.add(new Places(nama, latit, lng, iconurl));
                }


            } finally {
                connection.disconnect();
            }
        } catch (IOException | JSONException e) {
            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return placeslist;
    }

    private List<Places> buildNearbyPlacesURL(LatLng current, String transporttype) {
        String location = "location=" + current.latitude + "," + current.longitude + "&";
        String radius = "radius" + "=500" + "&";
        String type = "type=" + transporttype + "&";
        String key = "key=" + getText(R.string.google_maps_key);
        String params = location.concat(radius).concat(type).concat(key);
        return getListofLocationsNearby("https://maps.googleapis.com/maps/api/place/nearbysearch/json?".concat(params));
        //return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?".concat(params);
    }
    private void startJourney(){
        if (selectedLocation != null) {
            new AlertDialog.Builder(getContext()).setTitle("Start Journey??").setMessage("You would have to reset map if you wish to change destination?").setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getActivity(), "Destination Set!", Toast.LENGTH_SHORT).show();
                        R1 = new DirectionRoute(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), selectedLocation, getText(R.string.google_maps_key).toString());
                        clearPath();
                        PolylineOptions options = R1.generateRoute();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(selectedLocation).title("selectedLocation").icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station)));
                        polyline = mMap.addPolyline(options);
                        setStartJourneyButton(false);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        }

    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setTrafficEnabled(false);
            mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            lastKnownLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (lastKnownLocation == null || l.getAccuracy() < lastKnownLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    lastKnownLocation = l;
                }
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (locationPermissionGranted) {

                @SuppressLint("MissingPermission") Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            lastKnownLocation= (Location) task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        }
                    }
                });


        }

    }
    private void setStartJourneyButton(boolean t){
        if(t){
            btnStartRoute.setVisibility(View.VISIBLE);
        }else{
            btnStartRoute.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            } else {
                getLocationPermission();
            }
            return;
        }
        updateLocationUI();
    }
}
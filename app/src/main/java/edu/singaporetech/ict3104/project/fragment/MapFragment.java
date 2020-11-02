package edu.singaporetech.ict3104.project.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import edu.singaporetech.ict3104.project.DirectionRoute;
import edu.singaporetech.ict3104.project.LocationSteps;
import edu.singaporetech.ict3104.project.Places;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.PlaceOfInterest;

import android.os.Debug;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 15.0f;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    MarkerOptions markerOptions;
    Marker myMarker;
    List<Marker> listofmarker;
    List<Polyline> polyline;
    boolean locationPermissionGranted = false;
    Location lastKnownLocation;
    LatLng selectedLocation;
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Button btnReset, btnOpenARNavigation;
    LocationManager mLocationManager;
    MapView mMapView;
    Handler mHandler;
    Runnable runnable;
    DirectionRoute R1;
    RadioGroup rbg_RouteList;
    List<List<PolylineOptions>> listofAlternateRoute;
    int selectedRoute;
    List<Marker> listofPOIMarker;
    List<PlaceOfInterest> listofpoi;
    RelativeLayout ratingLayout;
    TextView tvfeatureName;
    RatingBar featureRating;
    int markerclickmode = 0;

    public MapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_map, container, false);    }
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if(lastKnownLocation!=null && R1!=null){
//                    if(doInBackground(R1.getLocationStepList())){
//                        Toast.makeText(getActivity(), "You are getting too far", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(getActivity(), "You are still on the route", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                new Handler().postDelayed(runnable,5000);
//            }
//        };
//        mHandler = new Handler();
        getAllPOIfromdb();
        listofPOIMarker = new ArrayList<>();
        ratingLayout = rootView.findViewById(R.id.ratingLayout);
        tvfeatureName = rootView.findViewById(R.id.tvfeatureName);
        featureRating = rootView.findViewById(R.id.featureRating);
        featureRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String featurename = tvfeatureName.getText().toString();
            float value = rating;
            insertValuetodb(featurename, value);

        });
        toggleRatingLayout();
        rbg_RouteList = rootView.findViewById(R.id.rbg_RouteList);
        btnOpenARNavigation = rootView.findViewById(R.id.btnOpenARNavigation);
        btnReset = rootView.findViewById(R.id.btnReset);
        setStartJourneyButton(true);
        btnOpenARNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startJourney();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetMap();
            }
        });
        rbg_RouteList.setOnCheckedChangeListener((group, checkedId) -> {
            // checkedId is the RadioButton selected
            clearPath();
            selectedRoute = checkedId - 1;
            List<PolylineOptions> selected = listofAlternateRoute.get(selectedRoute);
            PolylineOptions t = new PolylineOptions();
            polyline = new ArrayList<>();
            for (int i = 0; i < selected.size(); i++) {
                Polyline tpoly = mMap.addPolyline(selected.get(i));
                polyline.add(tpoly);
            }
        });
        return rootView;
    }

    public void resetMap() {
        if (myMarker != null) {
            myMarker.remove();
            mMap.clear();
        }
        clearPath();
        clearrbgList();
        setStartJourneyButton(false);
        CreateMarkers(populateListofNearbyPlaces());
        toggleMarkermode();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
//        mHandler.postDelayed(runnable,5000);
        setStartJourneyButton(false);
    }

    public void getAllPOIfromdb() {
        listofpoi = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collectionGroup("POI").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Iterate to get the products out of the queryDocumentSnapshots object
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    listofpoi.add(new PlaceOfInterest(document.getString("Name"), document.getDouble("Lat"), document.getDouble("Long"), document.getString("Item")));
//                    Log.d("FIREBASE", document.getId() + " => " + document.getDouble("Lat")+ " Lng" + document.getDouble("Long"));
                }
            }
        });

    }

    @Override
    public void onLocationChanged(Location newLocation) {
//        Toast.makeText(getActivity(), "Location changed", Toast.LENGTH_SHORT).show();
        if (listofpoi.size() != 0) {
            for (int i = 0; i < listofpoi.size(); i++) {
                PlaceOfInterest cur = listofpoi.get(i);
                Location test = new Location("");//provider name is unnecessary
                test.setLatitude(cur.getLatitude());//your coords of course
                test.setLongitude(cur.getLongitude());
                float distanceInMeters = newLocation.distanceTo(test);
                //if within
                if (distanceInMeters < 10000) {
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(test.getLatitude(), test.getLongitude()))
                            .title(cur.getFeaturename()));
                    listofPOIMarker.add(m);
                    listofpoi.remove(i);
                }
                //missing logic if leave area
            }
        if (markerclickmode == 0) {
                hideAllMarkers(listofPOIMarker);
                showAllMarkers(listofmarker);
            }
        } else if (markerclickmode == 1) {
            hideAllMarkers(listofmarker);
            showAllMarkers(listofPOIMarker);
        }

    }

    public void hideAllMarkers(List<Marker> input) {
        for (int i = 0; i < input.size(); i++) {
            input.get(i).setVisible(false);
        }
    }

    public void showAllMarkers(List<Marker> input) {
        for (int i = 0; i < input.size(); i++) {
            input.get(i).setVisible(true);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPolicy();
    }

    private void startAR() {
        if (selectedLocation == null) {
            //REJ
        } else {

        }
    }

    public void initRBGselection() {
        rbg_RouteList.check(0);
    }

    private void clearPath() {
        if (polyline != null)
            for (int i = 0; i < polyline.size(); i++) {
                //   polyline.get(i).remove();
                Polyline t = polyline.get(i);
                t.remove();
            }
        polyline = null;
    }

    private void setPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (markerclickmode==0) {
                    new AlertDialog.Builder(getContext()).setTitle("Set Destination?").setMessage("Do you really want to travel here?").setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(getActivity(), "Destination Set!", Toast.LENGTH_SHORT).show();
                                    clearPath();
                                    mMap.clear();
                                    selectedLocation = new LatLng(arg0.latitude, arg0.longitude);
                                    markerOptions = new MarkerOptions().position(selectedLocation).title("Selected Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station));
                                    myMarker = mMap.addMarker(markerOptions);
                                    setStartJourneyButton(true);
                                    R1 = new DirectionRoute(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), selectedLocation, getText(R.string.google_maps_key).toString());
                                    listofAlternateRoute = R1.getRouteList();
                                    clearrbgList();

                                    for (int j = 1; j <= listofAlternateRoute.size(); j++) {
                                        RadioButton rbn = new RadioButton(getContext());
                                        rbn.setId(j);
                                        rbn.setText("Route " + j);
                                        rbg_RouteList.addView(rbn);
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }

            }
        });
    }

    private void CreateMarkers(List<Places> description) {

        listofmarker = new ArrayList<Marker>();
        for (int i = 0; i < description.size(); i++) {
            Places t = description.get(i);
            LatLng sl = new LatLng(t.getLatitude(), t.getLongitude());
            MarkerOptions tmarkerOptions = new MarkerOptions().position(sl).title(t.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station));
            listofmarker.add(mMap.addMarker(tmarkerOptions));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        for (int i = 0; i < listofmarker.size(); i++) {
            if (marker.equals(listofmarker.get(i))) {
                Marker t = listofmarker.get(i);
                clearPath();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(t.getPosition()).title(t.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station)));
                selectedLocation = marker.getPosition();
                setStartJourneyButton(true);
                clearrbgList();
                R1 = new DirectionRoute(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), selectedLocation, getText(R.string.google_maps_key).toString());
                listofAlternateRoute = R1.getRouteList();
                for (int j = 1; j <= listofAlternateRoute.size(); j++) {
                    RadioButton rbn = new RadioButton(getContext());
                    rbn.setId(j);
                    rbn.setText("Route " + j);
                    rbg_RouteList.addView(rbn);
                }
            }

        }
        //Clicking a feature
        for (int i = 0; i < listofPOIMarker.size(); i++) {
            if (marker.equals(listofPOIMarker.get(i))) {
                tvfeatureName.setText(marker.getTitle());
                featureRating.setStepSize(0.1f);
                ratingLayout.setVisibility(RelativeLayout.VISIBLE);
            }


        }
        return true;
    }

    public void toggleMarkermode() {
        if (markerclickmode == 0) {
            markerclickmode = 1;
        } else {
            markerclickmode = 0;
        }
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

    private void startJourney() {
        if (selectedLocation != null) {
            new AlertDialog.Builder(getContext()).setTitle("Start Journey??").setMessage("You would have to reset map if you wish to change destination?").setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getActivity(), "Destination Set!", Toast.LENGTH_SHORT).show();
//                        R1 = new DirectionRoute(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), selectedLocation, getText(R.string.google_maps_key).toString());
//                        clearPath();
//                        mMap.clear();
//                        myMarker=mMap.addMarker(new MarkerOptions().position(selectedLocation).title("selectedLocation").icon(BitmapDescriptorFactory.fromResource(R.drawable.transit_station)));
                            List<PolylineOptions> selectedroute = listofAlternateRoute.get(selectedRoute);
                            PolylineOptions t = new PolylineOptions();
                            polyline = new ArrayList<>();
                            for (int i = 0; i < selectedroute.size(); i++) {
                                Polyline tpoly = mMap.addPolyline(selectedroute.get(i));
                                polyline.add(tpoly);
                            }
                            clearrbgList();
                            // polyline = mMap.addPolyline(options);
                            setStartJourneyButton(false);
                            List<List<LocationSteps>> list = R1.getLocationStepList();
                            //Pass to AR FROM HERE
                            List<LocationSteps> selectroute = list.get(selectedRoute);
                            toggleMarkermode();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
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
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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
                    if (task.isSuccessful()) {
                        lastKnownLocation = (Location) task.getResult();
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

    public void clearrbgList() {
        rbg_RouteList.removeAllViews();
    }

    private void setStartJourneyButton(boolean t) {
        if (t) {
            btnOpenARNavigation.setVisibility(View.VISIBLE);
        } else {
            btnOpenARNavigation.setVisibility(View.GONE);
        }
    }

    public Boolean doInBackground(List<LocationSteps> input) {
        //publishProgress();
        List<LatLng> list = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            list.add(input.get(i).getStart_location());
            list.add(input.get(i).getEnd_location());
        }
        //List<LatLng> list = list[0];
        getDeviceLocation();
        LatLng currentPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        for (int i = 1; i < list.size(); i++) {
            double t = obtainDistance(currentPosition, list.get(i), 'K');
            if (obtainDistance(currentPosition, list.get(i), 'K') <= 0.2) {
                //AS long as there is one there is within 0.5km Deem okay
                return false;
            }
        }
        return true;
    }

    public void insertValuetodb(String featurename, float value) {
        //INSERT TO RATING DB

        Toast.makeText(getActivity(), "Rating for" + featurename + "submitted", Toast.LENGTH_SHORT).show();
        toggleRatingLayout();
    }

    public void toggleRatingLayout() {
        if (ratingLayout.getVisibility() == RelativeLayout.GONE) {
            ratingLayout.setVisibility(RelativeLayout.VISIBLE);
        } else {
            ratingLayout.setVisibility(RelativeLayout.GONE);
        }
    }

    //M for Miles , K for kilometers , N for Nautical Miles
    private double obtainDistance(LatLng loc1, LatLng loc2, char unit) {
        double lat1 = loc1.latitude;
        double lon1 = loc1.longitude;
        double lat2 = loc2.latitude;
        double lon2 = loc2.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
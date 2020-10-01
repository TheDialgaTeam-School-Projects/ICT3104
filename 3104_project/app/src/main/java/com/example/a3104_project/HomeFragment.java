package com.example.a3104_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.security.AuthenticationChallenge;
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.esri.arcgisruntime.tasks.networkanalysis.TravelMode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 15.0f;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    MarkerOptions markerOptions;
    Marker myMarker;
    Polyline polyline;
    boolean locationPermissionGranted = false;
    Location lastKnownLocation;
    LatLng defaultLocation;
    LatLng selectedLocation;
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Button btnStartActivity, btnOpenARNavigation;
    PolylineOptions polylineOptions;
    LocationManager mLocationManager;
    Spinner spinnerNearby;
    MapView mMapView;
    Fragment ARNavFragment;
    // objects that implement Loadable must be class fields to prevent being garbage collected before loading
    private RouteTask mRouteTask;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        btnOpenARNavigation = rootView.findViewById(R.id.btnOpenARNavigation);
        btnOpenARNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAR();
            }
        });
        btnStartActivity = rootView.findViewById(R.id.btnStartActivity);
        btnStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startJourney();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        spinnerNearby = rootView.findViewById(R.id.spinnerNearby);
        // Inflate the layout for this fragment
        return rootView;
        // return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setPolicy();
        setupOAuthManager();
    }
    private void startAR(){
        if(selectedLocation==null){
            //REJ
        }else{

            // enable the user to specify a route once the service is ready
            mRouteTask.addDoneLoadingListener(() -> {
                if (mRouteTask.getLoadStatus() == LoadStatus.LOADED) {
                    try{
                        final ListenableFuture<RouteParameters> listenableFuture = mRouteTask.createDefaultParametersAsync();
                        RouteParameters routeParameters = listenableFuture.get();
                        // parameters needed for navigation (happens in ARNavigate)
                        routeParameters.setReturnStops(true);
                        routeParameters.setReturnDirections(true);
                        routeParameters.setReturnRoutes(true);
                        // this sample is intended for navigating while walking only
                        List<TravelMode> travelModes = mRouteTask.getRouteTaskInfo().getTravelModes();
                        TravelMode walkingMode = travelModes.get(0);
                        routeParameters.setTravelMode(walkingMode);
                        // add stops
                        Collection<Stop> routeStops = new ArrayList<>();
                        routeStops.add(new Stop(new Point(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude())));
                        routeStops.add(new Stop(new Point(selectedLocation.longitude,selectedLocation.latitude)));
                        routeParameters.setStops(routeStops);
                        // set return directions as true to return turn-by-turn directions in the result of
                        routeParameters.setReturnDirections(true);

                        // solve the route
                        ListenableFuture<RouteResult> routeResultFuture = mRouteTask.solveRouteAsync(routeParameters);
                        RouteResult routeResult = routeResultFuture.get();
                        Route route = routeResult.getRoutes().get(0);
                        // pass route to activity and navigate
                        ARNavFragment = new ARNavFragment(routeResult);
                        getParentFragmentManager().beginTransaction().replace(R.id.frament_layout,ARNavFragment).commit();
                        //ARNavigateActivity.sRouteResult = routeResult;
                        //Intent intent = new Intent(MapsActivity.this, ARNavigateActivity.class);
                        //Bundle bundle = new Bundle();
                        //startActivity(intent, bundle);
                    }
                    catch (InterruptedException | ExecutionException e) {
                        String error = "Error getting route result: " + e.getMessage();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        //Log.e(TAG, error);
                    }


                }
            });

        }




    }
    private void startJourney() throws IOException {
        if (selectedLocation == null) {
            //Toast.makeText(this, "Please Select a destination by tapping on the screen", Toast.LENGTH_SHORT).show();
        } else {

            DirectionRoute R1 = new DirectionRoute(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), selectedLocation, getText(R.string.google_maps_key).toString());
            clearPath();
            PolylineOptions options = R1.generateRoute();
            polyline = mMap.addPolyline(options);
        }
    }

    private void clearPath() {
        if (polyline != null)
            polyline.remove();
        polyline = null;
    }

    private void setPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void setupOAuthManager() {
        String clientId = getResources().getString(R.string.client_id);
        String redirectUrl = getResources().getString(R.string.redirect_url);
        try {
            OAuthConfiguration oAuthConfiguration = new OAuthConfiguration("https://www.arcgis.com", clientId, redirectUrl);
            AuthenticationChallengeHandler authenticationChallengeHandler = new AuthenticationChallengeHandler() {
                @Override
                public AuthenticationChallengeResponse handleChallenge(AuthenticationChallenge authenticationChallenge) {
                    return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL, new UserCredential("nova136", "BQVnv2RG"));
                }
            };
            AuthenticationManager.setAuthenticationChallengeHandler(authenticationChallengeHandler);
            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        getLocationPermission();
        final List<Places> placeslist = populateListofNearbyPlaces();

        PlaceAdapter arrayAdapter = new PlaceAdapter(getActivity(), R.layout.spinner_item, 1, placeslist);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerNearby.setAdapter(arrayAdapter);
        spinnerNearby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String placeName = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + placeName, Toast.LENGTH_LONG).show();
                if (selectedLocation != null) {
                    myMarker.remove();
                }
                Places t = placeslist.get(position);
                selectedLocation = new LatLng(t.latitude, t.longitude);
                markerOptions = new MarkerOptions().position(selectedLocation).title(t.name);
                myMarker = mMap.addMarker(markerOptions);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // create and load a route task from the world routing service. This will trigger logging in to your AGOL account
        mRouteTask = new RouteTask(getActivity(), getString(R.string.world_routing_service_url));
        mRouteTask.loadAsync();
    }

    private List<Places> populateListofNearbyPlaces() {

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
        updateLocationUI();
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

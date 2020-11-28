package edu.singaporetech.ict3104.project.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import edu.singaporetech.ict3104.java_to_unity_proxy.NavigationManager;
import edu.singaporetech.ict3104.java_to_unity_proxy.ObjectListener;
import edu.singaporetech.ict3104.java_to_unity_proxy.ObjectListenerManager;
import edu.singaporetech.ict3104.java_to_unity_proxy.PositionSensor;
import edu.singaporetech.ict3104.project.LocationSteps;
import edu.singaporetech.ict3104.project.PlaceOfInterest;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.activity.BaseActivity;

public class AugmentedRealityFragment extends Fragment implements LocationListener, OnMapReadyCallback, ObjectListener {

    public static List<LocationSteps> locationSteps = new ArrayList<>();
    public static List<PolylineOptions> polylineOptionsList = new ArrayList<>();
    public static int currentLocationStepIndex = 0;

    private final List<Location> locationHistory = new ArrayList<>();

    private PositionSensor positionSensor;
    private LocationManager locationManager;

    private NavController navController;

    private FrameLayout frameLayout;
    private MapView mapView;

    private TextView tv_degree;
    private ImageView iv_weather;

    private TextView textViewNavigationRoute;

    private List<PlaceOfInterest> listofpoi = new ArrayList<>();
    private List<Marker> listofPOIMarker = new ArrayList<>();
    private GoogleMap googleMap;
    private double temp = 0;
    private String wheathercondition;

    private Date lastRequestedWeatherDateTime = Calendar.getInstance().getTime();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_augmented_reality, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BaseActivity activity = (BaseActivity) requireActivity();

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (requireContext().checkSelfPermission(BaseActivity.CAMERA_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            activity.getNavController().navigate(R.id.action_augmentedRealityFragment_to_navigation_map);
            Toast.makeText(requireContext(), R.string.permission_missing, Toast.LENGTH_LONG).show();
            return;
        }

        if (requireContext().checkSelfPermission(BaseActivity.FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            activity.getNavController().navigate(R.id.action_augmentedRealityFragment_to_navigation_map);
            Toast.makeText(requireContext(), R.string.permission_missing, Toast.LENGTH_LONG).show();
            return;
        }

        if (requireContext().checkSelfPermission(BaseActivity.COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            activity.getNavController().navigate(R.id.action_augmentedRealityFragment_to_navigation_map);
            Toast.makeText(requireContext(), R.string.permission_missing, Toast.LENGTH_LONG).show();
            return;
        }

        // Assert that location steps is more than 0. If there isn't a location step, this view can't operate.
        if (locationSteps.size() < 1) {
            activity.getNavController().navigate(R.id.action_augmentedRealityFragment_to_navigation_map);
            Toast.makeText(requireContext(), "Unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        positionSensor = new PositionSensor(requireContext());

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);

        navController = activity.getNavController();

        frameLayout = view.findViewById(R.id.unityLayout);
        frameLayout.addView(activity.getUnityPlayer().getView());
        activity.getUnityPlayer().resume();

        mapView = view.findViewById(R.id.mapViewAr);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        tv_degree = view.findViewById(R.id.tv_degree);
        iv_weather = view.findViewById(R.id.iv_weather);

        textViewNavigationRoute = view.findViewById(R.id.textViewNavigationRoute);

        ObjectListenerManager.registerListener(this);
    }

    @Override
    public void onDestroyView() {
        frameLayout.removeAllViews();
        mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        positionSensor.onResume();
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        positionSensor.onPause();
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ((BaseActivity) requireActivity()).getUnityPlayer().pause();
        locationManager.removeUpdates(this);
        ObjectListenerManager.clearListener();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationHistory.add(location);

        if (locationHistory.size() >= 5) {
            locationHistory.remove(0);
        }

        location = locationHistory.stream().min((o1, o2) -> {
            if (o1.getAccuracy() == o2.getAccuracy()) return 0;
            return o1.getAccuracy() < o2.getAccuracy() ? -1 : 1;
        }).orElse(location);

        Log.d(AugmentedRealityFragment.class.getSimpleName(), location.toString());

        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

        if (currentLocationStepIndex >= AugmentedRealityFragment.locationSteps.size()) {
            Toast.makeText(requireContext(), "Location ended!", Toast.LENGTH_LONG).show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> navController.navigate(R.id.action_augmentedRealityFragment_to_navigation_map), 1000);
            locationManager.removeUpdates(this);
            return;
        }

        final LocationSteps locationSteps = AugmentedRealityFragment.locationSteps.get(currentLocationStepIndex);
        final LatLng end = locationSteps.getEnd_location();
        final Location endLocation = new Location(LocationManager.NETWORK_PROVIDER);
        endLocation.setLatitude(end.latitude);
        endLocation.setLongitude(end.longitude);

        final float distanceRemaining = location.distanceTo(endLocation);
        float hg= location.getAccuracy();
        if (distanceRemaining < location.getAccuracy()) {
            currentLocationStepIndex++;
        }

        NavigationManager.setDistanceRemaining(location.distanceTo(endLocation));
        NavigationManager.setTargetAzimuth(location.bearingTo(endLocation));

        textViewNavigationRoute.setText(HtmlCompat.fromHtml(locationSteps.getHtml_instructions(), HtmlCompat.FROM_HTML_MODE_COMPACT));

        updatePOILIST(location);
        updateWeather();
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.setTrafficEnabled(false);

        for (final PolylineOptions polylineOptions : polylineOptionsList) {
            googleMap.addPolyline(polylineOptions);
        }
    }

    @Override
    public void invokeObjectListener() {
        // Object on AR is pressed, do what?
        Log.d(AugmentedRealityFragment.class.getSimpleName(), "Triggered");
    }

    public void getAllPOIfromdb() {
        listofpoi = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collectionGroup("POI").get().addOnSuccessListener(queryDocumentSnapshots -> {
            //Iterate to get the products out of the queryDocumentSnapshots object
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                listofpoi.add(new PlaceOfInterest(document.getString("Name"), document.getDouble("Lat"), document.getDouble("Long"), document.getString("Item")));
            }
        });
    }

    public void updatePOILIST(Location location) {
        for (int j = 0; j < listofPOIMarker.size(); j++) {
            listofPOIMarker.get(j).remove();
        }
        if (listofpoi.size() != 0) {
            for (int i = 0; i < listofpoi.size(); i++) {
                PlaceOfInterest cur = listofpoi.get(i);
                Location test = new Location("");
                test.setLatitude(cur.getLatitude());
                test.setLongitude(cur.getLongitude());
                if (location.distanceTo(test) < 300) {
                    Marker m;
                    String title = cur.getFeaturename().substring(0, Math.min(cur.getFeaturename().length(), 34));
                    if (cur.getTag().equals("Staircase")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.staircase)));
                    } else if (cur.getTag().equals("Ramp")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.ramp)));
                    } else if (cur.getTag().contains("Path")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.path)));
                    } else if (cur.getTag().equals("Bollard")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.bollard)));
                    } else if (cur.getTag().equals("Bench")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.bench)));
                    } else if (cur.getTag().equals("Fencing")) {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.fence)));
                    } else {
                        m = googleMap.addMarker(new MarkerOptions().position(new LatLng(test.getLatitude(), test.getLongitude())).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.place_of_interest)));
                    }
                    listofPOIMarker.add(m);
                }
            }
        }

        getAllPOIfromdb();
    }

    public void updateWeather() {
        getWeatherStatus();
        double kelvin = 273.15;
        DecimalFormat df = new DecimalFormat("#.#");
        df.format(temp - kelvin);
        tv_degree.setText(String.format("%s°C", Double.parseDouble(df.format(temp - kelvin))));
        if (wheathercondition.equals("Clouds")) {
            iv_weather.setImageResource(R.drawable.ic_baseline_cloud_24);
        } else if (wheathercondition.equals("Rain")) {
            iv_weather.setImageResource(R.drawable.ic_baseline_rain_24);
        } else {
            iv_weather.setImageResource(R.drawable.ic_baseline_ac_unit_24);
        }
    }

    public void getWeatherStatus() {
        if (Calendar.getInstance().getTime().after(lastRequestedWeatherDateTime)) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            lastRequestedWeatherDateTime = calendar.getTime();
        } else {
            return;
        }

        try {
            String apiLink = "https://api.openweathermap.org/data/2.5/weather?q=singapore&appid=f5d4780942ffeb755aea90cf2df24e69";
            URL url2 = new URL(apiLink);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                String response = responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONObject main = jsonObject.getJSONObject("main");
                temp = main.getDouble("temp");
                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject firstobject = weather.getJSONObject(0);
                wheathercondition = firstobject.getString("main");
            } finally {
                connection.disconnect();
            }
        } catch (IOException | JSONException ignored) {
        }
    }

}
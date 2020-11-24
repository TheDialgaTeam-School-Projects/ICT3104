package edu.singaporetech.ict3104.project.fragment;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.singaporetech.ict3104.java_to_unity_proxy.NavigationManager;
import edu.singaporetech.ict3104.java_to_unity_proxy.PositionSensor;
import edu.singaporetech.ict3104.project.LocationSteps;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.activity.BaseActivity;

import static android.content.Context.LOCATION_SERVICE;

public class AugmentedRealityFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    public static List<LocationSteps> locationSteps = new ArrayList<>();
    public static List<PolylineOptions> polylineOptionsList = new ArrayList<>();
    public static int currentLocationStepIndex = 0;

    private PositionSensor positionSensor;
    private LocationManager locationManager;

    private FrameLayout frameLayout;
    private MapView mapView;

    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_augmented_reality, container, false);
    }

    @SuppressLint("MissingPermission")
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

        locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        frameLayout = view.findViewById(R.id.unityLayout);
        frameLayout.addView(activity.getUnityPlayer().getView());
        activity.getUnityPlayer().resume();

        mapView = view.findViewById(R.id.mapViewAr);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
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
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

        if (currentLocationStepIndex >= AugmentedRealityFragment.locationSteps.size()) {
            Toast.makeText(requireContext(), "Location ended!", Toast.LENGTH_LONG).show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> ((BaseActivity) requireActivity()).getNavController().navigate(R.id.action_augmentedRealityFragment_to_navigation_map), 1000);
            locationManager.removeUpdates(this);
            return;
        }

        final LocationSteps locationSteps = AugmentedRealityFragment.locationSteps.get(currentLocationStepIndex);
        final LatLng end = locationSteps.getEnd_location();
        final Location endLocation = new Location(LocationManager.NETWORK_PROVIDER);
        endLocation.setLatitude(end.latitude);
        endLocation.setLongitude(end.longitude);

        final float distanceRemaining = location.distanceTo(endLocation);

        if (distanceRemaining < location.getAccuracy()) {
            currentLocationStepIndex++;
        }

        NavigationManager.setDistanceRemaining(location.distanceTo(endLocation));
        NavigationManager.setTargetAzimuth(location.bearingTo(endLocation));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.setTrafficEnabled(false);

        for (final PolylineOptions polylineOptions: polylineOptionsList) {
            googleMap.addPolyline(polylineOptions);
        }
    }
}
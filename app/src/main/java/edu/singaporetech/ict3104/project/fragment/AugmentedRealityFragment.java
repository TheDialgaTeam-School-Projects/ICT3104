package edu.singaporetech.ict3104.project.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

public class AugmentedRealityFragment extends Fragment implements LocationListener {

    public static List<LocationSteps> locationSteps = new ArrayList<>();
    public static int currentLocationStepIndex = 0;

    private PositionSensor positionSensor;
    private LocationManager locationManager;

    private FrameLayout frameLayout;

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


        // Asset that location steps is more than 0. If there isn't a location step, this view can't operate.
        if (locationSteps.size() < currentLocationStepIndex + 1) {
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
    }

    @Override
    public void onDestroyView() {
        frameLayout.removeAllViews();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        positionSensor.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        positionSensor.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ((BaseActivity) requireActivity()).getUnityPlayer().pause();
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        final LocationSteps locationSteps = AugmentedRealityFragment.locationSteps.get(currentLocationStepIndex);
        final LatLng end = locationSteps.getEnd_location();
        final Location endLocation = new Location(LocationManager.NETWORK_PROVIDER);
        endLocation.setLatitude(end.latitude);
        endLocation.setLongitude(end.longitude);

        Log.d("TEST", "LAT: " + end.latitude + " LNG: " + end.longitude);

        NavigationManager.setDistanceRemaining(location.distanceTo(endLocation));
        NavigationManager.setTargetAzimuth(location.bearingTo(endLocation));
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
}
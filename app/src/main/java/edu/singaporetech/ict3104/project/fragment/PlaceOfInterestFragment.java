package edu.singaporetech.ict3104.project.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import edu.singaporetech.ict3104.project.ForgetPasswordActivity;
import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;

import static android.content.Context.LOCATION_SERVICE;

public class PlaceOfInterestFragment extends Fragment implements LocationListener {

    private Spinner spinner;
    Location curLocation;
    LocationManager mLocationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_of_interest, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.placeOIspinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.placeOfInterest, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);


//        Button button_add_place_of_Interest = view.findViewById(R.id.add_btn);
//        button_add_place_of_Interest.setOnClickListener(v -> {
//            startActivity(new Intent(this, MapFragment.class));
//        });
//        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        Button btnCheckFalAction = view.findViewById(R.id.add_btn); // you have to use rootview object..

        btnCheckFalAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                Toast.makeText(getActivity(), "place of interest", Toast.LENGTH_LONG).show();


            }
        });




    }
    @Override
    public void onLocationChanged(Location newLocation) {
        curLocation=newLocation;
        Toast.makeText(getActivity(),String.valueOf(curLocation.getLongitude() )+ String.valueOf(curLocation.getLatitude()),Toast.LENGTH_SHORT).show();
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
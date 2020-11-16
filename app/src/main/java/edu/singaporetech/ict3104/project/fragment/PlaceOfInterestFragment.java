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


import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.singaporetech.ict3104.project.ForgetPasswordActivity;
import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.helpers.FireStoreHelper;

import static android.content.Context.LOCATION_SERVICE;

public class PlaceOfInterestFragment extends Fragment implements LocationListener {

    private Spinner spinner;
    Location curLocation;
    LocationManager mLocationManager;

    private double longitude , Latitude;
    private String spinnerData = null;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(parent.getContext(),"on Item Select : \n " + parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
                spinnerData = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        EditText eTnameOfplace = view.findViewById(R.id.nameofplace);

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

                //Toast.makeText(getActivity(), "place of interest", Toast.LENGTH_LONG).show();
                Map<String, Object> data = new HashMap<>();
                data.put("Item", spinner.getSelectedItem());
                data.put("Lat", curLocation.getLatitude());
                data.put("Long", curLocation.getLongitude());
                data.put("Name", eTnameOfplace.getText().toString());

                db.collection("POI").document().set(data);
            }
        });
    }


    @Override
    public void onLocationChanged(Location newLocation) {
        curLocation=newLocation;
        //Toast.makeText(getActivity(), curLocation.getLongitude() + String.valueOf(curLocation.getLatitude()),Toast.LENGTH_SHORT).show();
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
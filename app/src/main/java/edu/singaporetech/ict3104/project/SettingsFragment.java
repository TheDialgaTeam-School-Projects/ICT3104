package edu.singaporetech.ict3104.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return view;
        }

        final String email = firebaseUser.getEmail();

        if (email == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return view;
        }

        final TextView textViewSettingsUsername = view.findViewById(R.id.textViewSettingsUsername);
        textViewSettingsUsername.setText(email);

        final Spinner spinnerSettingsCommuteMethod = view.findViewById(R.id.spinnerSettingsCommuteMethod);

        final ArrayList<String> commuteMethods = new ArrayList<>();
        commuteMethods.add("Walking");
        commuteMethods.add("Wheelchair");
        commuteMethods.add("Parent with Pram");

        spinnerSettingsCommuteMethod.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, commuteMethods));

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(email).get()
                .addOnFailureListener(requireActivity(), e -> {
                    Log.e(SettingsFragment.class.getName(), "Unable to retrieve user data.", e);
                    Toast.makeText(requireActivity(), "Unable to retrieve user data.", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(requireActivity(), documentSnapshot -> {
                    final String commuteType = documentSnapshot.getString("Commute Type");
                    if  (commuteType == null) return;

                    for (int i = 0; i < commuteMethods.size(); i++) {
                        if (!commuteMethods.get(i).contentEquals(commuteType)) continue;
                        spinnerSettingsCommuteMethod.setSelection(i);
                        break;
                    }
                });

        final Button buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave);
        buttonSettingsSave.setOnClickListener(v -> {
            firestore.collection("Users").document(email).update("Commute Type", spinnerSettingsCommuteMethod.getSelectedItem())
                    .addOnFailureListener(requireActivity(), e -> {
                        Log.e(SettingsFragment.class.getName(), "Unable to save user data.", e);
                        Toast.makeText(requireActivity(), "Unable to save user data.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnSuccessListener(requireActivity(), aVoid -> {
                        Toast.makeText(requireActivity(), "Successfully saved user data.", Toast.LENGTH_SHORT).show();
                    });
        });

        Button buttonSettingsLogout = view.findViewById(R.id.buttonSettingsLogout);
        buttonSettingsLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        return view;
    }
}
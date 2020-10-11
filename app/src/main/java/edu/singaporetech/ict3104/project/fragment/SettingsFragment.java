package edu.singaporetech.ict3104.project.fragment;

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

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.helpers.FireStoreHelper;

public class SettingsFragment extends Fragment {

    private static final String EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS_KEY";
    private static final String COMMUTE_METHOD_KEY = "COMMUTE_METHOD_KEY";

    private final ArrayList<String> commuteMethods = new ArrayList<>();

    private String email;

    private TextView textViewSettingsUsername;
    private Spinner spinnerSettingsCommuteMethod;

    public SettingsFragment() {
        commuteMethods.add("Walking");
        commuteMethods.add("Wheelchair");
        commuteMethods.add("Parent with Pram");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }

        email = firebaseUser.getEmail();

        if (email == null) {
            Toast.makeText(getContext(), "Unexpected error with the user.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewSettingsUsername = view.findViewById(R.id.textViewSettingsUsername);

        spinnerSettingsCommuteMethod = view.findViewById(R.id.spinnerSettingsCommuteMethod);
        spinnerSettingsCommuteMethod.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, commuteMethods));

        final Button buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave);
        buttonSettingsSave.setOnClickListener(v -> {
            final HashMap<String, Object> userData = new HashMap<>();
            userData.put("Commute Type", spinnerSettingsCommuteMethod.getSelectedItem());

            FireStoreHelper.setOrUpdateUserData(email, userData)
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
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            textViewSettingsUsername.setText(savedInstanceState.getString(EMAIL_ADDRESS_KEY, ""));
            spinnerSettingsCommuteMethod.setSelection(savedInstanceState.getInt(COMMUTE_METHOD_KEY, 0));
        } else {
            textViewSettingsUsername.setText(email);

            FireStoreHelper.getUserData(email)
                    .addOnFailureListener(requireActivity(), e -> {
                        Log.e(SettingsFragment.class.getName(), "Unable to retrieve user data.", e);
                        Toast.makeText(requireActivity(), "Unable to retrieve user data.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnSuccessListener(requireActivity(), documentSnapshot -> {
                        final String commuteType = documentSnapshot.getString("Commute Type");
                        if (commuteType == null) return;

                        for (int i = 0; i < commuteMethods.size(); i++) {
                            if (!commuteMethods.get(i).contentEquals(commuteType)) continue;
                            spinnerSettingsCommuteMethod.setSelection(i);
                            break;
                        }
                    });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_ADDRESS_KEY, textViewSettingsUsername.getText().toString());
        outState.putInt(COMMUTE_METHOD_KEY, spinnerSettingsCommuteMethod.getSelectedItemPosition());
    }

}
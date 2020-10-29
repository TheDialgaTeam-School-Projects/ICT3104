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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.MainActivity;
import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.helpers.FireStoreHelper;

public class SettingsFragment extends Fragment {

    private static final String EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS_KEY";
    private static final String COMMUTE_METHOD_KEY = "COMMUTE_METHOD_KEY";

    private final ArrayList<String> commuteMethods = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String commuteType;
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


        //Toast.makeText(getContext(),commuteType,Toast.LENGTH_LONG).show();

        //read commute method from user in fb

    }

    public void getCommuteMethod(String username) {
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            commuteType = documentSnapshot.getString("Commute Type");
                            System.out.println("LET GOOOOOOOOOOOOOOO : " + commuteType);
                        } else {
                            // Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
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

        //instantiate and declare
        final Button buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave);
        //On click save
        buttonSettingsSave.setOnClickListener(v -> {
            DocumentReference docRef = db.collection("Users").document(email);
            Map<String, Object> data = new HashMap<>();

            data.put("Commute Type", spinnerSettingsCommuteMethod.getSelectedItem().toString());

            docRef.update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("string", "onSuccess: yay, updated the doc");
                            Toast.makeText(requireActivity(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(),"Failed to Update",Toast.LENGTH_SHORT).show();

                        }
                    });
        });

        //instantiate and declare
        Button buttonSettingsLogout = view.findViewById(R.id.buttonSettingsLogout);
        //On click signout
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
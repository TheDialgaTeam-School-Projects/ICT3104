package com.example.a3104_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView userName,userNameTag;
    private Button logout;


    public SettingsFragment() {
        // Required empty public constructor
    }
    //implement change settings of walking

    //implement logout of firebase and application, return to log in




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialise
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        logout = (Button) view.findViewById(R.id.button_logout);
        userName = (TextView) view.findViewById(R.id.userName);
        userNameTag = (TextView) view.findViewById(R.id.userNameTag);

        logout.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    mAuth.signOut();
                    //clear back using fragments
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    int count = fm.getBackStackEntryCount();
                    for(int i = 0; i < count; ++i) {
                        fm.popBackStackImmediate();
                    }
                    Intent i = new Intent(getActivity(),
                            Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }
}

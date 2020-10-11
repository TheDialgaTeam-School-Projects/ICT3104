package edu.singaporetech.ict3104.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        final Button buttonHomeAr = view.findViewById(R.id.buttonHomeAr);
        buttonHomeAr.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_agumentedRealityFragment);
        });

        return view;
    }
}
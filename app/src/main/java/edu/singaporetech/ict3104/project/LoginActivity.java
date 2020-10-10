package edu.singaporetech.ict3104.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkMobilePermission();

        final EditText editTextLoginEmailAddress = findViewById(R.id.editTextLoginEmailAddress);
        final EditText editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        final Button buttonLogin = findViewById(R.id.buttonLogin);
        final TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        final TextView textViewForgetPassword = findViewById(R.id.textViewForgetPassword);

        buttonLogin.setOnClickListener(v -> {
            boolean valid = true;
            final String emailAddress = editTextLoginEmailAddress.getText().toString().trim();
            final String password = editTextLoginPassword.getText().toString();

            if (emailAddress.isEmpty()) {
                editTextLoginEmailAddress.setError("Email Address cannot be empty.");
                valid = false;
            }

            if (password.isEmpty()) {
                editTextLoginPassword.setError("Password cannot be empty.");
                valid = false;
            }

            if (valid) {
                login(emailAddress, password);
            }
        });

        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        textViewForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });
    }

    private void checkMobilePermission() {
        final String[] permissionToRequest = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(permissionToRequest, 0);
    }

    private void login(String email, String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(this, e -> {
                    Log.e(LoginActivity.class.getName(), "Authentication failed.", e);
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(this, authResult -> {
                    if (authResult.getUser() == null) {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final ArrayList<String> permissionLeftToGrant = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) continue;
            permissionLeftToGrant.add(permissions[i]);
        }

        if (permissionLeftToGrant.size() > 0) {
            String[] permissionToGrant = new String[permissionLeftToGrant.size()];
            permissionToGrant = permissionLeftToGrant.toArray(permissionToGrant);
            requestPermissions(permissionToGrant, 0);
        }
    }
}
package edu.singaporetech.ict3104.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        final EditText editTextForgetEmailAddress = findViewById(R.id.editTextForgetEmailAddress);
        final Button buttonForgetPasswordConfirm = findViewById(R.id.buttonForgetPasswordConfirm);

        buttonForgetPasswordConfirm.setOnClickListener(v -> {
            boolean isValid = true;
            String emailAddress = editTextForgetEmailAddress.getText().toString().trim();

            if (emailAddress.isEmpty()) {
                editTextForgetEmailAddress.setError("Email cannot be empty.");
                isValid = false;
            } else if (!emailAddress.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                editTextForgetEmailAddress.setError("Invalid email.");
                isValid = false;
            }

            if (isValid) {
                recoverAccount(emailAddress);
            }
        });
    }

    private void recoverAccount(String emailAddress) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnFailureListener(this, e -> {
                    Log.e(LoginActivity.class.getName(), "Unable to send recovery email.", e);
                    Toast.makeText(this, "Unable to send recovery email.", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(this, aVoid -> {
                    Toast.makeText(this, "Recovery email has been sent.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                });
    }
}
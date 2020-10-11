package edu.singaporetech.ict3104.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.ict3104.project.helpers.KeyboardHelper;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ForgetPasswordActivity.class.getName();

    private static final String EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS_KEY";

    private EditText editTextForgetEmailAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editTextForgetEmailAddress = findViewById(R.id.editTextForgetEmailAddress);

        final Button buttonForgetPasswordConfirm = findViewById(R.id.buttonForgetPasswordConfirm);
        buttonForgetPasswordConfirm.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(this);

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

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTextForgetEmailAddress.setText(savedInstanceState.getString(EMAIL_ADDRESS_KEY, ""));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_ADDRESS_KEY, editTextForgetEmailAddress.getText().toString());
    }

    private void recoverAccount(String emailAddress) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to send recovery email.", e);
                    Toast.makeText(this, "Unable to send recovery email.", Toast.LENGTH_LONG).show();
                })
                .addOnSuccessListener(this, aVoid -> {
                    Toast.makeText(this, "Recovery email has been sent.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                });
    }
}
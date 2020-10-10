package edu.singaporetech.ict3104.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText editTextSignUpEmailAddress = findViewById(R.id.editTextSignUpEmailAddress);
        final EditText editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        final EditText editTextSignUpPasswordConfirm = findViewById(R.id.editTextSignUpPasswordConfirm);
        final EditText editTextSignUpAge = findViewById(R.id.editTextSignUpAge);
        final RadioGroup radioGroupSignUpGender = findViewById(R.id.radioGroupSignUpGender);
        final RadioButton radioButtonSignUpMale = findViewById(R.id.radioButtonSignUpMale);
        final Spinner spinnerSignUpCommuteMethod = findViewById(R.id.spinnerSignUpCommuteMethod);
        final Button buttonSignUp = findViewById(R.id.buttonSignUp);

        radioButtonSignUpMale.setChecked(true);

        final ArrayList<String> commuteMethods = new ArrayList<>();
        commuteMethods.add("Walking");
        commuteMethods.add("Wheelchair");
        commuteMethods.add("Parent with Pram");

        spinnerSignUpCommuteMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, commuteMethods));
        spinnerSignUpCommuteMethod.setSelection(0);

        buttonSignUp.setOnClickListener(v -> {
            boolean isValid = true;

            final String emailAddress = editTextSignUpEmailAddress.getText().toString().trim();
            final String password = editTextSignUpPassword.getText().toString();
            final String passwordConfirm = editTextSignUpPasswordConfirm.getText().toString();
            final String age = editTextSignUpAge.getText().toString().trim();
            final String gender = ((RadioButton) findViewById(radioGroupSignUpGender.getCheckedRadioButtonId())).getText().toString();
            final String commuteMethod = spinnerSignUpCommuteMethod.getSelectedItem().toString();

            // Validation
            if (emailAddress.isEmpty()) {
                editTextSignUpEmailAddress.setError("Email address cannot be empty.");
                isValid = false;
            } else if (!emailAddress.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                editTextSignUpEmailAddress.setError("Email address is invalid.");
                isValid = false;
            }

            if (password.isEmpty()) {
                editTextSignUpPassword.setError("Password cannot be empty.");
                isValid = false;
            } else if (!password.contentEquals(passwordConfirm)) {
                editTextSignUpPassword.setError("Password do not match.");
                isValid = false;
            }

            if (passwordConfirm.isEmpty()) {
                editTextSignUpPasswordConfirm.setError("Password cannot be empty.");
                isValid = false;
            }

            if (age.isEmpty()) {
                editTextSignUpAge.setError("Age cannot be empty.");
                isValid = false;
            } else if (!age.matches("^\\d+$")) {
                editTextSignUpAge.setError("Invalid age.");
                isValid = false;
            }

            if (isValid) {
                Map<String, String> data = new HashMap<>();
                data.put("Email", emailAddress);
                data.put("Age", age);
                data.put("Gender", gender);
                data.put("Commute Type", commuteMethod);

                signUp(emailAddress, password, data);
            }
        });
    }

    private void signUp(String email, String password, Map<String, String> data) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(this, e -> {
                    Log.e(SignUpActivity.class.getName(), "Register failed.", e);
                    Toast.makeText(this, "Register failed.", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(this, authResult -> {
                    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Users").document(email).set(data)
                            .addOnFailureListener(this, e -> {
                                Log.e(SignUpActivity.class.getName(), "Register failed.", e);
                                Toast.makeText(this, "Register failed.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(this, aVoid -> {
                                startActivity(new Intent(this, LoginActivity.class));
                            });
                });
    }
}
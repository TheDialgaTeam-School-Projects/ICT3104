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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.ict3104.project.helpers.FireStoreHelper;
import edu.singaporetech.ict3104.project.helpers.KeyboardHelper;

public class SignUpActivity extends AppCompatActivity {

    String FBCode;

    private static final String TAG = SignUpActivity.class.getName();

    private static final String EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String PASSWORD_CONFIRM_KEY = "PASSWORD_CONFIRM_KEY";
    private static final String AGE_KEY = "AGE_KEY";
    private static final String GENDER_KEY = "GENDER_KEY";
    private static final String COMMUTE_METHOD_KEY = "COMMUTE_METHOD_KEY";

    private final ArrayList<String> commuteMethods = new ArrayList<>();

    private EditText editTextSignUpEmailAddress;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpPasswordConfirm;
    private EditText editTextSignUpAge;
    private RadioGroup radioGroupSignUpGender;
    private Spinner spinnerSignUpCommuteMethod;
    private EditText editTextCode;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SignUpActivity() {
        commuteMethods.add("Walking");
        commuteMethods.add("Wheelchair");
        commuteMethods.add("Parent with Pram");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpEmailAddress = findViewById(R.id.editTextSignUpEmailAddress);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        editTextSignUpPasswordConfirm = findViewById(R.id.editTextSignUpPasswordConfirm);
        editTextSignUpAge = findViewById(R.id.editTextSignUpAge);
        radioGroupSignUpGender = findViewById(R.id.radioGroupSignUpGender);
        spinnerSignUpCommuteMethod = findViewById(R.id.spinnerSignUpCommuteMethod);
        spinnerSignUpCommuteMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, commuteMethods));
        editTextCode = findViewById(R.id.editTextCode);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(this);

            boolean isValid = true;

            final String emailAddress = editTextSignUpEmailAddress.getText().toString().trim();
            final String password = editTextSignUpPassword.getText().toString();
            final String passwordConfirm = editTextSignUpPasswordConfirm.getText().toString();
            final String age = editTextSignUpAge.getText().toString().trim();
            final String gender = ((RadioButton) findViewById(radioGroupSignUpGender.getCheckedRadioButtonId())).getText().toString();
            final String commuteMethod = spinnerSignUpCommuteMethod.getSelectedItem().toString();
            final String code = editTextCode.getText().toString();

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
                Map<String, Object> data = new HashMap<>();
                data.put("Email", emailAddress);
                data.put("Age", age);
                data.put("Gender", gender);
                data.put("Commute Type", commuteMethod);
                if (code != FBCode)
                {
                    data.put("Role", "F");
                }
                else
                {
                    data.put("Role", "T");
                }
                signUp(emailAddress, password, data);
                db.collection("Users").document(editTextSignUpEmailAddress.getText().toString().trim()).set(data);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTextSignUpEmailAddress.setText(savedInstanceState.getString(EMAIL_ADDRESS_KEY, ""));
        editTextSignUpPassword.setText(savedInstanceState.getString(PASSWORD_KEY, ""));
        editTextSignUpPasswordConfirm.setText(savedInstanceState.getString(PASSWORD_CONFIRM_KEY, ""));
        editTextSignUpAge.setText(savedInstanceState.getString(AGE_KEY, ""));
        ((RadioButton) findViewById(savedInstanceState.getInt(GENDER_KEY, R.id.radioButtonSignUpMale))).setChecked(true);
        spinnerSignUpCommuteMethod.setSelection(savedInstanceState.getInt(COMMUTE_METHOD_KEY, 0));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_ADDRESS_KEY, editTextSignUpEmailAddress.getText().toString());
        outState.putString(PASSWORD_KEY, editTextSignUpPassword.getText().toString());
        outState.putString(PASSWORD_CONFIRM_KEY, editTextSignUpPassword.getText().toString());
        outState.putString(AGE_KEY, editTextSignUpAge.getText().toString());
        outState.putInt(GENDER_KEY, radioGroupSignUpGender.getCheckedRadioButtonId());
        outState.putInt(COMMUTE_METHOD_KEY, spinnerSignUpCommuteMethod.getSelectedItemPosition());
    }

    private void signUp(String email, String password, Map<String, Object> data) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(this, e -> {
                    if (e instanceof FirebaseAuthWeakPasswordException) {
                        final String reason = ((FirebaseAuthWeakPasswordException) e).getReason();
                        editTextSignUpPassword.setError(reason);
                        Log.e(TAG, reason, e);
                        Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        editTextSignUpPassword.setError(e.getMessage());
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                        editTextSignUpEmailAddress.setError(e.getMessage());
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnSuccessListener(this, authResult -> FireStoreHelper.setOrUpdateUserData(email, data)
                        .addOnFailureListener(this, e -> {
                            Log.e(TAG, "Unexpected error occurred when updating user data.", e);
                            Toast.makeText(this, "Unexpected error occurred when updating user data.", Toast.LENGTH_LONG).show();
                        })
                        .addOnSuccessListener(this, aVoid -> {
                            Toast.makeText(this, "Register success.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, LoginActivity.class));
                        }));
    }

}
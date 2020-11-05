package edu.singaporetech.ict3104.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.ict3104.project.helpers.KeyboardHelper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private static final String SHARED_PREFERENCE_KEY = "Credentials";

    private static final String EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";

    private ProgressBar progressBar;
    private EditText editTextLoginEmailAddress;
    private EditText editTextLoginPassword;
    private SharedPreferences sharedPreferences;

    String FireBaseUserRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        editTextLoginEmailAddress = findViewById(R.id.editTextLoginEmailAddress);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        progressBar.setVisibility(View.GONE);
        editTextLoginEmailAddress.setText(sharedPreferences.getString(EMAIL_ADDRESS_KEY, ""));
        editTextLoginPassword.setText(sharedPreferences.getString(PASSWORD_KEY, ""));

        final Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(this);

            boolean isValid = true;

            final String emailAddress = editTextLoginEmailAddress.getText().toString().trim();
            final String password = editTextLoginPassword.getText().toString();

            if (emailAddress.isEmpty()) {
                editTextLoginEmailAddress.setError("Email Address cannot be empty.");
                isValid = false;
            } else if (!emailAddress.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                editTextLoginEmailAddress.setError("Email address is invalid.");
                isValid = false;
            }

            if (password.isEmpty()) {
                editTextLoginPassword.setError("Password cannot be empty.");
                isValid = false;
            }

            if (isValid) {
                login(emailAddress, password);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        final TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        final TextView textViewForgetPassword = findViewById(R.id.textViewForgetPassword);
        textViewForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTextLoginEmailAddress.setText(savedInstanceState.getString(EMAIL_ADDRESS_KEY, ""));
        editTextLoginPassword.setText(savedInstanceState.getString(PASSWORD_KEY, ""));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_ADDRESS_KEY, editTextLoginEmailAddress.getText().toString());
        outState.putString(PASSWORD_KEY, editTextLoginPassword.getText().toString());
    }

    private void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnFailureListener(this, e -> {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnSuccessListener(this, authResult -> {
                    sharedPreferences.edit()
                            .putString(EMAIL_ADDRESS_KEY, email)
                            .putString(PASSWORD_KEY, password)
                            .apply();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Users").document(email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    FireBaseUserRole = document.getString("Role");
                                    if (FireBaseUserRole.equals("T"))
                                    {
                                        startActivity(new Intent(getApplicationContext(), PlannerMainActivity.class));
                                    }
                                    else
                                    {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                });
    }
}
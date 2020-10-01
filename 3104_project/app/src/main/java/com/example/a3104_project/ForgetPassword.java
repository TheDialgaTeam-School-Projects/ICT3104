package com.example.a3104_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String TAG = "Firebase";
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        Button b1 = (Button)findViewById(R.id.buttonLogin);
        username = (EditText)findViewById(R.id.editTextTextPersonName);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
                forgetPassword(username.getText().toString());
            }
        });
    }

    void forgetPassword(String username)
    {
        mAuth.sendPasswordResetEmail(username)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPassword.this, "Authentication passed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgetPassword.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
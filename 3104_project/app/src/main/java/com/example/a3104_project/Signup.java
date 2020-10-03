package com.example.a3104_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String TAG = "Firebase";
    Spinner spinner;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Assign variable
        spinner = findViewById(R.id.spinner_CM);
        username = (EditText)findViewById(R.id.editTextTextPassword2);

        ArrayList<String> numberList = new ArrayList<>();

        numberList.add("Walking");
        numberList.add("WheelChair");
        numberList.add("parent with pram");

        spinner.setAdapter(new ArrayAdapter<>(Signup.this,android.R.layout.simple_spinner_dropdown_item,numberList));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0 ){

                    Toast.makeText(getApplicationContext(),"walking",Toast.LENGTH_SHORT).show();


                }else if (position == 1 ){
                    Toast.makeText(getApplicationContext(),"2 ",Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(getApplicationContext(),"3 ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    void createAccount(String user, String pw)
    {
        mAuth.createUserWithEmailAndPassword(user, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
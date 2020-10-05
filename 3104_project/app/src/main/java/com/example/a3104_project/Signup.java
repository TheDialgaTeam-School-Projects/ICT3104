package com.example.a3104_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String TAG = "Firebase";
    Spinner spinner;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText username, password, age;
    RadioGroup radio;
    RadioButton radioButtonM, radioButtonF;
    String genderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assign variable
        radio = (RadioGroup)findViewById(R.id.radioGroup);
        username = (EditText)findViewById(R.id.editTextEmail);
        password = (EditText)findViewById(R.id.editTextPassword);
        age = (EditText)findViewById(R.id.editTextAge);
        spinner = findViewById(R.id.spinner_CM);
        radioButtonM = findViewById(R.id.radioButtonM);
        radioButtonF = findViewById(R.id.radioButtonF);
        Button btnSignUp = (Button)findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonM.isChecked()) {
                    genderType = radioButtonM.getText().toString();
                }
                else {
                    genderType = radioButtonF.getText().toString();
                }
                // Add document data with auto-generated id.
                System.out.println(age.getText().toString());
                System.out.println(spinner.getSelectedItem().toString());
                System.out.println(username.getText().toString());
                Map<String, Object> data = new HashMap<>();
                data.put("Age", age.getText().toString());
                data.put("Commute Type", spinner.getSelectedItem().toString());
                data.put("Gender", genderType);
                data.put("Email", username.getText().toString());
                db.collection("Users").document(username.getText().toString()).set(data);
                createAccount(username.getText().toString(), password.getText().toString());
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

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
        mAuth = FirebaseAuth.getInstance();
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
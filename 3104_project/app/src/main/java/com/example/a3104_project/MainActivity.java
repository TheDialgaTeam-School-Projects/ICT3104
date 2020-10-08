package com.example.a3104_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Fragment HomeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HomeFragment= new HomeFragment();
         // Bottom nav
        BottomNavigationView btnNav = findViewById(R.id.bottomNavigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);

        // Setting Home Fragment as main fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frament_layout,HomeFragment).commit();

    }


    // check if user have login
    protected void onStar(){
        super.onStart();

        // when firebase is link , we can use this if else to check if user has login or not

        if(true){
        startActivity(new Intent(this,Login.class));
        finish();
        }


    }





    // listener Nav bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.item1:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.item2:
                            selectedFragment = new AchivementFragment();
                            break;
                        case R.id.item3:
                            selectedFragment = new SettingsFragment();
                            break;

                    }

                    // begin transaction
                    getSupportFragmentManager().beginTransaction().replace(R.id.frament_layout,selectedFragment).commit();




                    return true;
                }
            };



}

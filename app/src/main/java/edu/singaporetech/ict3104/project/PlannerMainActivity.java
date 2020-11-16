package edu.singaporetech.ict3104.project;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import edu.singaporetech.ict3104.project.activity.BaseActivity;

public class PlannerMainActivity extends BaseActivity {

    private NavController navController;

    @Override
    public NavController getNavController() {
        return navController;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_planner);

        final BottomNavigationView navView = findViewById(R.id.nav_view_planner);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_planner);

        NavigationUI.setupWithNavController(navView, navController);
    }

}
package edu.singaporetech.ict3104.project.fragment;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import edu.singaporetech.ict3104.project.R;
import edu.singaporetech.ict3104.project.helpers.FireStoreHelper;

public class WeatherFragment extends Fragment  {
    private TextView tv_degree;
    private String apiLink = "https://api.openweathermap.org/data/2.5/weather?q=singapore&appid=f5d4780942ffeb755aea90cf2df24e69";
    private double kelvin = 273.15;
    private double temp=0;
    private Thread mythread;
    private boolean stopThread=false;
    DecimalFormat df = new DecimalFormat("#.##");
    private Handler mHandler;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public WeatherFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateWeather(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        tv_degree = rootView.findViewById(R.id.tv_degree);
        mHandler = new Handler();
        startRepeatingTask();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
       }
    public void updateWeather(){
        Log.i("UPDATE", "MyClass.getView() — get item number $position");

        df.format(getDegree()-kelvin);
        tv_degree.setText(String.format("%s°C", Double.toString(getDegree()-kelvin)));

//        tv_degree.setText(String.format("%s°C", Double.toString(Double.parseDouble(df.format(actualCelsius)))));
    }
    public double getDegree(){
        double temp=0;
        try{
            URL url2 = new URL(apiLink);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            try{
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                String response = responseStrBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONObject main = jsonObject.getJSONObject("main");
                temp = main.getDouble("temp");
            }
            finally
            {
                connection.disconnect();
            }
        }catch(IOException | JSONException e) {
            String t = e.getMessage();
        }
        return temp;
    }
}
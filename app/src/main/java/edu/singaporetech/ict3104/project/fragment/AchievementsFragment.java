package edu.singaporetech.ict3104.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;

public class AchievementsFragment extends Fragment {

    public AchievementsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate((R.layout.fragment_achievements),container,false);
        ListView mListView = (ListView) view.findViewById(R.id.planner_listview);

        //Create the feature objects
        feature feature1 = new feature("lamp",5,21, 'M', 'W');
        feature feature2 = new feature("staircase",5,21, 'M', 'W');
        feature feature3 = new feature("lamp",4,25, 'M', 'W');
        feature feature4 = new feature("staircase",2,21, 'F', 'W');
        feature feature5 = new feature("lamp",3,21, 'F', 'W');
        feature feature6 = new feature("staircase",4,21, 'F', 'W');

        //add the feature objects to an ArrayList
        ArrayList<feature> featureList = new ArrayList<>();
        featureList.add(feature1);
        featureList.add(feature2);
        featureList.add(feature3);
        featureList.add(feature4);
        featureList.add(feature5);
        featureList.add(feature6);

        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(),R.layout.adapter_view_layout,featureList);
        mListView=(ListView)view.findViewById(R.id.planner_listview);
        mListView.setAdapter(adapter);
//        ArrayAdapter adapter = new ArrayAdapter(
//                getContext(),
//                android.R.layout.simple_list_item_1,
//                data
//        );
//
//        ListView lvData = (ListView) view.findViewById(R.id.planner_listview);
//        lvData.setAdapter(adapter);
        return view;
    }

//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view ,savedInstanceState);
//
//
//        ListView listView = (ListView)view.findViewById(R.id.planner_listview);
//        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_list_item_1,city);
//
//        listView.setOnItemClickListener(this);
//
//    }
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.fragment_achievements);
//        ListView mListView = (ListView) findViewById(R.id.planner_listview);
//
//
//        //Create the feature objects
//        feature feature1 = new feature("lamp",5,21, 'M', 'W');
//        feature feature2 = new feature("staircase",5,21, 'M', 'W');
//        feature feature3 = new feature("lamp",4,25, 'M', 'W');
//        feature feature4 = new feature("staircase",2,21, 'F', 'W');
//        feature feature5 = new feature("lamp",3,21, 'F', 'W');
//        feature feature6 = new feature("staircase",4,21, 'F', 'W');
//
//        //add the feature objects to an ArrayList
//        ArrayList<feature> featureList = new ArrayList<>();
//        featureList.add(feature1);
//        featureList.add(feature2);
//        featureList.add(feature3);
//        featureList.add(feature4);
//        featureList.add(feature5);
//        featureList.add(feature6);
//
//        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
//        mListView.setAdapter(adapter);
//        }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if(position==0){
//            Toast.makeText(getActivity(), "Mumbai", Toast.LENGTH_SHORT).show();
//        }
//        if(position==1){
//            Toast.makeText(getActivity(), "Dehli", Toast.LENGTH_SHORT).show();
//        }
//        if(position==2){
//            Toast.makeText(getActivity(), "Bangalore", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

}
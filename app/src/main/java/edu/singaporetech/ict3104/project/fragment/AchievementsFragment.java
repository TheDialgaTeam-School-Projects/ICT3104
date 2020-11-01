package edu.singaporetech.ict3104.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;

public class AchievementsFragment extends Fragment {

    private TextView textViewSortFN;
    private TextView textViewSortFR;
    private TextView textViewSortFA;
    private TextView textViewSortFG;
    private TextView textViewSortFM;
//    private ArrayList<feature> featureList;
//    private FeatureListAdapter adapter;

    public AchievementsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate((R.layout.fragment_achievements), container, false);
        ListView mListView = (ListView) view.findViewById(R.id.planner_listview);

        //Create the feature objects
        feature feature1 = new feature("lamp", 5, 21, 'M', 'W');
        feature feature2 = new feature("staircase", 5, 21, 'M', 'W');
        feature feature3 = new feature("lamp", 4, 25, 'M', 'W');
        feature feature4 = new feature("staircase", 2, 21, 'F', 'W');
        feature feature5 = new feature("lamp", 3, 21, 'F', 'W');
        feature feature6 = new feature("staircase", 4, 21, 'F', 'W');
        //most likely for loop / interate through firebase data and instantiate new feature

        //add the feature objects to an ArrayList
        ArrayList<feature> featureList = new ArrayList<>();
        featureList.add(feature1);
        featureList.add(feature2);
        featureList.add(feature3);
        featureList.add(feature4);
        featureList.add(feature5);
        featureList.add(feature6);
        // for each feature object in data add in featureList


        //sorts by name
        //Collections.sort(featureList, (o1, o2) -> o1.feature_name.compareTo(o2.feature_name));
//        textViewSortFN = (TextView) view.findViewById(R.id.heading_name);
//        textViewSortFN.setOnClickListener(this);

        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);

        mListView = (ListView) view.findViewById(R.id.planner_listview);
        mListView.setAdapter(adapter);

        return view;
    }


//    @Override
//    public void onClick(View v) {
//
//        //implement things
//        Collections.sort(featureList, (o1, o2) -> o1.feature_name.compareTo(o2.feature_name));
//        Toast.makeText(requireActivity(), "Sort By Name.", Toast.LENGTH_SHORT).show();
//        adapter.notifyDataSetChanged();
//        //reload adapterview
//
//
//    }


}
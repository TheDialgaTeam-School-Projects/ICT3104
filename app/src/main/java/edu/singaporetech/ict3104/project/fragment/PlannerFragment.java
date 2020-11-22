package edu.singaporetech.ict3104.project.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.singaporetech.ict3104.project.R;

import static android.content.ContentValues.TAG;

public class PlannerFragment extends Fragment implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<feature> featureList = new ArrayList<>();
    private ListView mListView;
    private TextView textViewSortFN;
    private TextView textViewSortFR;
    private TextView textViewSortFA;
    private TextView textViewSortFG;
    private TextView textViewSortFM;

    public PlannerFragment() {

    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate((R.layout.fragment_planner), container, false);

        //declare textViews

        textViewSortFN = view.findViewById(R.id.heading_name);
        textViewSortFR = view.findViewById(R.id.heading_rating);
        textViewSortFA = view.findViewById(R.id.heading_age);
        textViewSortFG = view.findViewById(R.id.heading_gender);
        textViewSortFM = view.findViewById(R.id.heading_method);

        ListView mListView = (ListView) view.findViewById(R.id.planner_listview);
        // initialize
        textViewSortFN.setOnClickListener(this::onClick);
        textViewSortFR.setOnClickListener(this::onClick);
        textViewSortFA.setOnClickListener(this::onClick);
        textViewSortFG.setOnClickListener(this::onClick);
        textViewSortFM.setOnClickListener(this::onClick);

        db.collection("Survey")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            featureList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // for each document
                                // store into variables

                                String getFeatureN = document.getString("FeatureName");
                                Long getFeatureR = document.getLong("FeatureR");
                                int getFRInt = Math.toIntExact(getFeatureR);

                                Long getAge = document.getLong("Age");
                                //convert to int
                                int getAgeInt = Math.toIntExact(getAge);

                                //retrieve commuteMethod
                                String w = "walking";
                                String wc = "wheelchair";
                                String pwp ="parent with pram";
                                String getCommuteMethod = document.getString("CommuteMethod");
                                //shorten for display
                                if (getCommuteMethod.equalsIgnoreCase(w)) {
                                    getCommuteMethod = "W";
                                }
                                if (getCommuteMethod.equalsIgnoreCase(wc)) {
                                    getCommuteMethod = "WC";
                                }
                                if (getCommuteMethod.equalsIgnoreCase(pwp)){
                                    getCommuteMethod = "PwP";
                                }
                                //String getCMName =  getCommuteMethod.charAt(0);

                                String getGender = document.getString("Gender");
                                char getGender_Char = getGender.charAt(0);


                                feature featureObject = new feature(getFeatureN, getFRInt, getAgeInt, getGender_Char, getCommuteMethod);
                                featureList.add(featureObject);
                            }
                            ListView mListView = (ListView) view.findViewById(R.id.planner_listview);
                            FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
                            mListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

    @Override
    public void onClick(View v) {
//        View v2 = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
//        ListView mListView = (ListView) v2.findViewById(R.id.planner_listview);
//        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(),R.layout.adapter_view_layout, featureList);
        if (v == textViewSortFN) {
            //implement method
            Toast.makeText(getContext(), "Sort By Name", Toast.LENGTH_SHORT).show();
            sortFeatureListByN(this);
        }
        if (v == textViewSortFR) {
            Toast.makeText(getContext(), "Sort By Rating", Toast.LENGTH_SHORT).show();
            sortFLByRating(this);
        }
        if (v == textViewSortFA) {
            Toast.makeText(getContext(), "Sort By Age", Toast.LENGTH_SHORT).show();
            sortFLByAge(this);
        }
        if (v == textViewSortFG) {
            Toast.makeText(getContext(), "Sort By Gender", Toast.LENGTH_SHORT).show();
            sortFLByGender(this);
        }
        if (v == textViewSortFM) {
            Toast.makeText(getContext(), "Sort By Method", Toast.LENGTH_SHORT).show();
            sortFLByMethod(this);
        }
    }

    private void sortFeatureListByN(PlannerFragment v) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
        ListView mListView = (ListView) getView().findViewById(R.id.planner_listview);
        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
        Collections.sort(featureList, new Comparator<feature>() {
            @Override
            public int compare(feature o1, feature o2) {
                return o1.getFeature_name().compareTo(o2.getFeature_name());
            }
        });
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortFLByGender(PlannerFragment v) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
        ListView mListView = (ListView) getView().findViewById(R.id.planner_listview);
        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
        Collections.sort(featureList, new Comparator<feature>() {
            @Override
            public int compare(feature o1, feature o2) {
                return o2.getUser_gender() - (o1.getUser_gender());
            }
        });
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortFLByMethod(PlannerFragment v) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
        ListView mListView = (ListView) getView().findViewById(R.id.planner_listview);
        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
        Collections.sort(featureList, new Comparator<feature>() {
            @Override
            public int compare(feature o1, feature o2) {
                return o1.getUser_method().compareTo(o2.getUser_method());
            }
        });
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortFLByRating(PlannerFragment v) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
        ListView mListView = (ListView) getView().findViewById(R.id.planner_listview);
        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
        Collections.sort(featureList, new Comparator<feature>() {
            @Override
            public int compare(feature o1, feature o2) {
                return o2.getFeature_rating() - (o1.getFeature_rating());
            }
        });
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortFLByAge(PlannerFragment v) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
        ListView mListView = (ListView) getView().findViewById(R.id.planner_listview);
        FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);
        Collections.sort(featureList, new Comparator<feature>() {
            @Override
            public int compare(feature o1, feature o2) {
                return o2.getUser_age() - (o1.getUser_age());
            }
        });
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
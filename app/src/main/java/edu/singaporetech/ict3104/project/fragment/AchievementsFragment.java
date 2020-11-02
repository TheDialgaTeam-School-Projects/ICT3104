package edu.singaporetech.ict3104.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.singaporetech.ict3104.project.LoginActivity;
import edu.singaporetech.ict3104.project.R;

import static android.content.ContentValues.TAG;

public class AchievementsFragment extends Fragment {

    private TextView textViewSortFN;
    private TextView textViewSortFR;
    private TextView textViewSortFA;
    private TextView textViewSortFG;
    private TextView textViewSortFM;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;
//    private ArrayList<feature> featureList;
//    private FeatureListAdapter adapter;

    public AchievementsFragment() {

    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }

        email = firebaseUser.getEmail();

        if (email == null) {
            Toast.makeText(getContext(), "Unexpected error with the user.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }


        //Toast.makeText(getContext(),commuteType,Toast.LENGTH_LONG).show();

        //read commute method from user in fb

    }
    public void getFeatures(){
        db.collection("Survey")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long getAge = document.getLong("Age");

                                Log.d("Planner", document.getId() + " => " + document.getData());
                                System.out.println("PLANNER : " + document.getLong("Age"));
                                Log.d("PlannerS", document.getId() + " => " + document.getLong("Age"));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate((R.layout.fragment_achievements), container, false);
        ListView mListView = (ListView) view.findViewById(R.id.planner_listview);
        //read from database
        getFeatures();

        //
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
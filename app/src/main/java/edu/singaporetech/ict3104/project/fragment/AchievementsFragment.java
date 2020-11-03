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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private ArrayList<feature> featureList = new ArrayList<>();
    private TextView textViewSortFN;
    private TextView textViewSortFR;
    private TextView textViewSortFA;
    private TextView textViewSortFG;
    private TextView textViewSortFM;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;

    public AchievementsFragment() {

    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FeatureListAdapter adapter = new FeatureListAdapter(getActivity(), R.layout.adapter_view_layout, featureList);

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



    }
    public void getFeatures(){
        int i = 0;


    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate((R.layout.fragment_achievements), container, false);
        ListView mListView = (ListView) view.findViewById(R.id.planner_listview);


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
                                Long getAge = document.getLong("Age");
                                //convert to int
                                int getAgeInt = Math.toIntExact(getAge);
                                String getFeatureN = document.getString("FeatureName");
                                String getCommuteMethod = document.getString("CommuteMethod");
                                char getCM_Char = getCommuteMethod.charAt(0);
                                String getGender = document.getString("Gender");
                                char getGender_Char = getCommuteMethod.charAt(0);
                                Long getFeatureR = document.getLong("FeatureR");
                                int getFRInt = Math.toIntExact(getFeatureR);

                                 feature featureObject= new feature(getFeatureN,getFRInt,getAgeInt,getGender_Char,getCM_Char);
                                 featureList.add(featureObject);

                            }
                            ListView mListView = (ListView) view.findViewById(R.id.planner_listview);
                            FeatureListAdapter adapter = new FeatureListAdapter(getActivity(),R.layout.adapter_view_layout, featureList);
                            mListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

}
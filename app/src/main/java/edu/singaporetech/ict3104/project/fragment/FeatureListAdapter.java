package edu.singaporetech.ict3104.project.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import edu.singaporetech.ict3104.project.R;

public class FeatureListAdapter extends ArrayAdapter<feature> {

    private static final String TAG = "FeatureListAdapter";

    private FragmentActivity mContext;
    int mResource;

    public FeatureListAdapter(@NonNull FragmentActivity context, int resource, @NonNull ArrayList<feature> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        //get the feature information
        String name = getItem(position).getFeature_name();
        int rating = getItem(position).getFeature_rating();
        int age = getItem(position).getUser_age();
        char gender = getItem(position).getUser_gender();
        char method = getItem(position).getUser_method();
        //create the feature object with the info

        feature feature = new feature(name,rating,age,gender,method);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent ,false);

        TextView featureName = (TextView) convertView.findViewById(R.id.feature_name);
        TextView featureRating = (TextView) convertView.findViewById(R.id.feature_rating);
        TextView featureAge = (TextView) convertView.findViewById(R.id.feature_age);
        TextView featureGender = (TextView) convertView.findViewById(R.id.gender);
        TextView featureMethod = (TextView) convertView.findViewById(R.id.commute_method);

        featureName.setText(name);
        featureRating.setText(Integer.toString(rating));
        featureAge.setText(Integer.toString(age));
        featureGender.setText(Character.toString(gender));
        featureMethod.setText(Character.toString(method));

        return convertView;
    }
}


package com.example.a3104_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Places> {

    LayoutInflater flater;

    public PlaceAdapter(Activity context, int resouceId, int textviewId, List<Places> list){

        super(context,resouceId,textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Places places = getItem(position);
        LinearLayout rowview =(LinearLayout) flater.inflate(R.layout.spinner_item,null);
        TextView txtTitle = (TextView) rowview.findViewById(R.id.title);
        txtTitle.setText(places.getName());
        return rowview;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = flater.inflate(R.layout.spinner_item,parent, false);
        }
        Places rowItem = getItem(position);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(rowItem.getName());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        imageView.setBackground(LoadImageFromWebOperations(rowItem.getImageUrl()));
        return convertView;
    }


    private View rowview(View convertView , int position){

        Places rowItem = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spinner_item, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.title);
            holder.imageView = (ImageView) rowview.findViewById(R.id.icon);
            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        holder.imageView.setBackground(LoadImageFromWebOperations(rowItem.getImageUrl()));
        holder.txtTitle.setText(rowItem.getName());

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
        ImageView imageView;
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
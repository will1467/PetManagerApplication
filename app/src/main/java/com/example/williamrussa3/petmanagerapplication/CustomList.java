package com.example.williamrussa3.petmanagerapplication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {

    private Activity context;
    private String[] data;
    private Integer[] imageID;

    public CustomList(Activity context, String[] data, Integer[] imageId) {
        super(context, R.layout.list_single, data);
        this.context = context;
        this.data = data;
        this.imageID = imageId;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtCaption = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtCaption.setText(data[position]);

        imageView.setImageResource(imageID[position]);
        return rowView;
    }
}

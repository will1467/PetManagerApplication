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

/*Custom list used in PetActivity class to populate listView
    extends ArrayAdapter and implements GetView method
 */

public class CustomList extends ArrayAdapter<String> {

    //List contains data (text) and ID of image (array of drawable ID's)
    private Activity context;
    private String[] data;
    private Integer[] imageID;

    //Initialise variables, use layout.list_single for each item in list

    public CustomList(Activity context, String[] data, Integer[] imageId) {
        super(context, R.layout.list_single, data);
        this.context = context;
        this.data = data;
        this.imageID = imageId;

    }

    /*Get new view, inflate from layout.list_single, set text and image resource
        This method does not use View Holder pattern which would create smoother scrolling
        However, this list class is being used for a set array of 3 objects, therefore don't need to worry
         about ListView taking up the whole screen and starting to scroll
     */

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtCaption = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtCaption.setText(data[position]);

        imageView.setImageResource(imageID[position]);
        return rowView;
    }
}

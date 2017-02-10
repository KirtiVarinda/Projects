package com.lovelife.lovelife.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovelife.lovelife.BeanClasses.BeanForRestaurantDetail;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.R;

/**
 * Created by dx on 11/2/2016.
 */
public class RestaurantsAdapterClass extends ArrayAdapter<String> {

    private final Context context;
    BeanForRestaurantDetail[] restaurantBean;
    Activity currentActivity;

    public RestaurantsAdapterClass(Context context, String[] restaurantIDs, BeanForRestaurantDetail[] restaurantBean, Activity currentActivity) {
        super(context, R.layout.custom_restaurent_list, restaurantIDs);
        this.context = context;
        this.restaurantBean = restaurantBean;
        this.currentActivity = currentActivity;

    }

    public View getView(final int position, View view, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_restaurent_list, null, true);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        final TextView imageView = (TextView) rowView.findViewById(R.id.list_image);
        TextView phone = (TextView) rowView.findViewById(R.id.phone);
        TextView city = (TextView) rowView.findViewById(R.id.city);

        /** set text data in list row */
        name.setText(restaurantBean[position].getRestaurantName());
        phone.setText("Mobile - " + restaurantBean[position].getRestaurantphone());
        city.setText("City - " + restaurantBean[position].getRestaurantCity());

        if (restaurantBean[position].getRestaurantImageBitmap() == null) {
            new Thread() {
                public void run() {
                    setTiffinImage(position, imageView, restaurantBean[position].getRestaurantImageURL());
                }
            }.start();
        } else {

            imageView.setBackground(new BitmapDrawable(context.getResources(), restaurantBean[position].getRestaurantImageBitmap()));
            imageView.setText("");

        }

        return rowView;

    }


    private void setTiffinImage(int index, final TextView textView, String imgPath) {

        ServerSync serverSync = new ServerSync();
        final Bitmap btm = serverSync.fireUrlGetBitmap(imgPath);

        if (btm != null) {
            restaurantBean[index].setRestaurentImageBitmap(btm);
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    textView.setBackground(new BitmapDrawable(context.getResources(), btm));
                    textView.setText("");

                }
            });
        } else {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //  textView.setBackgroundResource(R.drawable.no_image_available);
                    textView.setText("problem while loading image.");

                }
            });
        }


    }


}
package com.tiffindaddy.app.tiffindaddy.TiffinAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.Cities;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.CartPage;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;
import com.tiffindaddy.app.tiffindaddy.ProductPage;
import com.tiffindaddy.app.tiffindaddy.R;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

/**
 * Created by dx on 2/3/2016.
 */
public class CityAdapter extends ArrayAdapter<String> {

    private Context context;
    private int cat_custom_layout;
    String cities[];
    Cities cities1[];
    private MySharedData mySharedData;
    Activity activity;

    public CityAdapter(Context context, int resource, String[] cities, Cities cities1[],Activity activity) {
        super(context, resource, cities);
        this.context = context;
        this.cat_custom_layout = resource;
        this.cities=cities;
        this.cities1=cities1;
        /**
         * variable to store data in session
         */
        mySharedData = new MySharedData(context);
        this.activity=activity;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(cat_custom_layout, parent, false);
        TextView city = (TextView) rowView.findViewById(R.id.city);

        city.setText(cities1[position].getCityName());
        city.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY, cities1[position].getCityName());
                mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY_ID, cities1[position].getCityId());

                ProductPage.citySelected=true;
                activity.finish();
                activity.overridePendingTransition(0,0);
                return false;
            }
        });

        return rowView;
    }


}

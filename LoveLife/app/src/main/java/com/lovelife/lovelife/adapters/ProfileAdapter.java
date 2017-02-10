package com.lovelife.lovelife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.lovelife.lovelife.R;

/**
 * Created by dx on 11/12/2016.
 */
public class ProfileAdapter extends ArrayAdapter<String> {

    private final Context context;
    String[] types;
    String[] values;


    public ProfileAdapter(Context context, String[] types, String[] values) {
        super(context, R.layout.custom_profile_list, types);
        this.context = context;
        this.types = types;
        this.values = values;

    }

    public View getView(final int position, View view, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_profile_list, null, true);

        TextView type = (TextView) rowView.findViewById(R.id.type);
        TextView value = (TextView) rowView.findViewById(R.id.value);

        /** set text data in list row */
        type.setText(types[position]);
        value.setText(values[position]);

        return rowView;

    }


}
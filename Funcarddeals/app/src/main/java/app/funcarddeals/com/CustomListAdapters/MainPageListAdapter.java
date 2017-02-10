package app.funcarddeals.com.CustomListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.MainPageBeanClass;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/1/2015.
 */
public class MainPageListAdapter extends ArrayAdapter<String> {
    private Context context;
    private MainPageBeanClass[] mainPageBeanClasses;
    private int custom_layout;

    public MainPageListAdapter(Context context, int resource, String[] allCities, MainPageBeanClass[] mainPageBeanClasses) {
        super(context, resource, allCities);
        this.context = context;
        this.mainPageBeanClasses = mainPageBeanClasses;
        custom_layout = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(custom_layout, parent, false);

        LinearLayout back = (LinearLayout) rowView.findViewById(R.id.cityRowBack);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView13);
        TextView cityName = (TextView) rowView.findViewById(R.id.textView22);
        TextView cost = (TextView) rowView.findViewById(R.id.textView23);
        TextView offers = (TextView) rowView.findViewById(R.id.textView24);
        if (mainPageBeanClasses[position].getCity_status().equals("0")) {
            back.setBackgroundResource(R.drawable.mainpage_enable_rowclicked);
            imageView.setImageResource(R.drawable.green_arrows);
        } else {
            back.setBackgroundResource(R.drawable.mainpage_disable_rowclicked);
            imageView.setImageResource(R.drawable.grey_arrow);
        }

        cityName.setText(mainPageBeanClasses[position].getCity_name());
        cost.setText(mainPageBeanClasses[position].getCity_cprice());
        offers.setText(mainPageBeanClasses[position].getCity_count()+" Offers");

        return rowView;
    }
}


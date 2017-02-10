package app.funcarddeals.com.CustomListAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.StoresPageBeanClass;
import app.funcarddeals.com.MainPage;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.R;
import app.funcarddeals.com.StoreOfferDetail;
import app.funcarddeals.com.StoreOffers;
import app.funcarddeals.com.StorePage;
import app.funcarddeals.com.Stores;

/**
 * Created by dx on 8/11/2015.
 */
public class StoresPageListAdapter extends ArrayAdapter<String> {
    private Context context;
    private StoresPageBeanClass[] storesPageBeanClass;
    private int stores_custom_layout;
    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;
    Activity currentActivity;


    public StoresPageListAdapter(Context context, int resource, String[] allStoreID, StoresPageBeanClass[] storesPageBeanClass, Activity currentActivity) {
        super(context, resource, allStoreID);
        this.context = context;
        this.storesPageBeanClass = storesPageBeanClass;
        this.stores_custom_layout = resource;
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();
        this.currentActivity = currentActivity;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(stores_custom_layout, parent, false);

        TextView store_left = (TextView) rowView.findViewById(R.id.store_left);
        TextView store_right = (TextView) rowView.findViewById(R.id.store_right);

        store_left.setText(storesPageBeanClass[position].getStore_name());
        store_right.setText(storesPageBeanClass[position].getStore_offer() + " Offers");


        store_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {

                /**
                 * switch activity with three values in putExtra.
                 */
                switchActivity.openActivity(currentActivity, StoreOffers.class, new String[]{Stores.STORE_ID, Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG}
                        , new String[]{storesPageBeanClass[position].getStore_id(), storesPageBeanClass[position].getStore_name()
                        , storesPageBeanClass[position].getStore_lat(), storesPageBeanClass[position].getStore_lng()});
            }


        });
        return rowView;
    }
}

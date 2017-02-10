package app.funcarddeals.com.CustomListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.StoreoffersBeanClass;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.FuncardDatabase.FuncardDealsContract;
import app.funcarddeals.com.Popups.FuncardToast;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/13/2015.
 */
public class StoreProductsListAdapter extends ArrayAdapter<String> {
    private Context context;
    private StoreoffersBeanClass[] storeoffersBeanClass;
    private int products_custom_layout;
    private String store_id, store_name, store_lat, store_lng;
    private String product_time = "0";
    private FunCardDealsDatabase funDB;
    private String REMINDER_ADDED="Added to Reminders";
    private String REMINDER_REMOVED="Removed from Reminders";

    private String FAVOURITE_ADDED="Added to Favorites";
    private String FAVOURITE_REMOVED="Removed from Favorites";

    public StoreProductsListAdapter(Context context, int resource, String[] allStoreProductsID, StoreoffersBeanClass[] storeoffersBeanClass, String[] storeDetail) {
        super(context, resource, allStoreProductsID);
        this.context = context;
        this.storeoffersBeanClass = storeoffersBeanClass;
        this.products_custom_layout = resource;
        funDB = new FunCardDealsDatabase(context);
        store_id = storeDetail[0];
        store_name = storeDetail[1];
        store_lat = storeDetail[2];
        store_lng = storeDetail[3];
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(products_custom_layout, parent, false);

        TextView product = (TextView) rowView.findViewById(R.id.product_name);
        final ImageView reminder = (ImageView) rowView.findViewById(R.id.rem);
        final ImageView favourite = (ImageView) rowView.findViewById(R.id.fav);

        if(funDB.isReminderExist(storeoffersBeanClass[position].getProduct_id())){
            reminder.setImageResource(R.drawable.clk);
        }else{
            reminder.setImageResource(R.drawable.clock);
        }

        if(funDB.isFavouriteExist(storeoffersBeanClass[position].getProduct_id())){
            favourite.setImageResource(R.drawable.remb);
        }else{
            favourite.setImageResource(R.drawable.remember);
        }


        reminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {


                if(funDB.isReminderExist(storeoffersBeanClass[position].getProduct_id())){      /** if reminder already exist */

                    funDB.deleteReminder(storeoffersBeanClass[position].getProduct_id());
                    FuncardToast.showToast(REMINDER_REMOVED, context);
                    reminder.setImageResource(R.drawable.clock);
                }else{ /** if reminder not exist then insert */
                    funDB.inserReminder(store_id, store_name, store_lat, store_lng, storeoffersBeanClass[position].getProduct_id(), storeoffersBeanClass[position].getProduct_name(), storeoffersBeanClass[position].getProduct_offers(), product_time);
                    FuncardToast.showToast(REMINDER_ADDED, context);
                    reminder.setImageResource(R.drawable.clk);
                }



            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {

                if(funDB.isFavouriteExist(storeoffersBeanClass[position].getProduct_id())){      /** if favourite already exist */

                    funDB.deleteFavourite(storeoffersBeanClass[position].getProduct_id());
                    FuncardToast.showToast(FAVOURITE_REMOVED, context);
                    favourite.setImageResource(R.drawable.remember);
                }else{ /** if favourite not exist then insert */
                    funDB.inserFavourites(store_id, store_name, store_lat, store_lng, storeoffersBeanClass[position].getProduct_id(), storeoffersBeanClass[position].getProduct_name(), storeoffersBeanClass[position].getProduct_offers(), product_time);
                    FuncardToast.showToast(FAVOURITE_ADDED, context);
                    favourite.setImageResource(R.drawable.remb);
                }



            }
        });
        product.setText(position + 1 + ". " + storeoffersBeanClass[position].getProduct_name());

        return rowView;
    }




}

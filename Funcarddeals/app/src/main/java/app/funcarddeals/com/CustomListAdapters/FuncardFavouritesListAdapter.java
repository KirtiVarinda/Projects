package app.funcarddeals.com.CustomListAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.FavouritesBeanClass;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/27/2015.
 */
public class FuncardFavouritesListAdapter extends ArrayAdapter<String> {

    private Context context;
    private FavouritesBeanClass[] favouritesBeanClass;
    private int reminder_custom_layout;
    private FunCardDealsDatabase funDB;
    Activity activity;
    private String alertString1 = "Attention!";
    private String alertstring2 = "Are you sure you want to remove this item from your favorites?";

    public FuncardFavouritesListAdapter(Context context, int resource, String[] allProductID, FavouritesBeanClass[] favouritesBeanClass,Activity activity) {
        super(context, resource, allProductID);
        this.favouritesBeanClass = favouritesBeanClass;
        this.context = context;
        this.reminder_custom_layout=resource;
        funDB=new FunCardDealsDatabase(context);
        this.activity=activity;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(reminder_custom_layout,parent,false);
        TextView index=(TextView) rowView.findViewById(R.id.fav_index);
        TextView toptext=(TextView) rowView.findViewById(R.id.fav_toptext);
        TextView textBottom=(TextView) rowView.findViewById(R.id.fav_bottomtext);
        final ImageView removeFav = (ImageView) rowView.findViewById(R.id.remove_fav);

        index.setText(position + 1 + "");
        /**
         * remove alarm with pop to get user reply
         */
        removeFav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {


                FuncardPopups.funcardPopupForDeleteReminderOrFavourites(activity, alertString1, alertstring2, funDB, favouritesBeanClass[position].getFAVOURITES_STORE_PRODUCT_ID(), "fav");


            }
        });


        toptext.setText(favouritesBeanClass[position].getFAVOURITES_STORE_PRODUCT_NAME());
        textBottom.setText("@" + favouritesBeanClass[position].getFAVOURITES_STORE_NAME());
        return rowView;
    }
}

package app.funcarddeals.com.CustomListAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.ReminderBeanClass;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/26/2015.
 */
public class FuncardReminderListAdapter extends ArrayAdapter<String> {
    private Context context;
    private ReminderBeanClass[] reminderBeanClass;
    private int reminder_custom_layout;
    private FunCardDealsDatabase funDB;
    Activity activity;
    private String alertString1 = "Attention!";
    private String alertstring2 = "Are you sure you want to remove this item from your reminders?";


    public FuncardReminderListAdapter(Context context, int resource, String[] allProductID, ReminderBeanClass[] reminderBeanClass, Activity activity) {
        super(context, resource, allProductID);
        this.context = context;
        this.reminderBeanClass = reminderBeanClass;
        this.reminder_custom_layout = resource;
        this.activity = activity;
        funDB = new FunCardDealsDatabase(context);


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(reminder_custom_layout, parent, false);
        TextView reminderTop = (TextView) rowView.findViewById(R.id.reminder_toptext);
        TextView reminderBottom = (TextView) rowView.findViewById(R.id.reminder_bottomtext);
        TextView reminderIndex = (TextView) rowView.findViewById(R.id.rem_index);
        final ImageView removeReminder = (ImageView) rowView.findViewById(R.id.remove);


        reminderIndex.setText(position + 1 + "");
            /**
             * remove alarm with pop to get user reply
             */
        removeReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {


                FuncardPopups.funcardPopupForDeleteReminderOrFavourites(activity, alertString1, alertstring2, funDB, reminderBeanClass[position].getREMINDER_STORE_PRODUCT_ID(),"rem");


            }
        });


        reminderTop.setText(reminderBeanClass[position].getREMINDER_STORE_PRODUCT_NAME());
        reminderBottom.setText("@" + reminderBeanClass[position].getREMINDER_STORE_NAME());

        return rowView;
    }
}

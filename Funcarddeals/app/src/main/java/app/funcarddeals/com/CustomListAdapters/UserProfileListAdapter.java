package app.funcarddeals.com.CustomListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.UserProfileBeanClass;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/24/2015.
 */
public class UserProfileListAdapter extends ArrayAdapter<String> {
    private Context context;
    private UserProfileBeanClass[] userProfileBeanClass;
    private int custom_layout;

    public UserProfileListAdapter(Context context, int resource, String[] allCardCodes, UserProfileBeanClass[] userProfileBeanClass) {
        super(context, resource, allCardCodes);
        this.context = context;
        this.userProfileBeanClass = userProfileBeanClass;
        custom_layout = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(custom_layout, parent, false);

        TextView cityCard = (TextView) rowView.findViewById(R.id.user_card);
        TextView userCity = (TextView) rowView.findViewById(R.id.user_city);
        TextView status = (TextView) rowView.findViewById(R.id.user_status);


        cityCard.setText(userProfileBeanClass[position].getUp_code());
        userCity.setText(userProfileBeanClass[position].getUp_city());
        status.setText(userProfileBeanClass[position].getUp_status());

        return rowView;
    }
}

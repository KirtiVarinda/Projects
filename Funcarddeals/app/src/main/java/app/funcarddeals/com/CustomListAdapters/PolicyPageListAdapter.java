package app.funcarddeals.com.CustomListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.PolicyBeanClass;
import app.funcarddeals.com.BeanClasses.UserProfileBeanClass;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/25/2015.
 */
public class PolicyPageListAdapter extends ArrayAdapter<String> {
    private Context context;
    private PolicyBeanClass[] policyBeanClass;
    private int custom_layout;

    public PolicyPageListAdapter(Context context, int resource, String[] allCardCodes, PolicyBeanClass[] policyBeanClass) {
        super(context, resource, allCardCodes);
        this.context = context;
        this.policyBeanClass = policyBeanClass;
        custom_layout = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(custom_layout, parent, false);

        TextView index = (TextView) rowView.findViewById(R.id.policy_index);
        TextView policy = (TextView) rowView.findViewById(R.id.policy_right);


        index.setText(position+1+".");
        policy.setText(policyBeanClass[position].getPolicy_detail());

        return rowView;
    }
}

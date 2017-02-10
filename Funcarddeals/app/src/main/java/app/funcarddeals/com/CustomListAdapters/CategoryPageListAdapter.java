package app.funcarddeals.com.CustomListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.funcarddeals.com.BeanClasses.CategoryPageBeanClass;
import app.funcarddeals.com.R;

/**
 * Created by dx on 8/5/2015.
 */
public class CategoryPageListAdapter  extends ArrayAdapter<String> {

    private Context context;
    private  CategoryPageBeanClass[] categoryPageBeanClass;
    private int cat_custom_layout;

    public CategoryPageListAdapter(Context context, int resource, String[] allCatID, CategoryPageBeanClass[] categoryPageBeanClass) {
        super(context, resource, allCatID);
        this.context = context;
        this.categoryPageBeanClass = categoryPageBeanClass;
        this.cat_custom_layout = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(cat_custom_layout, parent, false);

        TextView cat_name = (TextView) rowView.findViewById(R.id.textView28);
        TextView cat_offers = (TextView) rowView.findViewById(R.id.textView29);

        cat_name.setText(categoryPageBeanClass[position].getCat_name());
        cat_offers.setText(categoryPageBeanClass[position].getCat_offers()+" Offers");

        return rowView;
    }

}

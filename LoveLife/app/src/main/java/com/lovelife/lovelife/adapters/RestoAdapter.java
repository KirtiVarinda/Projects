package com.lovelife.lovelife.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovelife.lovelife.BeanClasses.BeanForRestaurantDetail;
import com.lovelife.lovelife.R;

/**
 * Created by Avnish on 12/7/2016.
 */
public class RestoAdapter extends ArrayAdapter<BeanForRestaurantDetail> {

    public RestoAdapter(Context context,BeanForRestaurantDetail[] beanForRestaurantDetails) {
        super(context, R.layout.custom_restaurent_list,beanForRestaurantDetails);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  viewHolder;

        if(convertView == null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.custom_restaurent_list,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)convertView.findViewById(R.id.name);
            viewHolder.city=(TextView)convertView.findViewById(R.id.city);
            convertView.setTag(viewHolder);


        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        BeanForRestaurantDetail beanForRestaurantDetail=getItem(position);

        viewHolder.name.setText(beanForRestaurantDetail.getRestaurantName());
        viewHolder.city.setText(beanForRestaurantDetail.getRestaurantCity());


        return convertView;
    }

    static class ViewHolder{


        private TextView name;
        private TextView city;


    }


}

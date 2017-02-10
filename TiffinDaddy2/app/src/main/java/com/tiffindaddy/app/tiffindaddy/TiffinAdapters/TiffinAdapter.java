package com.tiffindaddy.app.tiffindaddy.TiffinAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.ProductPage;
import com.tiffindaddy.app.tiffindaddy.R;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

/**
 * Created by dx on 2/3/2016.
 */
public class TiffinAdapter extends ArrayAdapter<String> {

    private Context context;
    private TiffinData[] tiffinData;
    private int cat_custom_layout;
    private Activity productPage;
    String imgPath;


    public TiffinAdapter(Context context, int resource, String[] allCatID, TiffinData[] tiffinData, Activity productPage, String imgPath) {
        super(context, resource, allCatID);
        this.context = context;
        this.tiffinData = tiffinData;
        this.cat_custom_layout = resource;
        this.productPage = productPage;
        this.imgPath = imgPath;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(cat_custom_layout, parent, false);
        final TextView image = (TextView) rowView.findViewById(R.id.textView5);
        TextView tiffinName = (TextView) rowView.findViewById(R.id.textView6);
        LinearLayout addon=(LinearLayout)rowView.findViewById(R.id.addon);
        TextView tiffinPrice = (TextView) rowView.findViewById(R.id.textView7);
        final TextView quantitySelected = (TextView) rowView.findViewById(R.id.textView3);
        final ImageView minusButton = (ImageView) rowView.findViewById(R.id.minusbutton);
        ImageView plusButton = (ImageView) rowView.findViewById(R.id.plusbutton);
        tiffinName.setText(tiffinData[position].getTiffinName());
        tiffinPrice.setText("\u20B9" + tiffinData[position].getTiffinPrice());

        if (tiffinData[position].getTiffinImageBitmap() == null) {
            System.out.println("image not sync again");
            new Thread() {
                public void run() {
                    setTiffinImage(position, image, tiffinData[position].getTiffinImage());
                }
            }.start();
        } else {

            image.setBackground(new BitmapDrawable(context.getResources(), tiffinData[position].getTiffinImageBitmap()));
            image.setText("");

        }


            if(tiffinData[position].getCategoryName().contains("addons_first")){
                addon.setVisibility(View.VISIBLE);

            }





        /**
         * show and hide the Floating icon according to the tiffin selected.
         */
        if (ProductPage.theMap.size() == 0) {
            ProductPage.fab.setVisibility(View.GONE);
        } else {
            ProductPage.fab.setVisibility(View.VISIBLE);
        }


        /** get number of quantities of tiffin clicked
         * default values is 0
         * */
        quantitySelected.setText(tiffinData[position].getQuantityPurchased() + "");
        if (tiffinData[position].getQuantityPurchased() == 0) {

            minusButton.setEnabled(false);
            minusButton.setBackgroundResource(R.drawable.grey_circle);

        } else {

            minusButton.setEnabled(true);
            minusButton.setBackgroundResource(R.drawable.green_circle_border);

        }


        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int qty1 = tiffinData[position].getQuantityPurchased();


                qty1 = qty1 - 1;
                tiffinData[position].setQuantityPurchased(qty1);
                quantitySelected.setText(qty1 + "");


                if (tiffinData[position].getQuantityPurchased() == 0) {
                    ProductPage.theMap.remove(tiffinData + "-l-" + tiffinData[position].getTiffinName() + "-l-" + position + "-l-" + "test"+ "-l-" + ProductPage.selectedDate);

                    minusButton.setEnabled(false);
                    minusButton.setBackgroundResource(R.drawable.grey_circle);


                } else {

                    minusButton.setEnabled(true);
                    minusButton.setBackgroundResource(R.drawable.green_circle_border);

                }

                /**
                 * show and hide the Floating icon according to the tiffin selected.
                 */
                if (ProductPage.theMap.size() == 0) {
                    ProductPage.fab.setVisibility(View.GONE);
                } else {
                    ProductPage.fab.setVisibility(View.VISIBLE);
                }

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int qty2 = tiffinData[position].getQuantityPurchased();
                qty2 = qty2 + 1;


                tiffinData[position].setQuantityPurchased(qty2);
                quantitySelected.setText(qty2 + "");


                // if (qty2 == 1) {

                // }

                if (tiffinData[position].getQuantityPurchased() == 0) {

                    minusButton.setEnabled(false);
                    minusButton.setBackgroundResource(R.drawable.grey_circle);


                } else {

                    tiffinData[position].setDeliveryDate(ProductPage.selectedDate);
                    ProductPage.theMap.put(tiffinData + "-l-" + tiffinData[position].getTiffinName() + "-l-" + position + "-l-" +  "test" + "-l-" + ProductPage.selectedDate, tiffinData[position]);

                    minusButton.setEnabled(true);
                    minusButton.setBackgroundResource(R.drawable.green_circle_border);

                }

                /**
                 * show and hide the Floating icon according to the tiffin selected.
                 */
                if (ProductPage.theMap.size() == 0) {
                    ProductPage.fab.setVisibility(View.GONE);
                } else {
                    ProductPage.fab.setVisibility(View.VISIBLE);
                }


            }
        });

        return rowView;
    }


    private void setTiffinImage(int index, final TextView textView, String imageName) {

        ServerSync serverSync = new ServerSync();
        final Bitmap btm = serverSync.fireUrlGetBitmap(imgPath + imageName);

        if (btm != null) {
            tiffinData[index].setTiffinImageBitmap(btm);
            productPage.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    textView.setBackground(new BitmapDrawable(context.getResources(), btm));
                    textView.setText("");

                }
            });
        } else {
            productPage.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    textView.setBackgroundResource(R.drawable.no_image_available);
                    textView.setText("");

                }
            });
        }


    }


}

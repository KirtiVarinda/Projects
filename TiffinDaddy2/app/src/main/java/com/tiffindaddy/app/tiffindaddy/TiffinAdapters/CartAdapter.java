package com.tiffindaddy.app.tiffindaddy.TiffinAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.CartPage;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;
import com.tiffindaddy.app.tiffindaddy.ProductPage;
import com.tiffindaddy.app.tiffindaddy.R;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

/**
 * Created by dx on 2/3/2016.
 */
public class CartAdapter extends ArrayAdapter<String> {

    private Context context;
    private TiffinData[] tiffinData;
    private int cat_custom_layout;
    private CartPage cartPage;
    String imgPath;
    String keys[];


    public CartAdapter(Context context, int resource, String[] keys, TiffinData[] tiffinData, CartPage cartPage, String imgPath) {
        super(context, resource, keys);
        this.context = context;
        this.tiffinData = tiffinData;
        this.cat_custom_layout = resource;
        this.cartPage = cartPage;
        this.imgPath = imgPath;

        this.keys=keys;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(cat_custom_layout, parent, false);
        TextView tiffinName = (TextView) rowView.findViewById(R.id.textView6);
        ImageView delete = (ImageView) rowView.findViewById(R.id.delete);
        final TextView quantitySelected = (TextView) rowView.findViewById(R.id.textView3);
        final ImageView minusButton = (ImageView) rowView.findViewById(R.id.minusbutton);
        ImageView plusButton = (ImageView) rowView.findViewById(R.id.plusbutton);
        tiffinName.setText(tiffinData[position].getTiffinName() + "\n (\u20B9" + tiffinData[position].getTiffinPrice() + ")");
        //tiffinPrice.setText();


         final String wholeKey[]=keys[position].split("-l-");




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


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * set quantity to zero if meal is removed from hash map or cart.
                 *
                 * also the values to previous page is chaged with this.
                 */
                ProductPage.theMap.get(keys[position]).setQuantityPurchased(0);
                ProductPage.theMap.remove(keys[position]);
                cartPage.setTifinDataForCart();


//                System.out.println("key= " + keys[position]);
//                System.out.println("position= "+position);
//                int indx=Integer.parseInt(wholeKey[2]);

//                /**
//                 * even update the data on the previous for remove and added tiffins
//                 */
//                if (tiffinData[indx].getDeliveryDate().equals(TimeManager.todayDate())) {
//
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.todayTiffinDataBreakFast[position].setQuantityPurchased(0);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.todayTiffinDataLunch[position].setQuantityPurchased(0);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.todayTiffinDataDinner[position].setQuantityPurchased(0);
//                    }
//
//                } else {
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.tomorrowTiffinDataBreakFast[position].setQuantityPurchased(0);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.tomorrowTiffinDataLunch[position].setQuantityPurchased(0);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.tomorrowTiffinDataDinner[position].setQuantityPurchased(0);
//                    }
//
//                }
//




            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int qty1 = tiffinData[position].getQuantityPurchased();
                // ProductPage.theMap.remove(tiffinData + "-l-" + tiffinData[position].getTiffinName() + "-l-" + position + "-l-" + qty1 + "-l-" + ProductPage.selectedDate);
                qty1 = qty1 - 1;


                /**
                 * even update the data on the previous for remove and added tiffins
                 */
//                if (tiffinData[position].getDeliveryDate().equals(TimeManager.todayDate())) {
//
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.todayTiffinDataBreakFast[position].setQuantityPurchased(qty1);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.todayTiffinDataLunch[position].setQuantityPurchased(qty1);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.todayTiffinDataDinner[position].setQuantityPurchased(qty1);
//                    }
//
//                } else {
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.tomorrowTiffinDataBreakFast[position].setQuantityPurchased(qty1);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.tomorrowTiffinDataLunch[position].setQuantityPurchased(qty1);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.tomorrowTiffinDataDinner[position].setQuantityPurchased(qty1);
//                    }
//
//                }


                tiffinData[position].setQuantityPurchased(qty1);
                quantitySelected.setText(qty1 + "");


                if (tiffinData[position].getQuantityPurchased() == 0) {
                    ProductPage.theMap.remove(keys[position]);


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


                cartPage.setTifinDataForCart();


            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int qty2 = tiffinData[position].getQuantityPurchased();
                qty2 = qty2 + 1;


                tiffinData[position].setQuantityPurchased(qty2);


                /**
                 * even update the data on the previous for remove and added tiffins
                 */
//                if (tiffinData[position].getDeliveryDate().equals(TimeManager.todayDate())) {
//
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.todayTiffinDataBreakFast[position].setQuantityPurchased(qty2);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.todayTiffinDataLunch[position].setQuantityPurchased(qty2);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.todayTiffinDataDinner[position].setQuantityPurchased(qty2);
//                    }
//
//                } else {
//                    if (tiffinData[position].getCategoryType().toLowerCase().equals("breakfast")) {
//
//                        ProductPage.tomorrowTiffinDataBreakFast[position].setQuantityPurchased(qty2);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("lunch")) {
//
//                        ProductPage.tomorrowTiffinDataLunch[position].setQuantityPurchased(qty2);
//                    } else if (tiffinData[position].getCategoryType().toLowerCase().equals("dinner")) {
//
//                        ProductPage.tomorrowTiffinDataDinner[position].setQuantityPurchased(qty2);
//                    }
//
//                }
//

                quantitySelected.setText(qty2 + "");


                // if (qty2 == 1) {

                // }

                if (tiffinData[position].getQuantityPurchased() == 0) {

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

                cartPage.setTifinDataForCart();
            }
        });

        return rowView;
    }


}

package com.lovelife.lovelife;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.TextView;

public class SingleRestaurentActivity extends Activity {


    /**Imageview variable*/

    private TextView restoImage,restoName,restoPrice,restoMobile,restoAddress,restoCity,restoArea,restoCountry,restoPostalCode;


    /** index for images */

    String index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurent);

        Intent intent = getIntent();
        if (null != intent) {
            index= intent.getStringExtra("index");
        }

        int indexValue=Integer.parseInt(index);
        /** initialize the activity profilePicView variables*/

        restoImage = (TextView) findViewById(R.id.textView2);
        restoName = (TextView) findViewById(R.id.textView3);
        restoPrice = (TextView) findViewById(R.id.textView4);
        restoMobile = (TextView) findViewById(R.id.textView5);
        restoAddress = (TextView) findViewById(R.id.textView6);
        restoCity = (TextView) findViewById(R.id.textView7);
        restoArea = (TextView) findViewById(R.id.textView8);
        restoCountry = (TextView) findViewById(R.id.textView9);
        restoPostalCode = (TextView) findViewById(R.id.textView10);

        restoImage.setBackground(new BitmapDrawable(getApplicationContext().getResources(), RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantImageBitmap()));
        restoName.setText(RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantName());
        restoPrice.setText("Price - $"+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantPrice());
        restoMobile.setText("Mobile - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantphone());
        restoAddress.setText("Address - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantAddr());
        restoCity.setText("City - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantCity());
        restoArea.setText("Area - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantArea());
        restoCountry.setText("Country - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantCountry());
        restoPostalCode.setText("Postal code - "+RestaurantActivity.beanForRestaurantDetail[indexValue].getRestaurantPCode());







    }

}

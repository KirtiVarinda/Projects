package com.lovelife.lovelife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.Lovelife_Services.LoveLifeFCMservice;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.GetUserProfileResource;


public class PartnerProfile extends AppCompatActivity implements Animation.AnimationListener {

    LinearLayout popup;
    PartnerProfile currentActivity;

    ConnectedPartnerBean connectedPartnerBean;

    /**
     * variable for activity switcher
     */

    public SwitchActivities switchActivities;


    /**
     * sessions variable
     */
    public MySharedData mySharedData;
    TextView email;
    ImageView partnerProfilePage;
    Animation slideIn,slideOut;

    private TextView name_value, phone_value, age_value, location_value, movies_value, weather_value, food_value, car_value, vacation_value, color_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_patner_connection);
        /** initialize current ativity */
        currentActivity = PartnerProfile.this;

        getRequestResultType();

        /** initialize sessions variable */
        mySharedData = new MySharedData(currentActivity);

        name_value = (TextView) findViewById(R.id.name_value);
        phone_value = (TextView) findViewById(R.id.phone_value);
        age_value = (TextView) findViewById(R.id.age_value);
        location_value = (TextView) findViewById(R.id.location_value);
        movies_value = (TextView) findViewById(R.id.movies_value);
        weather_value = (TextView) findViewById(R.id.weather_value);
        food_value = (TextView) findViewById(R.id.food_value);
        car_value = (TextView) findViewById(R.id.car_value);
        vacation_value = (TextView) findViewById(R.id.vacation_value);
        color_value = (TextView) findViewById(R.id.color_value);
        email = (TextView) findViewById(R.id.email);
        partnerProfilePage = (ImageView) findViewById(R.id.partnerProfilePage);

        popup = (LinearLayout) findViewById(R.id.popup);


        slideIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.open);
        slideOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.close);

        popup.startAnimation(slideIn);
        popup.setVisibility(View.VISIBLE);

        slideOut.setAnimationListener(this);



        /** set partner profile data */
        name_value.setText(connectedPartnerBean.getPartnerDisplayName());
        //  phone_value.setText(connectedPartnerBean.getPartnerDisplayName());
        age_value.setText(connectedPartnerBean.getPartnerDob());
        //  location_value.setText(connectedPartnerBean.getPartnerDisplayName());
        movies_value.setText(connectedPartnerBean.getPartnerFavouriteMovie());
        weather_value.setText(connectedPartnerBean.getPartnerFavouriteWeather());
        food_value.setText(connectedPartnerBean.getPartnerFavouriteFood());
        car_value.setText(connectedPartnerBean.getPartnerFavouriteCar());
        vacation_value.setText(connectedPartnerBean.getPartnerFavouriteVacation());
        color_value.setText(connectedPartnerBean.getPartnerFavouriteColor());
        email.setText("Email: " + connectedPartnerBean.getPartnerEmail());

        if (connectedPartnerBean.getPartnerImageBitmap() != null) {
            partnerProfilePage.setImageBitmap(connectedPartnerBean.getPartnerImageBitmap());
        }


        registerReceiver(myReceiver, new IntentFilter(LoveLifeFCMservice.INTENT_FILTER));


    }


    private void getRequestResultType() {
        Bundle extras = getIntent().getExtras();
        String TRANSITION_TYPE = "";
        if (extras != null) {
            TRANSITION_TYPE = extras.getString(GetUserProfileResource.TRANSITION_TYPE);
        }

        if (TRANSITION_TYPE.equals("GetUserProfile")) {

            connectedPartnerBean = GetUserProfile.connectedPartnerBean;
        } else if (TRANSITION_TYPE.equals("UserAddressAndFavorites")) {

            connectedPartnerBean = UserAddressAndFavorites.connectedPartnerBean;
        } else if (TRANSITION_TYPE.equals("Dashboard")) {
            connectedPartnerBean = Dashboard.connectedPartnerBean;
        }
    }

    /**
     * receiver that receive message from fcm
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             *below are the message received by FCM
             *
             * Request Accepted
             * Request Rejected
             * Request sent
             *   */

            if (intent.getExtras().getString("message").toLowerCase().contains("your request has been rejected")) {
                //  popup(intent.getExtras().getString("message"), "Sorry!");
            } else if (intent.getExtras().getString("message").toLowerCase().contains("your request has been cancelled")) {
                //  popup(intent.getExtras().getString("message"), "Sorry!");
            }

            // Toast.makeText(PartnerProfile.this, "FCM:- " + intent.getExtras().getString("message"), Toast.LENGTH_LONG).show();


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }


    public void close(View view) {
        popup.startAnimation(slideOut);
        popup.setVisibility(View.GONE);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        finish();
        overridePendingTransition(0, 0);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {

        popup.startAnimation(slideOut);
        popup.setVisibility(View.GONE);

    }
}

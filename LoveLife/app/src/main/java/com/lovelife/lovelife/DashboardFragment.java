package com.lovelife.lovelife;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.PieChart.ChartColor;
import com.lovelife.lovelife.SharedData.MySharedData;

import java.util.ArrayList;
import java.util.Map;


public class DashboardFragment extends Fragment {


    /**
     * lovemeter data variables
     */

    private String userName, partnerName, userImageURL, partnerImageURL;


    /**
     * variable to sync data from the server
     */
    ServerSync serverSync;

    /**
     * activity variables
     */
    private TextView UserName, PartnerName;
    private ImageView userPic, partnerPic;

    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;

    /**
     * session variable
     */
    MySharedData mySharedData;
    /**
     * current activity instance
     */
    private Dashboard currentActivity;


    /**
     * LoveMeter url
     */
    private String LOVEMETER_URL;


    /**
     * Parameter String keys
     * for sending data
     */
    private String KEY_ID = "id";
    private String KEY_TOKEN = "token";
    private String CONNECTION_ID = "conn_id";

    /**
     * Parameter String keys
     * for receving data
     */
    private String KEY_USERNAME = "user_name";
    private String KEY_PARTNER_NAME = "partner_name";
    private String KEY_USER_PIC = "user_pic";
    private String KEY_PARTNER_PIC = "partner_pic";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_layout, container, false);

        LOVEMETER_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.love_meter);
        currentActivity = (Dashboard) getActivity();
        /** initialize session variable */
        mySharedData = new MySharedData(currentActivity.getApplicationContext());


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * Inisialize Textviews username and partner name
         */
        UserName = (TextView) view.findViewById(R.id.username);
        PartnerName = (TextView) view.findViewById(R.id.partnername);

        /**
         * Inisialize Imageviews for user and partner
         */
        userPic = (ImageView) view.findViewById(R.id.userimg);
        partnerPic = (ImageView) view.findViewById(R.id.partnerImg);

        /**initialize server sync data variable*/
        serverSync = new ServerSync();


        /** chart work */
        PieChart pieChart = (PieChart) view.findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        entries.add(new Entry(10, 1));
        entries.add(new Entry(100, 2));

        PieDataSet dataset = new PieDataSet(entries, "");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("");
        labels.add("");
        labels.add("");


        PieData data = new PieData(labels, dataset);
        dataset.setColors(ChartColor.COLORFUL_COLORS);
        pieChart.setDescription("Description");
        pieChart.setData(data);

        pieChart.animateY(5000);

        pieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image


        /** get parameters values from shared prefrenses */
        String token = mySharedData.getGeneralSaveSession(mySharedData.TOKEN);
        String id = mySharedData.getGeneralSaveSession(mySharedData.USERID);
        String conn_id = mySharedData.getGeneralSaveSession(mySharedData.CONNECTED_PARNER_CONNECTION_ID);

        /** start progress bar*/
        progressBarManager.startProgressBar();
        final Map<String, String> postData = getKeyPairValueInHashMap(id, token, conn_id);

        /** get love meter points from the server */
        new Thread() {
            public void run() {

                getLoveMeterPoints(postData);

            }
        }.start();


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */

    private Map<String, String> getKeyPairValueInHashMap(String id, String token, String conn_id) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {KEY_ID, KEY_TOKEN, CONNECTION_ID};      // set keys
        String value[] = {id, token, conn_id};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * For getting love meter Details
     */
    private void getLoveMeterPoints(Map<String, String> postData) {


        /** get love meter data from server*/


        String response = serverSync.SyncServer(postData, LOVEMETER_URL);

        System.out.println("dash fragmentn response " + response);


        if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem .
             * them shows the network error.
             */


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                    Toast.makeText(getContext(),"handle network error in view",Toast.LENGTH_LONG).show();
                }
            });

        } else {

            String status = ParseJsonData.checkStatus(response);


            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {

                /** update view with server */
                getJsonAndSetTextVew(response);

                getImagesAndUpdateView(response);


            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */


            } else {

            }


            /** stop progress bar */
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
        }
    }


    /**
     * method to get names from server and set in view
     *
     * @param response
     */
    private void getJsonAndSetTextVew(String response) {
        /** get data from json if response is success */
        userName = ParseJsonData.getValueWithKey(response, KEY_USERNAME);
        partnerName = ParseJsonData.getValueWithKey(response, KEY_PARTNER_NAME);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserName.setText(userName);
                PartnerName.setText(partnerName);

            }
        });


    }



    /**
     * method to get images from server and set in view
     *
     * @param response
     */
    private void getImagesAndUpdateView(String response) {
        userImageURL = ParseJsonData.getValueWithKey(response, KEY_USER_PIC);
        partnerImageURL = ParseJsonData.getValueWithKey(response, KEY_PARTNER_PIC);

        /** different thread fro user image upload */
        new Thread() {
            public void run() {

                if (userImageURL.equals("")) {
                    /** set noImage in user image not exist */
                    ((Dashboard)getActivity()).setNoImage(userPic);
                } else {
                    final Bitmap userImg = serverSync.fireUrlGetBitmap(userImageURL);
                    /** update view with user pic */
                   // ((Dashboard)getActivity()).setImageView(userImg, userPic);
                }


            }
        }.start();

        /** different thread fro partner image upload */
        new Thread() {
            public void run() {


                if (partnerImageURL.equals("")) {
                    /** set noImage in partner image not exist */
                    ((Dashboard)getActivity()).setNoImage(partnerPic);
                } else {
                    final Bitmap partnerImage = serverSync.fireUrlGetBitmap(partnerImageURL);
                    /** update view with partner pic */
                   // ((Dashboard)getActivity()).setImageView(partnerImage, partnerPic);
                }


            }
        }.start();


    }



}
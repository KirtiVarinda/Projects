package com.lovelife.lovelife.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.ConnectToPartner;
import com.lovelife.lovelife.Dashboard;
import com.lovelife.lovelife.GetUserProfile;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.PartnerProfile;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.ManagePartnerResource;
import com.lovelife.lovelife.UserAddressAndFavorites;
import com.lovelife.lovelife.Validator.FormValidation;

import java.util.Map;

/**
 * Created by dx on 11/2/2016.
 */
public class RequestsAdapterClass extends ArrayAdapter<BeanRequestsFromPeople> {

    private final Context context;
    BeanRequestsFromPeople[] requestsBean;
    ConnectToPartner currentActivity;

    public RequestsAdapterClass(Context context, BeanRequestsFromPeople[] requestsBean, ConnectToPartner currentActivity) {
        super(context, R.layout.user_request_list, requestsBean);
        this.context = context;
        this.requestsBean = requestsBean;
        this.currentActivity = currentActivity;

    }


    public View getView(final int position, View view, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_request_list, null, true);


        LinearLayout receiveRequest = (LinearLayout) rowView.findViewById(R.id.addRequestLayout);
        LinearLayout sendNewRequest = (LinearLayout) rowView.findViewById(R.id.addPartnerLayout);


        if (requestsBean[position].getRequest_email().equals("sent_type")) {
            ImageView sendRequest = (ImageView) rowView.findViewById(R.id.plus_partner);
            final AutoCompleteTextView partnerEmailToConnect = (AutoCompleteTextView) rowView.findViewById(R.id.partnerEmailToConnect);
            sendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    attemptPartnerConnection(partnerEmailToConnect);

                }
            });

            receiveRequest.setVisibility(View.GONE);
            sendNewRequest.setVisibility(View.VISIBLE);


        } else {


            receiveRequest.setVisibility(View.VISIBLE);
            sendNewRequest.setVisibility(View.GONE);

            TextView requestName = (TextView) rowView.findViewById(R.id.request_name);
            TextView requestEmail = (TextView) rowView.findViewById(R.id.request_email);
            final TextView requestImage = (TextView) rowView.findViewById(R.id.requestImage);
            ImageView accept = (ImageView) rowView.findViewById(R.id.accept);


            requestName.setText(requestsBean[position].getRequest_email());
            requestEmail.setText(requestsBean[position].getRequest_email());
            new Thread() {
                public void run() {
                    setRequestPeopleImage(requestImage, requestsBean[position].getRequest_pic());
                }
            }.start();

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(currentActivity, "received request type clicked", Toast.LENGTH_SHORT).show();
                    responseToRequest(requestsBean[position].getRequest_email(), "true");
                }
            });

        }

        return rowView;

    }


    private void setRequestPeopleImage(final TextView textView, String imgPath) {

        ServerSync serverSync = new ServerSync();
        final Bitmap btm = serverSync.fireUrlGetBitmap(imgPath);

        if (btm != null) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    textView.setBackground(new BitmapDrawable(context.getResources(), btm));
                    textView.setText("");

                }
            });
        } else {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //  textView.setBackgroundResource(R.drawable.no_image_available);
                    textView.setText("problem while loading image.");

                }
            });
        }


    }


    /**
     * Method that validate the form
     * and send request if form data is valid
     */

    public void attemptPartnerConnection(AutoCompleteTextView partnerEmailToConnect) {


        /** Store values into String for values save on server.*/
        //String email = connectionEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        String email = partnerEmailToConnect.getText().toString();

        Toast.makeText(currentActivity, "sent type clicked " + email, Toast.LENGTH_SHORT).show();
        /** Check validation for email .*/
        if (!FormValidation.isValidEmail(email)) {
            partnerEmailToConnect.setError(currentActivity.getString(R.string.invalid_connectionEmail));
            focusView = partnerEmailToConnect;
            cancel = true;

        }

        if (cancel) {
            /** There was an error; don't go to next Page and focus the Email
             form field with an error.*/
            focusView.requestFocus();
        } else {
            /**
             * Save the connection detail on server
             */

            currentActivity.sendConnectionRequest(email);


        }
    }

    private void responseToRequest(final String email, String status) {
        /** start progress bar */
        currentActivity.progressBarManager.startProgressBar();

        final Map<String, String> postData = getKeyPairValueInHashMap(email, status);


        /** get request from server */
        new Thread() {
            public void run() {

                /**
                 * Make Connection and Pass URL to get response
                 */
                respondToRequest(postData);

            }
        }.start();


    }

    private void respondToRequest(Map<String, String> postData) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, currentActivity.getResources().getString(R.string.serverDomain) + currentActivity.getResources().getString(R.string.respondToRequest));

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */


            currentActivity.popup("Network problem.Please check your internet connectivity.", "Sorry!");
            // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            String message = ParseJsonData.getMessage(reponse);

            if (status.equals("accept")) {/** if response status is success then login successful. */

              /*  if (GetUserProfile.managePartnerConnection != null) {
                    GetUserProfile.managePartnerConnection.loadRequests();
                }*/
                GetUserProfile.isReturnFromConnectToPartnerPage = true;
                UserAddressAndFavorites.isReturnFromConnectToPartnerPage = true;
                Dashboard.isReturnFromConnectToPartnerPage = true;
                LoveLifePopups.loveLifePopupWithFinishCurrent(currentActivity, "Congratulations!", "You have been connected to partner Successfully.");
//                currentActivity.mySharedData.setGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL, connectedEmail);
                //   mySharedData.setGeneralSaveSession(MySharedData.CONNECTED_PARNER_CONNECTION_ID,connectedPartnerBean.getConnectionID());


            }/* else if (status.equals("reject")) { *//** if response status Fail. *//*
                currentActivity.switchActivities.openActivity(currentActivity, PartnerProfile.class);
                currentActivity.finish();
                // currentActivity.popup(message, "Sorry!");

            }*/
            else {


                currentActivity.popup("Server problem please try after some time.", "Sorry!");


            }


        }


    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String email, String status) {

        /** get usre id and token and email from session*/

        MySharedData mySharedData = new MySharedData(context);
        String userID = mySharedData.getGeneralSaveSession(MySharedData.USERID);
        String token = mySharedData.getGeneralSaveSession(MySharedData.TOKEN);
        String userEmail = mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL);
        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {ManagePartnerResource.EMAIL, ManagePartnerResource.TOKEN, ManagePartnerResource.USER_ID, "confirm_email", "connection_status"};      // set keys
        String value[] = {userEmail, token, userID, email, status};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


}
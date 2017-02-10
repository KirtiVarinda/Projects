package com.lovelife.lovelife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.GetUserProfileResource;
import com.lovelife.lovelife.StringResources.LoginResource;
import com.lovelife.lovelife.StringResources.ManagePartnerResource;
import com.lovelife.lovelife.Validator.FormValidation;
import com.lovelife.lovelife.adapters.RequestsAdapterClass;

import java.util.Map;

public class ConnectToPartner extends AppCompatActivity implements Animation.AnimationListener {


    String userIDAfterLogin, tokenafetrLogin;
    String emailAfterLogin;

    static String pendingPartnerEmail;
    public BeanRequestsFromPeople[] beanRequestsFromPeoples1;
    String requestResultType = "";
    /**
     * contains email to be cancel
     */
    private String cancelEmail = "";


    /**
     * variable for activity switcher
     */

    public SwitchActivities switchActivities;

    /**
     * sessions variable
     */
    public MySharedData mySharedData;


    /**
     * current activity instance
     */
    private ConnectToPartner currentActivity;


    /**
     * loaded variable
     */
    public ProgressBarManager progressBarManager;

    AutoCompleteTextView partnerEmailForRequest;

    LinearLayout pendingArea,popup;
    LinearLayout inviteArea;
    TextView pendingText;
    LinearLayout requestListLayout;
    LinearLayout getPartnerEmail;

    Animation slideIn,slideOut;

    ImageView partnerImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_partner);


        /** initialize current ativity */
        currentActivity = ConnectToPartner.this;


        /** initialize sessions variable */
        mySharedData = new MySharedData(currentActivity);


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);


        /**
         * Initialize activity switcher
         */
        switchActivities = new SwitchActivities();




        requestListLayout = (LinearLayout) findViewById(R.id.requestListLayout);
        getPartnerEmail = (LinearLayout) findViewById(R.id.getPartnerEmailLayout);
        partnerEmailForRequest = (AutoCompleteTextView) findViewById(R.id.partnerEmailForRequest);
//        Button addPartnerButton = (Button) findViewById(R.id.addPartnerButton);
        pendingArea = (LinearLayout) findViewById(R.id.pendingArea);
        inviteArea = (LinearLayout) findViewById(R.id.inviteArea);
        pendingText = (TextView) findViewById(R.id.pendingText);
        popup = (LinearLayout) findViewById(R.id.popup);
        partnerImage = (ImageView) findViewById(R.id.partnerImage);

        getRequestResultType();

        slideIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.open);
        slideOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.close);

        popup.startAnimation(slideIn);
        popup.setVisibility(View.VISIBLE);

        slideOut.setAnimationListener(this);


        /** get usre id and token and email from session*/

        emailAfterLogin = mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL);
        userIDAfterLogin = mySharedData.getGeneralSaveSession(MySharedData.USERID);
        tokenafetrLogin = mySharedData.getGeneralSaveSession(MySharedData.TOKEN);


        loadViewConditionally();

    }

    private void loadViewConditionally() {
        if (
                requestResultType.equals("noEventOccur")) {

            requestListLayout.setVisibility(View.GONE);
            getPartnerEmail.setVisibility(View.VISIBLE);


        } else if (requestResultType.equals("requestIsPending")) {
            requestListLayout.setVisibility(View.GONE);
            getPartnerEmail.setVisibility(View.VISIBLE);

            pendingArea.setVisibility(View.VISIBLE);
            inviteArea.setVisibility(View.GONE);

            pendingText.setText("Waiting for Approval from partner with email ID '" + pendingPartnerEmail + "'");
            cancelEmail = pendingPartnerEmail;
        } else if (requestResultType.equals("requestsReceived")) {

            Toast.makeText(ConnectToPartner.this, "requestsReceivedddds " + beanRequestsFromPeoples1.length + " " + beanRequestsFromPeoples1[0].getRequest_email() + " " + beanRequestsFromPeoples1[0].getRequest_name(), Toast.LENGTH_SHORT).show();
            requestListLayout.setVisibility(View.VISIBLE);
            getPartnerEmail.setVisibility(View.GONE);


            BeanRequestsFromPeople[] beanRequestsFromPeoples = new BeanRequestsFromPeople[beanRequestsFromPeoples1.length + 1];

            for (int i = 0; i < beanRequestsFromPeoples.length - 1; i++) {
                beanRequestsFromPeoples[i] = beanRequestsFromPeoples1[i];
            }


            BeanRequestsFromPeople beanRequests;

            /** set a dummy bean for adding send request module in listview */
            beanRequests = new BeanRequestsFromPeople();
            beanRequests.setRequest_email("sent_type");
            beanRequestsFromPeoples[beanRequestsFromPeoples1.length] = beanRequests;

            final RequestsAdapterClass adapter = new RequestsAdapterClass(getApplicationContext(), beanRequestsFromPeoples, this);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

        }
    }

    private void getRequestResultType() {
        Bundle extras = getIntent().getExtras();
        String TRANSITION_TYPE = "";
        if (extras != null) {
            TRANSITION_TYPE = extras.getString(GetUserProfileResource.TRANSITION_TYPE);
        }

        if (TRANSITION_TYPE.equals("GetUserProfile")) {
            requestResultType = GetUserProfile.requestResultType;
            beanRequestsFromPeoples1 = GetUserProfile.beanRequestsFromPeoples1;
            pendingPartnerEmail = GetUserProfile.pendingPartnerEmail;

            /** if user is male then partner assume to be female */
            if(GetUserProfile.uGender.toLowerCase().equals("male")){
                partnerImage.setImageResource(R.drawable.girl_heart);
            }else{
                partnerImage.setImageResource(R.drawable.add);
            }
        } else if (TRANSITION_TYPE.equals("UserAddressAndFavorites")) {
            requestResultType = UserAddressAndFavorites.requestResultType;
            beanRequestsFromPeoples1 = UserAddressAndFavorites.beanRequestsFromPeoples1;
            pendingPartnerEmail = UserAddressAndFavorites.pendingPartnerEmail;
            /** if user is male then partner assume to be female */
            if(UserAddressAndFavorites.uGender.toLowerCase().equals("male")){
                partnerImage.setImageResource(R.drawable.girl_heart);
            }else{
                partnerImage.setImageResource(R.drawable.add);
            }
        }else if (TRANSITION_TYPE.equals("Dashboard")) {
            requestResultType = Dashboard.requestResultType;
            beanRequestsFromPeoples1 = Dashboard.beanRequestsFromPeoples1;
            pendingPartnerEmail = Dashboard.pendingPartnerEmail;
        }
    }


    /**
     * Method that validate the form
     * and send request if form data is valid
     */

    public void attemptPartnerConnection(View view) {


        /** Store values into String for values save on server.*/
        //String email = connectionEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        String email = partnerEmailForRequest.getText().toString();


        /** Check validation for email .*/
        if (!FormValidation.isValidEmail(email)) {
            partnerEmailForRequest.setError(getString(R.string.invalid_connectionEmail));
            focusView = partnerEmailForRequest;
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

            sendConnectionRequest(email);


        }
    }


    public void sendConnectionRequest(final String email) {

        /** start progress bar */
        progressBarManager.startProgressBar();

        final Map<String, String> postData = getKeyPairValueInHashMapForSendingRequest(userIDAfterLogin, tokenafetrLogin, emailAfterLogin, email);
        /** get request from server */
        new Thread() {
            public void run() {

                /**
                 * Make Connection and Pass URL to get response
                 */
                sendRequest(postData, email);

            }
        }.start();


    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMapForSendingRequest(String userID, String token, String email, String emailToSend) {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        String key[] = {ManagePartnerResource.USER_ID_KEY, ManagePartnerResource.TOKEN_KEY, ManagePartnerResource.USER_EMAIL_KEY, ManagePartnerResource.REQUEST_EMAIL_KEY, ManagePartnerResource.MESSAGE_KEY};      // set keys
        String value[] = {userID, token, email, emailToSend, "no message"};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    private void sendRequest(Map<String, String> postData, final String email) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, currentActivity.getResources().getString(R.string.serverDomain) + currentActivity.getResources().getString(R.string.sendRequest));

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */


            popup("Network problem.Please check your internet connectivity.", "Sorry!");
            // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            String message = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then request sent successful. */


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /** Stop progress bar */
                        GetUserProfile.isReturnFromConnectToPartnerPage = true;
                        UserAddressAndFavorites.isReturnFromConnectToPartnerPage = true;
                        Dashboard.isReturnFromConnectToPartnerPage = true;

                        requestListLayout.setVisibility(View.GONE);
                        getPartnerEmail.setVisibility(View.VISIBLE);

                        pendingArea.setVisibility(View.VISIBLE);
                        inviteArea.setVisibility(View.GONE);

                        pendingText.setText("Waiting for Approval from partner with email ID '" + email + "'");
                        progressBarManager.stopProgressBar();
                        cancelEmail = email;

                    }
                });

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(message, "Sorry!");

            } else {


                popup("Server problem please try after some time.", "Sorry!");


            }


        }


    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String userID, String token, String email, String cancelEmailOrUSerEmail_key) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {cancelEmailOrUSerEmail_key, ManagePartnerResource.TOKEN, ManagePartnerResource.USER_ID};      // set keys
        String value[] = {email, token, userID};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    public void cancelRequestButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Alert!");
        builder.setMessage("Do you want cancel the request?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                progressBarManager.startProgressBar();
                new Thread() {
                    public void run() {
                        cancelRequest(getKeyPairValueInHashMap(userIDAfterLogin, tokenafetrLogin, cancelEmail, ManagePartnerResource.CANCEL_EMAIL_KEY));
                    }
                }.start();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                dialog.cancel();


            }
        });
        builder.create().show();


    }


    private void cancelRequest(Map<String, String> postData) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, currentActivity.getResources().getString(R.string.serverDomain) + currentActivity.getResources().getString(R.string.cancelRequest));

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */


            popup("Network problem.Please check your internet connectivity.", "Sorry!");
            // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            String message = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then login successful. */


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        /** Stop progress bar */
                        progressBarManager.stopProgressBar();

                        pendingArea.setVisibility(View.GONE);
                        inviteArea.setVisibility(View.VISIBLE);
                        progressBarManager.stopProgressBar();
                        Toast.makeText(currentActivity, "Your request has been canceled successfully", Toast.LENGTH_LONG).show();

                    }
                });

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(message, "Sorry!");

            } else {


                popup("Server problem please try after some time.", "Sorry!");


            }


        }


    }


    /**
     * Pop for login activity.
     *
     * @param message
     * @param titleText
     */
    public void popup(final String message, final String titleText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                /** Stop progress bar */
                LoveLifePopups.loveLifePopup(currentActivity, titleText, message);
            }
        });


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

package com.lovelife.lovelife;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.lovelife.lovelife.CustomEditText.BackAwareEditText;
import com.lovelife.lovelife.LoveLifeUtility.DataEncription;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity {
    String email, password;
    static private BackAwareEditText mEmailView,rePassword;
    String vcode;
    Button button;

    String CHANGE_PASS_URL;


    /**
     * Parameter String keys
     */
    private String EMAIL = "email";
    private String PASSWORD = "password";
    private String VCODE = "verificationCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_activity);
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
//        passwd = (TextInputEditText) findViewById(R.id.pass1);
        mEmailView = (BackAwareEditText) findViewById(R.id.pass1);
        rePassword= (BackAwareEditText) findViewById(R.id.repass1);
        Intent intent = getIntent();
        setResult(Activity.RESULT_OK, intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        CHANGE_PASS_URL = "http://lovelife.aicoach.net/setPassword";
        if (data != null) {
            String query = data.getQuery();
            String[] array = query.split("&");
            String[] emails = array[0].split("=");
            email = emails[1];

            String[] pass = array[1].split("=");
            vcode = pass[1];

            System.out.println("xdxdxdxdxdxdxdxdxdxd" + email + " " + vcode);

        }
        button = (Button) findViewById(R.id.passdchange);
     /*   button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });*/


    }


    private Map<String, String> getKeyPairValueInHashMap(String email, String MD5Password, String verification) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        String key[] = {EMAIL, PASSWORD, VCODE};      // set keys
        String value[] = {email, MD5Password, verification};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    public void ChangePassword(View view) {

        // Store values at the time of the login attempt.


        // Reset errors.
        mEmailView.setError(null);
        rePassword.setError(null);
        // Store values at the time of the login attempt.
        String password = mEmailView.getText().toString();
        String repassword = rePassword.getText().toString();
        System.out.println("fffffdd"+password+repassword);

        boolean cancel = false;
        View focusView = null;

         // Check for a valid email address.
        if (TextUtils.isEmpty(password)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(repassword)) {
            rePassword.setError(getString(R.string.error_field_required));
            focusView = rePassword;
            cancel = true;

        }else if (!repassword.equals(password)) {
            rePassword.setError(getString(R.string.rePassword_error));
            focusView = rePassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.


            //  Toast.makeText(currentActivity,"ifKeyBoardOpen "+ifKeyBoardOpen,Toast.LENGTH_LONG).show();


            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            /** start progress bar */



            /** Encrypt password to MD5 */

            String passwords = DataEncription.encriptToMD5(password);

            System.out.println("emptyyynooo"+passwords);

            final Map<String, String> postData = getKeyPairValueInHashMap(email, passwords, vcode);


            /** register user with collected data on different thread to server */
            new Thread() {
                public void run() {
                    ChangePass(postData);

                }
            }.start();
        }




















    }

    private void ChangePass(Map<String, String> postData) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        final String reponse = serverSync.SyncServer(postData, CHANGE_PASS_URL);

        System.out.println("responseaaaassssss " + reponse);

        LoveLifePopups.loveLifePopupForChangePass(ForgetPassword.this, "", " Password Updated.");

    }


}


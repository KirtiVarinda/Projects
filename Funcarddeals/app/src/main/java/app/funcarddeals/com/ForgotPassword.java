package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.Validator.FormValidation;
import app.funcarddeals.com.network.ServerSync;

public class ForgotPassword extends Activity {
    private EditText email;
    private String enteredEmail="";
    Map<String, String> theMap;
    private String FUNCARD_ADD_CARD_URL;
    private Context context;

    private MenuManager menuManager;

    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;

    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;

    /**
     * variable for getting response.
     */
    private String response = "";

    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = (EditText) findViewById(R.id.emailf);
        context=getApplicationContext();
        /**
         * current activity reference
         */
        currentActivity = ForgotPassword.this;
        FUNCARD_ADD_CARD_URL = context.getResources().getString(R.string.forgotpass);

        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForLogin(context);
        popString.PopupMessagesStringsForRegistration(context);

    }


    /**
     * get key value pairs to send in url using post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap() {
        /**
         * Get user id from session.
         */


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {"funcard_email"};      // set keys
        String value[] = {email.getText().toString()};                            //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);

    }


    /**
     * Add card code to funcrad on server.
     */
    private void getStoreDetailFromServerSetView() {

        new Thread() {
            public void run() {

                theMap = getKeyPairValueInHashMap();

                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_ADD_CARD_URL);


                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {
                    /**
                     * if there is a network problem while loading particular categories from server
                     * them shows the network error on page.
                     */
                    //showProblem();
                    FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.networkError);
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED)) {
                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);


                    } else if (response.equals(ServerSync.INVALID_FUNCARD_USER)) {
                        /**
                         * server error toast.
                         */
                        ForgotPassword.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {

                        if (response.equals("funcarddeals_invalid_email")) {

                            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, "This email id is not registered with FUNCARD.");
                        } else if (response.equals("funcarddeals_not_sent")) {

                            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, "Please check your Email Id.");
                        }else if (response.equals("funcarddeals_sent")) {

                            FuncardPopups.funcardPopupWithFinishCurrentActivityOnOK(currentActivity, popString.regSuccessTitle, "A new Password has been sent to your registered email id.");
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

                    }
                    progressBarManager.stopProgressBar();
                }
            }
        }.start();
    }


    public void submit(View view) {



        /**
         * get card code from the edit text
         */

        enteredEmail = email.getText().toString();

        if (FormValidation.isValidEmail(enteredEmail)) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            getStoreDetailFromServerSetView();
        } else {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage,  popString.regEmail);
        }

    }



}

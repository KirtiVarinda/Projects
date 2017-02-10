package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.Validator.FormValidation;
import app.funcarddeals.com.network.ServerSync;

public class ResendActivity extends Activity {



    private String EMAIL_SUCCESS = "email_success";
    private String EMAIL_EXIST =  "email_exist";
    private String EMAIL_SAME = "email_same";

    String newEmail = "";


    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;






    private EditText email;
    private MenuManager menuManager;
    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for getting response.
     */
    private String response = "";


    Map<String, String> theMap;

    private String RESEND_EMAIL_URL;
    /**
     * Variable for post key of url
     */
    private static String OLD_EMAIL = "oldemail";
    private static String NEW_EMAIL = "newemail";

    private Context context;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    /**
     * old email from previous activity
     * */

    private String oldEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend);
        context = getApplicationContext();
        email = (EditText) findViewById(R.id.resend_email);


        oldEmail = getIntent().getStringExtra("old_email");

       // System.out.println("old email = "+oldEmail);
        email.setText(oldEmail);



        /**
         * current activity reference
         */
        currentActivity = ResendActivity.this;

        progressBarManager = new ProgressBarManager(currentActivity);



        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForLogin(context);
        popString.PopupMessagesStringsForRegistration(context);

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);




        /**
         * Get add card url from string resource.
         */

        RESEND_EMAIL_URL = context.getResources().getString(R.string.resend_email);
    }


    /**
     * get key value pairs to send in url using post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap() {




        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {OLD_EMAIL, NEW_EMAIL};      // set keys
        String value[] = {oldEmail, newEmail};                            //set values


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
    private void resendVerificationEmail() {

        new Thread() {
            public void run() {

                theMap = getKeyPairValueInHashMap();

                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, RESEND_EMAIL_URL);
               // System.out.println("test= "+response);

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


                    } else if (response.equals(EMAIL_SUCCESS)) {
                        /**
                         * server error toast.
                         */
                        ResendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * switch to login activity when pressed ok from popup.
                                 */
                                FuncardPopups.funcardPopupForSwitchActivity(ResendActivity.this, popString.regSuccessTitle,  getResources().getString(R.string.email_verification_register), LoginScreen.class);

                            }
                        });


                    } else if (response.equals(EMAIL_SAME)) {
                        ResendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * switch to login activity when pressed ok from popup.
                                 */
                                FuncardPopups.funcardPopupForSwitchActivity(currentActivity, popString.regSuccessTitle,  getResources().getString(R.string.email_verification_register), LoginScreen.class);

                            }
                        });


                    } else if (response.equals(EMAIL_EXIST)) {

                        ResendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * user already exist.
                                 */

                                FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regUserAlreadyExist);
                            }
                        });

                    } else {

                        /**
                         * server error toast.
                         */
                        ResendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, "Server Problem");
                               // Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });



                    }
                    progressBarManager.stopProgressBar();
                }
            }
        }.start();
    }


    /**
     * Method for adding funcard Card Code
     *
     * @param view
     */
    public void resendEmail(View view) {


        /**
         *get  entered email
         */


        newEmail = email.getText().toString();

        if (FormValidation.isValidEmail(newEmail)) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();

            resendVerificationEmail();
        } else {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regEmail);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (MenuManager.selectedMenu(id, currentActivity)) return true;
        return super.onOptionsItemSelected(item);
    }


}

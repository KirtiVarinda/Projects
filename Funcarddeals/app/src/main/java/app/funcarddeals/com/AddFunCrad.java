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

public class AddFunCrad extends Activity implements StandardMenus{

    private static String CARD_VALID, CARD_NOT_VALID, CARD_WRONG;

    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;

    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * variable for error response from server
     */
    private static String FUNCARD_ACCEPTED = "funcard_accepted", FUNCARD_REJECTED = "funcard_rejected";

    private String funcardCardCode;
    private EditText cardTextBox;
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

    private String FUNCARD_ADD_CARD_URL;
    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_CARDCODE_KEY = "funcard_cardcode_id";
    private static String FUNCARD_FROM_KEY = "funcard_from";

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;
    private Context context;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fun_crad);
        context = getApplicationContext();
        cardTextBox = (EditText) findViewById(R.id.editText3);

        CARD_VALID = getResources().getString(R.string.card_added_successfuly);
        CARD_NOT_VALID = getResources().getString(R.string.addfuncard_not_valid);
        CARD_WRONG = getResources().getString(R.string.addfuncard_wrong);

        /**
         * current activity reference
         */
        currentActivity = AddFunCrad.this;

        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();

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
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /**
         * Get add card url from string resource.
         */

        FUNCARD_ADD_CARD_URL = context.getResources().getString(R.string.add_funcard);
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
        String USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_CARDCODE_KEY, FUNCARD_FROM_KEY};      // set keys
        String value[] = {USER_ID, funcardCardCode, "Android"};                            //set values


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
                        AddFunCrad.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {

                        if (response.equals(FUNCARD_ACCEPTED)) {

                            FuncardPopups.funcardPopupWithFinishCurrentActivityOnOK(currentActivity, popString.regSuccessTitle, CARD_VALID);
                        } else if (response.equals(FUNCARD_REJECTED)) {

                            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, CARD_WRONG);
                        }


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
    public void addCard(View view) {



        /**
         * get card code from the edit text
         */

        funcardCardCode = cardTextBox.getText().toString();

        if (!FormValidation.removeWhiteSpaces(funcardCardCode).equals("")) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();

            getStoreDetailFromServerSetView();
        } else {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, CARD_NOT_VALID);
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


    @Override
    public void home(View view) {
        menuManager.goToHomePage(currentActivity, MainPage.class);
    }

    @Override
    public void favorite(View view) {
        switchActivity.openActivity(currentActivity, FuncardFavourites.class);
    }

    @Override
    public void reminder(View view) {
        switchActivity.openActivity(currentActivity, FuncardReminders.class);
    }

    @Override
    public void scanner(View view) {

    }


    public void exit(View view) {
        switchActivity.openActivityFinishCurrent(currentActivity, AddFunCrad.class);
//        menuManager=new MenuManager(this);
//        menuManager.closeAppGoToDeviceHome(currentActivity);
    }

    public void menu(View view) {
        menuManager=new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }

}

package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClassForUserInfo;
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

public class PayWithCreditCrad extends Activity implements StandardMenus {

    private String purchaseSuccessfully="funcarddeals_yes";
    private String purchaseFaild="funcarddeals_no";
    /**
     * variable for getting response from server.
     */
    private String response = "";

    /**
     * progress bar manager.
     */
    private ProgressBarManager progressBarManager;

    /**
     * Funcard store product  url.
     */
    private static String  FUNCARD_AUTHORIZEDNET_URL;


    private String experiyMonth, userEnteredCardNumber;

    Map<String, String> theMap;
    /**
     * Reference variable for Shared Data (Session).
     */

    /**
     * Billing country used.
     */
    private static String BILLING_COUNTARY = "US";


    private static MySharedData mySharedData;

    private static String CITY = "funcard_city";
    private static String USERID = "funcard_user_id";
    private static String QUANTITY = "funcard_quantity";
    private static String RESELLER_ID = "funcard_reseller_id";
    private static String BILLING_NAME = "funcard_billing_name";
    private static String BILLING__ADDRESS = "funcard_billing_address";
    private static String BILLING_CITY = "funcard_billing_city";
    private static String BILLING_STATE = "funcard_billing_state";
    private static String BILLING_COUNTRY = "funcard_billing_country";
    private static String BILLING_ZIP = "funcard_billing_zip";

    private static String EXP_DATE = "exp_date";
    private static String FUNCARD_CARD = "funcard_card";


    private EditText cardNumber, exp_month, exp_year;

    private MenuManager menuManager;
    private Context context;


    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;


    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    private static BuyFuncardBeanClassForUserInfo buyFuncardBeanClassForUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_with_credit_crad);
        cardNumber = (EditText) findViewById(R.id.card_no);
        exp_month = (EditText) findViewById(R.id.month);
        exp_year = (EditText) findViewById(R.id.year);

        context = getApplicationContext();
        currentActivity = PayWithCreditCrad.this;

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);

        /** initialize activity switcher */
        switchActivity = new SwitchActivities();

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForRegistration(context);

        /** initialize progress bar.*/
        progressBarManager = new ProgressBarManager(currentActivity);

        /** authorined payment url */
        FUNCARD_AUTHORIZEDNET_URL = context.getResources().getString(R.string.pay_with_authorizenet);

        /**
         * get bean object from the extras dsend with intent.
         */
        buyFuncardBeanClassForUserInfo = (BuyFuncardBeanClassForUserInfo) getIntent().getSerializableExtra(BuyFuncardBillingAddress.key);

    }


    public void payNow(View view) {
        if (formValidation()) {

            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();


            experiyMonth = exp_month.getText().toString()+"_"+exp_year.getText().toString();
            userEnteredCardNumber = cardNumber.getText().toString();


            payWithCard();
        }


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

        String resellID="null";
        if(buyFuncardBeanClassForUserInfo.getResellerID()!=null){
            resellID=buyFuncardBeanClassForUserInfo.getResellerID();
        }
        String USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);
        String key[] = {CITY, USERID, QUANTITY, RESELLER_ID, BILLING_NAME, BILLING__ADDRESS, BILLING_CITY, BILLING_STATE, BILLING_COUNTRY, BILLING_ZIP, EXP_DATE, FUNCARD_CARD};      // set keys
        String value[] = {buyFuncardBeanClassForUserInfo.getSelectedCityID(), USER_ID, buyFuncardBeanClassForUserInfo.getCardQuantity(),
                resellID , buyFuncardBeanClassForUserInfo.getUserFullName(), buyFuncardBeanClassForUserInfo.getUserAddress(),
                buyFuncardBeanClassForUserInfo.getSelectedCity(), buyFuncardBeanClassForUserInfo.getUserState(), BILLING_COUNTARY, buyFuncardBeanClassForUserInfo.getUserZip(), experiyMonth, userEnteredCardNumber};



        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);


    }

    /**
     * pay with credit card method
     */
    private void payWithCard() {

        new Thread() {
            public void run() {
                /**
                 * get key value pair that will send in url to get products.
                 * not for redeem.
                 */
                theMap = getKeyPairValueInHashMap();

                /**
                 * get product detail json from funcard server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_AUTHORIZEDNET_URL);


                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {
                    FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.networkError);
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED)) {

                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);


                    } else if (response.equals(purchaseSuccessfully)) {  /** card purchased successfully */
                        FuncardPopups.funcardPopup(currentActivity, popString.regSuccessTitle,"Thanks for purchasing Fun Card for the city of "+buyFuncardBeanClassForUserInfo.getSelectedCityID()+". Your Fun Card is now ready to be used for Buy 1 Get 1 Deals in "+buyFuncardBeanClassForUserInfo.getSelectedCityID()+".");

                    } else if (response.equals(purchaseFaild)) { /** card purchased falied */
                        FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, "Your credit card number is invalid.");

                    }else{
                        FuncardPopups.funcardPopup(currentActivity, popString.titleMessage,"Server problem. Try again later.");

                    }

                    progressBarManager.stopProgressBar();
                }
            }
        }.start();
    }


    /**
     * validation for form
     *
     * @return
     */
    private boolean formValidation() {
        if (FormValidation.removeWhiteSpaces(cardNumber.getText().toString()).equals("") || cardNumber.getText().toString().length() != 16) {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, getResources().getString(R.string.invalid_card));
            return false;
        } else if (FormValidation.removeWhiteSpaces(exp_month.getText().toString()).equals("")) {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, getResources().getString(R.string.invalid_month));
            return false;
        } else if (FormValidation.removeWhiteSpaces(exp_year.getText().toString()).equals("")) {
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, getResources().getString(R.string.invalid_years));
            return false;
        } else {
            return true;
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

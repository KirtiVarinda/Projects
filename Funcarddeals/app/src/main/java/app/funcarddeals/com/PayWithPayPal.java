package app.funcarddeals.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClassForUserInfo;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;

public class PayWithPayPal extends Activity implements StandardMenus {


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * Billing country used.
     */
    private static String BILLING_COUNTARY = "US";


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;
    private MenuManager menuManager;

    /**
     * variable for keys
     */
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


    private WebView webView;
    private Context context;
    private ProgressBarManager progressBarManager;
    private Activity currentActivity;
    boolean loader = true;

    private String PAY_WITH_PAYPAL_URL;


    private static BuyFuncardBeanClassForUserInfo buyFuncardBeanClassForUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_with_paypal);
        context = getApplicationContext();
        currentActivity = PayWithPayPal.this;


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /**
         * url to load in webview
         */
        PAY_WITH_PAYPAL_URL = getResources().getString(R.string.pay_with_paypal_url);

        webView = (WebView) findViewById(R.id.webView);
        menuManager = new MenuManager(context);
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * initialize progress bar.
         */
        progressBarManager = new ProgressBarManager(currentActivity);

        buyFuncardBeanClassForUserInfo = (BuyFuncardBeanClassForUserInfo) getIntent().getSerializableExtra(BuyFuncardBillingAddress.key);

        PAY_WITH_PAYPAL_URL = context.getResources().getString(R.string.pay_with_paypal_url);

        loadWebPageOfGetFixer(PAY_WITH_PAYPAL_URL);


    }

    /**
     * get key value pairs to send in url using post
     *
     * @return
     */
    //  private Map<String, String> getKeyPairValueInHashMap() {
    /**
     * Get user id from session.
     */


    //  String postData = "username=my_username&password=my_password";

    /**
     * make key and values array for all the strings.
     * index of both should be same.
     */
     /*   String key[] = {CITY, USERID, QUANTITY, RESELLER_ID, BILLING_NAME, BILLING__ADDRESS, BILLING_CITY, BILLING_STATE, BILLING_COUNTRY, BILLING_ZIP};      // set keys
        String value[] = {buyFuncardBeanClassForUserInfo.getSelectedCity(), USER_ID, buyFuncardBeanClassForUserInfo.getCardQuantity(),
                buyFuncardBeanClassForUserInfo.getResellerID(), buyFuncardBeanClassForUserInfo.getUserFullName(), buyFuncardBeanClassForUserInfo.getUserAddress(),
                buyFuncardBeanClassForUserInfo.getSelectedCity(), buyFuncardBeanClassForUserInfo.getUserState(), BILLING_COUNTARY, buyFuncardBeanClassForUserInfo.getUserZip()};                //set values
*/

    /**
     * Get key value pairs .
     * KeyValues will be in hash map.
     * /
     */
    //   PairValues pairValues = new PairValues();
    //    return pairValues.funcardPairValue(key, value);
    // }
    protected void loadWebPageOfGetFixer(String url) {


        String USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);

        String postData = CITY + "=" + buyFuncardBeanClassForUserInfo.getSelectedCityID() + "&" + USERID + "=" + USER_ID + "&" + QUANTITY + "=" + buyFuncardBeanClassForUserInfo.getCardQuantity() + "&" + RESELLER_ID
                + "=" + "null" + "&" + BILLING_NAME + "=" + replaceSpaceAndBackSlash(buyFuncardBeanClassForUserInfo.getUserFullName()) + "&" + BILLING__ADDRESS + "=" + replaceSpaceAndBackSlash(buyFuncardBeanClassForUserInfo.getUserAddress())
                + "&" + BILLING_CITY + "=" + replaceSpaceAndBackSlash(buyFuncardBeanClassForUserInfo.getSelectedCity()) + "&" + BILLING_STATE + "=" + replaceSpaceAndBackSlash(buyFuncardBeanClassForUserInfo.getUserState()) +
                "&" + BILLING_COUNTRY + "=" + BILLING_COUNTARY + "&" + BILLING_ZIP + "=" + replaceSpaceAndBackSlash(buyFuncardBeanClassForUserInfo.getUserZip());




        // loader = true;

        webView.setWebViewClient(new WebViewClient() {


            // url links will open in new browser not in webview if not use this
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loader = true;
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                try {


                    if (loader) {
                        progressBarManager.startProgressBar();
                        loader = false;
                    }

                   /* if (progressDialog == null && loader) {


                    }*/
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    progressBarManager.stopProgressBar();
                   /* if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }*/
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });


        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(url);
      //  webView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay_with_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * replace all the spaces with "_" in a string.
     */
    private String replaceSpaceAndBackSlash(String str) {

        return str.replace(" ", "_").replace("/", "+");

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

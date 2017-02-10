package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.RedeemActivityBeanClass;
import app.funcarddeals.com.BeanClasses.StoreoffersBeanClass;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class RedeemActivity extends Activity implements StandardMenus {

    private String thanksMessage;

    private MenuManager menuManager;
    private Context context;


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    String FUNCRAD_STORE_ID_KEY = "funcard_store_id";
    /**
     * Funcard store product  url.
     */
    private static String FUNCARD_REDEEM_CONFIRM_URL;


    /**
     * progress bar manager.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for getting response from server.
     */
    private String response = "";


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;

    Map<String, String> theMap;


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    private String store_id, product_id, city_id, user_id,product_offer,product_validity;


    private TextView title, address, offer, redeem_text, expire_date, redeem_code;

    /**
     * variable for error

     */
    LinearLayout networkError;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        context = getApplicationContext();
        title = (TextView) findViewById(R.id.textView15);
        address = (TextView) findViewById(R.id.textView116);
        offer = (TextView) findViewById(R.id.textView117);
        redeem_text = (TextView) findViewById(R.id.textView118);
        expire_date = (TextView) findViewById(R.id.textView120);
        redeem_code = (TextView) findViewById(R.id.textView38);
        networkError = (LinearLayout) findViewById(R.id.redeem_error);
        scrollView=(ScrollView) findViewById(R.id.scrollview);


        thanksMessage=getResources().getString(R.string.redeem_thanksmessage);

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /**
         * Get particular categories url from string resource.
         */
        FUNCARD_REDEEM_CONFIRM_URL = context.getResources().getString(R.string.redeem_confirm);


        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        /**
         * current activity reference
         */
        currentActivity = RedeemActivity.this;

        /** initialize progress bar.*/
        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * Get values from previous activity pass with intent.
         */

        store_id = getIntent().getStringExtra(StoreOfferDetail.FUNCRAD_STORE_ID);
        product_id = getIntent().getStringExtra(StoreOfferDetail.FUNCARD_PRODUCT_ID);
        city_id = getIntent().getStringExtra(StoreOfferDetail.FUNCRAD_CITY_ID);
        user_id = getIntent().getStringExtra(StoreOfferDetail.FUNCRAD_USER_ID);
        product_offer= getIntent().getStringExtra(StoreOfferDetail.FUNCARD_PRODUCT_OFFER);
        product_validity= getIntent().getStringExtra(StoreOfferDetail.FUNCARD_PRODUCT_VALIDITY);



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
        String key[] = {FUNCRAD_STORE_ID_KEY, StoreOfferDetail.FUNCARD_PRODUCT_ID_KEY, StoreOfferDetail.FUNCRAD_CITY_ID_KEY, StoreOfferDetail.FUNCRAD_USER_ID_KEY};      // set keys
        String value[] = {store_id, product_id, city_id, user_id};                                            //set values





        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);


    }

    /**
     * Get store products json from funcard server.
     */
    private void checkRedeemFromServer() {

        new Thread() {
            public void run() {
                /**
                 * get key value pair that will send in url to get products.
                 */
                theMap = getKeyPairValueInHashMap();

                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_REDEEM_CONFIRM_URL);

                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {


                    /**
                     * if there is a network problem while loading store products from server
                     * them shows the network error on page.
                     */
                    showProblem();
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED)) {
                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);
                        progressBarManager.stopProgressBar();

                    } else if (response.equals(ServerSync.INVALID_FUNCARD_USER)) {
                        /**
                         * server error toast.
                         */
                        RedeemActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });

                        progressBarManager.stopProgressBar();

                    } else {
                        /** runs for categories*/
                        readDataFromJasonAndSetInView(response);
                        progressBarManager.stopProgressBar();

                    }
                }
            }
        }.start();
    }


    RedeemActivityBeanClass redeemActivityBeanClass;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Info");


            /**Iterate the jsonArray and print the info of JSONObjects*/

            redeemActivityBeanClass = new RedeemActivityBeanClass();
            JSONObject jsonObject = jsonArray.getJSONObject(0);


            String code = jsonObject.getString("code");
            String name = jsonObject.getString("name");
            String address1 = jsonObject.getString("address");
            String address2 = jsonObject.getString("address1");
            String serial = jsonObject.getString("serial");

            redeemActivityBeanClass.setRedeem_code(code);
            redeemActivityBeanClass.setStore_name(name);
            redeemActivityBeanClass.setStore_address1(address1);
            redeemActivityBeanClass.setStore_address2(address2);
            redeemActivityBeanClass.setSerial(serial);



            /**
             * set the values to layout after getting from json.
             */
            setView();
        } catch (JSONException e) {
            RedeemActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }


    }

    private void setView() {
        RedeemActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(redeemActivityBeanClass.getStore_name());
                address.setText(redeemActivityBeanClass.getStore_address1() + ", \r\n" + redeemActivityBeanClass.getStore_address2());
                offer.setText(product_offer);
                redeem_text.setText(getResources().getString(R.string.redeem_text4));
                expire_date.setText("This discount expires: " + product_validity);
                redeem_code.setText(redeemActivityBeanClass.getRedeem_code());
                progressBarManager.stopProgressBar();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();


        /**
         * Sync strore product from server only if the new instance of activity is created
         */
        if (response.equals("")) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get store product json from funcard server.
             */
            checkRedeemFromServer();

        }


    }




    /**
     * show listview and hide error message
     */
    private void ShowRedeemImage() {
        networkError.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

    }


    /**
     * show problem message and hide error message
     */
    private void showProblem() {
        RedeemActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkError.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }
        });

    }

    /**
     * Reload redeem page if network problem occurs.
     *
     * @param view
     */
    public void reload(View view) {
        ShowRedeemImage();

        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         * Getredeem json from funcard server.
         */

        checkRedeemFromServer();
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
        /**
         * switch to home activity when pressed ok from popup.
         */
        FuncardPopups.funcardPopupForSwitchActivity(currentActivity, "Attention!","Thank you for using this discount with the Fun Card.", MainPage.class);

    }

    @Override
    public void favorite(View view) {
        switchActivity.openActivity(currentActivity, FuncardFavourites.class);
    }

    @Override
    public void reminder(View view) {
        FuncardPopups.funcardPopupForSwitchActivity(currentActivity, "Attention!", "Thank you for using this discount with the Fun Card.", FuncardReminders.class);

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

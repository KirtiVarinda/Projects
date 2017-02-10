package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.StorePageBeanClass;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class StorePage extends Activity implements StandardMenus {

    private MenuManager menuManager;
    private Context context;


    StorePageBeanClass storePageBeanClass = new StorePageBeanClass();

    TextView address;
    TextView time;

    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for getting response.
     */
    private String response = "";


    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_STORE_ID_KEY = "funcard_store_id";

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    /**
     * Funcard all Stores  url.
     */
    private static String FUNCARD_STORE_PAGE_URL;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;

    Map<String, String> theMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);
        context = getApplicationContext();
        TextView storeTitle = (TextView) findViewById(R.id.textView15);
        time = (TextView) findViewById(R.id.textView17);
        address = (TextView) findViewById(R.id.textView16);

        /** initialize activity switcher */
        switchActivity = new SwitchActivities();


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        /**
         * Get particular categories url from string resource.
         */

        FUNCARD_STORE_PAGE_URL = context.getResources().getString(R.string.funcrad_store_page);


        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);

        /**
         * current activity reference
         */
        currentActivity = StorePage.this;

        progressBarManager = new ProgressBarManager(currentActivity);


        /**
         * Get values from the previous activity send in putExtra.
         */
        storePageBeanClass.setStore_id(getIntent().getStringExtra(Stores.STORE_ID));
        storePageBeanClass.setStore_name(getIntent().getStringExtra(Stores.STORE_NAME));
        storePageBeanClass.setStore_lat(getIntent().getStringExtra(Stores.STORE_LAT));
        storePageBeanClass.setStore_long(getIntent().getStringExtra(Stores.STORE_LNG));


        /** set store title name*/
        storeTitle.setText(storePageBeanClass.getStore_name());


        theMap = getKeyPairValueInHashMap();


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
        String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_STORE_ID_KEY};      // set keys
        String value[] = {USER_ID, storePageBeanClass.getStore_id()};                            //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);

    }


    @Override
    public void onResume() {
        super.onResume();


        /**
         * Sync store detail from server only if the new instance of activity is created
         */
        if (response.equals("")/* && storePageBeanClass.getStore_phone()==null*/) {

            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Getstore detail json from funcard server.
             */

            getStoreDetailFromServerSetView();

        }
    }


    /**
     * Get Particular categories json from funcard server.
     */
    private void getStoreDetailFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_STORE_PAGE_URL);
                System.out.println(" responseresponse1 " + response);

                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {


                    /**
                     * if there is a network problem while loading particular categories from server
                     * them shows the network error on page.
                     */
                    showProblem();
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED) || response.equals("funcarddeals_sorry")) {
                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);
                        progressBarManager.stopProgressBar();

                    } else if (response.equals(ServerSync.INVALID_FUNCARD_USER)) {
                        /**
                         * server error toast.
                         */
                        StorePage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });

                        progressBarManager.stopProgressBar();

                    } else {
                        /** runs for categories*/
                        readDataFromJasonAndSetInView(response);


                    }
                }
            }
        }.start();
    }


    /**
     * Read json data from response and set in listview.
     *
     * @param jsonData
     */


    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Store");


            /**
             * initialize the beansClass array of object.
             */

            JSONObject jsonObject = jsonArray.getJSONObject(0);


            storePageBeanClass.setStore_address1(jsonObject.getString("address"));
            storePageBeanClass.setStore_address2(jsonObject.getString("address1"));
            storePageBeanClass.setStore_phone(jsonObject.getString("phone"));
            storePageBeanClass.setStore_working_hours(jsonObject.getString("time"));
            storePageBeanClass.setStore_url(jsonObject.getString("url"));
            /**
             * set the values to layout after getting from json.
             */
            setListView();
        } catch (JSONException e) {
            StorePage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }


    }


    /**
     * set list view.
     */

    private void setListView() {

        StorePage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time.setText(storePageBeanClass.getStore_working_hours());
                address.setText(storePageBeanClass.getStore_address1() + ", \r\n" + storePageBeanClass.getStore_address2());
            }
        });

        progressBarManager.stopProgressBar();


    }


    /**
     * show problem message in textview
     */
    private void showProblem() {
        StorePage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time.setText("");
                address.setText(getResources().getString(R.string.network_error_for_list));
            }
        });

    }


    /**
     * Reload url to get the store data from server.
     *
     * @param view
     */
    public void reload(View view) {

        if (storePageBeanClass.getStore_address2() == null) {
            /**
             * start progress bar.
             */


            progressBarManager.startProgressBar();


            /**
             * get store dtail again.
             */

            getStoreDetailFromServerSetView();
        }

    }


    /**
     * it open the storeOffers page
     */
    public void funcardStore(View view) {
        switchActivity.openActivity(currentActivity, StoreOffers.class, new String[]{Stores.STORE_ID, Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG}
                , new String[]{storePageBeanClass.getStore_id(), storePageBeanClass.getStore_name(), storePageBeanClass.getStore_lat()
                        , storePageBeanClass.getStore_long()});
    }


    /**
     * it open the full map  page
     */
    public void funcardMap(View view) {

        switchActivity.openActivity(currentActivity, MapPage.class, new String[]{Stores.STORE_ID, Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG}
                , new String[]{storePageBeanClass.getStore_id(), storePageBeanClass.getStore_name(), storePageBeanClass.getStore_lat()
                        , storePageBeanClass.getStore_long()});
    }

    /**
     * opes store link in browser
     */
    public void openWebPage(View view) {

        if (storePageBeanClass.getStore_url() != null) {

            if (storePageBeanClass.getStore_url().equals("")) {
                Toast.makeText(context, "There is no website added for this store.", Toast.LENGTH_LONG).show();
            } else {
                progressBarManager.startProgressBar();
                new Thread() {

                    public void run() {

                        System.out.println(" url " + storePageBeanClass.getStore_url());
                        ServerSync sync = new ServerSync();
                        final String code = sync.checkURLStatus(storePageBeanClass.getStore_url());
                        System.out.println(" response code " + code);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || code.equals(ServerSync.HTTP_ERROR)) {
                                    Toast.makeText(context, "There is no website added for this store.", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storePageBeanClass.getStore_url()));
                                    startActivity(browserIntent);
                                }

                                progressBarManager.stopProgressBar();

                            }
                        });


                    }


                }.start();
            }


        } else {
            Toast.makeText(context, "Please check internet connectivity and refresh page.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * It calls on store number
     */
    public void callStore(View view) {
        if (storePageBeanClass.getStore_phone() != null) {
            Uri number = Uri.parse("tel:" + storePageBeanClass.getStore_phone());
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);
        } else {
            Toast.makeText(context, "Please check internet connectivity and refresh page.", Toast.LENGTH_LONG).show();
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
        menuManager = new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }
}

package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.StoreoffersBeanClass;
import app.funcarddeals.com.CustomListAdapters.StoreProductsListAdapter;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class StoreOffers extends Activity implements StandardMenus {


    /**
     * Funcard store product  url.
     */
    private static String FUNCARD_STORE_PRODUCTS_URL;


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * Variable to send data in intent
     */
    public static String FUNCARD_PRODUCT_ID = "funcard_product_id";
    public static String FUNCARD_PRODUCT_NAME = "funcard_product_name";
    public static String FUNCARD_PRODUCT_OFFERS = "funcard_product_offers";

    /**
     * Array of object that stores data for store products.
     */
    StoreoffersBeanClass[] storeoffersBeanClass;
    private String[] allStoreProductsID;


    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_STORE_ID_KEY = "funcard_store_id";
    Map<String, String> theMap;


    /**
     * variable to show error when network problem
     */
    LinearLayout reloadProblem;


    /**
     * progress bar manager.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for getting response from server.
     */
    private String response = "";


    /**
     * Store dteila array.
     */

    String storeDetail[];


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    /**
     * variable for current activity reference.
     */
    public static  Activity currentActivity;
    private MenuManager menuManager;
    private Context context;


    /**
     * initialize product list view
     */
    ListView productList;


    /**
     * variables to get values from previous activity that are send with intent
     */
    String store_id, store_name, store_lat, store_lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_offers);
        context = getApplicationContext();
        TextView storeTitle = (TextView) findViewById(R.id.textView15);
        reloadProblem = (LinearLayout) findViewById(R.id.store_products_layout_error);


        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);

        /**
         * current activity reference
         */
        currentActivity = StoreOffers.this;


        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /** initialize progress bar.*/
        progressBarManager = new ProgressBarManager(currentActivity);


        /**
         * Get particular categories url from string resource.
         */
        FUNCARD_STORE_PRODUCTS_URL = context.getResources().getString(R.string.funcrad_store_products);


        /**
         * Get values from the previous activity send in putExtra.
         */
        store_id = getIntent().getStringExtra(Stores.STORE_ID);
        store_name = getIntent().getStringExtra(Stores.STORE_NAME);
        store_lat = getIntent().getStringExtra(Stores.STORE_LAT);
        store_lng = getIntent().getStringExtra(Stores.STORE_LNG);

        storeDetail=new String[4];
        storeDetail[0]= store_id;
        storeDetail[1]= store_name;
        storeDetail[2]= store_lat;
        storeDetail[3]= store_lng;


        /**
         * set store name as title
         */
        storeTitle.setText(store_name);


        /**
         * get key value pair that will send in url to get products.
         */
        theMap = getKeyPairValueInHashMap();

        productList = (ListView) findViewById(R.id.store_product_list);


        /** open product detail page on click of listview */
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchActivity.openActivity(currentActivity, StoreOfferDetail.class, new String[]{Stores.STORE_ID,
                        Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG, FUNCARD_PRODUCT_ID, FUNCARD_PRODUCT_NAME, FUNCARD_PRODUCT_OFFERS}
                        , new String[]{store_id, store_name, store_lat, store_lng, storeoffersBeanClass[position].getProduct_id(),
                        storeoffersBeanClass[position].getProduct_name(), storeoffersBeanClass[position].getProduct_offers()});
            }
        });

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
        String value[] = {USER_ID, store_id};                //set values


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
    private void getStoreProductFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_STORE_PRODUCTS_URL);

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
                        StoreOffers.this.runOnUiThread(new Runnable() {
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


    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            StoreoffersBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Product");


            /**
             * initialize the beansClass array of object.
             */
            storeoffersBeanClass = new StoreoffersBeanClass[jsonArray.length()];


            allStoreProductsID = new String[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new StoreoffersBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String id = jsonObject.getString("pid");
                String name = jsonObject.getString("pname");
                String offers = jsonObject.getString("poffer");

                tempBean.setProduct_id(id);
                tempBean.setProduct_name(name);
                tempBean.setProduct_offers(offers);




                allStoreProductsID[i] = id;
                storeoffersBeanClass[i] = tempBean;


            }

            /**
             * set the values to layout after getting from json.
             */
            setListView();
        } catch (JSONException e) {
            StoreOffers.this.runOnUiThread(new Runnable() {
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

        final StoreProductsListAdapter catAdapter = new StoreProductsListAdapter(context, R.layout.storeoffers_list_row, allStoreProductsID, storeoffersBeanClass,storeDetail);
        StoreOffers.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productList.setAdapter(catAdapter);
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
        if (response.equals("") || storeoffersBeanClass==null) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get store product json from funcard server.
             */
            getStoreProductFromServerSetView();

        }else{
            setListView();
        }


    }


    /**
     * Reload particular categories if network problem occurs.
     *
     * @param view
     */
    public void reload(View view) {
        showListView();

        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         * Get Store offers json from funcard server.
         */

        getStoreProductFromServerSetView();
    }


    /**
     * show listview and hide error message
     */
    private void showListView() {
        reloadProblem.setVisibility(View.GONE);
        productList.setVisibility(View.VISIBLE);

    }

    /**
     * show problem messgae and hide error message
     */
    private void showProblem() {
        StoreOffers.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadProblem.setVisibility(View.VISIBLE);
                productList.setVisibility(View.GONE);
            }
        });

    }


    /**
     * go to Store page.
     * @param view
     */
    public void goToStorePage(View view){
        finish();
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

package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.CatAdvertisementBeanClass;
import app.funcarddeals.com.BeanClasses.StoresPageBeanClass;
import app.funcarddeals.com.CustomListAdapters.StoresPageListAdapter;
import app.funcarddeals.com.Manager.BannerThread;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class Stores extends Activity implements StandardMenus {



    public List<BannerThread> addThreadList = new ArrayList<BannerThread>();


    /**
     * banner image in front data variablse
     */
    private String adsStore_id, adsStore_name, idStore_lat, adsStore_long;
    static int sdk = android.os.Build.VERSION.SDK_INT;

    private int BannerInterval = 5 * 1000;   // 5 second of interval.

    /**
     * boolean value to stop banner ads when activity paused.
     */
    boolean stopBannerAds = false;

    /**
     * Funcard dvertisement url.
     */
    private static String FUNCARD_ADVERTISEMENT_URL;

    TextView cat_ads;
    private static ListView mStores;
    private MenuManager menuManager;
    private TextView categoryName;
    /**
     * list loading error layout
     */
    private LinearLayout networkError;


    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;


    /**
     * ads bean class variable that will hold bitmap too.
     */
    CatAdvertisementBeanClass[] catAdvertisementBeanClassWithBitmap;


    /**
     * variable for getting response.
     */
    private String response = "";


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * Variable gets value from previous activity.
     */
    private static String FUNCARD_CAT_ID_VALUE;
    private static String FUNCARD_CITY_ID_VALUE;
    private static String FUNCARD_CITY_NAME_VALUE;


    /**
     * Funcard all Stores  url.
     */
    private static String FUNCARD_PARTICULAR_CATEGORY_URL;


    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_CITY_ID_KEY = "funcard_city_id";
    private static String FUNCARD_CAT_ID_KEY = "funcard_category_id";


    Map<String, String> theMap;
    private Context context;
    CatAdvertisementBeanClass[] catAdvertisementBeanClass2;
    public static String STORE_ID = "store_id", STORE_NAME = "store_name", STORE_LAT = "store_latitude", STORE_LNG = "store_longitude";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        context = getApplicationContext();

        cat_ads = (TextView) findViewById(R.id.ads);
        networkError = (LinearLayout) findViewById(R.id.particular_cat_layout_error);
        mStores = (ListView) findViewById(R.id.storesList);
        categoryName = (TextView) findViewById(R.id.textView26);

        /**
         * current activity reference
         */
        currentActivity = Stores.this;


        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        /**
         * Get particular categories url from string resource.
         */

        FUNCARD_PARTICULAR_CATEGORY_URL = context.getResources().getString(R.string.funcrad_particular_category);
        FUNCARD_ADVERTISEMENT_URL = context.getResources().getString(R.string.stores_ads_url);


        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();

        /**
         * Get values from the previous activity send in putExtra.
         */
        FUNCARD_CAT_ID_VALUE = getIntent().getStringExtra(CategoryPage.KEY_CAT_ID);
        FUNCARD_CITY_ID_VALUE = getIntent().getStringExtra(MainPage.KEY_CITYID);
        FUNCARD_CITY_NAME_VALUE = getIntent().getStringExtra(CategoryPage.KEY_CAT_NAME);

        /** set name of category selected at list heading*/
        categoryName.setText(FUNCARD_CITY_NAME_VALUE);


        theMap = getKeyPairValueInHashMap();


        /**
         * open store page when list row clicked with all detail in put extras.
         */
        mStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switchActivity.openActivity(currentActivity, StorePage.class, new String[]{STORE_ID, STORE_NAME, STORE_LAT, STORE_LNG}
                        , new String[]{storesPageBeanClass[position].getStore_id(), storesPageBeanClass[position].getStore_name()
                        , storesPageBeanClass[position].getStore_lat(), storesPageBeanClass[position].getStore_lng()});
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
        String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_CAT_ID_KEY, FUNCARD_CITY_ID_KEY};      // set keys
        String value[] = {USER_ID, FUNCARD_CAT_ID_VALUE, FUNCARD_CITY_ID_VALUE};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * Get Particular categories json from funcard server.
     */
    private void getParticularCatFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_PARTICULAR_CATEGORY_URL);

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
                        Stores.this.runOnUiThread(new Runnable() {
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
    StoresPageBeanClass[] storesPageBeanClass;
    String[] allStoresID;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            StoresPageBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Stores");


            /**
             * initialize the beansClass array of object.
             */
            storesPageBeanClass = new StoresPageBeanClass[jsonArray.length()];


            allStoresID = new String[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new StoresPageBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                tempBean.setStore_id(id);                                           //store id
                tempBean.setStore_name(jsonObject.getString("name"));               //Set store name in beans object
                tempBean.setStore_offer(jsonObject.getString("offer"));             //Set store offers in beans object
                tempBean.setStore_lat(jsonObject.getString("lat"));                 //Set store latitude in beans object
                tempBean.setStore_lng(jsonObject.getString("lng"));                 //Set store longitude in beans object

            /*    System.out.println("FUNCARD_CAT_ID_VALUE " + FUNCARD_CAT_ID_VALUE);
                System.out.println("name " + jsonObject.getString("name"));
                System.out.println("offer " + jsonObject.getString("offer"));
                System.out.println("lat " + jsonObject.getString("lat"));
                System.out.println("lng " +jsonObject.getString("lng"));
                */
                allStoresID[i] = id;
                storesPageBeanClass[i] = tempBean;


            }

            /**
             * set the values to layout after getting from json.
             */
            setListView();
        } catch (JSONException e) {
            Stores.this.runOnUiThread(new Runnable() {
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
        final StoresPageListAdapter catAdapter = new StoresPageListAdapter(context, R.layout.stores_list_row, allStoresID, storesPageBeanClass, currentActivity);
        Stores.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStores.setAdapter(catAdapter);
                progressBarManager.stopProgressBar();
            }
        });

    }


    /**
     * Get cities ads json from funcard server.
     */


    private String adsReponse = "";

    private void getCatAdsFromServerSetView() {
        cat_ads.setVisibility(View.GONE);
        cat_ads.setText("");
        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */

                ServerSync serverSync = new ServerSync();

                adsReponse = serverSync.SyncServer(theMap, FUNCARD_ADVERTISEMENT_URL);

               // System.out.println("stores ads = "+ adsReponse);


                /**
                 * Check for error in response.
                 */
                if (adsReponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || adsReponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {

                    /**
                     * if there is a network problem while loading cities from server for the first time.
                     * them shows the network error below the listview on page.
                     */

                    Stores.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cat_ads.setVisibility(View.VISIBLE);
                            cat_ads.setText("Problem loading advertisement.");
                            cat_ads.setClickable(false);
                        }
                    });


                    progressBarManager.stopProgressBar();
                } else {

                    if (adsReponse.equals(ServerSync.FUNCARD_USER_DISABLED) || response.equals("funcarddeals_sorry")) {

                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);

                        progressBarManager.stopProgressBar();
                    } else if (adsReponse.equals(ServerSync.INVALID_FUNCARD_USER)) {



                        /** TODO  add a view to show server error.*/


                        progressBarManager.stopProgressBar();
                    } else {
                        //setCatAdsJsonToSession(reponse);
                        readadvertisementDataFromJasonAndSetInView(adsReponse);

                    }
                }
            }
        }.start();
    }


    private void readadvertisementDataFromJasonAndSetInView(String jsonData) {
        try {

            CatAdvertisementBeanClass tempBean2;
            JSONObject jsonRootObject2 = new JSONObject(jsonData);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray2 = jsonRootObject2.optJSONArray("Stores_ads");

            /**
             * initialize the beansClass array of object.
             */
            catAdvertisementBeanClass2 = new CatAdvertisementBeanClass[jsonArray2.length()];
            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray2.length(); i++) {
                tempBean2 = new CatAdvertisementBeanClass();
                JSONObject jsonObject = jsonArray2.getJSONObject(i);


                String image_url = jsonObject.getString("image");     //banner image.
                String store_id = jsonObject.getString("store_id");   //store id.
                String store_name = jsonObject.getString("store_name");   //store id.
                String store_lat = jsonObject.getString("store_lat");   //store id.
                String store_lang = jsonObject.getString("store_lang");   //store id.

               /* System.out.println("image_url "+image_url);
                System.out.println("store_id "+store_id);
                System.out.println("store_name "+store_name);
                System.out.println("store_lat "+store_lat);
                System.out.println("store_lang "+store_lang);
                System.out.println("======================================");
                */

                tempBean2.setImageURL(image_url);
                tempBean2.setStoreID(store_id);
                tempBean2.setStoreName(store_name);
                tempBean2.setStoreLat(store_lat);
                tempBean2.setStoreLong(store_lang);


                catAdvertisementBeanClass2[i] = tempBean2;
            }

            /**
             * get banners from url and set in view.
             */

            if (catAdvertisementBeanClass2.length > 0) {
                ServerSync sc = new ServerSync();
                catAdvertisementBeanClassWithBitmap = sc.getImageFromUrl(catAdvertisementBeanClass2,Stores.this);  /** here object holds bitmap also */
               // setImageBannersFromUrl(catAdvertisementBeanClassWithBitmap);
            }

        } catch (JSONException e) {
            Stores.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }
    }


    /**
     * method to get images from url.
     * images will be set in view.
     */

    ServerSync syncImages = new ServerSync();

    public void setImageBannersFromUrl(final BannerThread bThread) {

        if(syncImages.realTimeCatAdvertisement.size()>0){
            if (syncImages.realTimeCatAdvertisement.get(0).getBitmap() != null) {

                Stores.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        cat_ads.setVisibility(View.VISIBLE);
                        cat_ads.setClickable(true);
                    }
                });

            }


        }


        for (bThread.bannerIndex = 0; bThread.bannerIndex <= syncImages.realTimeCatAdvertisement.size();bThread.bannerIndex++) {

            /**
             * break the banner change loop if activity paused.
             */

            if (bThread.addStatus) {
                Stores.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        cat_ads.setVisibility(View.GONE);


                    }
                });

                bThread.bannerIndex = 0;

                break;
            } else {
                Stores.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        cat_ads.setVisibility(View.VISIBLE);


                    }
                });
            }

            if (bThread.bannerIndex == syncImages.realTimeCatAdvertisement.size()) {
                bThread.bannerIndex = 0;
            }


            Stores.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {



                    if (syncImages.realTimeCatAdvertisement.size()>0){
                        if (syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getBitmap() != null) {
                            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                cat_ads.setBackgroundDrawable(new BitmapDrawable(getResources(), syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getBitmap()));

                            }else{
                                cat_ads.setBackground(new BitmapDrawable(getResources(), syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getBitmap()));
                            }


                            adsStore_id = syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getStoreID();
                            adsStore_name = syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getStoreName();
                            idStore_lat = syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getStoreLat();
                            adsStore_long = syncImages.realTimeCatAdvertisement.get(bThread.bannerIndex).getStoreLong();
                        }

                    }






                }
            });


            try {
                Thread.sleep(BannerInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    public void openAdsStore(View view) {
        switchActivity.openActivity(currentActivity, StorePage.class, new String[]{STORE_ID, STORE_NAME, STORE_LAT, STORE_LNG}
                , new String[]{adsStore_id, adsStore_name, idStore_lat, adsStore_long});
    }


    @Override
    public void onPause() {
        super.onPause();

        /**
         * Call method to close all the thread.
         */
        removeAllThreads(addThreadList);

    }
    @Override
    public void onResume() {
        super.onResume();


        /**
         * Sync stores data from server only if the new instance of activity is created
         */
        if (response.equals("")) {

            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get stores data from funcard server.
             */

            getParticularCatFromServerSetView();

        }

        if (adsReponse.equals("")) {
            getCatAdsFromServerSetView();
        }else {

            if(catAdvertisementBeanClass2!=null){
                if (catAdvertisementBeanClass2.length > 0 && syncImages.realTimeCatAdvertisement.size() == catAdvertisementBeanClass2.length) {

                    new Thread() {
                        public void run() {
                            try {
                                removeAllThreads(addThreadList);
                                Thread.sleep(BannerInterval);


                                BannerThread bThread = new BannerThread();
                                bThread.addStatus = false;
                                addThreadList.add(bThread);


                                setImageBannersFromUrl(bThread);


                                /**
                                 * Call method to close all the thread expect one.
                                 */
                                // removeUnwantedThreads(addThreadList);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } else if (catAdvertisementBeanClass2.length > 0) {

                    syncImages.realTimeCatAdvertisement.clear();

                    new Thread() {
                        public void run() {
                            ServerSync sc = new ServerSync();
                            catAdvertisementBeanClassWithBitmap = sc.getImageFromUrl(catAdvertisementBeanClass2, Stores.this);  /** here object holds bitmap also */
                        }
                    }.start();
                }



            }






        }
    }



    /**
     * method to close all the thread.
     */
    public void removeAllThreads(List<BannerThread> addThreadList) {
        for (int i = 0; i < addThreadList.size(); i++) {

            addThreadList.get(i).addStatus = true;
            addThreadList.remove(i);

        }

    }


    /**
     * Reload particular categories page list if network problem occurs.
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
         * Get particular category json from funcard server.
         */

        getParticularCatFromServerSetView();
        getCatAdsFromServerSetView();
    }


    /**
     * show listview and hide error message
     */
    private void showListView() {
        networkError.setVisibility(View.GONE);
        mStores.setVisibility(View.VISIBLE);

    }

    /**
     * show problem message and hide error message
     */
    private void showProblem() {
        Stores.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkError.setVisibility(View.VISIBLE);
                mStores.setVisibility(View.GONE);
            }
        });

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

    @Override

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

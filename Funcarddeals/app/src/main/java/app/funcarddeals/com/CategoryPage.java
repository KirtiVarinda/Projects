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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.CatAdvertisementBeanClass;
import app.funcarddeals.com.BeanClasses.CategoryPageBeanClass;
import app.funcarddeals.com.CustomListAdapters.CategoryPageListAdapter;
import app.funcarddeals.com.Manager.BannerThread;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class CategoryPage extends Activity implements StandardMenus {


    public List<BannerThread> addThreadList = new ArrayList<BannerThread>();


    /**
     * banner image in front data variablse
     */
    private String adsStore_id, adsStore_name, idStore_lat, adsStore_long;
    static int sdk = android.os.Build.VERSION.SDK_INT;

    private int BannerInterval = 5 * 1000;   // 5 second of interval.
    public static String KEY_CAT_ID = "category_id";
    public static String KEY_CAT_NAME = "category_name";

    /**
     * Array of city id.
     */
    private String adsReponse = "";
    private String reponse = "";
    private String[] allCatID;
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_CITY_ID_KEY = "funcard_city_id";
    private static String FUNCARD_CITY_ID_VALUE;
    private MenuManager menuManager;

    private ProgressBarManager progressBarManager;
    private static ListView catList;
    private boolean syncat, synCatAds;
    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;

    /**
     * ads bean class variable that will hold bitmap too.
     */
    CatAdvertisementBeanClass[] catAdvertisementBeanClassWithBitmap;


    /**
     * list loading error layout
     */

    TextView cat_ads;
    LinearLayout reloadProblem;

    /**
     * variable for footer menus.
     */
    private Activity currentActivity;

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * Funcard categories url.
     */
    private static String FUNCARD_CATEGORY_URL;

    /**
     * Funcard advertisement url.
     */
    private static String FUNCARD_ADVERTISEMENT_URL;

    /**
     * Array of object that stores data for city.
     */
    CategoryPageBeanClass[] categoryPageBeanClass;
    CatAdvertisementBeanClass[] catAdvertisementBeanClass2;

    Map<String, String> theMap;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);
        context = getApplicationContext();
        syncat = true;
        synCatAds = true;

        cat_ads = (TextView) findViewById(R.id.ads);
        reloadProblem = (LinearLayout) findViewById(R.id.network_cat_layout_error);

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();

        /**
         * Get values from the previous activity send in putExtra.
         */
        FUNCARD_CITY_ID_VALUE = getIntent().getStringExtra(MainPage.KEY_CITYID);

        theMap = getKeyPairValueInHashMap();

        menuManager = new MenuManager(context);

        /**
         * current activity reference
         */
        currentActivity = CategoryPage.this;


        /**
         * gGet categories url from string resource.
         */

        FUNCARD_CATEGORY_URL = context.getResources().getString(R.string.funcrad_category);
        FUNCARD_ADVERTISEMENT_URL = context.getResources().getString(R.string.funcrad_category_advertisment);


        progressBarManager = new ProgressBarManager(currentActivity);


        catList = (ListView) findViewById(R.id.listView2);
        catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String cityName = categoryPageBeanClass[position].getCat_name();
                String selectCatId = parent.getItemAtPosition(position).toString();

                /**
                 * switch activity with three values in putExtra.
                 */
                switchActivity.openActivity(currentActivity, Stores.class, new String[]{KEY_CAT_ID, MainPage.KEY_CITYID, KEY_CAT_NAME}, new String[]{selectCatId, FUNCARD_CITY_ID_VALUE, cityName});


            }
        });

    }


    /**
     * Get cities ads json from funcard server.
     */

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



                /**
                 * Check for error in response.
                 */
                if (adsReponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || adsReponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {

                    /**
                     * if there is a network problem while loading cities from server for the first time.
                     * them shows the network error below the listview on page.
                     */

                    CategoryPage.this.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            cat_ads.setVisibility(View.VISIBLE);
                            cat_ads.setText("Problem loading advertisement.");
                            cat_ads.setClickable(false);
                        }
                    });


                    progressBarManager.stopProgressBar();
                } else {

                    if (adsReponse.equals(ServerSync.FUNCARD_USER_DISABLED) || reponse.equals("funcarddeals_sorry")) {

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

    /**
     * Get cities json from funcard server.
     */

    private void getCatFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */

                ServerSync serverSync = new ServerSync();

                reponse = serverSync.SyncServer(theMap, FUNCARD_CATEGORY_URL);

                /**
                 * Check for error in response.
                 */
                if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {

                    /**
                     * Shows the network error below the listview on page.
                     *
                     */


                    showProblem();


                    progressBarManager.stopProgressBar();
                } else {

                    if (reponse.equals(ServerSync.FUNCARD_USER_DISABLED) || reponse.equals("funcarddeals_sorry")) {

                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);

                        progressBarManager.stopProgressBar();
                    } else if (reponse.equals(ServerSync.INVALID_FUNCARD_USER)) {



                        /** TODO  add a view to show server error.*/


                        progressBarManager.stopProgressBar();
                    } else {
                        /** runs for categories*/
                        // setCatJsonToSession(reponse);
                        readDataFromJasonAndSetInView(reponse);


                    }


                }


            }
        }.start();
    }

    private Map<String, String> getKeyPairValueInHashMap() {
        /**
         * Get user id from session.
         */
        String USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_CITY_ID_KEY};      // set keys
        String value[] = {USER_ID, FUNCARD_CITY_ID_VALUE};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    private void readadvertisementDataFromJasonAndSetInView(String jsonData) {
        try {

            CatAdvertisementBeanClass tempBean2;
            JSONObject jsonRootObject2 = new JSONObject(jsonData);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray2 = jsonRootObject2.optJSONArray("Images");

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
                //  if (syncImages.realTimeCatAdvertisement.size() != catAdvertisementBeanClass2.length) {
                syncImages.realTimeCatAdvertisement.clear();
                ServerSync sc = new ServerSync();

                catAdvertisementBeanClassWithBitmap = sc.getImageFromUrl(catAdvertisementBeanClass2, CategoryPage.this);  /** here object holds bitmap also */

                //   } else {
                //     setImageBannersFromUrl();
                //   }

            }

        } catch (JSONException e) {
            System.out.println("exception now= " + e);
            CategoryPage.this.runOnUiThread(new Runnable() {
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

                CategoryPage.this.runOnUiThread(new Runnable() {
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
                CategoryPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        cat_ads.setVisibility(View.GONE);


                    }
                });

                bThread.bannerIndex = 0;

                break;
            } else {
                CategoryPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        cat_ads.setVisibility(View.VISIBLE);


                    }
                });
            }

            if (bThread.bannerIndex == syncImages.realTimeCatAdvertisement.size()) {
                bThread.bannerIndex = 0;
            }


            CategoryPage.this.runOnUiThread(new Runnable() {
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


    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            CategoryPageBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Category");


            /**
             * initialize the beansClass array of object.
             */
            categoryPageBeanClass = new CategoryPageBeanClass[jsonArray.length()];


            allCatID = new String[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new CategoryPageBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String id = jsonObject.getString("id");               //cat id
                String name = jsonObject.getString("name");          //cat name
                String offers = jsonObject.getString("count");       //cat offers

                tempBean.setCat_id(id);
                tempBean.setCat_name(name);
                tempBean.setCat_offers(offers);


                allCatID[i] = id;
                categoryPageBeanClass[i] = tempBean;


            }

            /**
             * set the values to layout after getting from json.
             */
            setListView();
        } catch (JSONException e) {
            CategoryPage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }


    }

    public static String STORE_ID = "store_id", STORE_NAME = "store_name", STORE_LAT = "store_latitude", STORE_LNG = "store_longitude";

    public void openAdsStore(View view) {
        switchActivity.openActivity(currentActivity, StorePage.class, new String[]{STORE_ID, STORE_NAME, STORE_LAT, STORE_LNG}
                , new String[]{adsStore_id, adsStore_name, idStore_lat, adsStore_long});
    }


    /**
     * set list view.
     */
    private void setListView() {
        final CategoryPageListAdapter catAdapter = new CategoryPageListAdapter(context, R.layout.categorypage_list_row, allCatID, categoryPageBeanClass);
        CategoryPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                catList.setAdapter(catAdapter);
                progressBarManager.stopProgressBar();
            }
        });

    }

/*
    @Override
    public void onStop() {
        super.onStop();

        stopBannerAds = true;

    }
*/

    @Override
    public void onDestroy() {
        super.onDestroy();


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
         * Sync category from server only if the new instance of activity is created
         */
        if (reponse.equals("")) {
            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get categories json from funcard server.
             */
            getCatFromServerSetView();


        }

        if (adsReponse.equals("")) {
            getCatAdsFromServerSetView();
        } else {

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
                            catAdvertisementBeanClassWithBitmap = sc.getImageFromUrl(catAdvertisementBeanClass2, CategoryPage.this);  /** here object holds bitmap also */
                        }
                    }.start();
                }



            }






        }


    }


    /**
     * method to close all the thread expect one.
     */
    public void removeUnwantedThreads(List<BannerThread> addThreadList) {
        for (int i = 0; i < addThreadList.size(); i++) {
            if (i != 0) {
                addThreadList.get(i).addStatus = true;
                addThreadList.remove(i);
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
         * Get particular category json from funcard server.
         */

        getCatFromServerSetView();
        getCatAdsFromServerSetView();
    }


    /**
     * show listview and hide error message
     */
    private void showListView() {
        reloadProblem.setVisibility(View.GONE);
        catList.setVisibility(View.VISIBLE);

    }

    /**
     * show problem messgae and hide error message
     */
    private void showProblem() {
        CategoryPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadProblem.setVisibility(View.VISIBLE);
                catList.setVisibility(View.GONE);
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

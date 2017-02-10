package com.lovelife.lovelife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanForRestaurantDetail;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.adapters.RestaurantsAdapterClass;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  RestaurantActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<String> allCities = new ArrayList<String>();

    public String selectedCity = "";
    Context context;
    Spinner spinner;

    public static BeanForRestaurantDetail[] beanForRestaurantDetail;


    /**
     * variable for activity switcher
     */

    SwitchActivities switchActivities;
    /**
     * Parameter String keys
     */
    private String CITIES = "city";
    // Spinner spinner;
    public static List<String> imageList;

    /**
     * String List to get All
     */
    ListView listView;

    /**
     * variable for reload Restaurants
     */

    private TextView reloadRestaurants;


    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;

    /**
     * current activity instance
     */
    private RestaurantActivity currentActivity;


    /**
     * Register user on Url
     */
    private String CITIES_URL, RESTAURANT_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);
        context = getApplicationContext();
        //  spinner = (Spinner) findViewById(R.id.spinner);
        // Get ListView object from xml


        /** Initialize reload variable */
        reloadRestaurants = (TextView) findViewById(R.id.reload);

        /** initialize list profilePicView */
        listView = (ListView) findViewById(R.id.list);
        /** initialize current ativity */
        currentActivity = RestaurantActivity.this;


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);

        /** set url for cities and restaurant */
        CITIES_URL = getResources().getString(R.string.restaurant_cities);
        RESTAURANT_URL = getResources().getString(R.string.restaurant_data);


        /** start loader */
        progressBarManager.startProgressBar();


        new Thread() {
            public void run() {
                try {
                    /**
                     * Make Connection and Pass URL to get response
                     */
                    getCities();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        /**
         * Initialize activity switcher
         */
        switchActivities = new SwitchActivities();

    }


    /**
     * get all cities from API
     *
     * @throws JSONException
     */
    public void getCities() throws JSONException {

        /** Get Cities From Restaurant Server */
        ServerSync serverSync = new ServerSync();
        String response = serverSync.SyncServer(CITIES_URL);


        if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */
            showError();
        /*    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });
*/

        } else {


            /**  Get all the cities in arrayString  */
            allCities = ParseJsonData.parseRestaurantCities(response);


            /** reset the option menus */
            invalidateOptionsMenu();


        }


    }

    /**
     * Pop for Restaurant activity.
     *
     * @param message
     * @param titleText
     */
    private void popup(final String message, final String titleText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                /** Stop progress bar */
                LoveLifePopups.loveLifePopup(RestaurantActivity.this, titleText, message);
            }
        });


    }


    /**
     * For getting Restaurant Details
     * according to city seleted in spinner.
     */
    private void getRestaurants() {

        /** set parameter as selected city to get restaurant */
        final Map<String, String> postData = getKeyPairValueInHashMap(selectedCity);


        /** get restaurant details from API */
        ServerSync serverSync = new ServerSync();
        String getParam = "";
        try {
            getParam = serverSync.getQueryGet(postData);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String response = serverSync.SyncServer(RESTAURANT_URL + getParam);
        System.out.println("");
        if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */
            showError();
        /*    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });*/


        } else {


            /** parse restaurent data and get in array of bean */
            beanForRestaurantDetail = ParseJsonData.parseRestaurantData(response);
            String restIDs[] = new String[beanForRestaurantDetail.length];
            for (int i = 0; i < beanForRestaurantDetail.length; i++) {
                restIDs[i] = beanForRestaurantDetail[i].getRestaurantID();
            }

            /** set restaurant list */
            setRestaurantsList(restIDs, beanForRestaurantDetail);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** Stop progress bar */
                    progressBarManager.stopProgressBar();

                }
            });


        }


        /**  JSONObject jObj = null;

         try {
         if (response != null) {
         jObj = new JSONObject(response);
         multiToLinearJsonForRestaurant(jObj);
         } else {

         }
         } catch (JSONException e) {
         e.printStackTrace();
         }

         */


    }


    /**
     * method that show error message on network problem
     */
    void showError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                reloadRestaurants.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        });

    }

    /**
     * method that shows on reload on restaurants
     */
    void showList() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadRestaurants.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

    }


    /**
     * reload restaurents when cilcked reload
     *
     * @param view
     */
    public void reload(View view) {
        showList();
        new Thread() {
            public void run() {
                try {
                    /**
                     * Make Connection and Pass URL to get response
                     */
                    getCities();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }


    private Map<String, String> getKeyPairValueInHashMap(String cities) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {CITIES};      // set keys
        String value[] = {cities};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);


        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu, menu);


        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, allCities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String items = spinner.getSelectedItem().toString();

                if (!selectedCity.equals(items)) {

                    /** start loader */
                    progressBarManager.startProgressBar();

                    selectedCity = items;
                    new Thread() {
                        public void run() {
                            /**
                             * Make Connection and Pass URL to get response
                             */
                            getRestaurants();

                        }
                    }.start();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //no action
            }
        });


        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * method to set the restaurent list adapter.
     *
     * @param restIDs
     * @param bean
     */
    public void setRestaurantsList(String[] restIDs, BeanForRestaurantDetail[] bean) {
        final RestaurantsAdapterClass adapter = new RestaurantsAdapterClass(getApplicationContext(), restIDs, bean, currentActivity);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });


        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switchActivities.openActivityWithPutExtra(currentActivity, SingleRestaurentActivity.class, new String[]{"index"}, new String[]{position + ""});

            }

        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation profilePicView item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

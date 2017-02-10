package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.StoresPageBeanClass;
import app.funcarddeals.com.BeanClasses.UserProfileBeanClass;
import app.funcarddeals.com.CustomListAdapters.MainPageListAdapter;
import app.funcarddeals.com.CustomListAdapters.UserProfileListAdapter;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;

public class UserProfile extends Activity implements StandardMenus{


    private MenuManager menuManager;

    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * Funcard user data url.
     */
    private static String FUNCARD_USER_DATA_URL;



    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for getting response.
     */
    private String response = "";


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";

    /**
     * variable to show error when network problem
     */
    LinearLayout reloadProblem;


    Map<String, String> theMap;
    private Context context;

    private ListView userCards;
    private TextView userName,userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        context = getApplicationContext();
        userCards=(ListView)findViewById(R.id.userList);
        userName=(TextView)findViewById(R.id.editText4);
        userEmail=(TextView)findViewById(R.id.editText5);
        reloadProblem = (LinearLayout) findViewById(R.id.user_layout_error);

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * current activity reference
         */
        currentActivity = UserProfile.this;


        /**
         * Get particular categories url from string resource.
         */
        FUNCARD_USER_DATA_URL = context.getResources().getString(R.string.user_profile);


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * get user name and email of user from session and set in view.
         */

        userName.setText(mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_NAME));
        userEmail.setText(mySharedData.getGeneralSaveSession(MySharedData.LOGIN_USER_EMAIL));

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
        String key[] = {FUNCRAD_USER_ID_KEY};      // set keys
        String value[] = {USER_ID};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }




    /**
     * Get user data json from funcard server.
     */
    private void getUserDataFromServerSetView() {

        new Thread() {
            public void run() {
                theMap = getKeyPairValueInHashMap();
                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_USER_DATA_URL);

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
                        UserProfile.this.runOnUiThread(new Runnable() {
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
    UserProfileBeanClass[] userProfileBeanClass;
    String[] allCardCodes;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            UserProfileBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Details");


            /**
             * initialize the beansClass array of object.
             */
            userProfileBeanClass = new UserProfileBeanClass[jsonArray.length()];


            allCardCodes = new String[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new UserProfileBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String code = jsonObject.getString("code");
                tempBean.setUp_code(code);
                tempBean.setUp_city(jsonObject.getString("city"));
                tempBean.setUp_status(jsonObject.getString("active"));


                allCardCodes[i] = code;
                userProfileBeanClass[i] = tempBean;

            }

            /**
             * set the values to layout after getting from json.
             */
            progressBarManager.stopProgressBar();
            setListView();
        } catch (JSONException e) {
            UserProfile.this.runOnUiThread(new Runnable() {
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
        final UserProfileListAdapter userAdapter = new UserProfileListAdapter(context, R.layout.userprofile_row, allCardCodes, userProfileBeanClass);
        UserProfile.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userCards.setAdapter(userAdapter);
                progressBarManager.stopProgressBar();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();


        /**
         * Syncuser data from server only if the new instance of activity is created
         */
        if (response.equals("")) {

            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get stores data from funcard server.
             */

            getUserDataFromServerSetView();
        }
    }



    /**
     * show listview and hide error message
     */
    private void showListView() {
        reloadProblem.setVisibility(View.GONE);
        userCards.setVisibility(View.VISIBLE);

    }

    /**
     * show problem messgae and hide error message
     */
    private void showProblem() {
        UserProfile.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadProblem.setVisibility(View.VISIBLE);
                userCards.setVisibility(View.GONE);
            }
        });

    }

    /**
     * Reload user data if network problem occurs.
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
         * Get user data json from funcard server.
         */

        getUserDataFromServerSetView();
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


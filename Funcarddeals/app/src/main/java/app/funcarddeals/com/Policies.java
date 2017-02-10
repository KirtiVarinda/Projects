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

import java.util.List;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.PolicyBeanClass;
import app.funcarddeals.com.BeanClasses.UserProfileBeanClass;
import app.funcarddeals.com.CustomListAdapters.PolicyPageListAdapter;
import app.funcarddeals.com.CustomListAdapters.UserProfileListAdapter;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;

public class Policies extends Activity {


    private String title = "";

    private String NO_POLICY = "funcarddeals_empty";

    private MenuManager menuManager;

    Map<String, String> theMap;

    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;

    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;

    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;

    /**
     * Funcard policy url.
     */
    private static String FUNCARD_POLICY_URL;


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * variable for getting response.
     */
    private String response = "";

    private ListView policyList;
    private Context context;
    private TextView titleText;

    /**
     * variable to show error when network problem
     */
    LinearLayout reloadProblem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policies);
        context = getApplicationContext();
        policyList = (ListView) findViewById(R.id.policyList);
        titleText = (TextView) findViewById(R.id.textView45);
        reloadProblem = (LinearLayout) findViewById(R.id.policy_layout_error);
        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        /**
         * current activity reference
         */
        currentActivity = Policies.this;


        progressBarManager = new ProgressBarManager(currentActivity);


        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * Get particular categories url from string resource.
         */
        FUNCARD_POLICY_URL = context.getResources().getString(R.string.policies);


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

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
        String key[] = {};      // set keys
        String value[] = {};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * Get policies json from funcard server.
     */
    private void getPoliciesFromServerSetView() {

        new Thread() {
            public void run() {
                theMap = getKeyPairValueInHashMap();
                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_POLICY_URL);

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
                        Policies.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });

                        progressBarManager.stopProgressBar();

                    } else if (response.equals(NO_POLICY)) {

                        titleText.setText("There is no Privacy Policy.");

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
    PolicyBeanClass[] policyBeanClass;
    String[] allCardCodes;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            PolicyBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Policy");


            /**
             * initialize the beansClass array of object.
             */
            policyBeanClass = new PolicyBeanClass[jsonArray.length() - 1];


            allCardCodes = new String[jsonArray.length() - 1];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new PolicyBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (i == 0) {
                    title = jsonObject.getString("title");
                } else {
                    allCardCodes[i - 1] = i + "";
                    tempBean.setPolicy_detail(jsonObject.getString("policy"));
                    policyBeanClass[i - 1] = tempBean;
                }

            }

            /**
             * set the values to layout after getting from json.
             */
            progressBarManager.stopProgressBar();
            setListView();
        } catch (JSONException e) {
            Policies.this.runOnUiThread(new Runnable() {
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
        final PolicyPageListAdapter policyAdapter = new PolicyPageListAdapter(context, R.layout.policy_row, allCardCodes, policyBeanClass);
        Policies.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleText.setText(title);
                policyList.setAdapter(policyAdapter);
                progressBarManager.stopProgressBar();
            }
        });
    }


    /**
     * Reload policy if network problem occurs.
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
         * Get policy json from funcard server.
         */

        getPoliciesFromServerSetView();
    }


    /**
     * show listview and hide error message
     */
    private void showListView() {
        reloadProblem.setVisibility(View.GONE);
        policyList.setVisibility(View.VISIBLE);

    }

    /**
     * show problem messgae and hide error message
     */
    private void showProblem() {
        Policies.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadProblem.setVisibility(View.VISIBLE);
                policyList.setVisibility(View.GONE);
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

            getPoliciesFromServerSetView();
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


}

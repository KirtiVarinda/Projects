package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClass;
import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClassForUserInfo;
import app.funcarddeals.com.BeanClasses.StoreoffersBeanClass;
import app.funcarddeals.com.Manager.MathFunctions;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.Validator.FormValidation;
import app.funcarddeals.com.network.ServerSync;

public class BuyFuncard extends Activity implements StandardMenus {


    public static String PAYWITHPAYPAL = "paypal";
    public static String PAYWITHCREDITCARD = "creditcard";

    /**
     * city spinner
     */
    private Spinner citySpinner;


    /**
     * variable for getting response.
     */
    private String response = "";

    /**
     * Funcard all Stores  url.
     */
    private static String FUNCARD_BUY_FUNCRAD_URL1;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;

    private MenuManager menuManager;
    private Context context;
    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * Variable for post key of url
     */
    private static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    private static String FUNCARD_DEVICE_KEY = "funcard_device";

    /**
     * Initialize progress bar.
     */
    private ProgressBarManager progressBarManager;


    Map<String, String> theMap;

    EditText quantity, total;


    /**
     * bean class referrnce
     */
    BuyFuncardBeanClassForUserInfo buyFuncardBeanClassForUserInfo;

    private String selectedCityID;

    private LinearLayout reloadProblem;
    private ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_funcard);
        citySpinner = (Spinner) findViewById(R.id.spinner);
        quantity = (EditText) findViewById(R.id.quantity);
        total = (EditText) findViewById(R.id.editText6);

        reloadProblem = (LinearLayout) findViewById(R.id.buy_error);
        scrollview = (ScrollView) findViewById(R.id.scrollview);


        buyFuncardBeanClassForUserInfo = new BuyFuncardBeanClassForUserInfo();




        /**
         * clear selected city if no of card entered changed.
         */
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                citySpinner.setSelection(0);
                total.setText(0.00 + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (!buyFuncardBeanClass[position].getCityPrice().equals("no_price")) {

                    String cardValue = quantity.getText().toString();
                    if (!FormValidation.removeWhiteSpaces(cardValue).equals("")) {
                        int cardEntered = Integer.parseInt(cardValue);                                         // get entered card value in integer.
                        float noOfCardsFromServer = Float.parseFloat(buyFuncardBeanClass[position].getCardQuantity());   // get received card value in integer.

                        /**
                         *check if number of cards entered are greater than present on server.
                         * if greater than give popup to check.
                         * also check the entered card should not be more than 10
                         */
                        if ((cardEntered > 0 && cardEntered <= 10)) {

                            if (cardEntered < noOfCardsFromServer) {
                                float price = Float.parseFloat(buyFuncardBeanClass[position].getCityPrice());
                                float price1 = (float) price * cardEntered;

                                /**
                                 * selected city id
                                 */
                                selectedCityID = buyFuncardBeanClass[position].getCityID();

                                /** round off the fraction to two decimal places */
                                BigDecimal decimal2 = MathFunctions.round(price1, 2);
                                total.setText(decimal2 + "");


                            } else {
                                FuncardPopups.funcardPopup(currentActivity, getResources().getString(R.string.pop_title),
                                        getResources().getString(R.string.card_empty) + " and Should not be more than " + noOfCardsFromServer + " for city " + buyFuncardBeanClass[position].getCityName());
                                citySpinner.setSelection(0);
                            }


                        } else {
                            FuncardPopups.funcardPopup(currentActivity, getResources().getString(R.string.pop_title),
                                    getResources().getString(R.string.validCardEntered));
                            citySpinner.setSelection(0);
                        }


                    } else {
                        /** TODO validation popup. */
                        FuncardPopups.funcardPopup(currentActivity, getResources().getString(R.string.pop_title), getResources().getString(R.string.card_empty));
                        citySpinner.setSelection(0);
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * current activity reference
         */
        currentActivity = BuyFuncard.this;
        context = getApplicationContext();

        /**
         * Get particular categories url from string resource.
         */

        FUNCARD_BUY_FUNCRAD_URL1 = context.getResources().getString(R.string.buy_url1);


        progressBarManager = new ProgressBarManager(currentActivity);


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
        String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_DEVICE_KEY};      // set keys
        String value[] = {USER_ID, "android"};                            //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);

    }


    /**
     * Get buy funcard json from funcard server.
     */
    private void getBuyFuncardDataFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * Authenticate user from funcard deals server.
                 */
                theMap = getKeyPairValueInHashMap();
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_BUY_FUNCRAD_URL1);

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
                        BuyFuncard.this.runOnUiThread(new Runnable() {
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
     * Read json data from response and set in view.
     *
     * @param jsonData
     */

    /**
     * Array of object that stores data for buy funcard.
     */
    BuyFuncardBeanClass[] buyFuncardBeanClass;
    String[] allCities;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            BuyFuncardBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("CardInfo");


            /**
             * initialize the beansClass array of object.
             */
            buyFuncardBeanClass = new BuyFuncardBeanClass[jsonArray.length() + 1];
            allCities = new String[jsonArray.length() + 1];

            allCities[0] = "Select City";
            tempBean = new BuyFuncardBeanClass();
            tempBean.setCityID("no_id");
            tempBean.setCityName("no_name");
            tempBean.setCityPrice("no_price");
            tempBean.setCardQuantity("no_quantity");

            buyFuncardBeanClass[0] = tempBean;

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new BuyFuncardBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("city_id");
                String name = jsonObject.getString("name");
                String price = jsonObject.getString("price");
                String quanitity = jsonObject.getString("quanitity");


                tempBean.setCityID(id);
                tempBean.setCityName(name);
                tempBean.setCityPrice(price);
                tempBean.setCardQuantity(quanitity);

                buyFuncardBeanClass[i + 1] = tempBean;
                allCities[i + 1] = name;
            }

            /**
             * set values in view
             * i.e spinner
             */
            setView();

        } catch (JSONException e) {
            BuyFuncard.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }
    }


    private void setView() {

        if(allCities==null){
            allCities = new String[1];
            allCities[0] = "Select City";
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(BuyFuncard.this, android.R.layout.simple_spinner_item, allCities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        BuyFuncard.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                citySpinner.setAdapter(adapter);
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
            getBuyFuncardDataFromServerSetView();

        }

    }


    /**
     * method run but "pay with paypal clicked".
     *
     * @param view
     */
    private String key = "buyFuncardBeanClassForUserInfo";

    public void payWithPayPAl(View view) {

        pay(PAYWITHPAYPAL);


    }

    /**
     * pay with credit card.
     *
     * @param view
     */
    public void payWithCreditCard(View view) {
        pay(PAYWITHCREDITCARD);
    }


    private void pay(String payWith) {
        if (citySpinner.getSelectedItem().toString().equals("Select City")) {
            FuncardPopups.funcardPopup(currentActivity, getResources().getString(R.string.pop_title),
                    getResources().getString(R.string.buy_city));
        } else {

            /**
             * set all the values from form to bean class
             * and travel this bean class object to billing address activity.
             */
            buyFuncardBeanClassForUserInfo.setCardQuantity(quantity.getText().toString());
            buyFuncardBeanClassForUserInfo.setTotalPrice(total.getText().toString());
            buyFuncardBeanClassForUserInfo.setSelectedCity(citySpinner.getSelectedItem().toString());
            buyFuncardBeanClassForUserInfo.setSelectedCityID(selectedCityID);
            buyFuncardBeanClassForUserInfo.setPayWith(payWith);

            switchActivity.openActivityWithPassBeanClassObject(currentActivity, BuyFuncardBillingAddress.class, key, buyFuncardBeanClassForUserInfo);
        }
    }


    /**
     * reset view.
     *
     * @param view
     */
    public void reset(View view) {

        quantity.setText("");
        citySpinner.setSelection(0);
        total.setText("0.00");


    }


    /**
     * go to add funcard page
     *
     * @param view
     */
    public void goToAddFuncard(View view) {
        switchActivity.openActivity(currentActivity, AddFunCrad.class);
    }


    /**
     * show scrollview and hide error message
     */
    private void showScrollViewView() {
        reloadProblem.setVisibility(View.GONE);
        scrollview.setVisibility(View.VISIBLE);

    }

    /**
     * show problem messgae and hide scrollview.
     */
    private void showProblem() {
        BuyFuncard.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadProblem.setVisibility(View.VISIBLE);
                scrollview.setVisibility(View.GONE);
            }
        });

    }


    /**
     * Reload scrollview if network problem occurs.
     *
     * @param view
     */
    public void reload(View view) {
        showScrollViewView();

        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         * Get buy card json from funcard server.
         */

        getBuyFuncardDataFromServerSetView();
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

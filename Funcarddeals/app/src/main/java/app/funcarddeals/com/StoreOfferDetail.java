package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.StoreOfferDetailBeanClass;
import app.funcarddeals.com.BeanClasses.StoreoffersBeanClass;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.FuncardToast;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class StoreOfferDetail extends Activity implements StandardMenus {


    private FunCardDealsDatabase funDB;
    private String product_time = "0";
    private String REMINDER__ADDED = "Added to Reminders";
    private String REMINDER_ALREADY_ADDED = "Already in Reminders";

    private String FAVOURITE__ADDED = "Added to Favorites";
    private String FAVOURITE_ALREADY_ADDED = "Already in Favorites";


    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;
    static int sdk = android.os.Build.VERSION.SDK_INT;

    /**
     * string to hold instructions.
     */
    private String insData = "";


    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    /**
     * list loading error layout
     */
    private LinearLayout networkError;
    private RelativeLayout imgArea;

    /**
     * variable for managing menus.
     */
    private MenuManager menuManager;


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    /**
     * progress bar manager.
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;


    /**
     * variable for getting response from server.
     */
    private String response = "";


    /**
     * Funcard store product  url.
     */
    private static String FUNCARD_STORE_PRODUCTS_DETAIL_URL, FUNCARD_REDEEM_URL;


    /**
     * variables to get values from previous activity that are send with intent
     */
    String store_id, store_name, store_lat, store_lng, product_id, product_name, product_offers;


    /**
     * Variable for post key of url
     */
    static String FUNCRAD_USER_ID_KEY = "funcard_user_id";
    static String FUNCARD_PRODUCT_ID_KEY = "funcard_product_id";
    static String FUNCRAD_CITY_ID_KEY = "funcard_city_id";

    static String FUNCRAD_USER_ID = "user_id";
    static String FUNCARD_PRODUCT_ID = "product_id";
    static String FUNCRAD_CITY_ID = "city_id";
    static String FUNCRAD_STORE_ID = "store_id";
    static String FUNCARD_PRODUCT_VALIDITY = "product_validity";
    static String FUNCARD_PRODUCT_OFFER = "product_offer";
    Context context;

    Map<String, String> theMap;

    TextView titleStoreName, productName, productPrice, productImage, validity, instructionText, clickMessage;
    ScrollView instructionScroll;
    String USER_ID;
    String city_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_offer_detail);
        context = getApplicationContext();
        titleStoreName = (TextView) findViewById(R.id.textView15);
        productName = (TextView) findViewById(R.id.textView16);
        productPrice = (TextView) findViewById(R.id.textView33);
        productImage = (TextView) findViewById(R.id.imageView14);
        networkError = (LinearLayout) findViewById(R.id.product_detail_layout_error);
        imgArea = (RelativeLayout) findViewById(R.id.imgArea);
        validity = (TextView) findViewById(R.id.textView34);
        instructionText = (TextView) findViewById(R.id.textView36);
        instructionScroll = (ScrollView) findViewById(R.id.scrollview);
        clickMessage = (TextView) findViewById(R.id.textView35);


        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        funDB = new FunCardDealsDatabase(context);


        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForLogin(context);


        /**
         * Get user id from session.
         */
        USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);


        /**
         * get city id from session
         */
        city_id = mySharedData.getGeneralSaveSession(MySharedData.FUNCARD_CITY_ID);


        /**
         * current activity reference
         */
        currentActivity = StoreOfferDetail.this;

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /** initialize progress bar.*/
        progressBarManager = new ProgressBarManager(currentActivity);


        /**
         * Get particular categories url from string resource.
         */
        FUNCARD_STORE_PRODUCTS_DETAIL_URL = context.getResources().getString(R.string.funcrad_store_products_detail);

        /** redeem url */
        FUNCARD_REDEEM_URL = context.getResources().getString(R.string.redeem_check);

        /**
         * Get values from the previous activity send in putExtra.
         */
        store_id = getIntent().getStringExtra(Stores.STORE_ID);
        store_name = getIntent().getStringExtra(Stores.STORE_NAME);
        store_lat = getIntent().getStringExtra(Stores.STORE_LAT);
        store_lng = getIntent().getStringExtra(Stores.STORE_LNG);
        product_id = getIntent().getStringExtra(StoreOffers.FUNCARD_PRODUCT_ID);
        product_name = getIntent().getStringExtra(StoreOffers.FUNCARD_PRODUCT_NAME);
        product_offers = getIntent().getStringExtra(StoreOffers.FUNCARD_PRODUCT_OFFERS);


        /** set title as store name */
        titleStoreName.setText(store_name);
        /** set product name */
        productName.setText(product_name);


    }


    /**
     * get key value pairs to send in url using post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(boolean isRedeem) {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        if (isRedeem) {


            String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_PRODUCT_ID_KEY, FUNCRAD_CITY_ID_KEY};      // set keys
            String value[] = {USER_ID, product_id, city_id};

            /**
             * Get key value pairs .
             * KeyValues will be in hash map.
             */
            PairValues pairValues = new PairValues();
            return pairValues.funcardPairValue(key, value);
        } else {
            String key[] = {FUNCRAD_USER_ID_KEY, FUNCARD_PRODUCT_ID_KEY};      // set keys
            String value[] = {USER_ID, product_id};
            /**
             * Get key value pairs .
             * KeyValues will be in hash map.
             */
            PairValues pairValues = new PairValues();
            return pairValues.funcardPairValue(key, value);
        }
        //set values


    }


    /**
     * Get store products detail json from funcard server.
     */
    private void getStoreProductDetailFromServerSetView() {

        new Thread() {
            public void run() {
                /**
                 * get key value pair that will send in url to get products.
                 * not for redeem.
                 */
                theMap = getKeyPairValueInHashMap(false);

                /**
                 * get product detail json from funcard server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_STORE_PRODUCTS_DETAIL_URL);


                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {


                    /**
                     * if there is a network problem while loading store products detail from server
                     * them shows the network error on page.
                     */

                    showProblem();
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED)) {
                        errorLoadingImage("User disable from server.");
                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);
                        progressBarManager.stopProgressBar();

                    } else if (response.equals(ServerSync.INVALID_FUNCARD_USER)) {

                        errorLoadingImage("Server problem.");
                        /**
                         * server error toast.
                         */
                        StoreOfferDetail.this.runOnUiThread(new Runnable() {
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

    StoreOfferDetailBeanClass storeOfferDetailBeanClass;

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {


            JSONObject jsonRootObject = new JSONObject(jsonData);

            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("Product");


            /**
             * initialize the beansClass array of object.
             */
            storeOfferDetailBeanClass = new StoreOfferDetailBeanClass();


            JSONObject jsonObject = jsonArray.getJSONObject(0);


            String id = jsonObject.getString("id");
            String offer = jsonObject.getString("offer");
            String price = jsonObject.getString("price");
            String valid = jsonObject.getString("valid");
            String image = jsonObject.getString("image");
            String text = jsonObject.getString("text");
            String instructions_count = jsonObject.getString("instructions_count");

            int ins_count = Integer.parseInt(instructions_count);
            for (int i = 0; i < ins_count; i++) {
                String instruction = jsonObject.getString("instruction_" + i);
                int j = i + 1;
                insData = insData + j + ". " + instruction + "\n\n";
            }

            storeOfferDetailBeanClass.setProduct_id(id);
            storeOfferDetailBeanClass.setProduct_offer(offer);
            storeOfferDetailBeanClass.setProduct_price(price);
            storeOfferDetailBeanClass.setProduct_validity(valid);
            storeOfferDetailBeanClass.setProduct_image(image);
            storeOfferDetailBeanClass.setProduct_text(text);
            storeOfferDetailBeanClass.setProduct_instruction_count(instructions_count);


            /**
             * set the values to layout after getting from json.
             */

            setImage(image);
            setView();
        } catch (JSONException e) {
            StoreOfferDetail.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }


    }

    /**
     * Get store products detail json from funcard server.
     */
    private void redeemCard() {

        new Thread() {
            public void run() {
                /**
                 * get key value pair that will send in url to get products.
                 * not for redeem.
                 */
                theMap = getKeyPairValueInHashMap(true);

                /**
                 * get product detail json from funcard server.
                 */
                ServerSync serverSync = new ServerSync();
                response = serverSync.SyncServer(theMap, FUNCARD_REDEEM_URL);


                /**
                 * Check for error in response.
                 */
                if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {
                    FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.networkError);
                    progressBarManager.stopProgressBar();


                } else {

                    if (response.equals(ServerSync.FUNCARD_USER_DISABLED)) {

                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);


                    } else if (response.equals(ServerSync.FUNCARD_REDEEM_SORRY)) {
                        /**
                         * Need to purchase funcard.
                         */
                        String title = getResources().getString(R.string.redeem_purchase_card);
//                        FuncardPopups.funcardPopupWithTwoActions(currentActivity, MainPage.class, "Sorry!", title, "Yes", "No Thanks", new String[]{FUNCRAD_STORE_ID,
//                                FUNCRAD_USER_ID, FUNCARD_PRODUCT_ID, FUNCRAD_CITY_ID}
//                                , new String[]{store_id, USER_ID, product_id, city_id}); /** make Mainpage to inapppurchase */

                        FuncardPopups.funcardPopup(currentActivity, "Sorry!", title);

                    } else if (response.equals(ServerSync.FUNCARD_REDEEM_EMPTY)) {
                        /**
                         * offer already redeemed.
                         */
                        String title = getResources().getString(R.string.card_already_redeemed);
                        FuncardPopups.funcardPopupWithTwoActions(currentActivity, StoreOffers.class, "Sorry!", title, "OK", "Try Offers", new String[]{FUNCRAD_STORE_ID,
                                        FUNCRAD_USER_ID, FUNCARD_PRODUCT_ID, FUNCRAD_CITY_ID}
                                , new String[]{store_id, USER_ID, product_id, city_id}); /** make Mainpage to inapppurchase */


                    } else if (response.equals(ServerSync.FUNCARD_REDEEM_YES)) {
                        /**
                         * Redeem available.
                         */
                        String title = getResources().getString(R.string.redeem_success);
                        FuncardPopups.funcardPopupWithTwoActions(currentActivity, RedeemActivity.class, "Congratulations!", title, "Yes", "No", new String[]{FUNCRAD_STORE_ID,
                                        FUNCRAD_USER_ID, FUNCARD_PRODUCT_ID, FUNCRAD_CITY_ID, FUNCARD_PRODUCT_VALIDITY, FUNCARD_PRODUCT_OFFER}
                                , new String[]{store_id, USER_ID, product_id, city_id, storeOfferDetailBeanClass.getProduct_validity(), storeOfferDetailBeanClass.getProduct_offer()});


                    } else {
                        String[] getRedeemTime = response.split("-");


                        if (getRedeemTime[0].equals(ServerSync.FUNCARD_REDEEM_TIME_BETWEEN)) {
                            FuncardPopups.funcardPopup(currentActivity, "Sorry!", "This product is available from " + getRedeemTime[1] + " to " + getRedeemTime[2] + " period of time");

                        } else if (getRedeemTime[0].equals(ServerSync.FUNCARD_REDEEM_TIME_FROM)) {

                            FuncardPopups.funcardPopup(currentActivity, "Sorry!", "This product will be redeemed after " + getRedeemTime[1] + "  minutes");


                        }


                    }

                    progressBarManager.stopProgressBar();
                }
            }
        }.start();
    }


    private void setImage(final String imageUrl) {
        new Thread() {
            public void run() {
                ServerSync serverSync = new ServerSync();
                final Bitmap bmp = serverSync.fireUrlGetBitmap(imageUrl);

                if (bmp != null) {
                    StoreOfferDetail.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productImage.setText("");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                productImage.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));

                            } else {
                                productImage.setBackground(new BitmapDrawable(getResources(), bmp));

                            }


                        }
                    });

                } else {
                    productImage.setText("Problem loading image. Click to refresh.");
                }


            }
        }.start();

    }

    private void setView() {
        StoreOfferDetail.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productPrice.setText("$" + storeOfferDetailBeanClass.getProduct_price() + " Value");
                validity.setText(Html.fromHtml("THIS DISCOUNT IS VALID TILL <b>" + storeOfferDetailBeanClass.getProduct_validity() + "</b>"));
                progressBarManager.stopProgressBar();

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();


        /**
         * Sync store product detail from server only if the new instance of activity is created.
         */
        if (response.equals("")) {


            /**
             * start progress bar.
             */
            progressBarManager.startProgressBar();
            /**
             * Get store product detail json from funcard server.
             */
            getStoreProductDetailFromServerSetView();

        }


    }


    /**
     * show listview and hide error message
     */
    private void showProductImage() {
        networkError.setVisibility(View.GONE);
        imgArea.setVisibility(View.VISIBLE);

    }


    /**
     * show problem message and hide error message
     */
    private void showProblem() {
        StoreOfferDetail.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkError.setVisibility(View.VISIBLE);
                imgArea.setVisibility(View.GONE);
            }
        });

    }


    String showInstruction = "no";

    /**
     * method to show Instructions.
     */
    public void showInstructions(View view) {

        if (showInstruction.equals("no")) {
            showProductInstrunction();
        } else {
            showProduct();
        }

    }


    private void showProductInstrunction() {
        instructionScroll.setVisibility(View.VISIBLE);
        imgArea.setVisibility(View.GONE);
        showInstruction = "yes";
        clickMessage.setText("Click here to go back to offer.");
        instructionText.setText(insData);
        productName.setText("INSTRUCTIONS");
    }


    private void showProduct() {
        instructionScroll.setVisibility(View.GONE);
        imgArea.setVisibility(View.VISIBLE);
        showInstruction = "no";
        clickMessage.setText("Click here for more Instructions.");
        productName.setText(product_name);
    }


    /**
     * Reload product detail page if network problem occurs.
     *
     * @param view
     */
    public void reload(View view) {
        showProductImage();

        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         * Get product detail json from funcard server.
         */

        getStoreProductDetailFromServerSetView();
    }

    private void errorLoadingImage(final String string) {
        StoreOfferDetail.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productImage.setText(string);
            }
        });
    }


    /**
     * method run when redeem button clicked.
     *
     * @param view
     */
    public void redeem(View view) {
        //switchActivity.openActivity(currentActivity, RedeemActivity.class);
        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();

        redeemCard();
    }

    /**
     * Method opens all store offers page.
     * goToStorePage
     *
     * @param view
     */
    public void allOffers(View view) {
        switchActivity.openActivityReorderToFront(currentActivity, StoreOffers.class, new String[]{Stores.STORE_ID, Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG}
                , new String[]{store_id, store_name, store_lat, store_lng});
    }


    /**
     * Method opens all store offers page.
     *
     * @param view
     */
    public void goToStorePage(View view) {
        switchActivity.openActivity(currentActivity, StorePage.class, new String[]{Stores.STORE_ID, Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG}
                , new String[]{store_id, store_name, store_lat, store_lng});
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


    /**
     * Add product to reminders
     *
     * @param view store_id, store_name, store_lat, store_lng, product_id, product_name, product_offers;
     */
    public void addReminder(View view) {
        if (funDB.isReminderExist(product_id)) {      /** if reminder already exist */


            FuncardToast.showToast(REMINDER_ALREADY_ADDED, context);

        } else { /** if reminder not exist then insert */
            funDB.inserReminder(store_id, store_name, store_lat, store_lng, product_id, product_name, product_offers, product_time);
            FuncardToast.showToast(REMINDER__ADDED, context);

        }

    }


    /**
     * Add product to favourites.
     *
     * @param view
     */
    public void addFavourite(View view) {
        if (funDB.isFavouriteExist(product_id)) {      /** if favourite already exist */

            FuncardToast.showToast(FAVOURITE_ALREADY_ADDED, context);

        } else { /** if favourite not exist then insert */
            funDB.inserFavourites(store_id, store_name, store_lat, store_lng, product_id, product_name, product_offers, product_time);
            FuncardToast.showToast(FAVOURITE__ADDED, context);

        }


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
        menuManager = new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }

}

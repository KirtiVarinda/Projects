package com.tiffindaddy.app.tiffindaddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.CartData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.CouponData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.SetCartData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.UserAddresses;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.WriteJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.ProgressBarManager;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeSlotManager;
import com.tiffindaddy.app.tiffindaddy.Popups.TiffinDaddyPopups;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.TiffinAdapters.CartAdapter;
import com.tiffindaddy.app.tiffindaddy.Validator.FormValidation;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.HashMap;
import java.util.Map;

public class CartPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private boolean isCashOnDevelivery = false;

    public static boolean isNewAddressAdded = false;

    /**
     * variable for special notes.
     */
    EditText todayBreakfastSpecial, tomorrowBreakfastSpecial, lunchTodaySpecial, lunchTommorowSpecial, todayDinnerSpecial, tomorrowDinnerSpecial;


    /**
     * static variables.
     */
    private static String COUPON = "coupon";
    private static String DEVICE_TOKEN = "device_token";
    private static String UID = "uid";


    /**
     * checkbox variables
     */

    CheckBox radioButton, radioButton2;


    /**
     * variables for top text.
     */
    TextView text1, text2, text3, text4, text5, text6;


    /**
     * variables for holding selected tiffi data.
     */


    CartData[] cartDatasLunchToday, cartDatasLunchTomorrow;

    CartData[] cartDatasBreakfastToday, cartDatasBreakfastTomorrow;

    CartData[] cartDatasDinnerToday, cartDatasDinnerTomorrow;


    /**
     * subtotal text variable
     */

    TextView suntotal1, suntotal2, suntotal3, suntotal4, suntotal5, suntotal6;

    TextView grandTotal;


    /**
     * variables for different layouts to show or hide
     */
    RelativeLayout layout1, layout2, layout3, layout4, layout5, layout6;

    /**
     * variables for listview.
     */
    ListView todayBreakfastLIst, tomorrowBreakfastLIst, lunchTodayLIst, lunchTommorowLIst, todayDinnerLIst, tomorrowDinnerLIst;


    /**
     * Spinner variable
     */

    Spinner addressSpinner1, addressSpinner2, addressSpinner3, addressSpinner4, addressSpinner5, addressSpinner6;
    Spinner timeSlotSpinner1, timeSlotSpinner2, timeSlotSpinner3, timeSlotSpinner4, timeSlotSpinner5, timeSlotSpinner6, timeSlotSpinner;

    String tiffinType = "";

    String whatDay = "";

    UserAddresses userAddresses[];

    private ProgressBarManager progressBarManager;

    private ParseJsonData parseJsonData;

    private MySharedData mySharedData;

    public static CartPage cartPage;

    LinearLayout cartbelow;


    /**
     * coupon edittext
     */

    EditText coupon;

    private String couponValue;


    /**
     * Tiffin adress url
     */
    private static String addressURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        mySharedData = new MySharedData(getApplicationContext());
        parseJsonData = new ParseJsonData();

        cartPage = CartPage.this;
        grandTotal = (TextView) findViewById(R.id.textView15);
        coupon = (EditText) findViewById(R.id.couponedit);
        radioButton = (CheckBox) findViewById(R.id.radioButton);
        radioButton2 = (CheckBox) findViewById(R.id.radioButton2);
        radioButton.setChecked(true);


        cartbelow = (LinearLayout) findViewById(R.id.cartbelow);


        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton2.setChecked(false);
                }

            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton.setChecked(false);
                }

            }
        });


        /**
         * intialize special notes.
         */
        todayBreakfastSpecial = (EditText) findViewById(R.id.todayBreakfastSpecial);
        tomorrowBreakfastSpecial = (EditText) findViewById(R.id.tomorrowBreakfastSpecial);
        lunchTodaySpecial = (EditText) findViewById(R.id.lunchTodaySpecial);
        lunchTommorowSpecial = (EditText) findViewById(R.id.lunchTommorowSpecial);
        todayDinnerSpecial = (EditText) findViewById(R.id.todayDinnerSpecial);
        tomorrowDinnerSpecial = (EditText) findViewById(R.id.tomorrowDinnerSpecial);

        /***
         * initialize subtotal texts
         */
        suntotal1 = (TextView) findViewById(R.id.subtotal1);
        suntotal2 = (TextView) findViewById(R.id.subtotal2);
        suntotal3 = (TextView) findViewById(R.id.subtotal3);
        suntotal4 = (TextView) findViewById(R.id.subtotal4);
        suntotal5 = (TextView) findViewById(R.id.subtotal5);
        suntotal6 = (TextView) findViewById(R.id.subtotal6);

        /**
         * Initialize spinner for address and time slot
         */

        addressSpinner1 = (Spinner) findViewById(R.id.todayBreakfastAddress);
        addressSpinner2 = (Spinner) findViewById(R.id.tomorrowBreakfastAddress);
        addressSpinner3 = (Spinner) findViewById(R.id.lunchTodayAddress);
        addressSpinner4 = (Spinner) findViewById(R.id.lunchTommorowAddress);
        addressSpinner5 = (Spinner) findViewById(R.id.todayDinnerAddress);
        addressSpinner6 = (Spinner) findViewById(R.id.tomorrowDinnerAddress);


        clickSpinner();


        timeSlotSpinner1 = (Spinner) findViewById(R.id.todayBreakfastTimeSlot);
        timeSlotSpinner2 = (Spinner) findViewById(R.id.tomorrowBreakfastTimeSlot);
        timeSlotSpinner3 = (Spinner) findViewById(R.id.lunchTodayTimeSlot);
        timeSlotSpinner4 = (Spinner) findViewById(R.id.lunchTommorowTimeSlot);
        timeSlotSpinner5 = (Spinner) findViewById(R.id.todayDinnerTimeSlot);
        timeSlotSpinner6 = (Spinner) findViewById(R.id.tomorrowDinnerTimeSlot);


        /**
         * Intialize textviews
         */

        text1 = (TextView) findViewById(R.id.todayBreakfastText);
        text2 = (TextView) findViewById(R.id.tomorrowBreakfastText);
        text3 = (TextView) findViewById(R.id.lunchTodayText);
        text4 = (TextView) findViewById(R.id.lunchTommorowText);
        text5 = (TextView) findViewById(R.id.todayDinnerText);
        text6 = (TextView) findViewById(R.id.tomorrowDinnerText);

        /**
         * initialize layouts
         */
        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        layout2 = (RelativeLayout) findViewById(R.id.layout2);
        layout3 = (RelativeLayout) findViewById(R.id.layout3);
        layout4 = (RelativeLayout) findViewById(R.id.layout4);
        layout5 = (RelativeLayout) findViewById(R.id.layout5);
        layout6 = (RelativeLayout) findViewById(R.id.layout6);


        /**
         * initialize listvies.
         */
        todayBreakfastLIst = (ListView) findViewById(R.id.todayBreakfastLIst);
        tomorrowBreakfastLIst = (ListView) findViewById(R.id.tomorrowBreakfastLIst);
        lunchTodayLIst = (ListView) findViewById(R.id.lunchTodayLIst);
        lunchTommorowLIst = (ListView) findViewById(R.id.lunchTommorowLIst);
        todayDinnerLIst = (ListView) findViewById(R.id.todayDinnerLIst);
        tomorrowDinnerLIst = (ListView) findViewById(R.id.tomorrowDinnerLIst);


        /** initialize address url */
        addressURL = getResources().getString(R.string.get_address);

        progressBarManager = new ProgressBarManager(CartPage.this);


        /** start feting address with loader */
        progressBarManager.startProgressBar();


        setTifinDataForCart();

        new Thread() {

            public void run() {

                getAddress();


            }


        }.start();


    }

    private void clickSpinner() {
        addressSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {

                        public void run() {

                            getAddress();


                        }


                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addressSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {

                        public void run() {

                            getAddress();


                        }


                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addressSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {
                        public void run() {

                            getAddress();


                        }
                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addressSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {
                        public void run() {

                            getAddress();


                        }
                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addressSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {
                        public void run() {

                            getAddress();


                        }
                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addressSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Add New Address")) {
                    switchActivity();
                } else if (selected.equals("Please click to reload.")) {
                    new Thread() {
                        public void run() {

                            getAddress();


                        }
                    }.start();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void switchActivity() {

        Intent intent = new Intent(CartPage.this, AddAdrress.class);
        startActivity(intent);


    }

    public void setTifinDataForCart() {

        if (ProductPage.theMap.size() < 1) {
            cartbelow.setVisibility(View.GONE);
            ProductPage.couponData = null;
        } else {
            cartbelow.setVisibility(View.VISIBLE);
        }
        suntotal1.setText("₹0");
        suntotal2.setText("₹0");
        suntotal3.setText("₹0");
        suntotal4.setText("₹0");
        suntotal5.setText("₹0");
        suntotal6.setText("₹0");


        hideallLayouts();


        String position;


        int t11 = 0, t12 = 0, t13 = 0, t14 = 0, t15 = 0, t16 = 0;
        System.out.println("length=v" + ProductPage.theMap.size());


        /**
         *reverse hash map
         */
        Map<String, TiffinData> theMap1 = new HashMap<String, TiffinData>();
        for (final String Key1 : ProductPage.theMap.keySet()) {
            TiffinData tiffinData1 = ProductPage.theMap.get(Key1);
            theMap1.put(Key1, tiffinData1);

        }


        ProductPage.theMap = theMap1;

        for (final String Key : ProductPage.theMap.keySet()) {
            TiffinData tiffinData = ProductPage.theMap.get(Key);
            String[] wholeKey = Key.split("-l-");
            /**
             * check if selected date is today or tomorrow
             */
            if (wholeKey[4].equals(TimeManager.todayDate())) {
                whatDay = "today";
            } else {
                whatDay = "tomorrow";

            }

            for (int i = 0; i < ProductPage.tiffinCategories.length; i++) {

                if (tiffinData.getCategoryName().contains(ProductPage.tiffinCategories[i].getCategoryName())) {
                    if (tiffinData.getCategoryName().toLowerCase().contains("breakfast")) {
                        if (whatDay.equals("today")) {
                            t11++;
                        } else {
                            t12++;
                        }

                    }
                    if (tiffinData.getCategoryName().toLowerCase().contains("lunch")) {
                        if (whatDay.equals("today")) {
                            t13++;
                        } else {
                            t14++;
                        }
                    }
                    if (tiffinData.getCategoryName().toLowerCase().contains("dinner")) {
                        if (whatDay.equals("today")) {
                            t15++;
                        } else {
                            t16++;
                        }
                    }
                }
            }


        }


        cartDatasBreakfastToday = new CartData[t11];
        cartDatasBreakfastTomorrow = new CartData[t12];
        cartDatasLunchToday = new CartData[t13];
        cartDatasLunchTomorrow = new CartData[t14];
        cartDatasDinnerToday = new CartData[t15];
        cartDatasDinnerTomorrow = new CartData[t16];


        int t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0;
        for (final String Key : ProductPage.theMap.keySet()) {
            CartData cartDatas1 = new CartData();
            TiffinData tiffinData = ProductPage.theMap.get(Key);

            /**
             * set key for tiffindata map to cartdata bean
             */
            cartDatas1.setMkey(Key);


            /**
             * set data in cartdata bean class.
             *
             */

            cartDatas1.setTiffinData(tiffinData);


            String[] wholeKey = Key.split("-l-");
            position = wholeKey[2];

            /**
             * setting array position of tiffin data.
             */
            cartDatas1.setPosition(Integer.parseInt(position));
            cartDatas1.setSelectedDate(wholeKey[4]);


            /**
             * check if selected date is today or tomorrow
             */
            if (wholeKey[4].equals(TimeManager.todayDate())) {
                whatDay = "today";
            } else {
                whatDay = "tomorrow";

            }
            /**
             * set the selected  day.
             */
            cartDatas1.setWhatDay(whatDay);


            for (int i = 0; i < ProductPage.tiffinCategories.length; i++) {


                if (tiffinData.getCategoryName().contains(ProductPage.tiffinCategories[i].getCategoryName())) {

                    if (tiffinData.getCategoryName().toLowerCase().contains("breakfast")) {
                        tiffinType = "breakfast";


                    }
                    if (tiffinData.getCategoryName().toLowerCase().contains("lunch")) {
                        tiffinType = "lunch";


                    }
                    if (tiffinData.getCategoryName().toLowerCase().contains("dinner")) {
                        tiffinType = "dinner";


                    }

                }
            }


            cartDatas1.setTiffinTYpe(tiffinType);


            if (tiffinType.contains("breakfast")) {


                if (whatDay.equals("today")) {
                    cartDatasBreakfastToday[t1] = cartDatas1;
                    t1++;
                } else {
                    cartDatasBreakfastTomorrow[t2] = cartDatas1;
                    t2++;
                }


            } else if (tiffinType.contains("lunch")) {

                if (whatDay.equals("today")) {
                    cartDatasLunchToday[t3] = cartDatas1;
                    t3++;
                } else {
                    cartDatasLunchTomorrow[t4] = cartDatas1;
                    t4++;
                }


            } else if (tiffinType.contains("dinner")) {
                if (whatDay.equals("today")) {
                    cartDatasDinnerToday[t5] = cartDatas1;
                    t5++;
                } else {
                    cartDatasDinnerTomorrow[t6] = cartDatas1;
                    t6++;
                }


            }

            System.out.println("tiffin type" + tiffinType);
        }


        if (cartDatasBreakfastToday.length > 0) {
            String date1 = "";
            String breakfastSlot1 = "";

            TiffinData tiffinData1[] = new TiffinData[cartDatasBreakfastToday.length];
            String name1[] = new String[cartDatasBreakfastToday.length];
            for (int i = 0; i < cartDatasBreakfastToday.length; i++) {
                date1 = cartDatasBreakfastToday[i].getSelectedDate();
                breakfastSlot1 = cartDatasBreakfastToday[i].getTiffinData().getDeliverySlot();


                name1[i] = cartDatasBreakfastToday[i].getMkey();
                tiffinData1[i] = cartDatasBreakfastToday[i].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasBreakfastToday[i].getTiffinData().getCategoryName());


            }

            /**
             * set list view
             */
            setLunchListAdapter(tiffinData1, name1, todayBreakfastLIst, suntotal1);
            layout1.setVisibility(View.VISIBLE);
            text1.setText("Breakfast - " + date1);


            /**
             * manupulate delevery slot.
             */
            String[] timeSlots1 = breakfastSlot1.split("-l-");
            String slots1[] = TimeSlotManager.calculateSlotsinMiliseconds(TimeManager.getNumberOfHours(timeSlots1[0], timeSlots1[1]), timeSlots1[0]);
            String actuallSlots1[] = TimeSlotManager.calculateSlots(slots1);
            setTimeSlotSpinner(actuallSlots1, timeSlotSpinner1);


            System.out.println("====================");

        }
        if (cartDatasBreakfastTomorrow.length > 0) {
            String date2 = "";
            String breakfastSlot2 = "";


            TiffinData tiffinData2[] = new TiffinData[cartDatasBreakfastTomorrow.length];
            String name2[] = new String[cartDatasBreakfastTomorrow.length];
            for (int j = 0; j < cartDatasBreakfastTomorrow.length; j++) {
                date2 = cartDatasBreakfastTomorrow[j].getSelectedDate();
                breakfastSlot2 = cartDatasBreakfastTomorrow[j].getTiffinData().getDeliverySlot();

                name2[j] = cartDatasBreakfastTomorrow[j].getMkey();


                tiffinData2[j] = cartDatasBreakfastTomorrow[j].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasBreakfastTomorrow[j].getTiffinData().getCategoryName());


            }


            /**
             * set list view
             */
            setLunchListAdapter(tiffinData2, name2, tomorrowBreakfastLIst, suntotal2);
            layout2.setVisibility(View.VISIBLE);
            text2.setText("Breakfast - " + date2);

            /**
             * manupulate delevery slot.
             */
            String[] timeSlots2 = breakfastSlot2.split("-l-");
            String slots2[] = TimeSlotManager.calculateSlotsinMiliseconds(TimeManager.getNumberOfHours(timeSlots2[0], timeSlots2[1]), timeSlots2[0]);
            String actuallSlots2[] = TimeSlotManager.calculateSlotsForTomorrow(slots2);
            setTimeSlotSpinner(actuallSlots2, timeSlotSpinner2);


        }
        if (cartDatasLunchToday.length > 0) {
            String date3 = "";
            String breakfastSlot3 = "";


            TiffinData tiffinData3[] = new TiffinData[cartDatasLunchToday.length];
            String name3[] = new String[cartDatasLunchToday.length];
            for (int k = 0; k < cartDatasLunchToday.length; k++) {
                date3 = cartDatasLunchToday[k].getSelectedDate();
                breakfastSlot3 = cartDatasLunchToday[k].getTiffinData().getDeliverySlot();


                name3[k] = cartDatasLunchToday[k].getMkey();
                tiffinData3[k] = cartDatasLunchToday[k].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasLunchToday[k].getTiffinData().getCategoryName());


            }

            /**
             * set list view
             */
            setLunchListAdapter(tiffinData3, name3, lunchTodayLIst, suntotal3);
            layout3.setVisibility(View.VISIBLE);
            text3.setText("Lunch - " + date3);


            /**
             * manupulate delevery slot.
             */
            String[] timeSlots3 = breakfastSlot3.split("-l-");
            String slots3[] = TimeSlotManager.calculateSlotsinMiliseconds(TimeManager.getNumberOfHours(timeSlots3[0], timeSlots3[1]), timeSlots3[0]);
            String actuallSlots3[] = TimeSlotManager.calculateSlots(slots3);
            setTimeSlotSpinner(actuallSlots3, timeSlotSpinner3);


            System.out.println("====================");

        }
        if (cartDatasLunchTomorrow.length > 0) {
            String date4 = "";
            String breakfastSlot4 = "";

            TiffinData tiffinData4[] = new TiffinData[cartDatasLunchTomorrow.length];
            String name4[] = new String[cartDatasLunchTomorrow.length];
            for (int l = 0; l < cartDatasLunchTomorrow.length; l++) {
                date4 = cartDatasLunchTomorrow[l].getSelectedDate();
                breakfastSlot4 = cartDatasLunchTomorrow[l].getTiffinData().getDeliverySlot();


                name4[l] = cartDatasLunchTomorrow[l].getMkey();
                tiffinData4[l] = cartDatasLunchTomorrow[l].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasLunchTomorrow[l].getTiffinData().getCategoryName());


            }

            /**
             * set list view
             */
            setLunchListAdapter(tiffinData4, name4, lunchTommorowLIst, suntotal4);
            layout4.setVisibility(View.VISIBLE);
            text4.setText("Lunch - " + date4);


            /**
             * manupulate delevery slot.
             */
            String[] timeSlots4 = breakfastSlot4.split("-l-");
            String slots4[] = TimeSlotManager.calculateSlotsinMiliseconds(TimeManager.getNumberOfHours(timeSlots4[0], timeSlots4[1]), timeSlots4[0]);
            String actuallSlots4[] = TimeSlotManager.calculateSlotsForTomorrow(slots4);
            setTimeSlotSpinner(actuallSlots4, timeSlotSpinner4);


        }
        if (cartDatasDinnerToday.length > 0) {
            String date5 = "";
            String breakfastSlot5 = "";


            TiffinData tiffinData5[] = new TiffinData[cartDatasDinnerToday.length];
            String name5[] = new String[cartDatasDinnerToday.length];
            for (int m = 0; m < cartDatasDinnerToday.length; m++) {
                date5 = cartDatasDinnerToday[m].getSelectedDate();
                breakfastSlot5 = cartDatasDinnerToday[m].getTiffinData().getDeliverySlot();


                name5[m] = cartDatasDinnerToday[m].getMkey();
                tiffinData5[m] = cartDatasDinnerToday[m].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasDinnerToday[m].getTiffinData().getCategoryName());


            }

            /**
             * set list view
             */
            layout5.setVisibility(View.VISIBLE);
            setLunchListAdapter(tiffinData5, name5, todayDinnerLIst, suntotal5);
            text5.setText("Dinner - " + date5);


            /**
             * manupulate delevery slot.
             */
            String[] timeSlots1 = breakfastSlot5.split("-l-");
            int totalHours = TimeManager.getNumberOfHours(timeSlots1[0], timeSlots1[1]);
            String slots[] = TimeSlotManager.calculateSlotsinMiliseconds(totalHours, timeSlots1[0]);
            String actuallSlots[] = TimeSlotManager.calculateSlots(slots);
            setTimeSlotSpinner(actuallSlots, timeSlotSpinner5);


            System.out.println("====================");

        }
        if (cartDatasDinnerTomorrow.length > 0) {
            String date6 = "";
            String breakfastSlot6 = "";

            TiffinData tiffinData6[] = new TiffinData[cartDatasDinnerTomorrow.length];
            String name6[] = new String[cartDatasDinnerTomorrow.length];
            for (int n = 0; n < cartDatasDinnerTomorrow.length; n++) {
                date6 = cartDatasDinnerTomorrow[n].getSelectedDate();
                breakfastSlot6 = cartDatasDinnerTomorrow[n].getTiffinData().getDeliverySlot();

                name6[n] = cartDatasDinnerTomorrow[n].getMkey();
                tiffinData6[n] = cartDatasDinnerTomorrow[n].getTiffinData();
                System.out.println("cartDatasBreakfastToday " + cartDatasDinnerTomorrow[n].getTiffinData().getCategoryName());


            }


            /**
             * set list view
             */
            setLunchListAdapter(tiffinData6, name6, tomorrowDinnerLIst, suntotal6);
            layout6.setVisibility(View.VISIBLE);
            text6.setText("Dinner - " + date6);


            /**
             * manupulate delevery slot.
             */
            String[] timeSlots6 = breakfastSlot6.split("-l-");
            String slots6[] = TimeSlotManager.calculateSlotsinMiliseconds(TimeManager.getNumberOfHours(timeSlots6[0], timeSlots6[1]), timeSlots6[0]);
            String actuallSlots6[] = TimeSlotManager.calculateSlotsForTomorrow(slots6);
            setTimeSlotSpinner(actuallSlots6, timeSlotSpinner6);


        }


        grandTotal.setText("Total = ₹" + getGrandTotal(new TextView[]{suntotal1, suntotal2, suntotal3, suntotal4, suntotal5, suntotal6}));


    }


    private String getGrandTotal(TextView[] subtotal) {
        float sum = 0;

        for (int i = 0; i < subtotal.length; i++) {

            String temp1[] = subtotal[i].getText().toString().split("₹");
            sum = (float) sum + Float.parseFloat(temp1[1]);
        }

        return sum + "";

    }


    private void setTimeSlotSpinner(String[] actuallSlots, Spinner spinner) {
        final ArrayAdapter<String> spinnerArrayAdapter2s = new ArrayAdapter<String>(this, R.layout.spinner_item, actuallSlots);
        spinnerArrayAdapter2s.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view

        spinner.setAdapter(spinnerArrayAdapter2s);
    }

    /**
     * method to set the list adapter for lunch.
     */
    private void setLunchListAdapter(TiffinData[] tiffinData, String tiffinName[], final ListView listView, TextView textview) {


        calculateSubTotal(tiffinData, textview);

        final CartAdapter catAdapter = new CartAdapter(getApplicationContext(), R.layout.cart_list_row, tiffinName, tiffinData, cartPage, "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(catAdapter);
                setListViewHeightBasedOnItems(listView);
                //  listView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 400));
                // progressBarManager.stopProgressBar();
            }
        });
    }


//
//    /***
//     * reverse an string array
//     */
//    private String[] reverseTiffinArray(String[] name) {
//
//        String[] name2 = new String[name.length];
//        int inx = name.length;
//
//
//        for (int i = 0; i < name.length; i++) {
//            name2[0] = name[inx - 1];
//
//            inx--;
//
//
//        }
//
//
//        return name2;
//    }
//    /***
//     * reverse an tiffin array
//     */
//    private TiffinData[] reverseTiffinArray(TiffinData[] tiffinData) {
//
//        TiffinData[] tiffinData2 = new TiffinData[tiffinData.length];
//        int inx = tiffinData.length;
//
//
//        for (int i = 0; i < tiffinData.length; i++) {
//            tiffinData2[0] = tiffinData[inx - 1];
//
//            inx--;
//
//
//        }
//
//
//        return tiffinData2;
//    }


    /**
     * calculate and set subprice
     *
     * @param tiffinData
     * @param textView
     */
    public void calculateSubTotal(TiffinData[] tiffinData, TextView textView) {

        float subtotal = 0;

        for (int ind = 0; ind < tiffinData.length; ind++) {

            float price = Float.parseFloat(tiffinData[ind].getTiffinPrice());
            int qty = tiffinData[ind].getQuantityPurchased();

            subtotal = (float) subtotal + ((float) price * qty);


        }


        /**
         * calculate coupon discount and apply to subtotal.
         */

        double currentTimeInMili = TimeManager.currentTimedateToString();

        if (ProductPage.couponData != null) {

            float minvalue = Float.parseFloat(ProductPage.couponData.getMinimumOrderAmount());

            /**
             * subtotal should be greater than minimum order amount
             */
            if (subtotal > minvalue) {

//                long currentTime = TimeManager.currentTimedateToString();
//                String[] startdate = ProductPage.couponData.getStartDate().split("-");
//                String[] enddate = ProductPage.couponData.getEndDate().split("-");
//                long startDate1 = TimeManager.timeToMillisecond(Integer.parseInt(startdate[0]), Integer.parseInt(startdate[1]), Integer.parseInt(startdate[1]), 00, 01);
//                long endDate1 = TimeManager.timeToMillisecond(Integer.parseInt(enddate[0]), Integer.parseInt(enddate[1]), Integer.parseInt(enddate[1]), 00, 01);
//
//                if (currentTime >= startDate1 && currentTime <= endDate1) {
                    if (ProductPage.couponData.getCouponType().equals("1")) {
                        float subtotalAfterDiscount = (float) subtotal - Float.parseFloat(ProductPage.couponData.getDiscount());
                        subtotal = subtotalAfterDiscount;

                    } else if (ProductPage.couponData.getCouponType().equals("2")) {


                        float discount = (float) ((float) Float.parseFloat(ProductPage.couponData.getDiscount()) * subtotal) / 100;
                        subtotal = (float) subtotal - discount;


                    }
           //     }

            }

        }

        textView.setText("Subtotal = \u20B9" + subtotal + "");
    }


    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int temp = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);

                if (temp == 0) {
                    temp = item.getMeasuredHeight();
                }
                totalItemsHeight += item.getMeasuredHeight();
            }

            int temp2 = temp / 4;
            // Get total height of all item dividers.

            //temp=temp;
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1) + temp2;

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isNewAddressAdded) {
            new Thread() {

                public void run() {

                    getAddress();


                }


            }.start();
            isNewAddressAdded = false;
        }


    }


    /**
     * Get Tiffin Categories from the server.
     */
    private void getAddress() {
        CartPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.startProgressBar();
            }
        });

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {DEVICE_TOKEN, UID};      // set keys
        String value[] = {mySharedData.getGeneralSaveSession(MySharedData.SESSION_TOKEN), mySharedData.getGeneralSaveSession(MySharedData.SESSION_USERID)};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);


        /**
         * Authenticate user from funcard deals server.
         */

        ServerSync serverSync = new ServerSync();
        String reponse = serverSync.SyncServer(theMap, addressURL);
        System.out.println("Response address= " + reponse);
        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */


            CartPage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Network problem. Unable to load address");

                }
            });

            String address[] = new String[2];
            address[0] = "Select Delivery Address";
            address[1] = "Please click to reload.";
            setAddressAdapter(address, addressSpinner1);
            setAddressAdapter(address, addressSpinner2);
            setAddressAdapter(address, addressSpinner3);
            setAddressAdapter(address, addressSpinner4);
            setAddressAdapter(address, addressSpinner5);
            setAddressAdapter(address, addressSpinner6);


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").equals("success")) {
                if (parseJsonData.checkStatus(reponse, "result").equals("")) {
                    // String timeSlot[] = {"Select Time"};
                    String address[] = new String[2];
                    address[0] = "Select Delivery Address";
                    address[1] = "Add New Address";
                    setAddressAdapter(address, addressSpinner1);
                    setAddressAdapter(address, addressSpinner2);
                    setAddressAdapter(address, addressSpinner3);
                    setAddressAdapter(address, addressSpinner4);
                    setAddressAdapter(address, addressSpinner5);
                    setAddressAdapter(address, addressSpinner6);
                } else {
                    userAddresses = parseJsonData.parseUserAddressJson(reponse);
                    // String timeSlot[] = {"Select Time"};
                    String address[] = new String[userAddresses.length + 2];
                    address[0] = "Select Delivery Address";
                    for (int a = 0; a < userAddresses.length; a++) {

                        address[a + 1] = userAddresses[a].getAddress() + ", " + userAddresses[a].getCityName();

                    }
                    address[address.length - 1] = "Add New Address";
                    setAddressAdapter(address, addressSpinner1);
                    setAddressAdapter(address, addressSpinner2);
                    setAddressAdapter(address, addressSpinner3);
                    setAddressAdapter(address, addressSpinner4);
                    setAddressAdapter(address, addressSpinner5);
                    setAddressAdapter(address, addressSpinner6);
                }


//
//                final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, timeSlot);
//                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
//


            } else {

            }


        }


        CartPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });


    }

    private void setAddressAdapter(String[] address, final Spinner spinner) {
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, address);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                spinner.setAdapter(spinnerArrayAdapter1);
                //      spinner2.setAdapter(spinnerArrayAdapter2);
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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


    private void hideallLayouts() {
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        layout6.setVisibility(View.GONE);

    }


    /**
     * textarea that leads you to shopping page.
     *
     * @param view
     */
    public void continueShopping(View view) {
        finish();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_right);
    }


    /**
     * method to apply coupon.
     *
     * @param view
     */

    public void applyCoupon(View view) {
        couponValue = coupon.getText().toString();
        if (FormValidation.removeWhiteSpaces(couponValue).equals("")) {
            TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please enter a valid coupon.");

        } else {

            new Thread() {
                public void run() {

                    checkCoupon();
                }


            }.start();

        }


    }

    /**
     * check coupon from server and apply to subtotal.
     */
    private void checkCoupon() {

        CartPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.startProgressBar();
            }
        });
        String couponURL = getResources().getString(R.string.coupon);

        String uidLogin = mySharedData.getGeneralSaveSession(MySharedData.SESSION_USERID);
        String token = mySharedData.getGeneralSaveSession(MySharedData.SESSION_TOKEN);


        System.out.println("couponValue=" + couponValue);
        System.out.println("token=" + token);
        System.out.println("UID=" + uidLogin);

        String key[] = {COUPON, DEVICE_TOKEN, UID};      // set keys
        String value[] = {couponValue, token, uidLogin};


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);


        /**
         * Authenticate user from funcard deals server.
         */

        ServerSync serverSync = new ServerSync();
        final String reponse = serverSync.SyncServer(theMap, couponURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            CartPage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Network problem.");

                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             *
             * {"":"BestDeal","":"1","":"10","":"5","":"100.00","":"2016-01-23","":"2016-01-

             27","status":"success","used_by_user":0}
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("success")) {
                /**
                 * set json data in coupon bean.
                 */
                final CouponData couponData = new CouponData();
                couponData.setCouponCode(parseJsonData.checkStatus(reponse, "coupon_code"));
                couponData.setCouponType(parseJsonData.checkStatus(reponse, "coupon_type"));
                couponData.setDiscount(parseJsonData.checkStatus(reponse, "discount"));
                couponData.setMaximumUse(parseJsonData.checkStatus(reponse, "max_use_per_user"));
                couponData.setMinimumOrderAmount(parseJsonData.checkStatus(reponse, "min_order_amount"));
                couponData.setStartDate(parseJsonData.checkStatus(reponse, "start_date"));
                couponData.setEndDate(parseJsonData.checkStatus(reponse, "end_date"));


                long currentTime = TimeManager.currentTimedateToString();
                String[] startdate = couponData.getStartDate().split("-");
                String[] enddate = couponData.getEndDate().split("-");
                long startDate1 = TimeManager.timeToMillisecond(Integer.parseInt(startdate[0]), Integer.parseInt(startdate[1]), Integer.parseInt(startdate[1]), 00, 01);
                long endDate1 = TimeManager.timeToMillisecond(Integer.parseInt(enddate[0]), Integer.parseInt(enddate[1]), Integer.parseInt(enddate[1]), 00, 01);

                if (currentTime >= startDate1 && currentTime <= endDate1) {
                    ProductPage.couponData = couponData;


                    CartPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TiffinDaddyPopups.funcardPopup(CartPage.this, "Congratulations!", "Coupon Applied Successfully.");

                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTifinDataForCart();
                        }
                    });
                }else{
                    CartPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Coupon can only be Applied from " + couponData.getStartDate() + " to " + couponData.getEndDate());

                        }
                    });
                }



            } else if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("error")) {

//                if(parseJsonData.checkStatus(reponse, "msg").toLowerCase().equals("Invalid Coupon Code")){
//
//                }
                CartPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Invalid Coupon Code.");

                    }
                });


            } else {
                CartPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Server error.");

                    }
                });
            }


        }
        CartPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });
        System.out.println("Response coupon= " + reponse);


    }


    /**
     * method call when checkout button is clicked.
     *
     * @param view
     */
    public void checkout(View view) {

        SetCartData setCartData = new SetCartData();

        if (cartDatasBreakfastToday.length > 0) {
            if (addressSpinner1.getSelectedItem().toString().equals("Add New Address") || addressSpinner1.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner1.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for today breakfast.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasBreakfastToday, addressSpinner1.getSelectedItem().toString(), todayBreakfastSpecial.getText().toString(), timeSlotSpinner1.getSelectedItem().toString());
            }


        }
        if (cartDatasBreakfastTomorrow.length > 0) {
            if (addressSpinner2.getSelectedItem().toString().equals("Add New Address") || addressSpinner2.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner2.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for tomorrow breakfast.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasBreakfastTomorrow, addressSpinner2.getSelectedItem().toString(), tomorrowBreakfastSpecial.getText().toString(), timeSlotSpinner2.getSelectedItem().toString());
            }
        }

        if (cartDatasLunchToday.length > 0) {
            if (addressSpinner3.getSelectedItem().toString().equals("Add New Address") || addressSpinner3.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner3.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for today lunch.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasLunchToday, addressSpinner3.getSelectedItem().toString(), lunchTodaySpecial.getText().toString(), timeSlotSpinner3.getSelectedItem().toString());
            }
        }
        if (cartDatasLunchTomorrow.length > 0) {
            if (addressSpinner4.getSelectedItem().toString().equals("Add New Address") || addressSpinner4.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner4.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for tomorrow lunch.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasLunchTomorrow, addressSpinner4.getSelectedItem().toString(), lunchTommorowSpecial.getText().toString(), timeSlotSpinner4.getSelectedItem().toString());
            }

        }
        if (cartDatasDinnerToday.length > 0) {
            if (addressSpinner5.getSelectedItem().toString().equals("Add New Address") || addressSpinner5.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner5.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for today dinner.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasDinnerToday, addressSpinner5.getSelectedItem().toString(), todayDinnerSpecial.getText().toString(), timeSlotSpinner5.getSelectedItem().toString());
            }
        }
        if (cartDatasDinnerTomorrow.length > 0) {
            if (addressSpinner6.getSelectedItem().toString().equals("Add New Address") || addressSpinner6.getSelectedItem().toString().equals("Please click to reload.") || addressSpinner6.getSelectedItem().toString().equals("Select Delivery Address")) {
                TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select address for tomorrow dinner.");
                return;
            } else {
                setCartData.setAddressInCartBean(cartDatasDinnerTomorrow, addressSpinner6.getSelectedItem().toString(), tomorrowDinnerSpecial.getText().toString(), timeSlotSpinner6.getSelectedItem().toString());
            }
        }


        String paymode = "";
        /**
         * check if payment option clicked.
         */
        if (!radioButton.isChecked() && !radioButton2.isChecked()) {
            TiffinDaddyPopups.funcardPopup(CartPage.this, "Sorry!", "Please select one of the payment option.");
        } else {
            progressBarManager.startProgressBar();
            if (radioButton.isChecked()) {
                isCashOnDevelivery = true;
                paymode = "2";
            } else if (radioButton2.isChecked()) {
                isCashOnDevelivery = false;
                paymode = "1";
            }


            String jsonData = WriteJsonData.writeJson2(cartDatasBreakfastToday, cartDatasBreakfastTomorrow, cartDatasLunchToday, cartDatasLunchTomorrow, cartDatasDinnerToday, cartDatasDinnerTomorrow, paymode, mySharedData.getGeneralSaveSession(MySharedData.SESSION_USERID), getApplicationContext());
            sendJsonDataToserver(jsonData);

        }


    }


    private void sendJsonDataToserver(final String json) {
        System.out.println("jsonData= " + json);

        final String orderDataUrl = getResources().getString(R.string.addOderJsonToServer);
        final ServerSync serverSync = new ServerSync();
        new Thread() {
            @Override
            public void run() {

                String orderDataResponse = serverSync.sendJsonDataToServer(json, orderDataUrl);
                System.out.println("orderDataResponse= " + orderDataResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarManager.stopProgressBar();
                    }
                });

            }
        }.start();

    }


}

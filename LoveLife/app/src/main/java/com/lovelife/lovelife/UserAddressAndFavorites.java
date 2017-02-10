package com.lovelife.lovelife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.ImageProcessing.ImageHandler;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeAbstraction.PartnerConnectionInterface;
import com.lovelife.lovelife.LoveLifeUtility.ManagePartnerConnection;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.GetUserProfileResource;
import com.lovelife.lovelife.StringResources.UserFavouritiesResource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class UserAddressAndFavorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PartnerConnectionInterface {

    public static String uGender = "";
    AutoCompleteTextView movie, food, flower, colour, sport, holiday, car;
    public static boolean isReturnFromConnectToPartnerPage = false;
    DrawerLayout drawer;
    RelativeLayout partnerContainer;
    static String pendingPartnerEmail;
    static BeanRequestsFromPeople[] beanRequestsFromPeoples1;
    static ConnectedPartnerBean connectedPartnerBean;
    static String requestResultType = "";


    private PartnerConnectionInterface partnerConnectionInterface;
    TextView addPartnerActionBarIcon;
    ImageView partnerImage;


    /**
     * user picture fro drawer
     */
    ImageView profilePic;

    ServerSync serverSync = new ServerSync();

    MenuItem filterMenuItem;
    /**
     * session variable
     */
    MySharedData mySharedData;


    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;


    /**
     * variable for activity switcher (to open another activity)
     */

    SwitchActivities switchActivity;

    /**
     * current activity instance
     */
    private UserAddressAndFavorites currentActivity;


    ManagePartnerConnection managePartnerConnection;


    LinearLayout linearLayout;

    ProgressBar progressBar2;
    LinearLayout menu_bg;
    ImageView menu_profiles;

    String USERDATA_URL;
    Button saveAndContinue;
    ImageView nav_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginActivity.checks == true) {
            System.out.println("maleeeeeeeee");
            setTheme(R.style.MaleTheme);
        } else {
            System.out.println("Femaleeeeeeeee");
            setTheme(R.style.FemaleTheme);
        }


        isReturnFromConnectToPartnerPage = false;
        setContentView(R.layout.activity_user_address_and_favorites);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.nav_header_dashboard, null);
        saveAndContinue = (Button) findViewById(R.id.button);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        menu_bg = (LinearLayout) header.findViewById(R.id.menu_bg);
        menu_profiles = (ImageView) header.findViewById(R.id.menu_profiles);
        movie = (AutoCompleteTextView) findViewById(R.id.movie);
        food = (AutoCompleteTextView) findViewById(R.id.food);
        flower = (AutoCompleteTextView) findViewById(R.id.flower);
        colour = (AutoCompleteTextView) findViewById(R.id.colour);
        sport = (AutoCompleteTextView) findViewById(R.id.sport);
        holiday = (AutoCompleteTextView) findViewById(R.id.holiday);
        car = (AutoCompleteTextView) findViewById(R.id.car);


        TextInputLayout textInputLayout =(TextInputLayout) findViewById(R.id.textlayout);


        if (LoginActivity.checks == true) {
            System.out.println("maleeeeeeeee111");
            saveAndContinue.setBackgroundResource(R.drawable.save_blue);
            navigationView.setBackgroundResource(R.drawable.gradient_back_blue);
            menu_bg.setBackgroundResource(R.drawable.profile_dp_l);
            menu_profiles.setBackgroundResource(R.drawable.profile_dp_r);
            movie.setCompoundDrawablesWithIntrinsicBounds(R.drawable.movieb, 0, 0, 0);
            food.setCompoundDrawablesWithIntrinsicBounds(R.drawable.foodb, 0, 0, 0);
            flower.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flowerb, 0, 0, 0);
            colour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.colorb, 0, 0, 0);
            sport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sports, 0, 0, 0);
            holiday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.holidaysb, 0, 0, 0);
            car.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sedan, 0, 0, 0);
        } else {
            System.out.println("maleeeeeeeee11122222");
            saveAndContinue.setBackgroundResource(R.drawable.saveandcontinue);
            navigationView.setBackgroundResource(R.drawable.gradient_back);
            menu_bg.setBackgroundResource(R.drawable.menu_bgs);
            menu_profiles.setBackgroundResource(R.drawable.menu_profiles);
            movie.setCompoundDrawablesWithIntrinsicBounds(R.drawable.movies, 0, 0, 0);
            food.setCompoundDrawablesWithIntrinsicBounds(R.drawable.resort, 0, 0, 0);
            flower.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flower, 0, 0, 0);
            colour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.colors, 0, 0, 0);
            sport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sport, 0, 0, 0);
            holiday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.holidays, 0, 0, 0);
            car.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_icon, 0, 0, 0);

        }
        textInputLayout.setHintTextAppearance(R.style.FemaleTheme);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.favt);

        // genderSpinner = (AutoCompleteTextView) findViewById(R.id.gender);
        currentActivity = UserAddressAndFavorites.this;
        /** initialize session variable */
        mySharedData = new MySharedData(currentActivity);

        uGender = GetUserProfile.uGender;
        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);

        /**
         * initialize activity switcher
         */
        switchActivity = new SwitchActivities();

        USERDATA_URL = getString(R.string.serverDomain) + getString(R.string.user_fav);


        System.out.println("xsssssssssssdchidc");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        /** code to detect if keyboard is open or not */
        drawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub

                System.out.println("csgfduyc");
                Rect r = new Rect();
                View rootview = getWindow().getDecorView(); // this = activity
                rootview.getWindowVisibleDisplayFrame(r);
           /*     Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(r);
                int statusBarHeight = r.top;
                int contentViewTop =
                        window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;

                Log.i("*** Elenasys :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);*/

                int screenHeight = rootview.getRootView().getHeight();
                int KeyboardHeight = screenHeight - (r.bottom - r.top);
                Log.d("Keyboard Size", "Size: " + KeyboardHeight + "screen" + screenHeight);

                //boolean visible = heightDiff > screenHeight / 3;


                if (KeyboardHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened

                    System.out.println("keyboard Opemn");
                    linearLayout.setVisibility(View.GONE);
                } else {
                    System.out.println("keyboard Close");
                    linearLayout.setVisibility(View.VISIBLE);
                    // keyboard is closed
                }


            }
        });


        /** up casting to parent reference*/
        partnerConnectionInterface = UserAddressAndFavorites.this;
        managePartnerConnection = new ManagePartnerConnection(getApplicationContext(), partnerConnectionInterface);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();


        nav_close = (ImageView) header.findViewById(R.id.imageView4);
        nav_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cccccclose");
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
            }
        });


        profilePic = (ImageView) header.findViewById(R.id.profilepic);
        TextView nav_username = (TextView) header.findViewById(R.id.nav_name);
        TextView nav_email = (TextView) header.findViewById(R.id.nav_email);

        nav_username.setText(mySharedData.getGeneralSaveSession(MySharedData.USERNAME));
        nav_email.setText(mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL));


        /** first we load user profile pic that was save earlier */
        if (mySharedData.getGeneralSaveSession(MySharedData.saveProfileImage).equals("")) {
            if (uGender.equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {

            profilePic.setImageBitmap(ImageHandler.getProfileImageFromSharedPrefernces(mySharedData));
        }

        /** code for profile pic */
        if (mySharedData.getGeneralSaveSession(MySharedData.USER_PROFILE_PIC).equals("")) {
            if (uGender.equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {
            ImageHandler imageHandler = new ImageHandler();
            imageHandler.loadProfilePicForDrawer(profilePic, currentActivity);
        }


    }


    /**
     * Attempts to submit user profile data specified by the user form.
     * If there are form errors, the
     * errors are presented and no actual user data submit  attempt is made.
     */

    private void attemptToSubmitUserData() {
           /*   // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);*/

        // Store values at the time of the login attempt.
        String movieText = movie.getText().toString();
        String foodText = food.getText().toString();
        String flowerText = flower.getText().toString();
        String colourText = colour.getText().toString();
        String sportText = sport.getText().toString();
        String holidayText = holiday.getText().toString();
        String carText = car.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid First Name.
     /*   if (TextUtils.isEmpty(movieText)) {
            movie.setError("Please enter your Favourite Movie.");
            focusView = movie;
            cancel = true;
        } else if (TextUtils.isEmpty(foodText)) {
            food.setError("Please enter your Favourite Food.");
            focusView = food;
            cancel = true;
        } else if (TextUtils.isEmpty(flowerText)) {
            flower.setError("Please enter your Favourite Flower.");
            focusView = flower;
            cancel = true;
        } else if (TextUtils.isEmpty(colourText)) {
            colour.setError("Please enter your Favourite Colour.");
            focusView = colour;
            cancel = true;
        } else if (TextUtils.isEmpty(sportText)) {
            sport.setError("Please enter your Favourite Sport.");
            focusView = sport;
            cancel = true;
        } else if (TextUtils.isEmpty(holidayText)) {
            holiday.setError("Please enter your Favourite Holiday/Vacation.");
            focusView = holiday;
            cancel = true;
        } else if (TextUtils.isEmpty(carText)) {
            car.setError("Please enter your Favourite Car.");
            focusView = car;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {*/
        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);

            /** start progress bar */

            progressBarManager.startProgressBar();
            final Map<String, String> postData;
            //  if (RECEIVED_FROM.equals("registration")) {
            postData = getKeyPairValueInHashMap(movieText, foodText, flowerText, colourText, sportText, holidayText, carText);

      /*      } else {

                System.out.print("postData");
                postData = getKeyPairValueInHashMap(city, state, country, zip, movie, food, weather);

            }
*/

            /** register user with collected data on different thread to server */
            new Thread() {
                public void run() {
                    updateUserProfile(postData);

                }
            }.start();

//        }
    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String movieText, String foodText, String flowerText, String colourText, String sportText, String holidayText, String carText) {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        String key[] = {UserFavouritiesResource.id, UserFavouritiesResource.token, UserFavouritiesResource.favorite_movie, UserFavouritiesResource.favorite_food, UserFavouritiesResource.favorite_flower, UserFavouritiesResource.favorite_colour,
                UserFavouritiesResource.favorite_sport, UserFavouritiesResource.favorite_vacations, UserFavouritiesResource.favorite_car};      // set keys
        String value[] = {mySharedData.getGeneralSaveSession(MySharedData.USERID), mySharedData.getGeneralSaveSession(MySharedData.TOKEN), movieText, foodText, flowerText, colourText, sportText, holidayText, carText};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }

    /**
     * Submit user data on server
     *
     * @param postData contains the post data that would be send to server.
     */
    private void updateUserProfile(Map<String, String> postData) {


        /** submit user data on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, USERDATA_URL);

        System.out.println("user data submit response d" + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while submitting user data on server.
             * then shows the network error.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    //  Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            final String message = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then submit is successful. */

                final String emailStatus = ParseJsonData.getValueWithKey(reponse, "confirm");
                String email = ParseJsonData.getValueWithKey(reponse, "email");
                mySharedData.setGeneralSaveSession(MySharedData.CONFIRM_EMAIL, emailStatus);
                mySharedData.setGeneralSaveSession(MySharedData.USEREMAIL, email);
                mySharedData.setGeneralSaveSession(MySharedData.FIRST_ADDRESSANDFAVORITES_UPDATE, "yes");

          /*      if (emailStatus.equals("0")) {
                    *//** Stop progress bar *//*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarManager.stopProgressBar();
                            LoveLifePopups.loveLifePopupWithFinishCurrent(UserAddressAndFavorites.this, "Congratulations!", message + " Please confirm your email Id to Continue.");

                        }
                    });
                } else {*/
                    /** Stop progress bar */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarManager.stopProgressBar();
                            switchActivity.openActivity(currentActivity, Dashboard.class);
                            mySharedData.setGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN, "yes");
                            finish();
                        }
                    });

//                }


                /** stop progress bar here if popup removed from here */

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(message, "Sorry!");

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        popup("Server problem please try after some time.", "Sorry!");

                    }
                });

            }


        }


    }


    /**
     * Pop for registration activity.
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
                LoveLifePopups.loveLifePopup(UserAddressAndFavorites.this, titleText, message);
            }
        });


    }


    private void startLoadingInActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                partnerContainer.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();


        inflater.inflate(R.menu.profile_page, menu);
        final Menu m = menu;
        filterMenuItem = menu.findItem(R.id.partnerAdded);
        final View menu_list = filterMenuItem.getActionView();

        addPartnerActionBarIcon = (TextView) menu_list.findViewById(R.id.actionbar_notifcation_textview1);
        partnerContainer = (RelativeLayout) menu_list.findViewById(R.id.partnerContainer);
        progressBar2 = (ProgressBar) menu_list.findViewById(R.id.progressBar2);
        partnerImage = (ImageView) menu_list.findViewById(R.id.partnerImage);

        if (uGender.toLowerCase().equals("male")) {
            partnerImage.setImageResource(R.drawable.girl_heart);
        } else {
            partnerImage.setImageResource(R.drawable.add);
        }

        /** load partner data */
        new Thread() {
            public void run() {
                startLoadingInActionBar();
                managePartnerConnection.loadRequests();
            }

        }.start();


        /** set tick image instead of add partner image if already connected to partner */
        if (mySharedData.getGeneralSaveSession(MySharedData.isConnectedToPartner).equals("yes")) {

            addPartnerActionBarIcon.setBackgroundResource(R.drawable.check);

        } else {
            addPartnerActionBarIcon.setBackgroundResource(R.drawable.plus);
        }


        filterMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ddddddddccc");
                m.performIdentifierAction(filterMenuItem.getItemId(), 0);
            }
        });
        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.partnerAdded) {

            if (requestResultType.equals("noEventOccur")) {
                Toast.makeText(UserAddressAndFavorites.this, "noEventOccur", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"UserAddressAndFavorites"});

            } else if (requestResultType.equals("requestIsPending")) {

                Toast.makeText(UserAddressAndFavorites.this, "requestIsPending", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"UserAddressAndFavorites"});

            } else if (requestResultType.equals("requestsReceived")) {

                Toast.makeText(UserAddressAndFavorites.this, "requestsReceived", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"UserAddressAndFavorites"});

            } else if (requestResultType.equals("onPartnerConnected")) {

                Toast.makeText(UserAddressAndFavorites.this, "onPartnerConnected", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, PartnerProfile.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"UserAddressAndFavorites"});

            } else if (requestResultType.equals("onNetworkError")) {
                popup();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void popup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Sorry!");
        builder.setMessage("Connectivity problem. Please click ok to reload");
        // builder.setCustomTitle(title);
        //builder.setView(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

                partnerContainer.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);

                /** load partner data */
                new Thread() {
                    public void run() {
                        startLoadingInActionBar();
                        managePartnerConnection.loadRequests();
                    }

                }.start();


            }
        });

        builder.create().show();


    }


    /**
     * For getting Profile Details
     */


    public void setImageFromString(String imagePath, final ImageView partnerImage) {

        try {

            URL url = new URL(imagePath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            final Bitmap bmp = BitmapFactory.decodeStream(is);

            if (null != bmp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedPartnerBean.setPartnerImageBitmap(bmp);
                        partnerImage.setImageBitmap(bmp);

                    }
                });


                System.out.println("The Bitmap okkk");
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                        partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                    }
                });


                System.out.println("The Bitmap is NULL");
            }

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                    partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                }
            });

            System.out.println("error1111111" + e);
        } finally {
            stopLoadingInActionBar();
        }

    }


    private void stopLoadingInActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                partnerContainer.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void noEventOccur() {
        System.out.println("noEventOccur ");
        requestResultType = "noEventOccur";
        stopLoadingInActionBar();
    }

    @Override
    public void requestIsPending(String partnerEmail) {
        System.out.println("requestIsPending " + partnerEmail);
        requestResultType = "requestIsPending";
        this.pendingPartnerEmail = partnerEmail;
        stopLoadingInActionBar();

    }


    @Override
    public void requestsReceived(BeanRequestsFromPeople[] beanRequestsFromPeoples) {
        System.out.println("requestsReceived " + beanRequestsFromPeoples.length);
        requestResultType = "requestsReceived";
        this.beanRequestsFromPeoples1 = beanRequestsFromPeoples;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addPartnerActionBarIcon.setBackgroundResource(R.drawable.blank_red);

                addPartnerActionBarIcon.setText("" + beanRequestsFromPeoples1.length);
            }
        });

        stopLoadingInActionBar();
    }

    @Override
    public void onPartnerConnected(final ConnectedPartnerBean connectedPartnerBean) {
        System.out.println("onPartnerConnected ");
        requestResultType = "onPartnerConnected";
        this.connectedPartnerBean = connectedPartnerBean;
        System.out.println("getPartnerProfilePicss " + connectedPartnerBean.getPartnerProfilePic());
        /** set connected partner sessions */
        mySharedData.connectedPartnerSession(connectedPartnerBean, mySharedData);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addPartnerActionBarIcon.setBackgroundResource(R.drawable.check);
                addPartnerActionBarIcon.setText("");

            }
        });

        /** set partner image in heart at action bar if connected */
        setImageFromString(connectedPartnerBean.getPartnerProfilePic(), partnerImage);


    }

    @Override
    public void onNetworkError() {
        System.out.println("onNetworkError ");
        requestResultType = "onNetworkError";
        stopLoadingInActionBar();
    }

    @Override
    public void sessionExpired() {
        LoveLifePopups.loveLifePopupFinishCurrentActivityAndLogout(currentActivity, "Alert!", getResources().getString(R.string.sessionExpired), LoginActivity.class, mySharedData);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation profilePicView item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            LoveLifePopups.loveLifePopupFinishCurrentActivityAndLogout(currentActivity, "Alert!", getResources().getString(R.string.logoutAlert), LoginActivity.class, mySharedData);

        }/* else if (id == R.id.nav_movie) {
        } else if (id == R.id.nav_profile) {
            switchActivities.openActivity(this, ProfilePage.class);
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_map) {
            switchActivities.openActivity(this, MapActivity.class);
        } else if (id == R.id.nav_setting) {
            switchActivities.openActivity(this, SettingsActivity.class);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isReturnFromConnectToPartnerPage) {
            new Thread() {
                public void run() {
                    startLoadingInActionBar();
                    managePartnerConnection.loadRequests();
                }

            }.start();
        }
    }


    public void saveAndContinue(View view) {
        attemptToSubmitUserData();
    }
}



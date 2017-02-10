package com.lovelife.lovelife;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanForFacebookData;
import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;
import com.lovelife.lovelife.ImageProcessing.Croping;
import com.lovelife.lovelife.ImageProcessing.ImageHandler;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveChat.ChatService;
import com.lovelife.lovelife.LoveLifeAbstraction.PartnerConnectionInterface;
import com.lovelife.lovelife.LoveLifeUtility.ManagePartnerConnection;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.LoveLifeUtility.TimeUtility;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.GetUserProfileResource;
import com.lovelife.lovelife.StringResources.LoginResource;
import com.lovelife.lovelife.Validator.FormValidation;
import com.lovelife.lovelife.adapters.CropingOptionAdapter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class GetUserProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PartnerConnectionInterface {


    private static int sTheme;
    NavigationView navigationView;
    private LinearLayout mToolbar;
    /**
     * user picture fro drawer
     */
    ImageView profilePic;
    LinearLayout menu_bg;
    ImageView menu_profiles;

    public static boolean isReturnFromConnectToPartnerPage = false;

    static String pendingPartnerEmail;
    public static BeanRequestsFromPeople[] beanRequestsFromPeoples1;
    static ConnectedPartnerBean connectedPartnerBean;
    static String requestResultType = "";


    private PartnerConnectionInterface partnerConnectionInterface;
    private String TRANSITION_TYPE;

    TextView addPartnerActionBarIcon;
    ImageView partnerImage;
    /**
     * Uri and file variable to store the values for cropping image.
     */
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    String imgString;

    /**
     * variable to handling camera and cropping
     */
    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private static final int REQUEST_CAMERA = 1;


    ServerSync serverSync = new ServerSync();

    /**
     * keys for put extra values
     */

    private static int RESULT_LOAD_IMAGE = 1;

    Bitmap photo;


    /**
     * session variable
     */
    MySharedData mySharedData;

    /**
     * Profile Image Url to send
     */
    private String PROFILE_IMAGE_URL;

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
    private GetUserProfile currentActivity;

    /**
     * form field data variables.
     */
    private EditText fullName;
    private AutoCompleteTextView mGender;
    private AutoCompleteTextView mDOB;
    private AutoCompleteTextView userEmail;
    private ImageView editImage;


    TextInputLayout fullName1, mGender1, mDOB1, userEmail1;
    int color,colors;
    Toolbar toolbar;
    /**
     * string tha containg actual data
     */
    private String uDisplayName = "";
    public static String uGender = "";
    private String uDob = "";
    private String uEmail = "";
    DrawerLayout drawer;

    /**
     * ImageView for profile image
     */

    ImageView profilePicView, nav_close;


    /**
     * User profile Url
     */
    private String USERDATA_URL;

    /**
     * contains url after uploaded to server or facebook profile pic url
     */
    String profilePicUrl = "";

    Animation show, hide;
    Button saveAndContinue;


    public static ManagePartnerConnection managePartnerConnection;
    ImageView drawerHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (LoginActivity.checks == true) {
            System.out.println("maleeeeeeeee");
            setTheme(R.style.MaleTheme);
        } else {
            System.out.println("Femaleeeeeeeee");
            setTheme(R.style.FemaleTheme);
        }*/

        setContentView(R.layout.activity_user_profile);

        fullName = (EditText) findViewById(R.id.full_name);
        mGender = (AutoCompleteTextView) findViewById(R.id.gender);
        mDOB = (AutoCompleteTextView) findViewById(R.id.dob);
        userEmail = (AutoCompleteTextView) findViewById(R.id.verifyEmail);
        editImage = (ImageView) findViewById(R.id.editImage);

        fullName1 = (TextInputLayout) findViewById(R.id.full_name1);
        mGender1 = (TextInputLayout) findViewById(R.id.gender1);
        mDOB1 = (TextInputLayout) findViewById(R.id.dob1);
        userEmail1 = (TextInputLayout) findViewById(R.id.verifyEmail1);


        mToolbar = (LinearLayout) findViewById(R.id.appbars);


        saveAndContinue = (Button) findViewById(R.id.button);
        navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_close = (ImageView) header.findViewById(R.id.imageView4);
        menu_bg = (LinearLayout) header.findViewById(R.id.menu_bg);
        menu_profiles = (ImageView) header.findViewById(R.id.menu_profiles);





      /*  if (LoginActivity.checks == true) {
            System.out.println("maleeeeeeeee111");
//            saveAndContinue.setBackgroundResource(R.drawable.save_blue);
            navigationView.setBackgroundResource(R.drawable.gradient_back_blue);
            menu_bg.setBackgroundResource(R.drawable.profile_dp_l);
            menu_profiles.setBackgroundResource(R.drawable.profile_dp_r);
        } else {
            System.out.println("maleeeeeeeee11122222");
//            saveAndContinue.setBackgroundResource(R.drawable.saveandcontinue);
            navigationView.setBackgroundResource(R.drawable.gradient_back);
            menu_bg.setBackgroundResource(R.drawable.menu_bgs);
            menu_profiles.setBackgroundResource(R.drawable.menu_profiles);
        }*/

        isReturnFromConnectToPartnerPage = false;
        sTheme = R.style.AppTheme1;

        /**
         * get Status Bar height
         */

        Rect r = new Rect();
        View rootview = getWindow().getDecorView(); // this = activity
        rootview.getWindowVisibleDisplayFrame(r);
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        int statusBarHeight = r.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        Log.i("*** Elenasys :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);


        /**
         * initializing the form variables
         */


        show = AnimationUtils.loadAnimation(this, R.anim.open);
        hide = AnimationUtils.loadAnimation(this, R.anim.close);


        /** up casting to parent reference*/
        partnerConnectionInterface = GetUserProfile.this;
        managePartnerConnection = new ManagePartnerConnection(getApplicationContext(), partnerConnectionInterface);


        /**
         * Create file to store image externally
         */
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "profile_pic.jpg");


        // genderSpinner = (AutoCompleteTextView) findViewById(R.id.gender);
        currentActivity = GetUserProfile.this;
        /** initialize session variable */
        mySharedData = new MySharedData(currentActivity);


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


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


         color = Color.parseColor("#ed5544");
         colors = Color.parseColor("#45bbcd");
        setUpperHintColor(colors);




        nav_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cccccclose");
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
            }
        });
        /**
         * Inisialize imageview To set Profile Pic on Drawer.
         */

        profilePic = (ImageView) header.findViewById(R.id.profilepic);
       /* View header = navigationView.getHeaderView(0);
        drawerHead = (ImageView) header.findViewById(R.id.back_head);
        Bitmap image1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.drawer_head);
        Bitmap image2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.profilepic);
        BitmapDrawable ob = new BitmapDrawable(getResources(), overlay(image1,image2));
        drawerHead.setBackground(ob);*/
        /**Initialize the ImageView of Profile Image*/
        profilePicView = (ImageView) findViewById(R.id.circleView);

        /** set url for user Image */
        PROFILE_IMAGE_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.userProfilePic);


        /** get data that came from previous activity*/


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            System.out.println("Checkkk1111111111");
            TRANSITION_TYPE = extras.getString(LoginResource.TRANSITION_TYPE);

            if (TRANSITION_TYPE.equals(LoginResource.SIMPLE_TRANSITION_SLASH)) {
                System.out.println("Checkkk22222222222");
                UserLoginBeanClass beanForLoginData = (UserLoginBeanClass) getIntent().getSerializableExtra(LoginResource.LOGIN_DATA);
                if (beanForLoginData != null) {
                    userEmail.setText(beanForLoginData.getUserEmail());

                    if (beanForLoginData.getEmailConfirmStatus().equals("0")) {
                        userEmail.setHint("Email Address Not Verified.");
                    } else {
                        userEmail.setHint("Email Verified.");
                        userEmail.setClickable(false);
                        userEmail.setFocusable(false);
                    }


                    fullName.setText(beanForLoginData.getUserName());
                    mGender.setText(mySharedData.getGeneralSaveSession(MySharedData.USER_GENDER));
                    mDOB.setText(beanForLoginData.getUserDOB());
                    profilePicUrl = beanForLoginData.getProfilePic();


                }

            } else if (TRANSITION_TYPE.equals(LoginResource.SIMPLE_TRANSITION)) {
                System.out.println("Checkkk3333333333");
//                mGender.setText("Female");
                userEmail.setText(mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL));

            } else if (TRANSITION_TYPE.equals(LoginResource.SOCIAL_TRANSITION)) {
                System.out.println("Checkkk444444444");

                BeanForFacebookData beanForFacebookData = (BeanForFacebookData) getIntent().getSerializableExtra(LoginResource.FACEBOOK_DATA);
                if (beanForFacebookData != null) {

                    System.out.println("beanForFacebookData = " + beanForFacebookData.getEmail());

                    if (beanForFacebookData.getEmail().equals("")) {

                        userEmail.setHint("Please enter your Email Address.");
                    } else {
                        userEmail.setHint("Email Verified.");
                        userEmail.setClickable(false);
                        userEmail.setFocusable(false);
                    }


                    /** set facebook data in view */
                    fullName.setText(beanForFacebookData.getFirstName() + " " + beanForFacebookData.getLastName());
                    mGender.setText(beanForFacebookData.getGender());
                    mDOB.setText(beanForFacebookData.getBirthday());
                    userEmail.setText(beanForFacebookData.getEmail());
                    profilePicUrl = beanForFacebookData.getProfile_pic();

                }

            } else if (TRANSITION_TYPE.equals(LoginResource.EDIT_PROFILE_TRANSITION)) {
                String userProfileResponse = extras.getString(GetUserProfileResource.EDIT_PROFILE_DATA);

                /**TODO do functionality here for edit profile */

            }
        } else {

            String name = mySharedData.getGeneralSaveSession(MySharedData.USER_GENDER);
            System.out.println("banghcsc" + name);
            mGender.setText(name);

        }


        /** set genter AutoCompleteTextView as spinner */
        String[] gender_value = {"Female", "Male"};
        mGender.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, gender_value));
        mGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    mGender.showDropDown();

                }
            }

        });
        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGender.showDropDown();


            }
        });

        mGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadTheme(mGender.getText().toString());

              /*  if (mGender.getText().toString().equals("female")) {
                    animateAppAndStatusBar(R.color.primary, R.color.CHECK_COLOR);
                } else {
                    animateAppAndStatusBar(R.color.accent, R.color.CHECK_COLOR);
                }*/

              /*  Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);*/


                /**
                 * Animation Effect
                 */
//                boolean on = ((ToggleButton) view).isChecked();


                System.out.println("ffffffffffff");


            }
        });


        mGender.setListSelection(1);

        /** set picture from facebook if exist */
        if (!profilePicUrl.equals("")) {

            new Thread() {
                public void run() {

                    final Bitmap fbPic = serverSync.fireUrlGetBitmap(profilePicUrl);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (fbPic != null) {

                                profilePicView.setImageBitmap(fbPic);


                            } else {
                                loadTheme(mGender.getText().toString());
                                //  profilePicView.setImageResource(R.drawable.profilepic);
                            }
                            progressBarManager.stopProgressBar();
                        }
                    });
                }
            }.start();
        } else {
            loadTheme(mGender.getText().toString());
            progressBarManager.stopProgressBar();
        }

        /** set gender spinner according to facebook data */
       /* ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_item,
                this.getResources().getTextArray(R.array.gender_value));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);


        if (gender.toLowerCase().equals("male")) {
            genderSpinner.setSelection(0);
        } else if (gender.toLowerCase().equals("female")) {
            genderSpinner.setSelection(1);
        }*/

        /** initialize current ativity */
        currentActivity = GetUserProfile.this;


        /** set url for user registration */
        USERDATA_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.userProfileData);


        /**
         * initialize activity switcher
         */
        switchActivity = new SwitchActivities();


        mDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mDOB.setError(null);
                    openCalender();
                }
            }

        });

        mDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender();
            }
        });


        /**
         *  Click Listener on the profile imageview
         */
       /* profilePicView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectImageOption();
            }
        });*/


        /** set user name and email on drawer*/
        TextView nav_username = (TextView) header.findViewById(R.id.nav_name);
        TextView nav_email = (TextView) header.findViewById(R.id.nav_email);

        nav_username.setText(mySharedData.getGeneralSaveSession(MySharedData.USERNAME));
        nav_email.setText(mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL));


        /** first we load user profile pic that was save earlier */
        if (mySharedData.getGeneralSaveSession(MySharedData.saveProfileImage).equals("")) {
            if (mGender.getText().toString().equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {

            profilePic.setImageBitmap(ImageHandler.getProfileImageFromSharedPrefernces(mySharedData));
        }

        /** code for profile pic */
        if (profilePicUrl.equals("")) {
            if (mGender.getText().toString().equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {
            ImageHandler imageHandler = new ImageHandler();
            imageHandler.loadProfilePicForDrawer(profilePic, currentActivity);
        }


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateAppAndStatusBar(int fromColor, final int toColor) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mToolbar,
                mToolbar.getWidth() / 2,
                mToolbar.getHeight() / 2, 0,
                mToolbar.getWidth() / 2);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToolbar.setBackgroundColor(getResources().getColor(toColor));

            }
        });

//        mRevealBackgroundView.setBackgroundColor(getResources().getColor(fromColor));
        animator.setStartDelay(250);
        animator.setDuration(250);
        animator.start();
        mToolbar.setVisibility(View.VISIBLE);
    }


    public static void setInputTextLayoutColor(EditText editText, @ColorInt int color) {
        TextInputLayout til = (TextInputLayout) editText.getParent();
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{color}));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{color}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStyleForTextForAutoComplete(int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(fullName.getBackground());
        Drawable wrappedDrawable1 = DrawableCompat.wrap(mGender.getBackground());
        Drawable wrappedDrawable2 = DrawableCompat.wrap(mDOB.getBackground());
        Drawable wrappedDrawable3 = DrawableCompat.wrap(userEmail.getBackground());

        DrawableCompat.setTint(wrappedDrawable, color);
        DrawableCompat.setTint(wrappedDrawable1, color);
        DrawableCompat.setTint(wrappedDrawable2, color);
        DrawableCompat.setTint(wrappedDrawable3, color);

        fullName.setBackgroundDrawable(wrappedDrawable);
        mGender.setBackgroundDrawable(wrappedDrawable1);
        mDOB.setBackgroundDrawable(wrappedDrawable2);
        userEmail.setBackgroundDrawable(wrappedDrawable3);
    }


    private void setUpperHintColor(int color) {
        try {
            Field field = fullName1.getClass().getDeclaredField("mFocusedTextColor");
            field.setAccessible(true);
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    color
            };
            ColorStateList myList = new ColorStateList(states, colors);
            field.set(fullName1, myList);

            Method method = fullName1.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(fullName1, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void loadTheme(String gender) {
        System.out.println("banghcscssss" + gender);
        mySharedData.setGeneralSaveSession(MySharedData.USER_GENDER, gender);


        if (gender.equals("Female")) {

            GetUserProfile.this.setTheme(R.style.green);
            LoginActivity.checks = false;
            Toast.makeText(currentActivity, "female", Toast.LENGTH_SHORT).show();
            navigationView.setBackgroundResource(R.drawable.gradient_back);
            menu_bg.setBackgroundResource(R.drawable.menu_bgs);
            menu_profiles.setBackgroundResource(R.drawable.menu_profiles);

            fullName1.setHintTextAppearance(R.style.feamleAutocomplete);
            mDOB1.setHintTextAppearance(R.style.feamleAutocomplete);
            mGender1.setHintTextAppearance(R.style.feamleAutocomplete);
            userEmail1.setHintTextAppearance(R.style.feamleAutocomplete);
            setStyleForTextForAutoComplete(getResources().getColor(R.color.female_colorAccent));


            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            for (int i = 0; i < toolbar.getChildCount(); i++) {
                final View v = toolbar.getChildAt(i);

                if (v instanceof ImageButton) {
                    ((ImageButton) v).setColorFilter(colorFilter);
                }
            }


//            setInputTextLayoutColor(fullName, colorBlue);
        /*    fullName.setHighlightColor(Color.BLUE);
            fullName.setHintTextColor(Color.BLUE);

            Drawable background = textInputLayout.getEditText().getBackground();
            DrawableCompat.setTint(background, Color.BLUE);
            fullName.setBackground(background);*/


            saveAndContinue.setBackgroundResource(R.drawable.saveandcontinue);
            if (profilePicUrl.equals("")) {
                profilePicView.setImageResource(R.drawable.profilepic);
            }

            if (partnerProfileImage.equals("") && partnerImage != null) {
                partnerImage.setImageResource(R.drawable.add);
            }

        } else {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(colors, PorterDuff.Mode.SRC_ATOP);

            for (int i = 0; i < toolbar.getChildCount(); i++) {
                final View v = toolbar.getChildAt(i);

                if (v instanceof ImageButton) {
                    ((ImageButton) v).setColorFilter(colorFilter);
                }
            }
          /*  fullName.setHighlightColor(Color.RED);
            fullName.setHintTextColor(Color.RED);

            Drawable background = textInputLayout.getEditText().getBackground();
            DrawableCompat.setTint(background, Color.RED);
            fullName.setBackground(background);*/

            fullName1.setHintTextAppearance(R.style.maleAutocomplete);
            mDOB1.setHintTextAppearance(R.style.maleAutocomplete);
            mGender1.setHintTextAppearance(R.style.maleAutocomplete);
            userEmail1.setHintTextAppearance(R.style.maleAutocomplete);
            setStyleForTextForAutoComplete(getResources().getColor(R.color.male_colorAccent));


            saveAndContinue.setBackgroundResource(R.drawable.save_blue);
            navigationView.setBackgroundResource(R.drawable.gradient_back_blue);
            menu_bg.setBackgroundResource(R.drawable.profile_dp_l);
            menu_profiles.setBackgroundResource(R.drawable.profile_dp_r);
            Toast.makeText(currentActivity, "Male", Toast.LENGTH_SHORT).show();

            LoginActivity.checks = true;

            if (profilePicUrl.equals("")) {
                profilePicView.setImageResource(R.drawable.boy_circle);
            }

            if (partnerProfileImage.equals("") && partnerImage != null) {
                partnerImage.setImageResource(R.drawable.girl_heart);
            }

//            setInputTextLayoutColor(fullName, colorRed);
        }
    }

    private void openCalender() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(currentActivity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                selectedmonth = selectedmonth + 1;
                mDOB.setText(selectedday + "/" + selectedmonth + "/" + selectedyear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.show();
    }


    /**
     * select Option to choose image for user profile
     */

    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(GetUserProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                /**
                 * Image SElect From Camera by capture image
                 */
                if (items[item].equals("Capture Photo")) {
                    // Check permission for CAMERA
                    if (ActivityCompat.checkSelfPermission(GetUserProfile.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Check Permissions Now
                        // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                        ActivityCompat.requestPermissions(GetUserProfile.this,
                                new String[]{Manifest.permission.CAMERA},
                                GetUserProfile.REQUEST_CAMERA);
                    } else {
                        // permission has been granted, continue as usual
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                        mImageCaptureUri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, CAMERA_CODE);
                    }

                    /**
                     * Choose Image From Gallery
                     */
                } else if (items[item].equals("Choose from Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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

        if (ContextCompat.checkSelfPermission(GetUserProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * After Choose image from gallery this activity will start to get image
         * path and set the image in image profilePicView.
         */
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * get Url of selected gallery Image
         */
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();
            System.out.println("Gallery Image URI : " + mImageCaptureUri);
            CropingIMG();

            /**
             * get Url of captured Camera Image
             */
        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            System.out.println("Camera Image URI : " + mImageCaptureUri);
            CropingIMG();

            /**
             * get data of cropped image
             */
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    photo = ImageHandler.decodeFile(outPutFile);
                    System.out.println("nfghf" + photo);
                    profilePicView.setImageBitmap(photo);
                    /** get the base 64 string*/
                    imgString = Base64.encodeToString(ImageHandler.getBytesFromBitmap(photo),
                            Base64.DEFAULT);
                    System.out.println("getttyte" + imgString);
                    // convert from bitmap to byte array

//                    getBytesFromBitmap(photo);


                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarManager.startProgressBar();
                        }
                    });

                    final Map<String, String> postData = getKeyPairValueInHashMap(imgString);
                    new Thread() {
                        public void run() {
                            sendImageOnServer(postData);

                        }
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void CropingIMG() {

        final ArrayList<Croping> cropOptions = new ArrayList<Croping>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final Croping co = new Croping();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String ImagePath) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {GetUserProfileResource.USER_ID, GetUserProfileResource.TOKEN, GetUserProfileResource.KEY_IMAGE};      // set keys

        String value[] = {mySharedData.getGeneralSaveSession(MySharedData.USERID), mySharedData.getGeneralSaveSession(MySharedData.TOKEN), ImagePath};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * Save the Image Name on server
     */

    public void sendImageOnServer(Map<String, String> postData) {


        /** send image on server */


        String reponse = serverSync.SyncServer(postData, PROFILE_IMAGE_URL);

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            profilePicUrl = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then Image Uploading successful. */
                mySharedData.setGeneralSaveSession(mySharedData.USER_PROFILE_PIC, profilePicUrl);
                System.out.println("profilePicUrl = " + profilePicUrl);

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profilePicView.setImageBitmap(photo);
                        progressBarManager.stopProgressBar();
                    }
                });


            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(profilePicUrl, "Sorry!");


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
                LoveLifePopups.loveLifePopup(GetUserProfile.this, titleText, message);
            }
        });


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
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void updateUserProfile() {
        /** need to initialize again as the variable is static and it initialized on 2nd profile page also.*/
        progressBarManager = new ProgressBarManager(currentActivity);

           /*   // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);*/

        // Store values at the time of the login attempt.

        uDisplayName = fullName.getText().toString();
        uGender = mGender.getText().toString();
        uDob = mDOB.getText().toString();
        uEmail = userEmail.getText().toString();

        Date birthDate = null;
        /** calculate age */
        if (!uDob.equals("")) {
            StringTokenizer tokens = new StringTokenizer(uDob, "/");
            Toast.makeText(currentActivity, "tokens.countTokens() " + tokens.countTokens(), Toast.LENGTH_LONG).show();
            if (tokens.countTokens() == 3) {
                int day = Integer.parseInt(tokens.nextToken());// this will contain "Fruit"
                int month = Integer.parseInt(tokens.nextToken());
                int year = Integer.parseInt(tokens.nextToken());
                System.out.println("checkkk" + day + month + year);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                //Yeh !! It's my date of birth :-)
                try {
                    birthDate = sdf.parse(uDob);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid First Name.
        if (TextUtils.isEmpty(uDisplayName)) {
            fullName.setError(getString(R.string.invalid_displayName));
            focusView = fullName;
            cancel = true;
        } else if (TextUtils.isEmpty(uDob)) {
            mDOB.setError(getString(R.string.invalid_dob));
            focusView = mDOB;
            cancel = true;
        } else if (birthDate == null) {
            mDOB.setError(getString(R.string.wrong_date));
            focusView = mDOB;
            cancel = true;
        } else if (!TimeUtility.doesUserPassesTimeConsraint(birthDate)) {
            mDOB.setError(getString(R.string.ageConstraint));
            focusView = mDOB;
            cancel = true;
        } else if (!FormValidation.isValidEmail(uEmail)) {
            userEmail.setError(getString(R.string.invalid_Email));
            focusView = userEmail;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            /** Start progress bar */
            progressBarManager.startProgressBar();


            final Map<String, String> postData = getKeyPairValueInHashMapForProfileUpdate(uDisplayName, uGender, uDob, uEmail);


            new Thread() {
                public void run() {
                    updateUser(postData);
                }
            }.start();
        }
    }


    /**
     * Submit user data on server
     *
     * @param postData contains the post data that would be send to server.
     */
    private void updateUser(Map<String, String> postData) {


        /** submit user data on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, USERDATA_URL);

        System.out.println("user data submit response " + reponse);


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


                if (emailStatus.equals("0")) {

//                    if (mySharedData.getGeneralSaveSession(MySharedData.FIRST_ADDRESSANDFAVORITES_UPDATE).equals("yes")) {

                        /** Stop progress bar */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarManager.stopProgressBar();
                                LoveLifePopups.loveLifePopup(GetUserProfile.this, "Congratulations!", message + ". Please confirm you email Id to Continue.");
                            }
                        });
                   /* } else {
                        switchActivity.openActivity(currentActivity, UserAddressAndFavorites.class);

                    }*/

                } else {

//                    if (mySharedData.getGeneralSaveSession(MySharedData.FIRST_ADDRESSANDFAVORITES_UPDATE).equals("yes")) {

                        /** Stop progress bar */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarManager.stopProgressBar();
//                                switchActivity.openActivity(currentActivity, Dashboard.class);
                                switchActivity.openActivity(currentActivity, UserAddressAndFavorites.class);
                                GetUserProfile.this.startService(new Intent(GetUserProfile.this, ChatService.class));

                                mySharedData.setGeneralSaveSession(MySharedData.FIRST_GETUSERPROFILE_UPDATE, "yes");
                                mySharedData.setGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN, "yes");
                                finish();
                            }
                        });
                   /* } else {
                        switchActivity.openActivity(currentActivity, UserAddressAndFavorites.class);
                    }*/


                  /*  *//** Stop progress bar *//*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBarManager.stopProgressBar();
                            switchActivity.openActivity(currentActivity, Dashboard.class);
                        }
                    });*/

                }
                /** Stop progress bar */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarManager.stopProgressBar();

                    }
                });


                //   switchActivity.openActivityWithPutExtra(currentActivity, UserAddressAndFavorites.class, new String[]{DISPLAY_NAME, DOB, GENDER, HEIGHT, WEIGHT, PIC_URL, RegisterActivity.EMAIL, RegisterActivity.PASSWORD, RegisterActivity.TRANSITION_FROM, ProfilePage.USER_DATA}, new String[]{displayName, dob, gender, height, weight, fbProfilePic, registered_email, registered_md5Password, RECEIVED_FROM, editProfileData});
                //  finish();


                /** stop progress bar here if popup removed from here */

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(message, "Sorry!");

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        popup("Server problem. Please try after some time.", "Sorry!");

                    }
                });

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressBarManager.stopProgressBar();

                }
            });


        }


    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMapForProfileUpdate(String fullName, String gender, String dob, String userEmail) {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        String key[] = {GetUserProfileResource.DISPLAY_NAME, GetUserProfileResource.GENDER, GetUserProfileResource.DOB, GetUserProfileResource.EMAIL, GetUserProfileResource.KEY_IMAGE, GetUserProfileResource.USER_ID, GetUserProfileResource.TOKEN};      // set keys
        String value[] = {fullName, gender, dob, userEmail, profilePicUrl, mySharedData.getGeneralSaveSession(MySharedData.USERID), mySharedData.getGeneralSaveSession(MySharedData.TOKEN)};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }

    MenuItem filterMenuItem;
    ProgressBar progressBar2;
    RelativeLayout partnerContainer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        /** load partner data */
        new Thread() {
            public void run() {
                startLoadingInActionBar();
                managePartnerConnection.loadRequests();
            }

        }.start();


        inflater.inflate(R.menu.profile_page, menu);
        final Menu m = menu;
        filterMenuItem = menu.findItem(R.id.partnerAdded);
        final View menu_list = filterMenuItem.getActionView();

        addPartnerActionBarIcon = (TextView) menu_list.findViewById(R.id.actionbar_notifcation_textview1);
        partnerContainer = (RelativeLayout) menu_list.findViewById(R.id.partnerContainer);
        progressBar2 = (ProgressBar) menu_list.findViewById(R.id.progressBar2);
        partnerImage = (ImageView) menu_list.findViewById(R.id.partnerImage);

        /** load image theme */
        loadTheme(mGender.getText().toString());

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
            uGender = mGender.getText().toString();
            if (requestResultType.equals("noEventOccur")) {
                Toast.makeText(GetUserProfile.this, "noEventOccur", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"GetUserProfile"});

            } else if (requestResultType.equals("requestIsPending")) {

                Toast.makeText(GetUserProfile.this, "requestIsPending", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"GetUserProfile"});

            } else if (requestResultType.equals("requestsReceived")) {

                Toast.makeText(GetUserProfile.this, "requestsReceived", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"GetUserProfile"});

            } else if (requestResultType.equals("onPartnerConnected")) {

                Toast.makeText(GetUserProfile.this, "onPartnerConnected", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, PartnerProfile.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"GetUserProfile"});

            } else if (requestResultType.equals("onNetworkError")) {
                popup();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * For getting Profile Details
     */

    String partnerProfileImage = "";

    public void setImageFromString(final String imagePath, final ImageView partnerImage) {

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
                        partnerProfileImage = imagePath;

                    }
                });


                System.out.println("The Bitmap okkk");
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                        loadTheme(mGender.getText().toString());
                        //  partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                    }
                });


                System.out.println("The Bitmap is NULL");
            }

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                    loadTheme(mGender.getText().toString());
                    //partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                }
            });

            System.out.println("error1111111" + e);
        } finally {
            stopLoadingInActionBar();
        }

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


    public void saveAndContinue(View view) {


        updateUserProfile();
    }

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));

    }


    public void editProfileImage(View view) {
        selectImageOption();
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


    private void stopProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
                    /*errorView.setVisibility(View.VISIBLE);
                    mainView.setVisibility(View.GONE);*/
            }
        });
    }


    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }


}

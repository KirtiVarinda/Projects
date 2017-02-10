package com.dx.dataapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.getfixrService.LocationTrackerService;
import com.dx.getfixrService.MyService;
import com.dx.model.Fixr;
import com.dx.model.FixrImage;
import com.dx.network.SendFixrDataToServer;
import com.dx.network.ServerSync;
import com.dx.readcallLogs.CallLogData;
import com.dx.readcallLogs.ReadCallLogs;
import com.loopj.android.http.RequestParams;
import com.dx.FixrNumberManager.MyParsingMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DataActivity extends Activity {
    Button loginButton, closeApp;
    int loginAttemptedLeft = 3;
    LauncherClass launcher;
    boolean mcamera, gallery;
    boolean locationReceived = false;
    private Drawable defaultDrawable;
    static final int RESULT_LOAD_IMAGE = 11;
    String verifyValue = "false";
    ProgressBar verifyProgress;
    TextView loginAttemped, verifyButton, currentLocationAddress;
    double currentLat, currentLang = 0;
    EditText mName, mPhone, mAddress, mAddressArea, mPerDayCost, mPerVisitCost, mCertification, callEdit;
    Spinner sCity, sLevel, sTrades, mExperience_years, mExperience_months;
    MultiSelectSpinner sWorkType;
    SessionData session;
    Button btnSubmit;
    boolean activity_finished;
    LinearLayout tagLayout;
    int unlock = 0;
    RequestParams params = new RequestParams();
    List<String> listCity, listWorkType;
    ArrayAdapter<String> cityAdapter;
    ArrayAdapter<String> workTypeAdapter;
    String[] strWorkType;
    TextView tab1, tab2, tab3, addNumber;
    boolean temp = true;
    WebView webview;
    LinearLayout mCalling;
    ProgressDialog pd;
    ScrollView scrollView;
    ImageView imgThumbnail, callButton;
    SharedPreferences sp;
    Context context;
    DatabaseHandler dbHandler;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean isNetworkAvilable;
    boolean isGpsAvilable;
    Context ctx;
    final int camera_open = 1;
    //captured picture uri
    private Uri picUri;
    File FilepicUri;
    String loginCheck;

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
       // System.out.println("Window focus lost hasFocus= " + hasFocus);
        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);
        if (!hasFocus) {
           // System.out.println("Window focus lost");
            //Intent closeDialog1 = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            //sendBroadcast(closeDialog1);
            //showToast("no focus");
            if (!activity_finished && !mcamera && !gallery) {
               // System.out.println("Window focus lost1");
                Intent i1 = new Intent(getApplicationContext(), DataActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                try{
                    startActivity(i1);
                }catch(Exception e){
                    System.out.println("activity already finish");
                    finish();
                }

                overridePendingTransition(0, 0);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionData(getApplicationContext());


        GeneralMethods.run = true;

        launcher = new LauncherClass(getApplicationContext());
        launcher.makePrefered("enable", "1");
        context = getApplicationContext();

        dbHandler = new DatabaseHandler(getApplicationContext());
        ctx = getApplicationContext();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean("firstTime1", false)) {
            new Thread() {
                public void run() {

                    MyParsingMethods gm = new MyParsingMethods();
                    try {
                        gm.getCitiesXmlandSaveToDatabase(ctx);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("firstTime1", true);
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /**

                     List<FixrMobile> mobList=db.getAllFixrMObile();
                     for(int h=0;h<mobList.size();h++){
                     System.out.println("MObilr from database="+mobList.get(h).getmMobileNumber());
                     }
                     */

                }
            }.start();


        }

        activity_finished = false;
        sp = getSharedPreferences("SessionData", MODE_PRIVATE);

        addActivityStatusInSession();


        loginCheck = session.getGeneralSaveSession(SessionData.privateLoginCheck);
        if (loginCheck.equals("") || loginCheck.equals("yes")) {
            loadLoginResources();
          //  System.out.println("my login");

        } else {
           // System.out.println("my access");
            loadResources();
        }


    }


    /**
     * function to load login page
     */
    EditText user, pass1;

    private void loadLoginResources() {
        setContentView(R.layout.login_page);
        // getActionBar().hide();
        loginAttemped = (TextView) findViewById(R.id.textView4);
        user = (EditText) findViewById(R.id.getemail);
        pass1 = (EditText) findViewById(R.id.getpass1);
        loginButton = (Button) findViewById(R.id.button);

    }

    /**
     * function for login page validation.
     */
    public void loginGetfixr(View view) {
        String userText = user.getText().toString().replaceAll("\\s+", "").trim();
        String userpass = pass1.getText().toString().replaceAll("\\s+", "").trim();
        if (userText.equals("")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter valid username and password.", "OK");
        } else if (userpass.equals("")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter valid username and password.", "OK");
        } else {
            new DoInBackground().execute("login");

        }
    }

    private void loadResources() {
        setContentView(R.layout.activity_data);
        // getActionBar().hide();


        String userType = session.getGeneralSaveSession(SessionData.privateUserType);
        /**
         * get references from layout.
         *
         */
        addNumber = (TextView) findViewById(R.id.addNumber);
        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab3 = (TextView) findViewById(R.id.tab3);
        webview = (WebView) findViewById(R.id.webview);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        mCalling = (LinearLayout) findViewById(R.id.calling);
        sCity = (Spinner) findViewById(R.id.spinnerCity);
        sTrades = (Spinner) findViewById(R.id.spinner_trade);
        sLevel = (Spinner) findViewById(R.id.spinner_level);
        sWorkType = (MultiSelectSpinner) findViewById(R.id.spinner_work_type);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        imgThumbnail = (ImageView) findViewById(R.id.imgthumbnail);
        tagLayout = (LinearLayout) findViewById(R.id.tags);
        verifyButton = (Button) findViewById(R.id.verifybutton);
        verifyProgress = (ProgressBar) findViewById(R.id.progressBar);
        currentLocationAddress = (TextView) findViewById(R.id.textView3);
        closeApp = (Button) findViewById(R.id.button3);
        defaultDrawable = verifyButton.getBackground();
        callEdit = (EditText) findViewById(R.id.editText2);
        callButton = (ImageView) findViewById(R.id.imageView3);

        if (userType.equals("admin")) {
            closeApp.setVisibility(View.VISIBLE);
            // callEdit.setVisibility(View.GONE);
            // callButton.setVisibility(View.GONE);
        } else if (userType.equals("user")) {
            closeApp.setVisibility(View.GONE);
            //  callEdit.setVisibility(View.VISIBLE);
            // callButton.setVisibility(View.VISIBLE);
        }


        sCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setVerifyButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setVerifyButton();
            }
        });
        sTrades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tagLayout.setVisibility(View.VISIBLE);

                if (parentView.getItemAtPosition(position).toString().equals("Carpenter")) {
                    strWorkType = getResources().getStringArray(R.array.worktype_carpenter);
                    sWorkType.setItems(strWorkType);
                } else if (parentView.getItemAtPosition(position).toString().equals("Electrician")) {
                    strWorkType = getResources().getStringArray(R.array.worktype_electrician);
                    sWorkType.setItems(strWorkType);
                } else if (parentView.getItemAtPosition(position).toString().equals("Plumber")) {
                    strWorkType = getResources().getStringArray(R.array.worktype_plumber);
                    sWorkType.setItems(strWorkType);
                } else if (parentView.getItemAtPosition(position).toString().equals("Mason")) {
                    strWorkType = getResources().getStringArray(R.array.worktype_mason);
                    sWorkType.setItems(strWorkType);
                } else if (parentView.getItemAtPosition(position).toString().equals("Painter")) {
                    strWorkType = getResources().getStringArray(R.array.worktype_painter);
                    sWorkType.setItems(strWorkType);
                } else {
                    tagLayout.setVisibility(View.GONE);
                }

                /**
                 * get the work categories to select.
                 *
                 */
                // strWorkType = GeneralMethods.getWorkTypeList();


                /**
                 * set the work types in the spinner.
                 *
                 */

                workTypeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.spinner_item, listWorkType);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        /**
         * edit text.
         */
        mName = (EditText) findViewById(R.id.edit_name);
        mPhone = (EditText) findViewById(R.id.editPhone);
        mAddress = (EditText) findViewById(R.id.edit);
        mAddressArea = (EditText) findViewById(R.id.edit2);
        mPerDayCost = (EditText) findViewById(R.id.edit_work_hours);
        mPerVisitCost = (EditText) findViewById(R.id.edit_avg_cost);
        mCertification = (EditText) findViewById(R.id.edit_certification);
        mExperience_years = (Spinner) findViewById(R.id.edit_experience);
        mExperience_months = (Spinner) findViewById(R.id.edit_experience1);

        /* accessToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Access token=" + accessToken.getText().toString());
                if (accessToken.getText().toString().equals(getResources().getString(R.string.access_token))) {
                    finishActivity();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        /**
         *
         */
        imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });


        /**
         * set initial tab color
         */

        tab1.setBackgroundResource(R.drawable.rectangle_shape2);
        tab2.setBackgroundResource(R.drawable.rectangle_shape);
        tab3.setBackgroundResource(R.drawable.rectangle_shape);

        /**
         * get cities from the server.
         *
         */
        listCity = new ArrayList<String>();
        SessionData data = new SessionData(context);
        String cities_length = data.getGeneralSaveSession("dataApp_cities_length");
        if (!cities_length.equals("")) {
            listCity = GeneralMethods.reteriveCitiesFromSession(cities_length, context);
            setSpinner();
        } else {
            new Thread() {
                public void run() {
                    syncDatacityFromServer();
                    DataActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSpinner();
                        }
                    });

                }
            }.start();


        }

        syncDatacityFromServer();


        /**
         * set the fixr list in the spinner.
         *
         */
        ArrayAdapter<String> tradeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, GeneralMethods.getFixrList());
        sTrades.setAdapter(tradeAdapter);


        /**
         * set the work level list in the spinner.
         *
         */
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, GeneralMethods.getWorkLevelList());
        sLevel.setAdapter(levelAdapter);

        loadWebView();
        // manageAlarms(getApplicationContext(),false);
    }

    public void closeApp(View view) {
        userConformation("closeApp", "Do you want to close the application.");

    }

    private void setVerifyButton() {
        verifyValue = "false";
        verifyButton.setBackgroundDrawable(defaultDrawable);
        verifyButton.setText("Verify");
    }

    private void finishActivity() {
        activity_finished = true;
        removeActivityStatusFromSession();
        // manageAlarms(getApplicationContext(),true);
        // cancelAlarms(getApplicationContext());
        // activity_finished=true;
        resetAllPickedImages();
        launcher.makePrefered("disable", "1");
        finish();
    }

    private void syncDatacityFromServer() {
        if (GeneralMethods.isNetworkActive(getApplicationContext())) {
            ServerSync serverSync = new ServerSync();
            String cities = serverSync.getDataFromServer(getResources().getString(R.string.fixr_city_url));
            if (!cities.equals("error")) {
                listCity = GeneralMethods.saveCitiesToSession(cities, getApplicationContext());
            }
        }
    }

    private void setSpinner() {
        cityAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, listCity);

        if (cityAdapter != null)
            sCity.setAdapter(cityAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();


        String userType=session.getGeneralSaveSession(SessionData.privateUserType);
        if(userType.equals("user") || userType.equals("admin")){

            context.startService(new Intent(context, LocationTrackerService.class));

        }else{
            AlarmsManager alarm=new AlarmsManager();
            alarm.setAlarm(10*60*1000l,context);
        }

        mcamera = false;
        gallery = false;
        activity_finished = false;
        manageAlarms(getApplicationContext(), true);

    }

    private void loadWebView() {
        if (GeneralMethods.isNetworkActive(getApplicationContext())) {
            // scrollView.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                /* @Override
                 public void onPageStarted(WebView view, String url, Bitmap favicon) {
                     // TODO Auto-generated method stub
                     super.onPageStarted(view, url, favicon);

                 }*/
                @Override
                public void onLoadResource(WebView webview, String url) {
                    try {
                        if (pd == null && temp) {
                            temp = false;
                            pd = new ProgressDialog(DataActivity.this);
                            pd.setTitle("Loading");
                            pd.setMessage("Please wait...");
                            pd.setIndeterminate(false);
                            // pd.setCancelable(false);
                            pd.show();
                        }


                        //  pd = ProgressDialog.show(WebActivity.this,"Connecting","Please Wait..");
                    } catch (Exception e) {

                    }

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    temp = true;
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);

                    } else {
                        view.loadUrl(url);
                    }
                    return true;

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // TODO Auto-generated method stub
                    try {
                        if (pd.isShowing()) {
                            pd.dismiss();
                            pd = null;
                        }


                    } catch (Exception e) {

                    }


                    // progressBar.setVisibility(View.GONE);
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }


    public void addActivityStatusInSession() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("app_active", true);
        ed.commit();
    }

    public void removeActivityStatusFromSession() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("app_active", false);
        ed.commit();

    }


    @Override
    public void onBackPressed() {
        // do nothing.
        if (!loginCheck.equals("") && !loginCheck.equals("yes")) {
            if (webview.getVisibility() == View.VISIBLE && webview.canGoBack()) {

                webview.goBack();
            } else {
                // Let the system handle the back button

         /*   if (unlock == 10) {
                System.out.println("Back pressed");

                finishActivity();
            }else{
                unlock++;
            }
          */
            }

        }


        //return;
    }


    protected void manageAlarms(Context context, boolean cancel) {
        Intent intent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                getApplicationContext(), 2, intent, 0);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(), 1000,
                sender);
        if (cancel) {
            am.cancel(sender);
        }
    }


    /**
     * Registration form open on button click
     *
     * @param view
     */
    public void registerationForm(View view) {


        tab1.setBackgroundResource(R.drawable.rectangle_shape2);
        tab2.setBackgroundResource(R.drawable.rectangle_shape);
        tab3.setBackgroundResource(R.drawable.rectangle_shape);
        tab1.setTextColor(Color.parseColor("#ffffff"));
        tab2.setTextColor(Color.parseColor("#f3b607"));
        tab3.setTextColor(Color.parseColor("#f3b607"));
        scrollView.setVisibility(View.VISIBLE);
        webview.setVisibility(View.GONE);
        mCalling.setVisibility(View.GONE);

    }

    /**
     * Website webview open on button click
     *
     * @param view
     */
    public void websiteView(View view) {

        // tab2.setBackgroundColor(Color.parseColor("#f3b607"));
        // tab1.setBackgroundColor(Color.parseColor("#ffffff"));
        tab1.setBackgroundResource(R.drawable.rectangle_shape);
        tab2.setBackgroundResource(R.drawable.rectangle_shape2);
        tab3.setBackgroundResource(R.drawable.rectangle_shape);
        tab1.setTextColor(Color.parseColor("#f3b607"));
        tab2.setTextColor(Color.parseColor("#ffffff"));
        tab3.setTextColor(Color.parseColor("#f3b607"));
        webview.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        mCalling.setVisibility(View.GONE);
        webview.loadUrl(getString(R.string.site_url));

    }

    /**
     * Website webview open on button click
     *
     * @param view
     */
    public void callView(View view) {

        // tab2.setBackgroundColor(Color.parseColor("#f3b607"));
        // tab1.setBackgroundColor(Color.parseColor("#ffffff"));

        tab1.setBackgroundResource(R.drawable.rectangle_shape);
        tab2.setBackgroundResource(R.drawable.rectangle_shape);
        tab3.setBackgroundResource(R.drawable.rectangle_shape2);
        tab1.setTextColor(Color.parseColor("#f3b607"));
        tab2.setTextColor(Color.parseColor("#f3b607"));
        tab3.setTextColor(Color.parseColor("#ffffff"));
        webview.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        mCalling.setVisibility(View.VISIBLE);


        /**  call log list view
         */
        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayList<CallLogData> logData = ReadCallLogs.getCallDetails(ctx);

        CallLogAdapter adapter = new CallLogAdapter(getApplicationContext(), R.layout.call_list_row, logData);
        listView.setAdapter(adapter);

    }

    /**
     * onSubmit of registeration form.
     *
     * @param view
     */
    public void onSubmit(View view) {


        //    decodeFile(profile_pic);
        //  new ImageUploadTask().execute();


        //get session variable
        String profile_pic_uri = session.getGeneralSaveSession("profile_pic");   //get profile pic uri
        String profile_pic_uri_thumb = session.getGeneralSaveSession("profile_pic_thumb");

        boolean validate = formValidation(profile_pic_uri);

        if (validate) {

            // showToast("ok");

            /**
             * get profile uri and gallery image uri
             */
            String phone = addNumber.getText().toString();                           //get phone
            String[] allNumbers = phone.split(",");
            String profile650 = "";
            String phone5 = allNumbers[0].substring(allNumbers[0].length() - 5, allNumbers[0].length());  //retrieve last 5 digit of phone
            String[] str = mName.getText().toString().split(" ");                 //retrieve first name
            //System.out.println("5 number=" + phone5);
            /**
             * create a custom name for progile image
             */


            //System.out.println("ImagesName="+phone5);

            String name = str[0];
            profile650 = sCity.getSelectedItem().toString() + "~" + phone5 + "_" + name.toLowerCase() + "_profile";
            String profileThumb = profile650 + "_t";
            // showToast(profile650);
            /**
             * get length of saved gallery images
             */
            String length1 = session.getGeneralSaveSession("length_of_saved_images");
            int length;
            if (length1.equals("")) {
                length = 0;
            } else {
                length = Integer.parseInt(length1);
            }

            /**
             * initialize gallery images array
             */
            String[] galleryUris = null;
            String[] galleryImageNames = null;
            galleryUris = new String[length];
            if (length > 0) {
                galleryImageNames = new String[length];
            }

            /**
             * save gallery images in database
             */
            String visit = "";
            String perDay = "";
            if (mPerVisitCost.getText().toString().equals("")) {
                visit = "N/A";
            } else {
                visit = mPerVisitCost.getText().toString();
            }
            if (mPerDayCost.getText().toString().equals("")) {
                perDay = "N/A";
            } else {
                perDay = mPerDayCost.getText().toString();
            }

            //    if(!profile_pic_uri.equals("")){
            //    showToast("profile_pic_uri is not null");
            dbHandler.saveFixr(new Fixr(mName.getText().toString().toLowerCase(),
                    mAddress.getText().toString() + ", " + mAddressArea.getText().toString(),
                    sCity.getSelectedItem().toString(),
                    addNumber.getText().toString(),
                    sWorkType.getSelectedItemsAsString(),
                    sTrades.getSelectedItem().toString(),
                    sLevel.getSelectedItem().toString(),
                    perDay,
                    visit,
                    mCertification.getText().toString(),
                    mExperience_years.getSelectedItem().toString() + "~" + mExperience_months.getSelectedItem().toString(),
                    verifyValue,
                    session.getGeneralSaveSession(SessionData.privateUserName),
                    session.getGeneralSaveSession(SessionData.privateDataLatitude),
                    session.getGeneralSaveSession(SessionData.privateDataLongitude)
            ));
            int lnth = dbHandler.getSavedFixrLength();
            //  Toast.makeText(getApplicationContext(), "record length is " + lnth, Toast.LENGTH_SHORT).show();

            long id = dbHandler.saveFixrImage(new FixrImage(profile650, profile_pic_uri));
            dbHandler.saveFixrImage(new FixrImage(profileThumb, profile_pic_uri_thumb));

            for (int i = 0; i < length; i++) {
                int j = i + 1;
                galleryUris[i] = session.getGeneralSaveSession("gallery_image" + i);
                galleryImageNames[i] = sCity.getSelectedItem().toString() + "~" + phone5 + "_" + name.toLowerCase() + "_work" + j;
                dbHandler.saveFixrImage(new FixrImage(galleryImageNames[i], galleryUris[i]));
                // showToast(galleryUris[i] + "***" + galleryImageNames[i]);
            }


            loadResources();


            resetImages(length);
            GeneralMethods.alertDialogBox(DataActivity.this, "Congratulation!", "Data Saved Successfully.", "OK");

            /**
             * start service
             */
            if (!isServiceRunning() && InternetCheck.netCheck(getApplicationContext())) {
                startService(new Intent(getApplicationContext(), MyService.class));
            }

            //  }else{
            //      showToast("profile_pic_uri is null");
            //   }

            //  prgDialog.setMessage("Sending Profile Picture to server.");
            //  prgDialog.show();
            //   encodeImagetoString(profile_pic);

            /**
             * send data to server
             * if form validation is ok.
             *
             */
            //   new SendDataToFixrServer().execute();

            /**
             * use DatabaseHandler to store data
             *
             */

            //  int  lnth= dbHandler.getSavedFixrLength();
            // Toast.makeText(getApplicationContext(),"record length is "+lnth,Toast.LENGTH_SHORT).show();

        }

    }

    private boolean formValidation(String profile_pic_uri) {
        boolean validate = false;
        if (mName.getText().toString().trim().equals("")) {
            // fucusArea(mName);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter name.", "OK");
            // errorMessageManage(mName, "Please enter name.");

        } else if (sCity.getSelectedItem().toString().equals("Select City")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select city.", "OK");

        } else if (sTrades.getSelectedItem().toString().equals("Select Trade")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select Trader.", "OK");

        } else if (sWorkType.getSelectedItemsAsString().contains("Select")) {
            if (sWorkType.getSelectedItemsAsString().contains(",")) {
                GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please unselected the 'select' option from tags.", "OK");
            } else {
                GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select Tags.", "OK");
            }


        } else if (addNumber.getText().toString().equals("")) {
            fucusArea(mPhone);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter mobile number and click add button.", "OK");


        } else if (sLevel.getSelectedItem().toString().equals("Select Work Level")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select Work Level.", "OK");

        } else if (mAddress.getText().toString().trim().equals("")) {
            fucusArea(mAddress);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter Street Address.", "OK");
        } else if (mAddressArea.getText().toString().trim().equals("")) {
            fucusArea(mAddressArea);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter Address Area.", "OK");
        }/* else if (mPerDayCost.getText().toString().trim().equals("")) {
            fucusArea(mPerDayCost);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter per day cost.", "OK");
        }*/ else if (mCertification.getText().toString().trim().equals("")) {
            fucusArea(mCertification);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter certification.", "OK");
        } else if (mExperience_years.getSelectedItem().toString().equals("Select Years")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select Experience in years.", "OK");
        } else if (mExperience_months.getSelectedItem().toString().equals("Select Months")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select Experience in months.", "OK");
        } else if (profile_pic_uri.equals("")) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please provide a profile pic first.", "OK");
            // Toast.makeText(getApplicationContext(), "Please provide a profile pic", Toast.LENGTH_SHORT).show();
        } else {
            validate = true;
        }

        return validate;
    }

    private void resetImages(int length) {

        for (int i = 0; i < length; i++) {

            session.setGeneralSaveSession("gallery_image" + i, "");
        }
        session.setGeneralSaveSession("profile_pic", "");
        session.setGeneralSaveSession("profile_pic_thumb", "");
        session.setGeneralSaveSession("length_of_saved_images", "");
    }

    private void fucusArea(View view) {
        view.setFocusable(true);
    }
/*
    private void errorMessageManage(EditText editText, String setMessage) {
        mName.setError(null);
        mPhone.setError(null);
        mAddress.setError(null);
        mPerDayCost.setError(null);
        mPerVisitCost.setError(null);
        mCertification.setError(null);

        editText.setError(setMessage);
    }
*/

    /**
     * onSubmit of registeration form.
     *
     * @param view
     */
    public void onReset(View view) {
        /**
         * get length of saved gallery images
         */
        if (locationManager != null && locationListener != null) {
            stopGettingLocations();
        }
        resetAllPickedImages();


        loadResources();


    }

    private void resetAllPickedImages() {
        String length1 = session.getGeneralSaveSession("length_of_saved_images");
        int length;
        if (length1.equals("")) {
            length = 0;
        } else {
            length = Integer.parseInt(length1);
        }
        resetImages(length);
    }

    /**
     * get gallery images.
     *
     * @param view
     */
    public void getGallery(View view) {
        SessionData data = new SessionData(context);
        String profileUri = data.getGeneralSaveSession("profile_pic");
        boolean validate = formValidation(profileUri);

        if (validate) {
            gallery = true;
            startActivity(new Intent(getApplicationContext(), ImageActivity.class));
        }


    }

    /**
     * camera launch
     */

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                FilepicUri = GeneralMethods.createDirectoryAndSaveFile1(GeneralMethods.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            picUri = Uri.fromFile(FilepicUri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            mcamera = true;
            startActivityForResult(takePictureIntent, camera_open);
        }
    }


    /**
     * Camera and crop result function
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == camera_open && resultCode == RESULT_OK) {
            //System.out.println("camera onresult");
            // picUri = data.getData();

            /**
             * get Bitmap from Uri.
             */
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * cropped the clicked image and add with a water mark.
             */

            Bitmap scaledphoto = null;

            scaledphoto = ImageProcessing.scaleCenterCrop(bitmap, bitmap.getWidth(), bitmap.getWidth());


            Bitmap scaledphoto1 = null;
            scaledphoto1 = Bitmap.createScaledBitmap(scaledphoto, 650, 650, true);


            /**
             * add water mark image.
             */
            Bitmap scaledphoto2 = null;
            scaledphoto2 = FixrWaterMark.mark(scaledphoto1, new Point(30, 30), 70, 40, false, getApplicationContext());
            /**
             * save image in storage with cropped resolution 650px X 650px
             * and water mark with logo
             */
            GeneralMethods.saveImageWithFile(scaledphoto2, FilepicUri);

            Bitmap scaledphototoThumb = null;
            scaledphototoThumb = Bitmap.createScaledBitmap(scaledphoto2, 65, 65, true);
            File FilepicUri1 = null;
            try {
                FilepicUri1 = GeneralMethods.createDirectoryAndSaveFile1(GeneralMethods.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            GeneralMethods.saveImageWithFile(scaledphototoThumb, FilepicUri1);


            /**
             * save image uri to session
             */
            SessionData data1 = new SessionData(context);
            data1.setGeneralSaveSession("profile_pic", picUri.getPath());
            //System.out.println("thumb image path=" + FilepicUri1.getPath());
            data1.setGeneralSaveSession("profile_pic_thumb", FilepicUri1.getPath());
            imgThumbnail.setImageBitmap(scaledphoto2);
        }
    }

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
    /*------------------------------background task starts-----------------------------*/


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     */


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    public void addMobileNumber(View view) {
        if (mPhone.getText().toString().length() < 10) {
            fucusArea(mPhone);
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please a enter valid phone number.", "OK");

        } else {
            if (addNumber.getText().toString().equals("")) {
                new DoInBackground().execute("numberCheck");
            } else {
                addNumber();
            }
        }


    }

    private void addNumber() {
        addNumber.setText(addNumber.getText().toString() + mPhone.getText().toString() + ",");
        mPhone.setText("");
    }

    public void onVerify(View view) {
        if (GeneralMethods.isNetworkActive(getApplicationContext())) {
            if (sCity.getSelectedItem().toString().equals("Select City")) {
                GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please select city", "OK");

            } else if (!mAddressArea.getText().toString().equals("") && !mAddress.getText().toString().equals("")) {

                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                isNetworkAvilable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                isGpsAvilable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGpsAvilable && !isNetworkAvilable) {
                    GeneralMethods.alertDialogBoxForLocationSetting(DataActivity.this);
                } else {
                    verifyButton.setVisibility(View.GONE);
                    verifyProgress.setVisibility(View.VISIBLE);

                    getLoactionListner();
                }


            } else {
                GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter Street Address and Address Area", "OK");
            }

        } else {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Internet connectivity required for address verification.", "OK");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * stop getting loactions;
         *
         */
        if (!activity_finished && !mcamera && !gallery) {
            manageAlarms(getApplicationContext(), false);
        }

        if (locationManager != null && locationListener != null) {
            stopGettingLocations();
        }


    }

    /**
     * stop getting locations
     */
    protected void stopGettingLocations() {

        locationManager.removeUpdates(locationListener);

    }

    protected void getLoactionListner() {
        /**
         *  for getting the latest locations.
         *
         */

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    locationReceived = true;
                    currentLat = location.getLatitude();
                    currentLang = location.getLongitude();
                    String address = GeneralMethods.getAddressFromLatLong(getApplicationContext(), currentLat, currentLang);
                    // System.out.println("currentLat=" + currentLat);
                    //System.out.println("currentLang=" + currentLang);
                    System.out.println("address=" + address);
                    currentLocationAddress.setText(address);


                    if (address.toLowerCase().trim().contains(sCity.getSelectedItem().toString().toLowerCase().trim())) {
                        boolean valid = validateAddreass(address, mAddressArea.getText().toString());
                        if (valid) {
                            verified();
                        } else {
                            notVerified();
                        }

                    } else {
                        notVerified();

                    }

                    /*
                    if (address.toLowerCase().contains(mAddressArea.getText().toString().toLowerCase())) {

                    } else {

                    }
                    */
                } else {
                    notVerified();

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  0, 0, locationListener);


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 5, 4, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 4, locationListener);


    }

    private void verified() {
        verifyProgress.setVisibility(View.GONE);
        verifyButton.setVisibility(View.VISIBLE);
        verifyButton.setBackgroundResource(R.drawable.tick);
        verifyButton.setText("");
        verifyValue = "true";
        if (locationManager != null && locationListener != null) {
            stopGettingLocations();
        }

    }

    private void notVerified() {
        verifyProgress.setVisibility(View.GONE);
        verifyButton.setVisibility(View.VISIBLE);
        verifyButton.setText("");
        verifyValue = "false";
        if (locationManager != null && locationListener != null) {
            stopGettingLocations();
        }
        verifyButton.setBackgroundResource(R.drawable.cross);
    }
/*
    private void stopGettingLocationItself(){
       Thread tt= new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000*40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!locationReceived){
                    notVerified();
                }


            }
        };
        tt.start();

    }

*/


    /**
     * validate the Area part of the address with 75% of word match.
     *
     * @param googleAddress
     * @param matchAddress
     * @return
     */
    private boolean validateAddreass(String googleAddress, String matchAddress) {

        int matchCase = 0;
        googleAddress = googleAddress.replaceAll(",", " "); //remove all commas(,).
        googleAddress = googleAddress.replaceAll("\\s+", " ").trim();  // remove multiple
        matchAddress = matchAddress.replaceAll(",", " "); //remove all commas(,).with space
        matchAddress = matchAddress.replaceAll("\\s+", " ").trim();  // remove multiple
        String googleAddressToLower = googleAddress.toLowerCase();
        String matchAddressToLower = matchAddress.toLowerCase();
        boolean valid = false;
        if (googleAddressToLower.contains(matchAddressToLower)) {
            valid = true;
        } else {

            String[] splitedString = matchAddressToLower.split(" ");
            int noOfWordsInMatchString = splitedString.length;
            for (int i = 0; i < splitedString.length; i++) {
                if (googleAddressToLower.contains(splitedString[i])) {
                    int index = googleAddressToLower.indexOf(splitedString[i]);
                    googleAddressToLower = googleAddressToLower.substring(index, googleAddressToLower.length());
                    googleAddressToLower = googleAddressToLower.replace(splitedString[i], "").trim();
                    matchCase++;

                }
            }


            float matchValue = (float) 50 * noOfWordsInMatchString;         // 50 % of word match.

            matchValue = matchValue / 100;
            if (matchCase > matchValue) {
                valid = true;
            } else {
                valid = false;
            }


        }

        return valid;
    }
/*------------------------------background task starts-----------------------------*/

    private class DoInBackground extends AsyncTask<String, Void, Void>
            implements DialogInterface.OnCancelListener {
        private ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = ProgressDialog.show(DataActivity.this, "", "Loading. Please wait...", true);
        }

        protected Void doInBackground(final String... param) {
            new Thread() {
                public void run() {
                    final SendFixrDataToServer server = new SendFixrDataToServer();

                    if (param[0].equals("login")) {


                        final String loginResponce = server.userAuthentication(user.getText().toString(), pass1.getText().toString(), ctx);

                        if (loginResponce.toLowerCase().equals("sorry")) {

                            DataActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (loginAttemptedLeft == 3) {
                                        countDown2();
                                    }

                                    loginAttemptedLeft--;
                                    if (loginAttemptedLeft != 0) {
                                        loginAttemped.setText("You have left " + loginAttemptedLeft + " login attempted.");
                                    } else {
                                        loginButton.setEnabled(false);
                                        user.setEnabled(false);
                                        pass1.setEnabled(false);
                                        countDown();
                                    }

                                    GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Wrong username or password", "OK");
                                    dialog.dismiss();
                                }
                            });
                        } else if (loginResponce.toLowerCase().equals("admin")) {
                            DataActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startService(new Intent(getApplicationContext(), LocationTrackerService.class));
                                    session.setGeneralSaveSession(SessionData.privateUserName, user.getText().toString());
                                    session.setGeneralSaveSession(SessionData.privateUserType, "admin");
                                    session.setGeneralSaveSession(SessionData.privateLoginCheck, "no");
                                    loadResources();
                                }
                            });
                            dialog.dismiss();
                        } else if (loginResponce.toLowerCase().equals("user")) {

                            DataActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startService(new Intent(getApplicationContext(), LocationTrackerService.class));
                                    session.setGeneralSaveSession(SessionData.privateUserName, user.getText().toString());
                                    session.setGeneralSaveSession(SessionData.privateUserType, "user");
                                    session.setGeneralSaveSession(SessionData.privateLoginCheck, "no");
                                    loadResources();
                                }
                            });

                      /*  Intent intent=new Intent(DataActivity.this,DataActivity.class);
                        startActivity(intent);
                        finish();*/
                            dialog.dismiss();
                        } else {
                            DataActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Network error.", "OK");
                                    dialog.dismiss();
                                }
                            });
                        }


                    } else if (param[0].equals("numberCheck")) {


                        DataActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseHandler ddb = new DatabaseHandler(ctx);

                                boolean check = ddb.CheckIsDataAlreadyInDBorNot(mPhone.getText().toString());
                               /* List<FixrMobile> mobList=ddb.getAllFixrMObile();
                                for(int h=0;h<mobList.size();h++){
                                    System.out.println("MObilr from database="+mobList.get(h).getmMobileNumber());
                                }*/
                               // System.out.println("Fixr=1");
                              //  List<Fixr> mobList=ddb.getAllFixrs();
                               /* for(int h=0;h<mobList.size();h++){
                                    System.out.println("Fixr= "+mobList.get(h).getName());
                                }*/
                                if (!check) {
                                    addNumber.setVisibility(View.VISIBLE);
                                    addNumber();
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "FiXR already exist.", "OK");
                                }

                              /*
                               final String reponse = server.checkPhoneNumber(mPhone.getText().toString(), ctx);
                                System.out.println("reponce phone check= " + reponse);
                              if (reponse.toLowerCase().equals("ok")) {

                                    addNumber.setVisibility(View.VISIBLE);
                                    addNumber();
                                    dialog.dismiss();


                                } else if (reponse.toLowerCase().equals("sorry")) {
                                    dialog.dismiss();
                                    GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "FiXR already exist.", "OK");
                                } else {
                                    dialog.dismiss();
                                    addNumber.setVisibility(View.VISIBLE);
                                    addNumber();
                                    //  GeneralMethods.alertDialogBox(DataActivity.this, "Sorry! Network error.", "Please write the detail on paper and try again later.", "OK");
                                }*/
                            }
                        });
                    }


                }
            }.start();
            return null;
        }

        protected void onPostExecute(Void unused) {

            //populate_listview();
        }

        public void onCancel(DialogInterface dialog) {

        }
    }
    /*------------------------------background task ends-----------------------------*/


    /**
     * logout button
     *
     * @param view
     */
    public void logout(View view) {
        userConformation("logout", "Do you want to logout?");


    }

    private void userConformation(final String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DataActivity.this);
        alertDialogBuilder.setTitle("Attention!");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (type.equals("logout")) {
                            stopService(new Intent(getApplicationContext(), LocationTrackerService.class));
                            session.setGeneralSaveSession(SessionData.privateUserName, "no_user");
                            session.setGeneralSaveSession(SessionData.privateLoginCheck, "yes");
                            session.setGeneralSaveSession(SessionData.privateUserType, "");
                            loadLoginResources();
                            arg0.cancel();
                        } else if (type.equals("closeApp")) {
                            finishActivity();
                        }

                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        arg0.cancel();


                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * count down timer for login.
     */
    static long noOfMIn = 30;          // minutes for countdown
    static long oneMinute = 60 * 1000;
    long min;
    int sec = 60;

    private void countDown() {
        new CountDownTimer(noOfMIn * oneMinute, 1000) {

            public void onTick(long millisUntilFinished) {
                min = millisUntilFinished / 1000;
                min = min / 60;
                //  sec1 = sec / noOfMIn;
                sec--;
                loginAttemped.setText("You have " + min + ":" + sec + " time left to login again.");

                if (sec <= 0) {
                    sec = 60;
                }

            }

            public void onFinish() {
                loginAttemptedLeft = 3;
                loginAttemped.setText("");
                loginButton.setEnabled(true);
                user.setEnabled(true);
                pass1.setEnabled(true);
                // mTextField.setText("done!");
            }
        }.start();


    }

    private void countDown2() {
        new CountDownTimer(noOfMIn * oneMinute, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                loginAttemptedLeft = 3;
                loginAttemped.setText("");
            }
        }.start();


    }

    public void callContact(View view) {
        String number = callEdit.getText().toString();
        if (number.length() < 10) {
            GeneralMethods.alertDialogBox(DataActivity.this, "Sorry!", "Please enter a valid number to call.", "OK");
        } else {
            Uri number1 = Uri.parse("tel:" + number);
            Intent callIntent = new Intent(Intent.ACTION_CALL, number1);
            startActivity(callIntent);
        }
    }

}
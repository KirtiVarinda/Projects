package com.lovelife.lovelife;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.adapters.ProfileAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class ProfilePage extends Activity {


    public static String TRANSITION_FROM = "transition_from";
    public static String USER_DATA = "userData";


    private String types[] = {"Display Name", "Email", "Mobile", "Gender", "Date of birth", "Height", "Weight", "City", "State", "Country", "Zip code", " Favorite Movie", "Favorite Food", "Favorite Weather"};
    public static ArrayList<String> profileData = new ArrayList<String>();

    ListView profileList;
    TextView netWorkError;


    String imagePath = "";

    TextView UserName;
    /**
     * Get Profile data From Url
     */
    private String PROFILE_URL;

    /**
     * session variable
     */
    MySharedData mySharedData;
    /**
     * current activity instance
     */
    private ProfilePage currentActivity;

    /**
     * Parameter String keys
     */
    private String KEY_ID = "id";
    private String KEY_TOKEN = "token";
    String id, token;

    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;
    ImageView profilePicView;

    String response;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        UserName = (TextView) findViewById(R.id.textView11);
        /** initialize session variable */
        mySharedData = new MySharedData(getApplicationContext());

        /** get parameters values from shared prefrenses */
        token = mySharedData.getGeneralSaveSession(mySharedData.TOKEN);
        id = mySharedData.getGeneralSaveSession(mySharedData.USERID);
        System.out.println("check id,token" + id + token);


        netWorkError = (TextView) findViewById(R.id.errorNetwork);

        /** initialize profile list */
        profileList = (ListView) findViewById(R.id.profileList);


        /** initialize current ativity */
        currentActivity = ProfilePage.this;

        /**Inisialize the ImageView of Profile Image*/

        profilePicView = (ImageView) findViewById(R.id.circleView);





        /** set url for profile data */
        PROFILE_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.user_profile);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         /*       Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                SwitchActivities switchActivities = new SwitchActivities();
                switchActivities.openActivityWithPutExtra(currentActivity, GetUserProfile.class, new String[]{TRANSITION_FROM, USER_DATA}, new String[]{"profile", response});


            }
        });

//        fab.setVisibility(View.GONE);  // remove this live for edit profile  module




    }


    @Override
    protected void onResume() {
        super.onResume();
        /** start loader */
        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);
        new Thread() {
            public void run() {

                /**
                 * Make Connection and Pass URL to get response
                 */
                getProfileData();


            }
        }.start();

    }

    private Map<String, String> getKeyPairValueInHashMap(String id, String token) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {KEY_ID, KEY_TOKEN};      // set keys
        String value[] = {id, token};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * reload data from server if network error exist
     */
    public void reload(View view) {
        /** start progress bar */

        netWorkError.setVisibility(View.GONE);
        profileList.setVisibility(View.VISIBLE);

        new Thread() {
            public void run() {
                getProfileData();
            }
        }.start();


    }


    /**
     * For getting Profile Details
     */


    public void setImageFromString() {

        try {

            URL url = new URL(imagePath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            final Bitmap bmp = BitmapFactory.decodeStream(is);

            if (null != bmp) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profilePicView.setImageBitmap(bmp);

                    }
                });


                System.out.println("The Bitmap okkk");
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        profilePicView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                                profilePicView.setOnClickListener(null);
                                new Thread() {
                                    public void run() {
                                        setImageFromString();
                                    }
                                }.start();

                            }
                        });

                        profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.error));
                    }
                });


                System.out.println("The Bitmap is NULL");
            }

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    profilePicView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                            profilePicView.setOnClickListener(null);
                            new Thread() {
                                public void run() {
                                    setImageFromString();
                                }
                            }.start();

                        }
                    });

                    profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.error));
                }
            });

            System.out.println("error1111111" + e);
        }

    }


    private void getProfileData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.startProgressBar();
            }
        });
        /** set parameter as selected city to get Profile Detail */
        final Map<String, String> postData = getKeyPairValueInHashMap(id, token);

        /** get Profile details from API */
        ServerSync serverSync = new ServerSync();
        response = serverSync.SyncServer(postData, PROFILE_URL);
        System.out.println("Profileresponse" + response);

        if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.error));
                    netWorkError.setVisibility(View.VISIBLE);
                    profileList.setVisibility(View.GONE);
                    /** Stop progress bar */
                    progressBarManager.stopProgressBar();
                    fab.setVisibility(View.GONE);

                }
            });

        } else {
            String status = ParseJsonData.checkStatus(response);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success . */


                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 *
                 */

                /** Parse name of user */
                String firstName = ParseJsonData.getValueWithKey(response, "first_name");
                String lastName = ParseJsonData.getValueWithKey(response, "last_name");
                final String name = firstName + " " + lastName;
                System.out.println("bnameee" + firstName + " " + lastName);

                /** Parse response and get the URL for Profile Image */
                imagePath = ParseJsonData.getValueWithKey(response, "profile_pic");
                System.out.println("imagessss11" + imagePath);


                /** set the Image into ImageView */


                String[] values = ParseJsonData.parseUserProfileDetail(response);

                final ProfileAdapter adapter = new ProfileAdapter(getApplicationContext(), types, values);

                if (!imagePath.equals("")) {
                    setImageFromString();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            profilePicView.setImageDrawable(getResources().getDrawable(R.drawable.noimage));
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.VISIBLE);
                        profileList.setAdapter(adapter);
                        UserName.setText(name);

                        /** Stop progress bar */
                        progressBarManager.stopProgressBar();

                    }
                });


            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */
                System.out.println("STATUS_FAIL");

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("else");

                    }
                });

            }


        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}

package com.tiffindaddy.app.tiffindaddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.Cities;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.WriteJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String SERIALIZED_KEY="serialized_key";

    /**
     * variable for serializable bean class
     */
    public static TiffinCategories tiffinCategories[];
    private ParseJsonData parseJsonData;
    private Context context;

    private static boolean categoryFetched, after5Sec = false;
    private static int SplashSeconds = 5;
    private TextView networkError;

//    private MySharedData
    /**
     * Tiffin Categories url
     */
    private static String categoryURL,citiesURL;
    private ProgressBar progressBar;


    public static Cities cities[];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        context = getApplicationContext();
        categoryURL = getResources().getString(R.string.get_categoories);
        citiesURL = getResources().getString(R.string.getCities);


        networkError = (TextView) findViewById(R.id.textView2);
        parseJsonData = new ParseJsonData();
        new Thread() {

            public void run() {
                getCategories();


            }


        }.start();


        /**
         *  switch from splash screen after secified time.
         */
        removeSplashScreen();
    }

    /**
     * Get Tiffin Categories from the server.
     */
    private void getCities() {


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
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);


        /**
         * Authenticate user from funcard deals server.
         */

        ServerSync serverSync = new ServerSync();


        String reponse = serverSync.SyncServer(theMap, citiesURL);





        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    networkError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").equals("success")) {
                cities= parseJsonData.parseCitiesData(reponse);       // get categories from json

                if(after5Sec){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    Intent intent = new Intent(MainActivity.this, ProductPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    intent.putExtra(SERIALIZED_KEY, tiffinCategories);
                    startActivity(intent);
                    finish();
                }
                categoryFetched = true;


            } else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkError.setVisibility(View.VISIBLE);
                        networkError.setText("Server Problem");
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }


        }

        System.out.println("Response= " + reponse);


    }



    /**
     * Get Tiffin Categories from the server.
     */
    private void getCategories() {


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
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);


        /**
         * Authenticate user from funcard deals server.
         */

        ServerSync serverSync = new ServerSync();









        String reponse = serverSync.SyncServer(theMap, categoryURL);





        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    networkError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").equals("success")) {
                tiffinCategories= parseJsonData.parseCategoryJson(reponse);       // get categories from json

                getCities();



            } else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkError.setVisibility(View.VISIBLE);
                        networkError.setText("Server Problem");
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }


        }

        System.out.println("Response= " + reponse);


    }




    /**
     * Method that removes splash screen after specified period of time.
     */
    private void removeSplashScreen() {
        new Thread() {
            public void run() {

                /**
                 * n second delay
                 */
                try {
                    Thread.sleep(SplashSeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                after5Sec=true;
                /**
                 * load landing page after 5 seconds.
                 */
                if (categoryFetched) {

//                    ArrayList<TiffinCategories> categories = new ArrayList<TiffinCategories>();
//
//                    for(int i=0; i<tiffinCategories.length;i++){
//                        categories.add(tiffinCategories[i]);
//
//                    }

                    Intent intent = new Intent(MainActivity.this, ProductPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);



                    //intent.putExtra(SERIALIZED_KEY, categories);
                    startActivity(intent);
                    finish();
                }

            }
        }.start();
    }


}

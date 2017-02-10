package com.tiffindaddy.app.tiffindaddy;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.TiffinAdapters.TiffinAdapter;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class AddonsFragment extends Fragment {
    private ListView listView;
    private Context context;
    public static String imagePath;
    private String CATEGORY_KEY = "category";
    /**
     * variable for serializable bean class
     */
    private TiffinCategories tiffinCategories[];
    /**
     * Tiffin Categories url
     */
    private static String addonsURL;
    private ParseJsonData parseJsonData;


    LinearLayout error4;

    /**
     * variable for serializable bean class
     */
    //  private TiffinCategories tiffinCategories[];
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;


        addonsURL = getResources().getString(R.string.get_addons);
        parseJsonData = new ParseJsonData();
        // tiffinCategories=MainActivity.tiffinCategories;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_fragment4, container, false);


        return rootView;


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.listView3);
        error4 = (LinearLayout) view.findViewById(R.id.error4);
        boolean check;
        TiffinData tiffinData[];
        if(ProductPage.checkDayIsToday){
            check=ProductPage.todayAddons!= null;
            tiffinData=ProductPage.todayAddons;

        }else{
            check=ProductPage.tomorrowAddons != null;
            tiffinData=ProductPage.tomorrowAddons;
        }


        if (DinnerFragment.imagePath == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error4.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            });

        } else if (check) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error4.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            });

            setAddonsListAdapter(tiffinData);
        } else {
            new Thread() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error4.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    });

                    getTiffins();

                }
            }.start();
        }


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    /**
     * Get Tiffin Categories from the server.
     */
    private void getTiffins() {


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
        String reponse = serverSync.SyncServer(theMap, addonsURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").equals("success")) {

//               // imagePath = parseJsonData.checkStatus(reponse, "image_path");
//                try {
//
//                   // imagePath = URLDecoder.decode(imagePath, "UTF-8");
//                    System.out.println("imagePath=" + imagePath);
//
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

                if(ProductPage.checkDayIsToday) {
                    ProductPage.todayAddons = parseJsonData.parseTiffinJson(reponse, "no", "no","no");     // get categories from json
                    setAddonsListAdapter(ProductPage.todayAddons);
                }else{
                    ProductPage.tomorrowAddons = parseJsonData.parseTiffinJson(reponse, "no", "no","no");       // get categories from json
                    setAddonsListAdapter(ProductPage.tomorrowAddons);
                }



            } else {


            }


        }

        System.out.println("Response= " + reponse);


    }

    private void setAddonsListAdapter(TiffinData[] tiffinData) {
        String     tiffinNames[] = new String[tiffinData.length];
        for (int i = 0; i < tiffinData.length; i++) {
            tiffinNames[i] = tiffinData[0].getTiffinName();

        }

        final TiffinAdapter catAdapter = new TiffinAdapter(context, R.layout.product_list_row, tiffinNames, tiffinData, getActivity(), DinnerFragment.imagePath);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(catAdapter);
                // progressBarManager.stopProgressBar();
            }
        });
    }

}
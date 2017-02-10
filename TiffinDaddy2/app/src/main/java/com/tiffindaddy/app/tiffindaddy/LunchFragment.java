package com.tiffindaddy.app.tiffindaddy;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.MergeData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;
import com.tiffindaddy.app.tiffindaddy.TiffinAdapters.TiffinAdapter;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class LunchFragment extends Fragment {

    private ListView listView;
    private Context context;
    public static String imagePath;
    private String CATEGORY_KEY = "category";

    /**
     * Tiffin Categories url
     */
    private static String tiffinURL;
    private ParseJsonData parseJsonData;

    private long startTime, endTime, currentTime;
    private TextView textView;
    private LinearLayout error;


    /**
     * variable for serializable bean class
     */
    private TiffinCategories tiffinCategories[];


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;


        tiffinURL = getResources().getString(R.string.get_tiffins);
        parseJsonData = new ParseJsonData();
        tiffinCategories=ProductPage.tiffinCategories;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_tab_fragment2, container, false);



        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.listView1);
        textView=(TextView)view.findViewById(R.id.textView123);
        error=(LinearLayout)view.findViewById(R.id.error2);



            if(tiffinCategories.length==2){
                System.out.println("lunch2");
                new Thread(){
                    public void run() {
                        getTiffins(0);

                    }
                }.start();

            }else if(tiffinCategories.length==3){
                System.out.println("lunch3");
                new Thread(){
                    public void run() {
                        getTiffins(1);

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
    private void getTiffins(int index) {
        boolean runSuccess = false;
        String time1[] = tiffinCategories[index].getCategoryStartTime().split(":");
        String time2[] = tiffinCategories[index].getCategoryEndTime().split(":");


        Long oneHourBefore= TimeManager.timeBeforeHour(Integer.parseInt(time2[0]),Integer.parseInt(time2[1]));
       final  String before=TimeManager.milisecToTime(oneHourBefore);


        final String sTime = TimeManager.CurrenttwelevFormatTime(time1[0], time1[1]);
        final String eTime = TimeManager.CurrenttwelevFormatTime(time2[0], time2[1]);


        startTime = TimeManager.dateToString(Integer.parseInt(time1[0]), Integer.parseInt(time1[1]));
        endTime = TimeManager.dateToString(Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));


        currentTime = TimeManager.currentTimedateToString();


        if ( currentTime < endTime ) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            });

            runSuccess = true;




        } else {
            runSuccess = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    error.setVisibility(View.VISIBLE);
                    textView.setText("We deliver Lunch till " + eTime + " and last order time is " + before);
                    listView.setVisibility(View.GONE);
                }
            });

        }


        /**
         *
         * if tomorrow button is clicked.
         *
         */

        if(!ProductPage.checkDayIsToday){
            runSuccess=true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            });
        }




        if(runSuccess) {
            TiffinData tiffinData[];
            boolean check;
            if(ProductPage.checkDayIsToday){
                check=ProductPage.todayTiffinDataLunch != null;
                tiffinData=ProductPage.todayTiffinDataLunch;

            }else{
                check=ProductPage.tomorrowTiffinDataLunch != null;
                tiffinData=ProductPage.tomorrowTiffinDataLunch;
            }

            if (check) {
                setLunchListAdapter(tiffinData);

            } else {

                /**
                 * make key and values array for all the strings.
                 * index of both should be same.
                 */
                String key[] = {CATEGORY_KEY};      // set keys
                String value[] = {tiffinCategories[index].getCategoryId()};                //set values


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
                String reponse = serverSync.SyncServer(theMap, tiffinURL);

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

                        imagePath = parseJsonData.checkStatus(reponse, "image_path");
                        try {

                            imagePath = URLDecoder.decode(imagePath, "UTF-8");
                            System.out.println("imagePath=" + imagePath);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        if(ProductPage.checkDayIsToday) {
                            TiffinData[] tiffiDataLunchToday = parseJsonData.parseTiffinJson(reponse, tiffinCategories[index].getCategoryId(), tiffinCategories[index].getCategoryName(),tiffinCategories[index].getCategoryStartTime()+"-l-"+tiffinCategories[index].getCategoryEndTime());       // get categories from json

                            TiffinData[] addonsDataLunchToday= parseJsonData.parseTiffinJson(ProductPage.addonResponse, tiffinCategories[index].getCategoryId(), tiffinCategories[index].getCategoryName()+"_addons", tiffinCategories[index].getCategoryStartTime() + "-l-" + tiffinCategories[index].getCategoryEndTime());       // get categories from json

                            ProductPage.todayTiffinDataLunch =MergeData.mergeTwoBeans(tiffiDataLunchToday, addonsDataLunchToday);

                            setLunchListAdapter(ProductPage.todayTiffinDataLunch);
                        }else{
                            TiffinData[] tiffiDataLunchTomorrow  = parseJsonData.parseTiffinJson(reponse, tiffinCategories[index].getCategoryId(), tiffinCategories[index].getCategoryName(),tiffinCategories[index].getCategoryStartTime()+"-l-"+tiffinCategories[index].getCategoryEndTime());       // get categories from json
                            TiffinData[] addonsDataLunchTomorrow  = parseJsonData.parseTiffinJson(ProductPage.addonResponse, tiffinCategories[index].getCategoryId(), tiffinCategories[index].getCategoryName()+"_addons",tiffinCategories[index].getCategoryStartTime()+"-l-"+tiffinCategories[index].getCategoryEndTime());       // get categories from json

                            ProductPage.tomorrowTiffinDataLunch =MergeData.mergeTwoBeans(tiffiDataLunchTomorrow, addonsDataLunchTomorrow);
                            setLunchListAdapter(ProductPage.tomorrowTiffinDataLunch);
                        }





                    } else {


                    }


                }

                System.out.println("Response= " + reponse);
            }

        }
    }

    /**
     * method to set the list adapter for lunch.
     */
    private void setLunchListAdapter(TiffinData[] tiffinData) {
        String tiffinNames[] = new String[tiffinData.length];
        for (int i = 0; i < tiffinData.length; i++) {
            tiffinNames[i] = tiffinData[0].getTiffinName();

        }



        if(getActivity() == null)
            return;

        final TiffinAdapter catAdapter = new TiffinAdapter(context, R.layout.product_list_row, tiffinNames, tiffinData, getActivity(),imagePath);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(catAdapter);
                // progressBarManager.stopProgressBar();
            }
        });
    }

}
package app.funcarddeals.com.network;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.funcarddeals.com.BeanClasses.CatAdvertisementBeanClass;
import app.funcarddeals.com.CategoryPage;
import app.funcarddeals.com.Manager.BannerThread;
import app.funcarddeals.com.Stores;

public class ServerSync {
    public static String HTTP_ERROR = "http_error";
    public static String DEFAULT_RESPONSE_VALUE = "error";

    /**
     * server authentication variables.
     */
    public static String VALID_FUNCARD_USER = "funcarddeals_ok";
    public static String INVALID_FUNCARD_USER = "funcarddeals_sorry";
    public static String FUNCARD_USER_DISABLED = "funcarddeals_disable";

    /**
     * funcard redeem response.
     */
    public static String FUNCARD_REDEEM_YES = "funcarddeals_yes";
    public static String FUNCARD_REDEEM_EMPTY = "funcarddeals_empty";
    public static String FUNCARD_REDEEM_SORRY = "funcarddeals_sorry";
    public static String FUNCARD_REDEEM_TIME_BETWEEN = "funcarddeals_time_between";
    public static String FUNCARD_REDEEM_TIME_FROM = "funcarddeals_from";

    public String SyncServer(Map<String, String> keyValuePairs, String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        URL url = null;
        HttpURLConnection urlConn;
        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(50000);
            urlConn.setConnectTimeout(55000);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            //urlConn.setRequestProperty("Content-Type","application/json");

            OutputStream os = urlConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            String pairValue = getQuery(keyValuePairs);
            writer.write(pairValue);
           // System.out.println("pairValue= "+pairValue);
            writer.flush();
            writer.close();
            os.close();
            urlConn.connect();
            response = getResponceAndDisconnectConnection(urlConn);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }
    public String SyncServeByGet(String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        URL url = null;
        HttpURLConnection urlConn;
        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(20000);
            urlConn.setConnectTimeout(25000);
            urlConn.setRequestMethod("GET");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);


            urlConn.connect();
            response = getResponceAndDisconnectConnection(urlConn);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }


    /**
     * Method for getting response in return of url fire.
     *
     * @param urlConnection
     * @return
     */
    public String getResponceAndDisconnectConnection(HttpURLConnection urlConnection) {
        StringBuilder sb = new StringBuilder();
        String response = DEFAULT_RESPONSE_VALUE;
        try {
            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                response = sb.toString();
               //  System.out.println("excat responseaaaa="+response);

            } else {
                response = HTTP_ERROR;
             //  System.out.println(urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String pair : params.keySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(pair), "UTF-8"));
        }

        return result.toString();
    }


    /**
     * Method for getting images from the url.
     */


    Bitmap bmp;

    public Bitmap fireUrlGetBitmap(String URL) {

        bmp = null;
        URL url = null;
        HttpURLConnection urlConn;
        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(20000);
            urlConn.setConnectTimeout(25000);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            InputStream os = urlConn.getInputStream();
            urlConn.connect();
            bmp = BitmapFactory.decodeStream(os);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;


    }


    /**
     * method to set bitmap in each object of advertisement bean class
     *
     * @param catAdvertisementBeanClass
     * @return
     */
    public CatAdvertisementBeanClass[] getImageFromUrl(CatAdvertisementBeanClass[] catAdvertisementBeanClass) {
        Bitmap bmp;

        for (int i = 0; i < catAdvertisementBeanClass.length; i++) {
            bmp = fireUrlGetBitmap(catAdvertisementBeanClass[i].getImageURL());
            catAdvertisementBeanClass[i].setBitmap(bmp);
        }

        return catAdvertisementBeanClass;
    }


    /**
     * method to set bitmap in each object of advertisement bean class
     *
     * @param catAdvertisementBeanClass
     * @return
     */
    public static List<CatAdvertisementBeanClass> realTimeCatAdvertisement = new ArrayList<CatAdvertisementBeanClass>();
    boolean runShowImage = true;

    public CatAdvertisementBeanClass[] getImageFromUrl(CatAdvertisementBeanClass[] catAdvertisementBeanClass, final CategoryPage context) {
        Bitmap bmp;
        // realTimeCatAdvertisement=new CatAdvertisementBeanClass[catAdvertisementBeanClass.length];
        for (int i = 0; i < catAdvertisementBeanClass.length; i++) {
            bmp = fireUrlGetBitmap(catAdvertisementBeanClass[i].getImageURL());
            catAdvertisementBeanClass[i].setBitmap(bmp);
            realTimeCatAdvertisement.add(catAdvertisementBeanClass[i]);
            if (runShowImage) {
                runShowImage = false;
                new Thread() {
                    public void run() {

                        BannerThread bThread = new BannerThread();
                        bThread.addStatus = false;
                        context.addThreadList.add(bThread);
                        context.setImageBannersFromUrl(bThread);

                    }


                }.start();
            }
        }

        return catAdvertisementBeanClass;
    }


    public CatAdvertisementBeanClass[] getImageFromUrl(CatAdvertisementBeanClass[] catAdvertisementBeanClass, final Stores context) {
        Bitmap bmp;
        // realTimeCatAdvertisement=new CatAdvertisementBeanClass[catAdvertisementBeanClass.length];
        for (int i = 0; i < catAdvertisementBeanClass.length; i++) {
            bmp = fireUrlGetBitmap(catAdvertisementBeanClass[i].getImageURL());
            catAdvertisementBeanClass[i].setBitmap(bmp);
            realTimeCatAdvertisement.add(catAdvertisementBeanClass[i]);
            if (runShowImage) {
                runShowImage = false;
                new Thread() {
                    public void run() {

                        BannerThread bThread = new BannerThread();
                        bThread.addStatus = false;
                        context.addThreadList.add(bThread);
                        context.setImageBannersFromUrl(bThread);

                    }


                }.start();
            }
        }

        return catAdvertisementBeanClass;
    }


    /**
     * method to check if url is working or not.
     * @param URL
     * @return
     */

    public String checkURLStatus(String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        URL url = null;
        HttpURLConnection urlConn;
        try {
            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(10000);
            urlConn.setConnectTimeout(15000);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            //urlConn.setRequestProperty("Content-Type","application/json");
            urlConn.connect();
            response = getResponceAndDisconnectConnection(urlConn);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }


}

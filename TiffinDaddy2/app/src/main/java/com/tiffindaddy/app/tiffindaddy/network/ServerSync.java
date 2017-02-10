package com.tiffindaddy.app.tiffindaddy.network;


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
import java.util.Map;


public class ServerSync {
    public static String HTTP_ERROR = "http_error";
    public static String DEFAULT_RESPONSE_VALUE = "error";

    public String SyncServer(Map<String, String> keyValuePairs, String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        java.net.URL url = null;
        HttpURLConnection urlConn;
        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(20000);
            urlConn.setConnectTimeout(25000);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            //urlConn.setRequestProperty("Content-Type","application/json");

            OutputStream os = urlConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            String pairValue = getQuery(keyValuePairs);
            writer.write(pairValue);

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
        java.net.URL url = null;
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
               System.out.println("excat response="+response);

            } else {
                response = HTTP_ERROR;
                System.out.println(urlConnection.getResponseMessage());
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
        java.net.URL url = null;
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

//
//    /**
//     * method to set bitmap in each object of advertisement bean class
//     *
//     * @param catAdvertisementBeanClass
//     * @return
//     */
//    public CatAdvertisementBeanClass[] getImageFromUrl(CatAdvertisementBeanClass[] catAdvertisementBeanClass) {
//        Bitmap bmp;
//
//        for (int i = 0; i < catAdvertisementBeanClass.length; i++) {
//            bmp = fireUrlGetBitmap(catAdvertisementBeanClass[i].getImageURL());
//            catAdvertisementBeanClass[i].setBitmap(bmp);
//        }
//
//        return catAdvertisementBeanClass;
//    }
//
//
//    /**
//     * method to set bitmap in each object of advertisement bean class
//     *
//     * @param catAdvertisementBeanClass
//     * @return
//     */
//    public static List<CatAdvertisementBeanClass> realTimeCatAdvertisement = new ArrayList<CatAdvertisementBeanClass>();
//    boolean runShowImage = true;
//
//    public CatAdvertisementBeanClass[] getImageFromUrl(CatAdvertisementBeanClass[] catAdvertisementBeanClass, final CategoryPage context) {
//        Bitmap bmp;
//        // realTimeCatAdvertisement=new CatAdvertisementBeanClass[catAdvertisementBeanClass.length];
//        for (int i = 0; i < catAdvertisementBeanClass.length; i++) {
//            bmp = fireUrlGetBitmap(catAdvertisementBeanClass[i].getImageURL());
//            catAdvertisementBeanClass[i].setBitmap(bmp);
//            realTimeCatAdvertisement.add(catAdvertisementBeanClass[i]);
//            if (runShowImage) {
//                runShowImage = false;
//                new Thread() {
//                    public void run() {
//
//                        BannerThread bThread = new BannerThread();
//                        bThread.addStatus = false;
//                        context.addThreadList.add(bThread);
//                        context.setImageBannersFromUrl(bThread);
//
//                    }
//
//
//                }.start();
//            }
//        }
//
//        return catAdvertisementBeanClass;
//    }
public String sendJsonDataToServer(String json, String URL) {
    String response = DEFAULT_RESPONSE_VALUE;
    java.net.URL url = null;
    HttpURLConnection urlConn;
    try {

        url = new URL(URL);
        urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setReadTimeout(20000);
        urlConn.setConnectTimeout(25000);
        urlConn.setRequestMethod("POST");
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
       // urlConn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = urlConn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));

        writer.write(json);

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

}

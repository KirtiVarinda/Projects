package com.lovelife.lovelife.NetworkManager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Map;

/**
 * a class that Sync data with server and perform actions like login and registration with server
 */
public class ServerSync {
    public static String HTTP_ERROR = "http_error";
    public static String DEFAULT_RESPONSE_VALUE = "error";
    private String TAG = "LoveLife Exception";


    /**
      * @param keyValuePairs it contains Post data to send in key value pairs.
     * @param URL           It contains the url that sync with server.
     * @return it returns string response from the server. Response will be in json format.
     */
    public String SyncServer(Map<String, String> keyValuePairs, String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        System.out.println("URL   g"+URL);
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
            System.out.println("pairValued= "+pairValue);
            writer.flush();
            writer.close();
            os.close();
            urlConn.connect();
            response = getResponseAndDisconnectConnection(urlConn);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }




    public String SyncServer( String URL) {
        String response = DEFAULT_RESPONSE_VALUE;
        try {

            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public String getQueryGet(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String pair : params.keySet()) {
            if (first) {
                first = false;
                result.append("?");
            } else
                result.append("&");
            result.append(URLEncoder.encode(pair, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(pair), "UTF-8"));
        }

        return result.toString();
    }


    /**
     * Method for getting response in return of url fire.
     * Response is in json format.
     *
     * @param urlConnection
     * @return
     */
    public String getResponseAndDisconnectConnection(HttpURLConnection urlConnection) {
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
                //  System.out.println("exact response ="+response);

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

    /**
     * @param params it contains Post data to send in key value pairs.
     * @return  Returns string that contains data in format to send in url.
     * @throws UnsupportedEncodingException
     */
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


    Bitmap bmp;

    /**
     * Method for getting images from the url.
     *
     * @param URL contains the url to get image bitmap
     * @return
     */
    public Bitmap fireUrlGetBitmap(String URL) {
        bmp = null;
        URL url = null;
        HttpURLConnection urlConn;
        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();

           // urlConn.setRequestMethod("POST");
          //  urlConn.setDoInput(fals);
          //  urlConn.setDoOutput(true);
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
     * method to check if url is working or not.
     *
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
            response = getResponseAndDisconnectConnection(urlConn);


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

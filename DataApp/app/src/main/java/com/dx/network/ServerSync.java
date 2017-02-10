package com.dx.network;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Dx on 1/14/2015.
 * Class is used for sending data to the server.
 */
public class ServerSync {
        Context context;
    public ServerSync(){
        //this.context=context;
    }
    /*sends the data to the server */
    public String getDataFromServer(String url){
        System.out.println("server==="+url);

        // 1) Connect via HTTP. 2) Encode data. 3) Send data.
        try{
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);  //set the time in milisecond for checking network respond after that gives error.
            HttpConnectionParams.setSoTimeout(httpParameters, 17000);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
           // httppost.setEntity(new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute(httppost);
            Log.i("postData", response.getStatusLine().toString());
            return handleResponce(response);
            //Could do something better with response.
            
        }
        catch(Exception e){
            Log.e("log_tag", "Error:  "+e.toString());
            return "error";
        }
    }


    /*handle response from the server and return response as string.*/
    protected String handleResponce(HttpResponse response)throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuffer responseString = new StringBuffer("");
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }

        return  responseString.toString();
    }
}

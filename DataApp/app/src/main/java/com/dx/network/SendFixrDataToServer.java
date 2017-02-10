package com.dx.network;

import android.content.Context;
import android.util.Log;

import com.dx.dataapp.GeneralMethods;
import com.dx.dataapp.R;
import com.dx.dataapp.SessionData;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by getfixr on 4/13/2015.
 */
public class SendFixrDataToServer {
    SessionData session;

    /*collects the childs messages data and send to the server*/
    public String collectAndSendDataforFixrForm(String fixrName,String city,String workType,String trades,
        String phone,String workLevel,String address,String perDayCost,String perVisitCost,String certification,String experience
            ,String verify,String loginUser,String dataLatitude,String dataLongitude ,Context ctx){
        session = new SessionData(ctx);
        //Add data to be send.
        String url=ctx.getResources().getString(R.string.send_fixr_data_to_serevr);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair("name", fixrName));
        nameValuePairs.add(new BasicNameValuePair("city",city));
        nameValuePairs.add(new BasicNameValuePair("tag",workType));
        nameValuePairs.add(new BasicNameValuePair("trade",trades));
        nameValuePairs.add(new BasicNameValuePair("phone",phone));
        nameValuePairs.add(new BasicNameValuePair("level",workLevel));
        nameValuePairs.add(new BasicNameValuePair("address",address));
        nameValuePairs.add(new BasicNameValuePair("cost",perDayCost));
        nameValuePairs.add(new BasicNameValuePair("visit",perVisitCost));
        nameValuePairs.add(new BasicNameValuePair("certification",certification));
        nameValuePairs.add(new BasicNameValuePair("experience",experience));
        nameValuePairs.add(new BasicNameValuePair("address_verification",verify));
        nameValuePairs.add(new BasicNameValuePair("deviceId",  loginUser+"_"+GeneralMethods.getDeviceId(ctx)));
        nameValuePairs.add(new BasicNameValuePair("data_latitude",dataLatitude));
        nameValuePairs.add(new BasicNameValuePair("data_longitude",dataLongitude));
        String reponcefromServer= this.sendDataToServer(nameValuePairs,ctx,url);       //sends the collected mesaages data to the server.
        return  reponcefromServer;
    }



    /*sends the data to the server */
    protected String sendDataToServer(ArrayList<NameValuePair> data,Context ctx,String url){
        // 1) Connect via HTTP. 2) Encode data. 3) Send data.
        try{
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);  //set the time in milisecond for checking network respond after that gives error.
            HttpConnectionParams.setSoTimeout(httpParameters, 22000);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);


            httppost.setEntity(new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute(httppost);
            Log.i("postData", response.getStatusLine().toString());
            return handleResponce(response,ctx);
            //Could do something better with response.
        }catch(Exception e){
            Log.e("log_tag", "Error:  "+e.toString());
            return "error";
        }
    }


    /*handle response from the server and return response as string.*/
    protected String handleResponce(HttpResponse response,Context ctx)throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuffer responseString = new StringBuffer("");
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }

      /*  //	 Log.i("reader",responseString.toString());
        System.out.println(responseString.toString());

        System.out.println(responseString.toString()+12);
        if(responseString.toString().equals("ok")){
        //    Toast.makeText(ctx, "Wrong Username and Password", Toast.LENGTH_SHORT).show();
            System.out.println(responseString.toString()+"15");
        }
        System.out.println(responseString.toString()+"13");*/
        return  responseString.toString();
    }


    /*collects the childs messages data and send to the server*/
    public String userAuthentication(String user,String pass,Context ctx){
        //Add data to be send.
        String url=ctx.getResources().getString(R.string.login_from_server);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("user", user));
        nameValuePairs.add(new BasicNameValuePair("pass",pass));

        nameValuePairs.add(new BasicNameValuePair("deviceId", GeneralMethods.getDeviceId(ctx)));
        String reponcefromServer= this.sendDataToServer(nameValuePairs,ctx,url);       //sends the collected mesaages data to the server.
        return  reponcefromServer;
    }


    /*collect  data and send to the server*/
    public String sendTabLocationToServer(String jasonData,Context ctx){
        //Add data to be send.
        String url=ctx.getResources().getString(R.string.location_track_server);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("jason_data", jasonData));

        String reponcefromServer= this.sendDataToServer(nameValuePairs,ctx,url);       //sends the collected mesaages data to the server.
        return  reponcefromServer;
    }

    /*collect  data and send to the server*/
    public String checkPhoneNumber(String phone,Context ctx){
        //Add data to be send.
        String url=ctx.getResources().getString(R.string.location_track_server);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("phone", phone));

        String reponcefromServer= this.sendDataToServer(nameValuePairs,ctx,url);       //sends the collected mesaages data to the server.
        return  reponcefromServer;
    }
}

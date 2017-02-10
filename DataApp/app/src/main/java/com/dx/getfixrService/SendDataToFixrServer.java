package com.dx.getfixrService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dx.dataapp.DatabaseHandler;
import com.dx.network.SendFixrDataToServer;

/**
 * Created by dx on 24/04/2015.
 */
public class SendDataToFixrServer extends AsyncTask<String, String, String> implements DialogInterface.OnCancelListener{
    private ProgressDialog dialog;
    private String name, city, worktype, trades, phone, level,
     address, workHours, cost, certification, experience,verify,loginUser,dataLatitude,dataLongitude;
    private int id;
    Context context;
    SendFixrDataToServer sendData;
    DatabaseHandler dbHandler;
    public SendDataToFixrServer() {
        super();

    }

    public void setData(Context context,int id,String name, String city, String worktype, String trades,
                        String phone, String level, String address, String workHours, String cost,
                        String certification, String experience,String verify, String loginUser,String dataLatitude,String dataLongitude){
        this.context=context;
        this.id=id;
        this.name = name;
        this.city = city;
        this.worktype = worktype;
        this.trades = trades;
        this.phone = phone;
        this.level = level;
        this.address = address;
        this.workHours = workHours;
        this.cost = cost;
        this.certification = certification;
        this.experience = experience;
        this.verify = verify;
        this.loginUser = loginUser;
        this.dataLatitude = dataLatitude;
        this.dataLongitude = dataLongitude;
//        System.out.println("name is "+name.toString());
    }
    protected void onPreExecute() {
     //   dialog = ProgressDialog.show(DataActivity.this, "", "Sending data to server. Please wait...", true);
    }
    protected String doInBackground(String... params) {

        sendData  = new SendFixrDataToServer();
        System.out.println(name.toString());
        System.out.println("dataLatitude=" + dataLatitude);
        System.out.println("dataLongitude=" + dataLongitude);

        String printeddata =sendData.collectAndSendDataforFixrForm(name, city, worktype, trades
                , phone, level, address,workHours, cost, certification
                , experience,verify,loginUser,dataLatitude,dataLongitude, context);

        System.out.println("printeddata=" + printeddata);
        if(printeddata.contains("ok")){
            return "ok";
        }else{
            return null;
        }

    }
    protected void onPostExecute(String result) {
       /* dialog.dismiss();*/
        if(dbHandler==null){
            dbHandler=new DatabaseHandler(context);
        }

        if(("ok").equals(result)){
            System.out.println("record deleted=");
            dbHandler.deleteFixrDetails(id);
        }
        System.out.println("result is"+result);

        //populate_listview();
    }
    public void onCancel(DialogInterface dialog) {
        cancel(true);
       /* dialog.dismiss();*/
    }
}

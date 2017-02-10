package com.dx.getfixrService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dx.dataapp.DatabaseHandler;
import com.dx.model.Fixr;
import com.dx.model.FixrImage;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    DatabaseHandler dbHandler;

    //SessionData sessionData;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    List<FixrImage> allFixrImages;
    List<Fixr> allFixrs;

    @Override
    public void onCreate() {


        // it will run when internet is connected and within the range.

        //   Toast.makeText(getApplicationContext(),"Connnected",Toast.LENGTH_SHORT).show();

        /**
         * create database reference
         */
        dbHandler = new DatabaseHandler(getApplicationContext());
        allFixrImages = new ArrayList<FixrImage>();
        allFixrs = new ArrayList<Fixr>();
        /**
         * get all fixrs length in database
         */


        /**
         * get all fixrs and image details from database
         */


        // } else {           // it will run when internet is connected but out of range.
        //  Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_SHORT).show();
        /**
         * stop service if no internet
         */
        //         stopSelf();

        //  }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        final int fixrcount = dbHandler.getSavedFixrLength();
        final int imageCount = dbHandler.getSavedImagesLength();
        new Thread() {
            public void run() {

                //   String webAddressToPost = getResources().getString(R.string.send_fixr_data_to_serevr);
                if (imageCount > 0) {
                    allFixrImages = dbHandler.getAllFixrImages();
                    for (int i = 0; i < allFixrImages.size(); i++) {
                        ImageUploadTask imgTask = new ImageUploadTask(getApplicationContext(),
                                allFixrImages.get(i).getImageUri(), allFixrImages.get(i).getImageName(),
                                allFixrImages.get(i).getImageId());
                        imgTask.encodeImagetoString();
                        // imgTask.encodeImagetoString();
                        // allFixrImages.get(0);
                    }
                }

            }


        }.start();

        new Thread() {
            public void run() {
                if (fixrcount > 0) {
                    allFixrs = dbHandler.getAllFixrs();
                    for (int i = 0; i < allFixrs.size(); i++) {
                        SendDataToFixrServer sdfs = new SendDataToFixrServer();
                        sdfs.setData(getApplicationContext(), allFixrs.get(i).getId(), allFixrs.get(i).getName(), allFixrs.get(i).getCity(),
                                allFixrs.get(i).getWork_type(),
                                allFixrs.get(i).getTrade(), allFixrs.get(i).getPhone(),
                                allFixrs.get(i).getLevel(), allFixrs.get(i).getAddress(), allFixrs.get(i).getWork_hours(), allFixrs.get(i).getCost(),
                                allFixrs.get(i).getCertification(), allFixrs.get(i).getExperience(), allFixrs.get(i).getVerification(), allFixrs.get(i).getLoginUser(),
                                allFixrs.get(i).getDataLatitude(), allFixrs.get(i).getDataLongitude());
                        sdfs.execute();

                    }

             /*   for(Fixr fixr:allFixrs){
                    new SendDataToFixrServer(getApplicationContext(),fixr.getId(),fixr.getName(),fixr.getCity(),fixr.getWork_type(),
                            fixr.getTrade(),fixr.getPhone(),fixr.getLevel(),fixr.getAddress(),fixr.getWork_hours(),fixr.getCost(),
                            fixr.getCertification(),fixr.getExperience()).execute();

                }*/

                }

            }


        }.start();




        return START_STICKY;
    }
}


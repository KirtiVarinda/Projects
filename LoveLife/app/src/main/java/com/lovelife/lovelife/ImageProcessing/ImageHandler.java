package com.lovelife.lovelife.ImageProcessing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dx on 12/29/2016.
 */
public class ImageHandler {

    public static Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    public static Bitmap drawableToBitmap(int img, Context ctx) {
        return BitmapFactory.decodeResource(ctx.getResources(),
                img);
    }



    public void loadProfilePicForDrawer(final ImageView profilePic,final Activity activity){

        new Thread() {
            public void run() {

                /** set url into Bitmap */
                MySharedData mySharedData=new MySharedData(activity);
                String getProfilePic = mySharedData.getGeneralSaveSession(mySharedData.USER_PROFILE_PIC);
                ServerSync serverSync=new ServerSync();
                Bitmap btm = serverSync.fireUrlGetBitmap(getProfilePic);
                setImageView(btm, profilePic,activity,mySharedData);


            }
        }.start();

    }

    public void setImageView(final Bitmap btmp, final ImageView pic,final Activity activity,final MySharedData mySharedData) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (btmp != null) {
                    pic.setImageBitmap(btmp);
                    saveProfileImageInSharedPrefernces(btmp,mySharedData);
                }
            }
        });


    }


    public static void saveProfileImageInSharedPrefernces(Bitmap btm,MySharedData mySharedData){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        btm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] byt = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byt, Base64.DEFAULT);

        mySharedData.setGeneralSaveSession(MySharedData.saveProfileImage, encodedImage);

    }
    public static Bitmap getProfileImageFromSharedPrefernces(MySharedData mySharedData){
        byte[] imageAsBytes = Base64.decode( mySharedData.getGeneralSaveSession(MySharedData.saveProfileImage).getBytes(),Base64.DEFAULT);
        return  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }



}

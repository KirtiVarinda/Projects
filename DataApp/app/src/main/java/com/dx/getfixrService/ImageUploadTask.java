package com.dx.getfixrService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.dx.dataapp.DatabaseHandler;
import com.dx.dataapp.GeneralMethods;
import com.dx.dataapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;

/**
 * Created by dx on 28/04/2015.
 */

class ImageUploadTask {
    //  private String webAddressToPost = getResources().getString(R.string.send_fixr_data_to_serevr);
    Bitmap bitmap;
    Context context;
    String encodedString, bitmapName, bitmapPath;
    RequestParams params;
    DatabaseHandler dbHandler;
    int imageId;

    ImageUploadTask(Context context, String bitmapPath, String bitmapname, int imageId) {
        this.context = context;
        this.bitmapPath = bitmapPath;
        this.bitmapName = bitmapname;
        this.imageId = imageId;
    }

    public void encodeImagetoString() {
        params = new RequestParams();
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {


            }


            @Override
            protected String doInBackground(Void... param) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                bitmap = BitmapFactory.decodeFile(bitmapPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);

                params.put("filename", bitmapName);
                params.put("image", encodedString);
                // Trigger Image upload
                triggerImageUpload();
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
               /* params.put("filename", "imageName1");
                params.put("image", encodedString);
                // Trigger Image upload
                triggerImageUpload();*/
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        System.out.println("Upload triggered");
        String webAddressToPost = context.getResources().getString(R.string.send_fixr_data_to_serevr_images);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(webAddressToPost,  params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog

                        System.out.println("responce is " + response);
                        System.out.println("imageId " + imageId);


                        if (dbHandler == null) {
                            dbHandler = new DatabaseHandler(context);
                        }
                        if (response.equals("ok")) {
                            GeneralMethods.deleteRecordedFile(bitmapPath);
                            dbHandler.deleteFixrImage(imageId);
                        }

                        /**
                         * get gellery lenth to send gallery images on server.
                         *
                         */
                     /*   session.setGeneralSaveSession("profile_pic","");

                        String imagesLength=session.getGeneralSaveSession("length_of_saved_images");
                        int galleryLength=0;
                        if(!imagesLength.equals("")){
                            galleryLength=Integer.parseInt(imagesLength);

                            if(galleryLength>0){
                                galleryLength=galleryLength-1;
                                String galleryImages=session.getGeneralSaveSession("gallery_image"+galleryLength);
                                prgDialog.setMessage("Sending gallery pictures to server.");
                                prgDialog.show();



                                 session.setGeneralSaveSession("length_of_saved_images", galleryLength+"");
                                encodeImagetoString(galleryImages);

                            }else{

                            }
                        }

                    */

                        //  Toast.makeText(getApplicationContext(), "onsuccess=" + response,
                        //        Toast.LENGTH_LONG).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        // When Http response code is '404'
                        if (statusCode == 404) {

                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {

                        }
                        // When Http response code other than 404, 500
                        else {

                        }
                    }
                });
    }


}



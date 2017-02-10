package com.dx.dataapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageActivity extends Activity {
    final int camera_open = 1;
    private Uri picUri;
    private File FilepicUri;
    Button addImageButton;
    SessionData session;
    ImageView mImageView[];
    private Uri imageUri, imageUri1;
    private Uri galleryImageUri;
    Bitmap bitmap;
    Vibrator vibrator;

    // static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
       // getActionBar().hide();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mImageView = new ImageView[5];

        addImageButton = (Button) findViewById(R.id.add);
        session = new SessionData(getApplicationContext());
        mImageView[0] = (ImageView) findViewById(R.id.imageView);
        mImageView[1] = (ImageView) findViewById(R.id.imageView11);
        mImageView[2] = (ImageView) findViewById(R.id.imageView12);
        mImageView[3] = (ImageView) findViewById(R.id.imageView13);
        mImageView[4] = (ImageView) findViewById(R.id.imageView14);
        setGalleryImages(null);
        mImageView[0].setOnLongClickListener(new View.OnLongClickListener() {
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                removeImage(getApplicationContext(), 0);
                return true;
            }
        });
        mImageView[1].setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                removeImage(getApplicationContext(), 1);
                return true;
            }
        });
        mImageView[2].setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                removeImage(getApplicationContext(), 2);
                return true;
            }
        });
        mImageView[3].setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                removeImage(getApplicationContext(), 3);
                return true;
            }
        });
        mImageView[4].setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                removeImage(getApplicationContext(), 4);
                return true;
            }
        });


    }

    private void setGalleryImages(Bitmap bmp) {
        /**
         * get images from session
         * set images to the imageView
         */

        String imagesIndex = session.getGeneralSaveSession("length_of_saved_images");
        int length;
        if (imagesIndex.equals("")) {
            length = 0;
        } else {
            length = Integer.parseInt(imagesIndex);
        }
        //  Toast.makeText(getApplicationContext(),"length is "+length,Toast.LENGTH_SHORT).show();

        String galleryImages;
        for (int i = 0; i < length; i++) {
            //  mImageView[i].setImageBitmap(null);
            if (bmp != null) {
                mImageView[i].setImageBitmap(bmp);
            } else {
                mImageView[i].setImageURI(null);
            }

            //  Drawable toRecycle= mImageView[i].getDrawable();
            //  if (toRecycle != null) {
            //     ((BitmapDrawable)mImageView[i].getDrawable()).getBitmap().recycle();
            //  }

        }
        for (int i = 0; i < length; i++) {


            galleryImages = session.getGeneralSaveSession("gallery_image" + i);
            galleryImageUri = Uri.parse(galleryImages);
            //   Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            //    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),galleryImageUri);
            try {
                mImageView[i].setImageURI(galleryImageUri);
            } catch (OutOfMemoryError e) {

            }
            //  Toast.makeText(getApplicationContext(),"i is "+i,Toast.LENGTH_SHORT).show();
            //   System.out.println("imag2e="+galleryImages);
        }

        if (length < 5) {
            addImageButton.setEnabled(true);
            addImageButton.setText("ADD PICTURE TO GALLERY.");
        } else {
            addImageButton.setEnabled(false);
            addImageButton.setText("LONG PRESS IMAGE TO DELETE.");
        }


    }


    String mCurrentPhotoPath;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromCamera) {
        super.onActivityResult(requestCode, resultCode, intentFromCamera);
        if (resultCode == RESULT_OK && requestCode == camera_open) {

            /**
             * get Bitmap from Uri.
             */
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            /**
             * cropped the clicked image and add with a water mark.
             */

            Bitmap scaledphoto = null;

            scaledphoto = ImageProcessing.scaleCenterCrop(bitmap, bitmap.getWidth(), bitmap.getWidth());


            Bitmap scaledphoto1 = null;
            scaledphoto1 = Bitmap.createScaledBitmap(scaledphoto, 650, 650, true);

            Bitmap scaledphoto2 = null;
            scaledphoto2 = FixrWaterMark.mark(scaledphoto1, new Point(30, 30), 70, 40, false, getApplicationContext());

            /**
             * save image in storage with cropped resolution 650px X 650px
             * and water mark with logo
             */
            GeneralMethods.saveImageWithFile(scaledphoto2, FilepicUri);


            String imagesIndex = session.getGeneralSaveSession("length_of_saved_images");
            int length;
            if (imagesIndex.equals("")) {
                length = 0;
            } else {
                length = Integer.parseInt(imagesIndex);
            }
            length = length + 1;
            System.out.println("lengt2h=" + length);
            if (length <= 5) {
                session.setGeneralSaveSession("length_of_saved_images", length + "");
                int index = length - 1;
                session.setGeneralSaveSession("gallery_image" + index, picUri.getPath());

                //  Bitmap bmp=getBitmapFromUri(imageUri);
                // Bitmap  photo = Bitmap.createScaledBitmap(bmp, 650, 650, false);
                //   Bitmap bmp1=FixrWaterMark.mark(bmp,"www.getfixr.in",new Point(20,20),8,35,false);


                setGalleryImages(scaledphoto2);
            }


        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                FilepicUri = GeneralMethods.createDirectoryAndSaveFile1(GeneralMethods.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            picUri = Uri.fromFile(FilepicUri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);

            startActivityForResult(takePictureIntent, camera_open);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    public void takePicture(View view) {
        dispatchTakePictureIntent();
    }

    /**
     * alert box for general.
     */
    protected void removeImage(final Context ctx, final int imageeNo) {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setMessage("Do you want to delete this image.");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        removeImage(imageeNo);
                        arg0.cancel();
                    }
                });
        alertDialogBuilder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void removeImage(int ImageNo) {

        String length = session.getGeneralSaveSession("length_of_saved_images");
        int length1;
        length1 = Integer.parseInt(length);
        String galleryImages[] = new String[length1];
        System.out.println("delete index=" + length1);
        for (int i = 0; i < length1; i++) {
            galleryImages[i] = session.getGeneralSaveSession("gallery_image" + i);
        }


        for (int j = 0; j < length1; j++) {
            session.setGeneralSaveSession("gallery_image" + j, "");
            //  ((BitmapDrawable)mImageView[j].getDrawable()).getBitmap().recycle();

            mImageView[j].setImageURI(null);
        }

        int newIndex = 0;
        for (int k = 0; k < length1; k++) {
            if (k != ImageNo) {

                session.setGeneralSaveSession("gallery_image" + newIndex, galleryImages[k]);
                newIndex = newIndex + 1;
            }

        }
        int length2 = galleryImages.length - 1;
        session.setGeneralSaveSession("length_of_saved_images", length2 + "");
        setGalleryImages(null);


    }


}

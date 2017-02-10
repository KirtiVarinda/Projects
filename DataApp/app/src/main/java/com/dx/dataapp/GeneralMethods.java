package com.dx.dataapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.dx.model.FixrLocation;
import com.dx.network.SendFixrDataToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dx on 4/3/2015.
 */
public class GeneralMethods {
    static String[] locationTrackId;
    static boolean run = true;


    /**
     * alert box for general.
     *
     * @param ctx
     * @param headerText
     * @param DetailString
     * @param buttonText
     */
    protected static void alertDialogBox(Context ctx, String headerText, String DetailString, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setTitle(headerText);
        builder.setMessage(DetailString);
        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id1) {
                dialog.cancel();


            }
        });

        builder.create().show();


    }

    static public List<String> saveCitiesToSession(String str, Context ctx) {
        List<String> list = new ArrayList<String>();
        if (run) {
            run = false;
            String[] str1 = str.split(",");

            if (str1[0].equals("ok")) {
                SessionData data = new SessionData(ctx);
                data.setGeneralSaveSession("dataApp_cities_length", str1.length + "");
                for (int i = 0; i < str1.length; i++) {
                    if (i == 0) {
                        data.setGeneralSaveSession("data_city" + i, "Select City");
                        list.add("Select City");
                    } else {
                        String city1[] = str1[i].split("&7&");
                        data.setGeneralSaveSession("data_city" + i, city1[0].trim());
                        list.add(city1[0]);
                    }
                }
            } else {
                list = null;
            }
            run = true;
        }
        return list;
    }

    static List<String> reteriveCitiesFromSession(String length, Context ctx) {
        List<String> list = new ArrayList<String>();

        SessionData data = new SessionData(ctx);
        int length1 = Integer.parseInt(length);
        for (int i = 0; i < length1; i++) {
            list.add(data.getGeneralSaveSession("data_city" + i));
        }

        return list;
    }

    static boolean isNetworkActive(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;

    }

    public static List<String> getWorkLevelList() {
        List<String> listLevel = new ArrayList<String>();
        listLevel.add("Select Work Level");
        listLevel.add("Individual");
        listLevel.add("Contractor");
        listLevel.add("Part Timer");
        return listLevel;
    }

    public static List<String> getFixrList() {
        List<String> listTrades = new ArrayList<String>();
        listTrades.add("Select Trade");
        listTrades.add("Carpenter");
        listTrades.add("Electrician");
        listTrades.add("Plumber");
        listTrades.add("Mason");
        listTrades.add("Painter");
        return listTrades;
    }

    public static String[] getWorkTypeList1() {
        String[] str = {"AC Installation",
                "AC Service",
                "Air Conditioning",
                "Banner Painting",
                "Bath Fittings",
                "Bath Tub installation",
                "Brick Work",
                "Building Construction",
                "Cane Furniture",
                "Carpenter",
                "Ceiling Slabs",
                "Circuits and Wiring",
                "Civil Contracters",
                "Commercial Fitting",
                "Commercial Furniture",
                "Commercial Painter",
                "Concrete work",
                "Cupboard",
                "Custom Doors",
                "Disposal Systems",
                "Doors Repair",
                "Elecrical Wiring",
                "Electrical Equipment Installation",
                "Electrical Fittings",
                "Electrical Repairs",
                "Electrician",
                "Exterior Painting",
                "Fall Ceiling Wiring",
                "Fency Light Installation",
                "Foundation work",
                "Furniture Repair",
                "General Wood Repairwork",
                "Home Appliances Repair",
                "Home Improvement",
                "House Repairs and Maintenance",
                "Indoor lighting",
                "Interior Decorators",
                "Interior Designers",
                "Interior Painting",
                "Kitchen Cabinet",
                "Kitchen Remodeling",
                "Labour Contracters",
                "Leakage Repairs",
                "LED Lighting",
                "Main Electrical Panels",
                "Mason Work",
                "Modular Kitchen",
                "Motor Repairs",
                "Office Furniture",
                "Oil Painting",
                "Outdoor lighting",
                "Pipe blockage",
                "Pipe Fitting",
                "Plaster of Paris Work",
                "Plastic Painting",
                "Plumber",
                "Plumbing Contracters",
                "PVC Fittings",
                "Residential Painter",
                "Roof Coating and Painting",
                "Sanitary Fitting",
                "Sewar and Drain Cleaning",
                "Shower Installation",
                "Sofa repair",
                "Texture Painting",
                "Tile Work",
                "Unclogging",
                "Wall Painting",
                "Wall Texture",
                "Water proofing",
                "White Wash",
                "Window Repair",
                "Wood Polish",
                "Wood varnish",
                "Wood Windows and Frames",
                "Wood Work",
                "Wooden Cabinet",
                "MultiControls"};
        return str;
    }

    static public Bitmap decodeUri(Uri selectedImage, Context ctx) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                ctx.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                ctx.getContentResolver().openInputStream(selectedImage), null, o2);
    }

    /**
     * alert box for general.
     */
    protected static void alertDialogBoxForLocationSetting(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage("Please enable locations for better usability");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id1) {
                dialog.cancel();

                String action = "com.google.android.gms.location.settings.GOOGLE_LOCATION_SETTINGS";
                Intent settings = new Intent(action);
                ctx.startActivity(settings);
            }
        });

        builder.create().show();


    }

    public static File createDirectoryAndSaveFile1(String fileName) throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/GetFixr/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static Uri createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/GetFixr/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // File direct = new File(Environment.getExternalStorageDirectory() + "/GetFixr/");


        // File file = new File(direct, fileName);
        /*   if (file.exists()) {
            file.delete();
        }*/
        Uri imageUri = Uri.fromFile(image);
        try {
            FileOutputStream out = new FileOutputStream(image);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUri;
    }

    public static Uri saveImageandgetUri(Bitmap imageToSave, File image) {
        Uri imageUri = Uri.fromFile(image);
        try {
            FileOutputStream out = new FileOutputStream(image);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUri;
    }

    public static String getName() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        return imageFileName;
    }

    public static String getImageFirstName(String firstName, String phone) {
        String name = firstName.trim().replace(" ", "_");
        return name + "_" + phone;
    }


    public static void saveImageWithFile(Bitmap imageToSave, File image) {
        try {
            FileOutputStream out = new FileOutputStream(image);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteRecordedFile(String filePath) {
        boolean deleted = false;
        File file = new File(filePath);
        if (file.exists()) {
            deleted = file.delete();
        }
        return deleted;
    }

    /**
     * get the current address with the lat,lang.
     *
     * @param ctx
     * @param lat
     * @param lang
     * @return
     */

    protected static String getAddressFromLatLong(Context ctx, double lat, double lang) {
        Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        StringBuilder sb = new StringBuilder();
        try {
            addresses = gcd.getFromLocation(lat, lang, 1); //gives the address of current location
            final List<Address> finalAddresses = addresses;
            if (finalAddresses != null && finalAddresses.size() > 0) {
                Address address = finalAddresses.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(", ");
                }
                // sb.append(address.getLocality()).append(", ");
                //  sb.append(address.getPostalCode()).append(", ");
                sb.append(address.getCountryName());
            }
            return sb.toString();
        } catch (IOException e) {
            return sb.toString();
        }

    }


    public static String getDeviceId(Context ctx) {

        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static String getLocationDataAsJasonFromDatabase(Context context) throws JSONException {

        // System.out.println("test test");
        JSONObject jobj;
        JSONArray arr = new JSONArray();
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        List<FixrLocation> locations = dbHandler.get100TabLocations();

        locationTrackId=new String[locations.size()];

        String jString = "";
        if (locations.size() > 0) {
            for (int m = 0; m < locations.size(); m++) {
                jobj = new JSONObject();
                locationTrackId[m]=locations.get(m).getLocationid()+"";
                jobj.put("Id", locations.get(m).getLocationid());
                jobj.put("user_name", locations.get(m).getUserName());
                jobj.put("latitude", locations.get(m).getLatitude());
                jobj.put("longitude", locations.get(m).getLongitude());
                jobj.put("time", locations.get(m).getTimeIniliSec());
                arr.put(jobj);

                /*
                System.out.println("id " + locations.get(m).getLocationid());
                System.out.println("getUserName " + locations.get(m).getUserName());
                System.out.println("getLatitude " + locations.get(m).getLatitude());
                System.out.println("getLongitude " + locations.get(m).getLongitude());
                System.out.println("getTimeIniliSec " + locations.get(m).getTimeIniliSec());
                */
            }

            JSONObject jbj = new JSONObject();
            jbj.put("data", arr);

            jString = jbj.toString();
        }

        return jString;
    }

    public static void getLocationsFromServerAndSendToServer(final Context context) {
        new Thread() {
            public void run() {
                String jsn = "";
                try {
                    jsn = GeneralMethods.getLocationDataAsJasonFromDatabase(context);
                   // System.out.println("jsn= " + jsn);
                    if (!jsn.equals("")) {
                        SendFixrDataToServer data = new SendFixrDataToServer();
                        String response = data.sendTabLocationToServer(jsn, context);
                        if(response.toString().equals("ok")){
                            deleteRowFromLocation(context);
                        }
                      //  System.out.println("responce=" + response);
                    }else{
                      //  System.out.println(" blank jsn= " );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private static void deleteRowFromLocation(Context context){
        DatabaseHandler db=new DatabaseHandler(context);
        for(int i=0;i<locationTrackId.length;i++){
            db.deleteRowFromLocationTrackTable(locationTrackId[i]);
        }
    }

    public static Long currentTimeInMillisec(){
        Calendar cl=Calendar.getInstance();
       // cl.add(Calendar.DAY_OF_MONTH,-2);
        return cl.getTimeInMillis();
    }


}

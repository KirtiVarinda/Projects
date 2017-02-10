package app.funcarddeals.com.Manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;

import app.funcarddeals.com.R;

/**
 * Created by dx on 7/21/2015.
 */
public class General {

    /**
     * Get value of the edit text.
     * value is trim and remove extra white space
     */

    public static String getEditTextValue(EditText editText) {
        return editText.getText().toString().trim().replaceAll("\\s+", " ");
    }


    /**
     * Get vlaues from spinner.
     *
     * @param spinner
     * @return
     */
    public static String getSpinnerValue(Spinner spinner) {

        return spinner.getSelectedItem().toString().trim().replaceAll("\\s+", " ");
    }


    /**
     * method for checking location setting in device
     */
    public static void checkLocationSetting(final Activity activity, Context ctx) {


        if (!isLocationEnabled(ctx)) {


            /**
             * Alert box should run on UIThread.
             */
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Alert!");
                    builder.setMessage("Please enable locations.");
                    // builder.setCustomTitle(title);
                    //builder.setView(message);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(callGPSSettingIntent);
                            dialog.cancel();

                        }
                    });

                    builder.create().show();
                }
            });


        }

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
}

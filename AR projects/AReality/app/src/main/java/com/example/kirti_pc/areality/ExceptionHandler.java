package com.example.kirti_pc.areality;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.world.World;

/**
 * Created by Kirti-PC on 10/4/2016.
 */
public class ExceptionHandler extends FragmentActivity {
    public static BeyondarFragmentSupport mBeyondarFragment;
    public static World mWorld;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void checkIfSensorsAvailable() {
        PackageManager pm = this.getPackageManager();
        boolean compass = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        boolean accelerometer = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        if (!compass && !accelerometer) {
            throw new IllegalStateException(getClass().getName()
                    + " can not run without the compass and the acelerometer sensors.");
        } else if (!compass) {
            System.out.println("can not run without the compass sensor.");
            Toast toast = Toast.makeText(this.getApplicationContext(), "nooooooooo", Toast.LENGTH_SHORT);
            toast.show();
            //throw new IllegalStateException(getClass().getName() + " can not run without the compass sensor.");
        } else if (!accelerometer) {
            throw new IllegalStateException(getClass().getName()
                    + " can not run without the acelerometer sensor.");
        }else{
            System.out.println("ooooooooooookkkkkkkkkkk sensor.");
            Toast toast = Toast.makeText(this.getApplicationContext() , "okkkkkk", Toast.LENGTH_SHORT);
            toast.show();
            // ... and send it to the fragment
            mBeyondarFragment.setWorld(mWorld);

            // We also can see the Frames per seconds
            mBeyondarFragment.showFPS(true);
        }

    }
}

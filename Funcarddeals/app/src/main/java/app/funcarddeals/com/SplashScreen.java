package app.funcarddeals.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;


public class SplashScreen extends Activity {
    private static int SplashSeconds = 5;     // splash screen for 5 seconds
    private static boolean isUserLogin=false;
    private static SwitchActivities switchActivity;
    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        switchActivity = new SwitchActivities();

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(getApplicationContext());

        if(mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID).equals("")){
            isUserLogin=false;
        }else{
            isUserLogin=true;
        }


        /**
         * switch activity.
         */
        removeSplashScreen();


    }


    /**
     * Method that removes splash screen after specified period of time.
     */
    private void removeSplashScreen() {
        new Thread() {
            public void run() {

                /**
                 * n second delay
                 */
                try {
                    Thread.sleep(SplashSeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /**
                 * load landing page after 5 seconds.
                 */
                if(!isUserLogin){
                    switchActivity.openActivity(SplashScreen.this, LandingPage.class);
                }else{
                    switchActivity.openActivity(SplashScreen.this, MainPage.class);
                }


                finish();
            }
        }.start();
    }


}
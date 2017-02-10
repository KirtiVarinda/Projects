package app.funcarddeals.com;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import app.funcarddeals.com.Manager.SwitchActivities;


public class LandingPage extends Activity {
    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        switchActivity=new SwitchActivities();
    }

    /**
     * go for sign in page.
     * @param view
     */
    public void signIn(View view){

        switchActivity.openActivity(LandingPage.this, LoginScreen.class);
        finish();
    }

    /**
     * open registeration form.
     * @param view
     */
    public void register(View view){
        switchActivity.openActivity(LandingPage.this, Registration.class);

    }

    /**
     * Go for tutorial page.
     * @param view
     */
    public void tutorial(View view){

        switchActivity.openActivity(LandingPage.this, Tutorial.class);
        finish();
    }

    /**
     *
     * @param menu
     * @return
     */


}

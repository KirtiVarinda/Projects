package app.funcarddeals.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;


public class Tutorial extends Activity {
    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;


    private Activity currentActivity;

    /**
     * get all tutorial images.
     */
    int tutorials[] = {R.drawable.tutorial2, R.drawable.tutorial3, R.drawable.tutorial4};
    int i = 0;
    LinearLayout tutorialArea;
    Button moreInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        switchActivity = new SwitchActivities();
        tutorialArea = (LinearLayout) findViewById(R.id.tutorialArea);
        moreInfoButton = (Button) findViewById(R.id.button9);

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(getApplicationContext());


        /**
         * current activity reference
         */
        currentActivity = Tutorial.this;

    }

    /**
     * @param view
     */
    public void gotIt(View view) {

        if (mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID).equals("")) {
            switchActivity.openActivity(Tutorial.this, LoginScreen.class);
        }
        finish();
    }

    /**
     * @param view
     */
    public void moreInfo(View view) {
        if (i < tutorials.length) {


            tutorialArea.setBackgroundResource(tutorials[i]);
            if (i == 2) {
                moreInfoButton.setText("Finish");
            }
            i++;
        } else {
            if (mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID).equals("")) {
                switchActivity.openActivity(Tutorial.this, LoginScreen.class);
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (MenuManager.selectedMenu(id, currentActivity)) return true;


        return super.onOptionsItemSelected(item);
    }
}

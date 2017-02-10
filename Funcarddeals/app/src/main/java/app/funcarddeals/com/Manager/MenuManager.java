package app.funcarddeals.com.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;

import app.funcarddeals.com.AddFunCrad;
import app.funcarddeals.com.BuyFuncard;
import app.funcarddeals.com.LoginScreen;
import app.funcarddeals.com.Policies;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.R;
import app.funcarddeals.com.Registration;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.Tutorial;
import app.funcarddeals.com.UserProfile;

/**
 * Created by dx on 7/28/2015.
 * Class is for all the menus on all the pages and all the menus in default android menus.
 */
public class MenuManager extends Activity {
    private Context ctx;

    /**
     * Reference variable for Shared Data (Session).
     */

    private static MySharedData mySharedData;


    /**
     * general class reference for change activity.
     */

    private static SwitchActivities switchActivity;

    public MenuManager(Context context) {
        ctx = context;
        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();
    }


    public void goToHomePage(Activity switchFrom, Class switchTo) {
        switchActivity.openActivityAndClearAllOtherActivities(switchFrom, switchTo);
        switchFrom.finish();
    }


    /**
     * Logout user from funcard.
     *
     * @param switchFrom
     * @param switchTo
     */
    public void logOut(Activity switchFrom, Class switchTo) {
        String logoutString = ctx.getResources().getString(R.string.logout);
        FuncardPopups.funcardPopupForLogout(this, switchFrom, switchTo, "Alert!", logoutString);

    }

    public void removeSessionAndGoToLoginPage(Activity switchFrom, Class switchTo) {
        /**
         * Save user_id,Email,Password in Session if user if valid.
         */
        mySharedData.removeGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);
        mySharedData.removeGeneralSaveSession(MySharedData.LOGIN_USER_EMAIL);
        mySharedData.removeGeneralSaveSession(MySharedData.LOGIN_USER_ENCRIPTED_MD5_PASSWORD);
        switchActivity.openActivityByRemovingAll(switchFrom, switchTo);
        switchFrom.finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menus, menu);
        return true;
    }

//    public void closeAppGoToDeviceHome(Activity activity){
////        Intent home = new Intent("android.intent.action.MAIN");
////        home.addCategory("android.intent.category.HOME");
//        switchActivity.exits(this, AddFunCrad.class);
//
//    }

    public void openMenu(Activity activity){
        activity.openOptionsMenu();
    }



    /**
     * logout user from funcard if disabled from server.
     *
     * @param switchFrom
     * @param switchTo
     */
    public void logoutDisableUser(Activity switchFrom, Class switchTo) {
        String userDisable = ctx.getResources().getString(R.string.pop_message_disable_user);
        FuncardPopups.funcardPopupForLogoutForSomeProblem(this, switchFrom, switchTo, "Attention!", userDisable);
    }


    /**
     * open the selected menu from the activity.
     * menu is selected from the hide menus.
     *
     * @param id
     * @param currentActivity
     * @return
     */
    public static boolean selectedMenu(int id, Activity currentActivity) {
      /*  if (id == R.id.buy_funcard) {

            switchActivity.openActivity(currentActivity,  BuyFuncard.class);
            return true;
        } else */if (id == R.id.add_funcrad) {

            switchActivity.openActivity(currentActivity, AddFunCrad.class);
            return true;
        } else if (id == R.id.user_account) {

            switchActivity.openActivity(currentActivity, UserProfile.class);
            return true;
        } else if (id == R.id.policy) {
            switchActivity.openActivity(currentActivity, Policies.class);
            return true;
        } else if (id == R.id.tutorial) {
            switchActivity.openActivity(currentActivity, Tutorial.class);
            return true;
        }else if(id == R.id.logout){
            MenuManager menuManager=new MenuManager(currentActivity);
            menuManager.logOut(currentActivity, LoginScreen.class);
        }
        return false;
    }

}

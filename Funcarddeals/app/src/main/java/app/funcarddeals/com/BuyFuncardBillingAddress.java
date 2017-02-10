package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClassForUserInfo;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.Validator.FormValidation;

public class BuyFuncardBillingAddress extends Activity implements StandardMenus {

    private EditText fullName, address, city, zip;
    private Spinner spinnerState;
    private MenuManager menuManager;

    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;

    /**
     * variable for current activity reference.
     */
    private Activity currentActivity;

    /**
     * general class refernce for change activity.
     */
    SwitchActivities switchActivity;


    Context context;
    public static String key = "buyFuncardBeanClassForUserInfo";
    private static BuyFuncardBeanClassForUserInfo buyFuncardBeanClassForUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_funcard_billing_address);
        fullName = (EditText) findViewById(R.id.bill_name);
        address = (EditText) findViewById(R.id.bill_address);
        city = (EditText) findViewById(R.id.bill_city);
        zip = (EditText) findViewById(R.id.bill_zip);
        spinnerState = (Spinner) findViewById(R.id.select_bill_state);
        context = getApplicationContext();
        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForRegistration(context);


        /** initialize activity switcher */
        switchActivity = new SwitchActivities();


        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);


        currentActivity = BuyFuncardBillingAddress.this;

        /** get serialized data from previous activity.*/
        buyFuncardBeanClassForUserInfo = (BuyFuncardBeanClassForUserInfo) getIntent().getSerializableExtra(key);

    }




    public void contnue(View view) {

        if (isFormDetailValidation()) {

            /** collect data in bean class object and send object as serialized form*/
            buyFuncardBeanClassForUserInfo.setUserFullName(fullName.getText().toString());
            buyFuncardBeanClassForUserInfo.setUserAddress(address.getText().toString());
            buyFuncardBeanClassForUserInfo.setUserCity(city.getText().toString());
            buyFuncardBeanClassForUserInfo.setUserState(spinnerState.getSelectedItem().toString());
            buyFuncardBeanClassForUserInfo.setUserZip(zip.getText().toString());

            if (buyFuncardBeanClassForUserInfo.getPayWith().equals(BuyFuncard.PAYWITHPAYPAL)) {
                switchActivity.openActivityWithPassBeanClassObject(currentActivity, PayWithPayPal.class, key, buyFuncardBeanClassForUserInfo);

            }else{
                switchActivity.openActivityWithPassBeanClassObject(currentActivity, PayWithCreditCrad.class, key, buyFuncardBeanClassForUserInfo);

            }
        }

    }


    private boolean isFormDetailValidation() {
        if (!FormValidation.isValidFullName(fullName.getText().toString())) { /** get values from full name edit text and check it is valid */
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regFullName);
            return false;
        } else if (!FormValidation.isValidCity(address.getText().toString())) { /** get values from address and check it is valid */
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, getResources().getString(R.string.billing_valid_address));
            return false;
        } else if (!FormValidation.isValidCity(city.getText().toString())) { /** get values from city edit text and check it is valid */
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regCity);
            return false;
        } else if (spinnerState.getSelectedItem().toString().equals("Select State")) { /** get values from sate spinner and check it is valid */
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regState);
            return false;
        } else if (!FormValidation.isValidZipCode(zip.getText().toString())) { /** get values from zipCode edit text and check it is valid */
            FuncardPopups.funcardPopup(currentActivity, popString.titleMessage, popString.regZip);
            return false;
        } else {
            return true;
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


    @Override
    public void home(View view) {
        menuManager.goToHomePage(currentActivity, MainPage.class);
    }

    @Override
    public void favorite(View view) {
        switchActivity.openActivity(currentActivity, FuncardFavourites.class);
    }

    @Override
    public void reminder(View view) {
        switchActivity.openActivity(currentActivity, FuncardReminders.class);
    }

    @Override
    public void scanner(View view) {

    }


    public void exit(View view) {
        switchActivity.openActivityFinishCurrent(currentActivity, AddFunCrad.class);
//        menuManager=new MenuManager(this);
//        menuManager.closeAppGoToDeviceHome(currentActivity);
    }

    public void menu(View view) {
        menuManager=new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }
}

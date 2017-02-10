package app.funcarddeals.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

import app.funcarddeals.com.Manager.DataEncription;
import app.funcarddeals.com.Manager.General;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.Validator.FormValidation;
import app.funcarddeals.com.network.ServerSync;


public class Registration extends Activity {


    /**
     * server authentication variables.
     */
    private static String REGISTRATION_SUCCESSS = "funcarddeals_ok";
    private static String REGISTRATION_FAILED = "funcarddeals_sorry";

    private static String FUNCARD_FIRST_NAME = "first_name";
    private static String FUNCARD_LAST_NAME = "last_name";
    private static String FUNCARD_ZIP = "funcarddeals_zip";
    private static String FUNCARD_EMAIL = "funcarddeals_email";
    private static String FUNCARD_PASSWORD = "funcarddeals_password";
    private static String USER_REGISTERATION_URL;

    private static String nameValue, lastNameValue, zipCodeValue, emailValue, passwordValue, retype_passwordValue;
    /**
     * Reference to get funcard popup messages.
     */
    private PopupMessagesStrings popString;
    private CheckBox policyCheck;

    /**
     * Get references of the edittext widgets.
     */
    private static EditText fullName, lastName, zipCode, email, password, retype_password;
    private static Context context;
    private static Activity registrationPage;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = getApplicationContext();
        registrationPage = Registration.this;
        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForRegistration(context);

        /**
         * Initialize Edit text references.
         */
        fullName = (EditText) findViewById(R.id.name);
        lastName = (EditText) findViewById(R.id.lastName);
        zipCode = (EditText) findViewById(R.id.zip);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        retype_password = (EditText) findViewById(R.id.re_enterpass);
        policyCheck = (CheckBox) findViewById(R.id.checkBox2);

        textview=(TextView)findViewById(R.id.textView13);
        textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


    }


    /**
     * Method will invoked when register button clicked.
     *
     * @param view
     */


    public void openPolicies(View view) {
        Intent intent=new Intent(Registration.this,Policies.class);
        startActivity(intent);
    }


    public void onRegistration(View view) {

        new DoInBackground().execute();
    }


    /**
     * get all the values from the form and store in their respective Strings;
     */
    private void getDataFromForm() {
        nameValue = General.getEditTextValue(fullName);
        lastNameValue = General.getEditTextValue(lastName);
        zipCodeValue = General.getEditTextValue(zipCode);
        emailValue = General.getEditTextValue(email);
        passwordValue = General.getEditTextValue(password);
        retype_passwordValue = General.getEditTextValue(retype_password);
    }


    /**
     * Form validation.
     *
     * @return
     */
    private boolean isFormValid() {
        if (!FormValidation.isValidFullName(nameValue)) { /** get values from full name edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regFullName);
            return false;
        } else if (!FormValidation.isValidFullName(lastNameValue)) { /** get values from phoneNo edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regLastName);
            return false;
        }else if (!FormValidation.isValidEmail(emailValue)) { /** get values from email edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regEmail);
            return false;
        } else if (!FormValidation.isValidZipCode(zipCodeValue)) { /** get values from zipCode edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regZip);
            return false;
        } /*else if (!FormValidation.isValidCity(cityValue)) { // get values from city edit text and check it is valid
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regCity);
            return false;
        }  */ else if (!FormValidation.isValidPassword(passwordValue)) { /** get values from password edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regPass);
            return false;
        } else if (!passwordValue.equals(retype_passwordValue)) { /** get values from Retype pass edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regRetype_pass);
            return false;
        } else if (!policyCheck.isChecked()) { /** get values from Retype pass edit text and check it is valid */
            FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regpolicy_check);
            return false;
        } else {
            return true;
        }


    }

    /**
     * class for background tasks
     */
    private class DoInBackground extends AsyncTask<Void, Void, Void>
            implements DialogInterface.OnCancelListener {
        private ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = ProgressDialog.show(Registration.this, "", "Funcard Registration. Please wait...", true);
        }

        protected Void doInBackground(Void... unused) {


            getDataFromForm();

            /**
             * Register user with funcard server.
             * if the from detail is valid.
             */
            if (isFormValid()) {
                /**
                 * Initialize url for registering user
                 */
                USER_REGISTERATION_URL = context.getResources().getString(R.string.funcard_registeration_url);

                /**
                 * Encript Password to MD5
                 */
                String md5Password = DataEncription.encriptToMD5(passwordValue);

                String key[] = {FUNCARD_FIRST_NAME, FUNCARD_LAST_NAME, FUNCARD_ZIP, FUNCARD_EMAIL, FUNCARD_PASSWORD,"user_from"};      // set keys
                String value[] = {nameValue, lastNameValue, zipCodeValue, emailValue, md5Password,"android"};                //set values


                /**
                 * Get key value pairs for form data
                 * KeyValues will be in hash map.
                 */
                PairValues pairValues = new PairValues();
                Map<String, String> theMap = pairValues.funcardPairValue(key, value);

                /**
                 * Authenticate user from funcard deals server.
                 */
                ServerSync serverSync = new ServerSync();




                String reponse = serverSync.SyncServer(theMap, USER_REGISTERATION_URL);
                 //System.out.println("register response="+reponse);
                if (reponse.equals(REGISTRATION_SUCCESSS)) {


                    /**
                     * switch to login activity when pressed ok from popup.
                     */
                    FuncardPopups.funcardPopupForSwitchActivity(registrationPage, popString.regSuccessTitle,  getResources().getString(R.string.email_verification_register), LoginScreen.class);


                } else if (reponse.equals(REGISTRATION_FAILED)) {
                    /**
                     * user already exist.
                     */
                    FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.regUserAlreadyExist);

                } else {
                    /**
                     * network problem.
                     */
                    FuncardPopups.funcardPopup(registrationPage, popString.titleMessage, popString.networkError);
                }

            }


            return null;
        }

        protected void onPostExecute(Void unused) {
            dialog.dismiss();

        }

        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }

}

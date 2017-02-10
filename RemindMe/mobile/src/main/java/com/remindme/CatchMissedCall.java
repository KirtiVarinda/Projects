package com.remindme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CatchMissedCall extends Activity {
    String number, time,type;
    TextView callTime, mNumber, mCallContact, mAddReminder,receiveType;
    String namewithNumber;
    boolean turnOff = false;
    View layout1, layout2;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type= extras.getString("type");
            number = extras.getString("number");
            time = extras.getString("callTime");
            if (number.equals("") || number == null) {
                finish();
            } else {
                setContentView(R.layout.activity_catch_missed_call);
                this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);
                layout1 = findViewById(R.id.relativeLayout1);
                layout2 = findViewById(R.id.linearLayout2);
                imageView=(ImageView)findViewById(R.id.imageView9);
                callTime = (TextView) findViewById(R.id.textView13);
                receiveType = (TextView) findViewById(R.id.textView10);
                callTime.setText(time);
                mNumber = (TextView) findViewById(R.id.textView5);
                mCallContact = (TextView) findViewById(R.id.textView2);
                namewithNumber = getContactName(number);

                if(type.equals("call")){
                    receiveType.setText(getResources().getString(R.string.missed_call));
                    if (namewithNumber == null || namewithNumber.equals("")) {
                        mNumber.setText(number);
                    } else {
                        mNumber.setText(namewithNumber + "(" + number + ")");
                    }
                }else{
                    receiveType.setText(getResources().getString(R.string.sms_received));
                    if (namewithNumber == null || namewithNumber.equals("")) {
                       finish();
                    } else {
                        imageView.setImageResource(R.drawable.chat_icon);
                        mNumber.setText(namewithNumber + "(" + number + ")");
                    }
                }










                /**
                 * button to call contact.
                 */
                mCallContact.setOnClickListener(new OnClickListener() {
                    public void onClick(View viewParam) {
                        callContact(number);

                    }
                });

                /**
                 * button to add reminder.
                 */
                mAddReminder = (TextView) findViewById(R.id.textView3);
                mAddReminder.setOnClickListener(new OnClickListener() {
                    public void onClick(View viewParam) {
                        addReminder(namewithNumber, number);

                    }
                });
            }
        }

    }

    protected void callContact(String recipient) {
        finish();
        try {
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + recipient));
            //	intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent1);
            /*	Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:"+ recipient));
			startActivity(intent); */
        } catch (Exception e) {
            System.out.println("Problem ==" + e);
        }


    }

    protected void addReminder(String name, String number) {
        //	layout1.setBackgroundColor((Color.parseColor("#D9000000")));
        layout2.setVisibility(View.GONE);
        turnOff = true;
        Intent in = new Intent(this, CallReminder.class);
        in.putExtra("nameToCall", name);
        in.putExtra("numberToCall", number);
        in.putExtra("fromCatchCall", "yes");
        this.startActivity(in);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.catch_missed_call, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (turnOff) {
            overridePendingTransition(0, 0);
            turnOff = false;
            finish();
        }
		 
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * function to get name from contact number
     *
     * @param phoneNumber
     * @return
     */
    public String getContactName(final String phoneNumber) {
        Uri uri;
        String[] projection;
        Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
        projection = new String[]{Contacts.People.NAME};
        try {
            Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
            mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
            projection = new String[]{"display_name"};
        } catch (Exception e) {
        }


        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(0);
        }

        cursor.close();
        cursor = null;

        return contactName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




}

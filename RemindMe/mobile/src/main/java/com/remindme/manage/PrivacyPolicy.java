package com.remindme.manage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.remindme.GeneralClass;
import com.remindme.R;
import com.remindme.RemindMeSession.SharedData;

/**
 * Created by dx on 7/16/2015.
 */
public class PrivacyPolicy {
    public static String url;
    private static String POLICY_TITLE;
    private static String POLICY_MSG;
    private static SharedData shareData;

    public static void privacyPolicyCheck(final Activity mthis, final Context ctx) {
        shareData = new SharedData(ctx);
        POLICY_TITLE = ctx.getResources().getString(R.string.policy_title);
        POLICY_MSG = ctx.getResources().getString(R.string.policy_message);
        url = ctx.getResources().getString(R.string.privacy_policy_link);
        /*final TextView message = new TextView(mthis);
        final SpannableString s =
                new SpannableString(Html.fromHtml("<a href='http://remind-me.pw/#color_f1'>http://remind-me.pw/#color_f1</a>"));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());*/


        /**
         * title text
         */
        TextView title = new TextView(mthis);
        title.setText(POLICY_TITLE);
        title.setBackgroundColor(Color.WHITE);
        title.setPadding(20, 20, 20, 20);
        // title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        title.setTextSize(22);


        /**
         * message text
         */
        TextView message = new TextView(mthis);
        message.setText(POLICY_MSG);
        message.setBackgroundColor(Color.WHITE);
        message.setPadding(20, 20, 20, 20);
        //message.setGravity(Gravity.CENTER);
        message.setTextColor(Color.GRAY);
        message.setTextSize(18);


        AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
        builder.setCancelable(false);
        builder.setCustomTitle(title);
        builder.setView(message);
        builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // OpenURL.openUrlInBrowser(mthis,url);
                shareData.setGeneralSaveSession(shareData.PRIVACYPOLICY, "no");

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareData.setGeneralSaveSession(SharedData.USERDATASENDFORNUMBER, "no");
                shareData.setGeneralSaveSession(SharedData.USERDATASENDFOREMAIL, "no");
                shareData.setGeneralSaveSession(shareData.PRIVACYPOLICY, "no");
                GeneralClass.defaultAlarm(ctx);

                dialog.cancel();
            }
        });
        builder.create().show();
    }


}

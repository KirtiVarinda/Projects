package com.remindme;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remindme.AnaliticsG.TrackerName;

public class AnaliticsForActivity {
	public void gAnalitics(Context ctx,String pageName){
		 // Get tracker.
       Tracker t = ((AnaliticsG) ctx).getTracker(
           TrackerName.APP_TRACKER);

       // Set screen name.
       // Where path is a String representing the screen name.
       t.setScreenName(pageName);

       // Send a screen view.
       t.send(new HitBuilders.AppViewBuilder().build());
	}

}

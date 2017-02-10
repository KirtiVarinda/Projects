package com.remindme;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerListenerService extends WearableListenerService {

	private static final String START_ACTIVITY_PATH = "/start/MainActivity";
	private static final String CLOSE_WATCH_FACE = "mCloseWatchFace";
	@Override
	public void onPeerConnected(Node peer) {
		super.onPeerConnected(peer);

		String id = peer.getId();
		String name = peer.getDisplayName();
		Log.d("Device connected  ", "Connected peer name & ID: " + name + "|" + id);
	}
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		
		
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {

		String[] mStartActivityPathSend = messageEvent.getPath().split("_--");
	/*	for(int i=0;i<mStartActivityPathSend.length;i++){
		
			System.out.println(mStartActivityPathSend[i]);
		}*/
		if (mStartActivityPathSend[0].equals(START_ACTIVITY_PATH)) {
			Intent startIntent = new Intent(this, MainActivity.class);
			startIntent.putExtra("message", messageEvent.getPath());
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
		}else if ( (mStartActivityPathSend[0].equals(CLOSE_WATCH_FACE))){
			MainActivity.close();

		}
	}


}

package com.remindme;


import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTouchListener {
	boolean snoozSelected=false,useSelected=false;
	boolean dragging = false;
	int centerY;
	int centerX;
	boolean imgCheck=true;
	public static Activity mWatchContext;
	private GoogleApiClient mGoogleApiClient;
	private TextView mTextView,mUpperText ;
	String mUpper,mLower;
	String[] mMessage;
	TextView mdragImage,mdragBehind;
	TextView smallIcon,backColorStrip,snooz,use;
	public static String START_ACTIVITY_PATH ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		mWatchContext=this;

		
		
		//to inflate screens of watch
		WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				mTextView = (TextView) stub.findViewById(R.id.text);
				mUpperText = (TextView) stub.findViewById(R.id.textView6);
				mdragImage = (TextView) stub.findViewById(R.id.textView3);
				stub.setOnTouchListener(MainActivity.this);
				mdragImage.setOnTouchListener(MainActivity.this);
				mdragBehind = (TextView) stub.findViewById(R.id.textView2);
				snooz= (TextView )findViewById(R.id.textView1);
				use= (TextView )findViewById(R.id.textView8);
				Bundle extras =  getIntent().getExtras();
				if(extras!=null){
					String msg = extras.getString("message");
					mMessage=msg.split("_--");
					mUpperText.setText(mMessage[1]);
				}

			}
		});
		
		
		
		
		
		// close alarm after 38 seconds
		smartAlarmSnooz();
		
		
	}
	
	
	
	
	
	
	//change the color according to the theme 
	/*	protected void themeColor(String theme_selector){
		if(theme_selector.equals("purple")){
			backColorStrip.setBackgroundColor((Color.parseColor("#9000d9")));
		}else if(theme_selector.equals("red")){
			backColorStrip.setBackgroundColor((Color.parseColor("#e14c4c")));
		}else if(theme_selector.equals("yellow")){
			backColorStrip.setBackgroundColor((Color.parseColor("#FFB846")));
		}else if(theme_selector.equals("green")){
			backColorStrip.setBackgroundColor((Color.parseColor("#24C595")));
		}else if(theme_selector.equals("white")){
			backColorStrip.setBackgroundColor((Color.parseColor("#A5A5A5")));
		}else if(theme_selector.equals("blue")){
			backColorStrip.setBackgroundColor((Color.parseColor("#2EB8FF")));
		} 
	}*/
	
	
	
	
	//sets the small icons according to the alarm
	/*protected void setSmallIcon(String msg){
		if(msg.equals("Wake")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.alarmiconsmall));
		}else if(msg.equals("textMsg")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.smsiconsmall));
		}else if(msg.equals("email")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.emailiconsmall));
		}else if(msg.equals("call")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.calliconsmall));
		}else if(msg.equals("Todo")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.todoiconsmall));
		}else if(msg.equals("Event")){
			smallIcon.setBackground(getResources().getDrawable(R.drawable.eventiconsmall));
		} 
	}*/
	
	
	
	//To close the app 
	protected static void close(){
		mWatchContext.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	
	
	
	//Background task for google data layer and sending message.
	private class DoInBackground extends AsyncTask<String, Void, Void>
	implements DialogInterface.OnCancelListener{
		private ProgressDialog dialog;
		protected void onPreExecute() {
		}
		protected Void doInBackground(final String... a) {

			new Thread() {
				public void run() { 
					mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
					.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
						@Override
						public void onConnected(Bundle connectionHint) {
							Log.d(" " , "onConnected: " + connectionHint);
							// tellWatchConnectedState("connected");
							//  "onConnected: null" is normal.
							//  There's nothing in our bundle.
						}
						@Override
						public void onConnectionSuspended(int cause) {
							Log.d(" ", "onConnectionSuspended: " + cause);
						}
					})
					.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
						@Override
						public void onConnectionFailed(ConnectionResult result) {
							Log.d( " ", "onConnectionFailed: " + result);
						}
					})
					.addApi(Wearable.API)
					.build();

					mGoogleApiClient.connect();


					List<Node> nodeList=getNodes();
					for(Node node : nodeList) {
						Log.v(" ", "telling " + node.getId() );

						PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
								mGoogleApiClient,
								node.getId(),
								START_ACTIVITY_PATH,
								null
								);

						result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
							@Override
							public void onResult(MessageApi.SendMessageResult sendMessageResult) {
								Log.v("   ", "Phone: " + sendMessageResult.getStatus().getStatusMessage());
							}
						});
					}

					/*	
							SendMessageResult result = Wearable.MessageApi.sendMessage(
									mGoogleApiClient, node.getId(), START_ACTIVITY_PATH, null).await();
							if (!result.getStatus().isSuccess()) {
								System.out.println("Recevive message1 error");
								Log.e("", "ERROR: failed to send Message: " + result.getStatus());
							}else{
								System.out.println("Recevive message1 success");
								Log.e("ffff", "success to send Message: " + result.getStatus());
							}
					 */


				}
			}.start();	
			return null;
		}
		protected void onPostExecute(Void unused) {
			//populate_listview();
		}
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
	}



	private List<Node> getNodes() {
		List<Node> nodes = new ArrayList<Node>();
		NodeApi.GetConnectedNodesResult rawNodes =
				Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
		for (Node node : rawNodes.getNodes()) {
			nodes.add(node);
			String nodeID = node.getId();
		}
		return nodes;

	}
	
	
	
	//for slide animation
	
	private Rect hitRectSnooz = new Rect();
	private Rect hitRectUse = new Rect();
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(imgCheck){
			centerX=(int) mdragImage.getX();
			centerY=(int) mdragImage.getY();
			imgCheck=false;
		}
		PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
		PointF StartPT = new PointF();
		boolean eventConsumed = true;
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:{
			if(v==mdragImage){
				dragging=true;
				mdragBehind.setVisibility(View.INVISIBLE);
				mdragImage.setVisibility(View.VISIBLE);
				snooz.setVisibility(View.VISIBLE);
				use.setVisibility(View.VISIBLE);
				eventConsumed = false;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (dragging) {
				snooz.getHitRect(hitRectSnooz);
				use.getHitRect(hitRectUse);
				moveImage(event, DownPT, StartPT);
				if (hitRectSnooz.contains(x, y)){

					mdragImage.setVisibility(View.INVISIBLE);
					snoozSelected=true;
					useSelected=false;
				}else if(hitRectUse.contains(x, y)){

					mdragImage.setVisibility(View.INVISIBLE);
					snoozSelected=false;
					useSelected=true;
				} else{
					mdragImage.setVisibility(View.VISIBLE);
					snoozSelected=false;
					useSelected=false;

				}

			}
			eventConsumed = false;
			break;

		}
		case MotionEvent.ACTION_UP:{
			if (dragging) {
				mdragBehind.setVisibility(View.VISIBLE);

				moveImage1();
				mdragImage.setVisibility(View.VISIBLE);
				snooz.setVisibility(View.INVISIBLE);
				use.setVisibility(View.INVISIBLE);
			}
			if(snoozSelected){
				START_ACTIVITY_PATH = "snooze";
				new DoInBackground().execute("");
				finish();


			}else if(useSelected){
				START_ACTIVITY_PATH = "done";
				new DoInBackground().execute("");
				finish();

			}
			dragging = false;
			snoozSelected=false;
			useSelected=false;
		}

		break;
		}
		return eventConsumed;
	}
	protected void moveImage1() {
		PointF StartPT;
		mdragImage.setX((centerX));
		mdragImage.setY((centerY));
		StartPT = new PointF( mdragImage.getX(), mdragImage.getY() );
	}
	protected void moveImage(MotionEvent event, PointF DownPT, PointF StartPT) {
		PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
		mdragImage.setX((int)(StartPT.x+mv.x-(mdragImage.getWidth() / 2)));
		mdragImage.setY((int)(StartPT.y+mv.y-(mdragImage.getWidth() / 2)));
		StartPT = new PointF( mdragImage.getX(), mdragImage.getY() );
	}
	protected void smartAlarmSnooz(){
		Thread th=new Thread() {
			public void run() {
				// TODO for text to speech code
				try {
					Thread.sleep(38*1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				finish();

			}
		};
		th.start();
	}
}

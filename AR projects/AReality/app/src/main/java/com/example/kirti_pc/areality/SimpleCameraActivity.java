/*
 * Copyright (C) 2014 BeyondAR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kirti_pc.areality;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import java.util.ArrayList;
import java.util.Iterator;

public class SimpleCameraActivity extends FragmentActivity implements
		OnTouchBeyondarViewListener, OnClickBeyondarObjectListener {

	private BeyondarFragmentSupport mBeyondarFragment;
	private World mWorld;
	int duration = Toast.LENGTH_SHORT;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setContentView(R.layout.simple_camera);
		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);


		World world = new World(getApplicationContext());

// The user can set the default bitmap. This is useful if you are
// loading images form Internet and the connection get lost


		world.setDefaultBitmap(R.drawable.beyondar_default_unknow_icon,0);

// User position (you can change it using the GPS listeners form Android
// API)
		world.setGeoPosition(30.713942, 76.696130);

// Create an object with an image in the app resources.
		GeoObject go1 = new GeoObject(1l);
		go1.setGeoPosition(30.713942, 76.696130);
		go1.setImageResource(R.drawable.creature_1);
		go1.setName("Creature 1");

// Is it also possible to load the image asynchronously form internet
		GeoObject go2 = new GeoObject(2l);
		go2.setGeoPosition(30.713942, 76.696130);
		go2.setImageUri("http://beyondar.com/sites/default/files/logo_reduced.png");
		go2.setName("Online image");

// Also possible to get images from the SDcard
		GeoObject go3 = new GeoObject(3l);
		go3.setGeoPosition(30.713942, 76.696130);
		go3.setImageUri("/sdcard/someImageInYourSDcard.jpeg");
		go3.setName("IronMan from sdcard");

// And the same goes for the app assets
		GeoObject go4 = new GeoObject(4l);
		go4.setGeoPosition(30.713942, 76.696130);
		go4.setImageUri("assets://creature_7.png");
		go4.setName("Image from assets");

// We add this GeoObjects to the world
		world.addBeyondarObject(go1);
		world.addBeyondarObject(go2);
		world.addBeyondarObject(go3);
		world.addBeyondarObject(go4);

// Finally we add the Wold data in to the fragment
		checkIfSensorsAvailable();
		mBeyondarFragment.setOnClickBeyondarObjectListener(this);
	}
	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
		// The first element in the array belongs to the closest BeyondarObject
		Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onTouchBeyondarView(MotionEvent event, BeyondarGLSurfaceView beyondarView) {

		float x = event.getX();
		float y = event.getY();

		ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();

		// This method call is better to don't do it in the UI thread!
		// This method is also available in the BeyondarFragment

		beyondarView.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);
		String textEvent = "";
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				textEvent = "Event type ACTION_DOWN: ";
				break;
			case MotionEvent.ACTION_UP:
				textEvent = "Event type ACTION_UP: ";
				break;
			case MotionEvent.ACTION_MOVE:
				textEvent = "Event type ACTION_MOVE: ";
				break;
			default:
				break;
		}

		Iterator<BeyondarObject> iterator = geoObjects.iterator();
		while (iterator.hasNext()) {
			BeyondarObject geoObject = iterator.next();
			// ...
			// Do something
			// ...
		}
	}




	private void checkIfSensorsAvailable() {
		PackageManager pm = this.getPackageManager();
		boolean compass = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
		boolean accelerometer = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
		if (!compass && !accelerometer) {
			Toast toast = Toast.makeText(this.getApplicationContext() , "aaaa + ccccccc", duration);
			toast.show();
			//throw new IllegalStateException(getClass().getName()
			//		+ " can not run without the compass and the acelerometer sensors.");
		} else if (!compass) {
			System.out.println("can not run without the compass sensor.");
			Toast toast = Toast.makeText(this.getApplicationContext(), "nooooooooo", duration);
			toast.show();
			//throw new IllegalStateException(getClass().getName() + " can not run without the compass sensor.");
		} else if (!accelerometer) {
			Toast toast = Toast.makeText(this.getApplicationContext() , "aaaaaaa", duration);
			toast.show();
			//throw new IllegalStateException(getClass().getName()
			//		+ " can not run without the acelerometer sensor.");
		}else{
			System.out.println("ooooooooooookkkkkkkkkkk sensor.");
			Toast toast = Toast.makeText(this.getApplicationContext() , "okkkkkk", duration);
			toast.show();
			// ... and send it to the fragment
			mBeyondarFragment.setWorld(mWorld);

			// We also can see the Frames per seconds
			mBeyondarFragment.showFPS(true);
		}

	}

}

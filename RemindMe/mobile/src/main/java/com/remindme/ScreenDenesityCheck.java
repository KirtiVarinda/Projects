package com.remindme;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
public class ScreenDenesityCheck {
	static Bitmap[] resizedBitmap;
	public static Bitmap[] checkScreenDensityAndResize( Bitmap bitmap[],DisplayMetrics metrics,String screenDensity,String screenSize1){
		
		resizedBitmap=new Bitmap[bitmap.length];
		for(int i=0;i<bitmap.length;i++){
			float scaleWidth;
			float scaleHeight;
			Bitmap bitmapOrg = bitmap[i];
			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();
			if( screenDensity.equals("xhigh")){
				scaleWidth = (metrics.scaledDensity)/1.3f;
				scaleHeight = (metrics.scaledDensity)/1.3f;  
			} else if( screenDensity.equals("medium") && screenSize1.equals("normalScreen")){
				scaleWidth = (metrics.scaledDensity)/1.5f;
				scaleHeight = (metrics.scaledDensity)/1.5f;  
			}else if( screenDensity.equals("medium") && screenSize1.equals("largeScreen")){
				scaleWidth = (metrics.scaledDensity)/1.2f;
				scaleHeight = (metrics.scaledDensity)/1.2f;
			} else{
				scaleWidth = (metrics.scaledDensity)/1.5f;
				scaleHeight = (metrics.scaledDensity)/1.5f;
			}
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			resizedBitmap[i] = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		}
		return resizedBitmap;
	}

}

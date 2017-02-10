package com.dx.dataapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by dx on 07/04/2015.
 */
public class FixrWaterMark {

    public static Bitmap mark(Bitmap src, Point location, int alpha, int textsize, boolean underline,Context ctx) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, src.getConfig());

     Bitmap largeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.logo);
       /*    int w1 = largeIcon.getWidth();
        int h1 = largeIcon.getHeight();
        Bitmap bmp2 = Bitmap.createBitmap(w, h, largeIcon.getConfig());
        */

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(src, 0, 0, null);
        //canvas.drawBitmap(bmp2, 0, 0, null);


        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(alpha);
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
       // canvas.drawText(watermark, location.x, location.y, paint);
        canvas.drawBitmap(largeIcon, location.x, location.y, paint);
        return bmp;
    }
}

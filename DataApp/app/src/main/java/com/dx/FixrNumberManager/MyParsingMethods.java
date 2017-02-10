package com.dx.FixrNumberManager;

import android.content.Context;

import com.dx.dataapp.DatabaseHandler;
import com.dx.dataapp.GeneralMethods;
import com.dx.dataapp.R;
import com.dx.dataapp.SessionData;
import com.dx.model.FixrMobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by dx on 3/3/2015.
 */
public class MyParsingMethods {
    DatabaseHandler db;
    FixrMobile FM;
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    public InputStream downloadUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(30000 /* milliseconds */);
        conn.setConnectTimeout(35000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream is = conn.getInputStream();
        return is;

    }

    public void getCitiesXmlandSaveToDatabase(Context ctx) throws Exception {
        db=new DatabaseHandler(ctx);
        SessionData session=new SessionData(ctx);
        String URL = ctx.getResources().getString(R.string.fixer_number);
        InputStream stream = null;
        GetFixrNumberXmlParser getFixrNumberCitiesXmlParser = new GetFixrNumberXmlParser();
        List<GetFixrNumberXmlParser.Numbers> number = null;

        MyParsingMethods myPM = new MyParsingMethods();


        String xmlTime=session.getGeneralSaveSession(SessionData.FixrNumberXmlTime);
        if(xmlTime.equals("")){
            xmlTime="0";
        }
        URL=URL+"?time="+xmlTime;
        System.out.println("number xml URL="+URL);
        stream = myPM.downloadUrl(URL);

        number = getFixrNumberCitiesXmlParser.parse(stream);

        for (GetFixrNumberXmlParser.Numbers numb : number) {
            FM=new FixrMobile();
            FM.setmMobileNumber(numb.number);
            db.saveFixrMobile(FM);
            System.out.println("fixr number= "+numb.number);


        }


        session.setGeneralSaveSession(SessionData.FixrNumberXmlTime, GeneralMethods.currentTimeInMillisec()+"");
    }

}

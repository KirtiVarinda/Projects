package com.dx.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.dx.dataapp.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Avnish on 28/04/2015.
 */
public class SendImageToServer {

    public String sendImageToServer(Context context,Bitmap bitmap){
        String url=context.getResources().getString(R.string.send_fixr_data_to_serevr);
        String sResponse="";
        try {
            //   String webAddressToPost = context.getResources().getString(R.string.send_fixr_data_to_serevr);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            String file = Base64.encodeToString(data, Base64.DEFAULT);

            entity.addPart("uploaded", new StringBody(file));
            //  entity.addPart("someOtherStringToSend", new StringBody("your string here"));

            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost,localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));

             sResponse = reader.readLine();

        } catch (Exception e) {
            System.out.println("problem sending image.");

            // something went wrong. connection with the server error
        }
        return sResponse;
    }
}

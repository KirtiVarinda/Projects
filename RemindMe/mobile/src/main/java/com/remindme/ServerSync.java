package com.remindme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ServerSync {
	private static String USERDATA_URL;


	ServerSync(Context ctx){
		USERDATA_URL=ctx.getResources().getString(R.string.user_data);
	}
	public String SyncServer(String jsonData,String jsonType){



		String response="";
		URL url = null;
		HttpURLConnection urlConn;
		OutputStreamWriter printout;
		DataInputStream  input;
		try {
			//System.out.println(jsonType+" jsonDatfa="+jsonData);
		//	System.out.println("USERDATA_URL="+USERDATA_URL);
			url = new URL (USERDATA_URL);
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setReadTimeout(20000);
			urlConn.setConnectTimeout(25000);
			urlConn.setRequestMethod("POST");
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			//urlConn.setRequestProperty("Content-Type","application/json"); 
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair(jsonType, jsonData));


			//  


			//Create JSONObject here
			//JSONObject jsonParam = new JSONObject();

			//jsonParam.put(jsonType, jsonData);
			OutputStream os = urlConn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(params));

			//printout = new OutputStreamWriter(urlConn.getOutputStream ());
			//printout.write(URLEncoder.encode(params,"UTF-8"));
			writer.flush ();
			writer.close ();
			os.close();
			urlConn.connect();  
			response=getResponceAndDisconnectConnection(urlConn);


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}


	/**
	 * Method for getting response in return of url fire.
	 * 
	 * @param urlConnection
	 * @return
	 */
	public String getResponceAndDisconnectConnection(HttpURLConnection urlConnection){
		StringBuilder sb = new StringBuilder(); 
		String response="error";
		try {
			int HttpResult =urlConnection.getResponseCode();  
			if(HttpResult ==HttpURLConnection.HTTP_OK){ 
				BufferedReader br = new BufferedReader(new InputStreamReader(  
						urlConnection.getInputStream(),"utf-8"));  
				String line = null;  

				while ((line = br.readLine()) != null) {  
					sb.append(line);  
				}  

				br.close();  
				response=sb.toString();
				//System.out.println("json response= "+sb.toString());

			}else{  
				response="http_error";
			//	System.out.println(urlConnection.getResponseMessage());
			}  
		} catch (MalformedURLException e) {  

			e.printStackTrace();  
		}  
		catch (IOException e) {  

			e.printStackTrace();  
		}finally{  
			if(urlConnection!=null)  
				urlConnection.disconnect();  
		} 
		return response;
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

}

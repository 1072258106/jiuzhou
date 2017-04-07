package com.liu.AssetsScan.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

//import com.fz.ilucky.LuckyApplication;


public class HttpHelper {
	protected static HttpHelper instance;
	private int connectionTimeout = 20000;
	protected ConnectivityManager connectivityManager = null;
	private HttpClient httpclient = null;
	protected Context mContext = null;
	private int soTimeout = 20000;
	private int socketBufferSize = 8192;

	private HttpHelper(Context context) {
		this.mContext = context;
	}

	private Map<String, String> addLocalParams(Map<String, String> value) {
		if (value == null)
			value = new HashMap<String, String>();
		/*
		value.put("appId", LuckyApplication.APP_ID);
		Object[] s = value.keySet().toArray();
		Arrays.sort(s);
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<s.length;i++){
			sb.append(value.get(s[i]));
		}
		sb.append(LuckyApplication.APP_KEY);
		value.put("sign", SignatureUtil.encode(SignatureUtil.SHA256, sb.toString()));
		*/
		return value;
	}

	public static HttpHelper getInstance(Context context) {
		if (instance == null)
			instance = new HttpHelper(context);
		return instance;
	}

	private void initHttpClient() {
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(localBasicHttpParams,
				false);
		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
				this.connectionTimeout);
		HttpConnectionParams.setSoTimeout(localBasicHttpParams, this.soTimeout);
		HttpConnectionParams.setSocketBufferSize(localBasicHttpParams,
				this.socketBufferSize);
		this.httpclient = new DefaultHttpClient(localBasicHttpParams);
	}

	public String Post(String requrl, Map<String, Object> value) {
		return Post(requrl, value, 0);
	}

	public String Post(String requrl, Map<String, Object> value, int timeout) {
		String requestGson = "";
		HttpPost httppost;
		ArrayList<BasicNameValuePair> arraylist;
		HttpResponse httpresponse;
		HttpEntity httpentity;
		InputStream is;
		ByteArrayOutputStream bytearrayoutputstream;
		byte temp[];
		int j;
		Matcher matcher;
		if (timeout == 0)
			httpclient = CustomerHttpClient.getHttpClient();
		else
			httpclient = CustomerHttpClient.getHttpClientWhitTimeOut(timeout);
		
		Log.e("eeee",requrl);
/*
		if (LuckyApplication.NetworkMode != 1) 
		{//是否是测试模式
			httppost = new HttpPost(requrl);
		} 
		else 
		{
			matcher = Common.getMatcher(requrl, "http://.*?/");
			String tmp = "";
			while (matcher.find()) {
				tmp = matcher.group().replace("http://", "").replace("/", "");
			}
			httppost = new HttpPost(requrl.replace(tmp, "10.0.0.172"));
			httppost.addHeader("X-Online-Host", tmp);
		}
*/
		httppost = new HttpPost(requrl);
		//httppost.setHeader("Cookie", "AspxAutoDetectCookieSupport=1");
		arraylist = new ArrayList<BasicNameValuePair>();
		//value = addLocalParams(value);
		/*
		if (value != null) {
			Iterator<Entry<String, String>> iterator = value.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				if(entry.getValue() != null && entry.getValue().length() > 0){
					BasicNameValuePair basicnamevaluepair = new BasicNameValuePair(
							entry.getKey(), entry.getValue());
					arraylist.add(basicnamevaluepair);
					
					
					Log.e(entry.getKey(),entry.getValue());
				}
			}
		}
		*/
		//JSONObject msgBody = new JSONObject(value);
		
		
		//Log.e("qqqqqqqqqqqqqqqqqqqq",msgBody.toString());
		
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		String json = gson.toJson(value);
		
		Log.e("rrrrrrrrrrrrrrrr",json);
		
		
		try {
			//httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF8"));
			/*
			Log.e("stringstring",arraylist.toString());
			StringEntity se = new StringEntity(arraylist.toString()); 
			httppost.setEntity(se);
			*/
			StringEntity entity = new StringEntity(/*msgBody.toString()*/json, HTTP.UTF_8);
			httppost.setEntity(entity);
			httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
		
			//httppost.addHeader("Accept-Encoding", "gzip");
			//httppost.addHeader("Connection", "Keep-Alive");
			httpresponse = httpclient.execute(httppost);
			httpentity = httpresponse.getEntity();
			
			Log.e("return",httpresponse.getStatusLine().getStatusCode()+"");
			
			
			if (httpresponse.getStatusLine().getStatusCode() == 200
					&& httpentity != null) {
				is = httpentity.getContent();
				//Header header = httpresponse.getFirstHeader("Content-Encoding");
				/*
				if (header != null
						&& header.getValue().equalsIgnoreCase("gzip"))
					is = new GZIPInputStream(is);
					*/
				bytearrayoutputstream = new ByteArrayOutputStream();
				temp = new byte[128];
				while ((j = is.read(temp)) != -1) {
					bytearrayoutputstream.write(temp, 0, j);
				}
				bytearrayoutputstream.flush();
				requestGson = new String(bytearrayoutputstream.toByteArray());
				
				Log.e("requestGson",requestGson);
				
				bytearrayoutputstream.close();
				is.close();
			}
			if (httpentity != null)
				httpentity.consumeContent();
		} catch (Exception ex) {
			return "";
		}
		return requestGson;
	}

	protected HttpURLConnection URLConnection(String url) {
		HttpURLConnection localHttpURLConnection = null;
		if (checkNetWordState() > 0) {
			try {
				localHttpURLConnection = (HttpURLConnection) new URL(url)
						.openConnection();
				if (localHttpURLConnection != null) {
					localHttpURLConnection.setConnectTimeout(20000);
					localHttpURLConnection.setReadTimeout(20000);
				}

			} catch (Exception localException) {
				localException.printStackTrace();
				localHttpURLConnection = null;
			}
		}
		return localHttpURLConnection;
	}

	protected int checkNetWordState() {
		 int i = ConnectivityManager.TYPE_WIFI;
		NetworkInfo localNetworkInfo = this.connectivityManager
				.getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable())) {
			return localNetworkInfo.getType();
		}
		return -1;
	}
	
	public static String getResultForHttpGet(String url) throws ClientProtocolException, IOException{  
        String result="";  
         
        HttpGet httpGet=new HttpGet(url);
        HttpResponse response=new DefaultHttpClient().execute(httpGet);  
        if(response.getStatusLine().getStatusCode()==200){  
            HttpEntity entity=response.getEntity();  
            result=EntityUtils.toString(entity, HTTP.UTF_8);  
        }  
        return result;  
	}
	
}

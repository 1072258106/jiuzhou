package com.liu.AssetsScan.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

public class CustomerHttpClient
{
  private static final String CHARSET = "UTF-8";
  private static HttpClient customerHttpClient;

  public static HttpClient getHttpClient()
  {
      if (customerHttpClient == null)
      {
        BasicHttpParams localBasicHttpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(localBasicHttpParams, HttpVersion.HTTP_1_0   /*HTTP_1_1*/);
        HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
        ConnManagerParams.setTimeout(localBasicHttpParams, 10000L);
        HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 20000);
        HttpConnectionParams.setSoTimeout(localBasicHttpParams, 20000);
        SchemeRegistry localSchemeRegistry = new SchemeRegistry();
        localSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //localSchemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        customerHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(localBasicHttpParams, localSchemeRegistry), localBasicHttpParams);
      }
      HttpClient localHttpClient = customerHttpClient;
      return localHttpClient;
  }

  public static HttpClient getHttpClientWhitTimeOut(int timeout)
  {
      BasicHttpParams localBasicHttpParams = new BasicHttpParams();
      HttpProtocolParams.setVersion(localBasicHttpParams, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
      HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
      ConnManagerParams.setTimeout(localBasicHttpParams, timeout);
      HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, timeout);
      HttpConnectionParams.setSoTimeout(localBasicHttpParams, timeout);
      SchemeRegistry localSchemeRegistry = new SchemeRegistry();
      localSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
      localSchemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
      DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(localBasicHttpParams, localSchemeRegistry), localBasicHttpParams);
      return localDefaultHttpClient;
   
  }
}
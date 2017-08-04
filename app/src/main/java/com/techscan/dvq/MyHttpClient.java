package com.techscan.dvq;

import android.support.annotation.Nullable;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


public class MyHttpClient 
{
    @Nullable
    private static       HttpClient mHttpClient = null;
    private static final String     CHARSET     = HTTP.UTF_8;
    //�����캯�������ֻ��ͨ������ӿ�����ȡHttpClientʵ��  
    private MyHttpClient(){  
  
    }  
    @Nullable
    public static HttpClient getHttpClient(){
        if(mHttpClient == null){  
            mHttpClient = new DefaultHttpClient();  
        }  
        return mHttpClient;  
    }  
    @Nullable
    public static synchronized HttpClient getSaveHttpClient(){
        if(mHttpClient == null){  
            HttpParams params = new BasicHttpParams();  
            //���û�������  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params, "gb2312");  
            HttpProtocolParams.setUseExpectContinue(params, true);  
            
            
            //��ʱ����  
            /*�����ӳ���ȡ���ӵĳ�ʱʱ��*/  
            ConnManagerParams.setTimeout(params, 1000);  
            /*���ӳ�ʱ*/  
            HttpConnectionParams.setConnectionTimeout(params, 5000);  
            /*����ʱ*/  
            HttpConnectionParams.setSoTimeout(params, 60000);  
            //����HttpClient֧��HTTp��HTTPS����ģʽ  
            SchemeRegistry schReg = new SchemeRegistry();  
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
            //ʹ���̰߳�ȫ�����ӹ���������HttpClient  
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);  
            mHttpClient = new DefaultHttpClient(conMgr, params);  
        }  
        return mHttpClient;  
    }  
	
	
	
	  public static void clearHttp()
	  {
		  mHttpClient = null;
	  }
	  
}
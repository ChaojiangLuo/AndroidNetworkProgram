package com.luocj.android.httpapache;

import android.app.Application;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


/**
 * Created by TS on 2017/7/30.
 */

public class HttpApacheApplication extends Application {

    private HttpClient mHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mHttpClient = creatHttpClient();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdownHttpClient();
    }

    private HttpClient creatHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient();
    }

    private void shutdownHttpClient() {
        if (mHttpClient != null && mHttpClient.getConnectionManager() != null) {
            mHttpClient.getConnectionManager().shutdown();
        }
    }

    public HttpClient getHttpClient() {
        return mHttpClient;
    }
}

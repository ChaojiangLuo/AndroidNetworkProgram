package com.luocj.android.httpapache;

import android.app.Application;

import org.apache.http.client.HttpClient;


/**
 * Created by TS on 2017/7/30.
 */

public class HttpApacheApplication extends Application{

    private HttpClient mHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mHttpClient = creatHeepClient();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

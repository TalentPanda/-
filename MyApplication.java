package com.demo.demos.FindU.SearchByWiFi.core.Application;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

    public static Application getInstance(){
        if (mInstance == null){
            mInstance = new MyApplication();
        }
        return mInstance;
    }

}

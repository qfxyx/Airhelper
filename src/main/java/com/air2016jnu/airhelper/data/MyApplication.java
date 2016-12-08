package com.air2016jnu.airhelper.data;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/12/7.
 */
public class MyApplication extends Application {

    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}

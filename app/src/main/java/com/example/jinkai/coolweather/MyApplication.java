package com.example.jinkai.coolweather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by jinkai on 2017/5/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}

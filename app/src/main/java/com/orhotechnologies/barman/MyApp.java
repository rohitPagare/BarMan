package com.orhotechnologies.barman;

import android.app.Application;

import com.narify.netdetect.NetDetect;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetDetect.init(this);
    }
}

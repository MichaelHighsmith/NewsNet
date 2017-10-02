package com.satyrlabs.newsnet;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();;
        //Initilaize ActiveAndroid (for data storage)
        ActiveAndroid.initialize(this);
    }
}

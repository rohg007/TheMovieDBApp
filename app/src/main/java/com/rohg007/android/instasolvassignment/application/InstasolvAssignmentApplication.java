package com.rohg007.android.instasolvassignment.application;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class InstasolvAssignmentApplication extends Application {

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.setAppContext(getApplicationContext());
    }

}

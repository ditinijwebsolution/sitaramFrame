package com.om.sitaramfrem;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class SitaramFremApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}

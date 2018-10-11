package com.jsonwizard.utils;

import android.app.Application;

import com.vijay.jsonwizard.R;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme);
    }
}

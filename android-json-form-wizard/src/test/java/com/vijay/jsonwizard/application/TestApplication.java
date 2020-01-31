package com.vijay.jsonwizard.application;

import android.app.Application;

import com.vijay.jsonwizard.R;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        setTheme(R.style.NativeFormsAppTheme);
        super.onCreate();
    }
}
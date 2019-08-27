package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class NativeFormLangUtils {

    public static String getLanguage(Context ctx) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(ctx));
        return allSharedPreferences.fetchLanguagePreference();
    }

    public static Context setAppLocale(Context context, String language) {
        Locale locale = new Locale(language);

        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        try {
            context = context.createConfigurationContext(conf);
        } catch (Exception e) {
            Log.d("LangUtils", e.toString());
        }

        return context;
    }
}

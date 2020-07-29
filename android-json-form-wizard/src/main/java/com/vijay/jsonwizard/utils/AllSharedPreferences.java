package com.vijay.jsonwizard.utils;

import android.content.SharedPreferences;


public class AllSharedPreferences {

    public static final String LANGUAGE_PREFERENCE_KEY = "locale";
    public static final String DEFAULT_LOCALE = "en";

    private SharedPreferences preferences;

    public AllSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }


    public String fetchLanguagePreference() {
        return preferences.getString(LANGUAGE_PREFERENCE_KEY, DEFAULT_LOCALE).trim();
    }

    public void saveLanguagePreference(String languagePreference) {
        preferences.edit().putString(LANGUAGE_PREFERENCE_KEY, languagePreference).apply();
    }
}

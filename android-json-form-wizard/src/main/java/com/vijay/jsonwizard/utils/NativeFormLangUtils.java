package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class NativeFormLangUtils {

    public static String getLanguage(Context ctx) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(ctx));
        return allSharedPreferences.fetchLanguagePreference();
    }

    public static Context setAppLocale(Context ctx, String language) {
        Locale locale = new Locale(language);

        Resources res = ctx.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        Context context;
        try {
            context = ctx.createConfigurationContext(conf);
        } catch (Exception e) {
            context = ctx;
            Log.d("LangUtils", e.toString());
        }

        return context;
    }

    public static String getTranslatedString(String jsonForm) {
        String translationsFileName = getTranslationsFileName(jsonForm);
        if (translationsFileName.isEmpty()) {
            Timber.e("Could not translate the String. Translation file name is not specified!");
            return jsonForm;
        }

        ResourceBundle mlsResourceBundle = ResourceBundle.getBundle(getTranslationsFileName(jsonForm));

        StringBuffer stringBuffer = new StringBuffer();
        Pattern interpolatedStringPattern = Pattern.compile("\\{\\{([a-zA-Z_0-9\\.]+)\\}\\}");
        Matcher matcher = interpolatedStringPattern.matcher(jsonForm);
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, mlsResourceBundle.getString(matcher.group(1)));
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    public static String getTranslationsFileName(String jsonForm) {
        Pattern propertiesFileNamePattern = Pattern.compile("\"?properties_file_name\"?: \"([a-zA-Z_0-9\\.]+)\"");
        Matcher matcher = propertiesFileNamePattern.matcher(jsonForm);
        return matcher.find() ? matcher.group(1) : "";
    }
}

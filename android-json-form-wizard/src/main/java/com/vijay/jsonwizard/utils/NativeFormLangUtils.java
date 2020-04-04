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
    private static Locale locale;
    private static String fileName;

    public static Locale getLocale() {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static void setLocale(Locale locale) {
        NativeFormLangUtils.locale = locale;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        NativeFormLangUtils.fileName = fileName;
    }

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

    /**
     * Performs translation on an interpolated {@param jsonFormString}
     * i.e. a String containing tokens in the format {{string_name}},
     * replacing these tokens with their corresponding values for the current Locale
     *
     * @param jsonFormString
     * @return
     */
    public static String getTranslatedString(String jsonFormString) {
        getTranslationsFileName(jsonFormString);
        if (getFileName().isEmpty()) {
            Timber.e("Could not translate the String. Translation file name is not specified!");
            return jsonFormString;
        }

        ResourceBundle mlsResourceBundle = ResourceBundle.getBundle(getFileName());

        StringBuffer stringBuffer = new StringBuffer();
        Pattern interpolatedStringPattern = Pattern.compile("\\{\\{([a-zA-Z_0-9\\.]+)\\}\\}");
        Matcher matcher = interpolatedStringPattern.matcher(jsonFormString);
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, mlsResourceBundle.getString(matcher.group(1)));
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    /**
     * Gets the name of the translation file to be applied to the {@param str}
     *
     * @param str
     * @return
     */
    public static void getTranslationsFileName(String str) {
        Pattern propertiesFileNamePattern = Pattern.compile("\"?properties_file_name\"?: ?\"([a-zA-Z_0-9\\.]+)\"");
        Matcher matcher = propertiesFileNamePattern.matcher(str);
        setFileName(matcher.find() ? matcher.group(1) : "");
    }
}

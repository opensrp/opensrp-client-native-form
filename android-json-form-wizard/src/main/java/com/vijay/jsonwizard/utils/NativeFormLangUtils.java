package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.MLS.PROPERTIES_FILE_NAME;

public class NativeFormLangUtils {
    private static Locale locale;
    private static String translatedFileName;

    public static Locale getLocale() {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static void setLocale(Locale locale) {
        NativeFormLangUtils.locale = locale;
    }

    public static String getTranslatedFileName() {
        return translatedFileName;
    }

    public static void setTranslatedFileName(String translatedFileName) {
        NativeFormLangUtils.translatedFileName = translatedFileName;
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
        if (getTranslatedFileName().isEmpty()) {
            Timber.e("Could not translate the String. Translation file name is not specified!");
            return jsonFormString;
        }
        return translateString(jsonFormString, ResourceBundle.getBundle(getTranslatedFileName(), getLocale()));
    }

    public static String getTranslatedString(String jsonFormString, String propertyFilesFolderPath) {
        getTranslationsFileName(jsonFormString);
        String translatedString = jsonFormString;
        try {
            ResourceBundle mlsResourceBundle = ResourceBundle.getBundle(getTranslatedFileName(), getLocale(), getPathURL(propertyFilesFolderPath));
            translatedString = translateString(jsonFormString, mlsResourceBundle);
        } catch (MalformedURLException e) {
            Timber.e(e);
        }
        return translatedString;
    }

    private static String translateString(String str, ResourceBundle mlsResourceBundle) {
        StringBuffer stringBuffer = new StringBuffer();
        Pattern interpolatedStringPattern = Pattern.compile("\\{\\{([a-zA-Z_0-9\\.]+)\\}\\}");
        Matcher matcher = interpolatedStringPattern.matcher(str);
        while (matcher.find()) {
            String replacement = Matcher.quoteReplacement(mlsResourceBundle.getString(matcher.group(1))
                    .replace("\n", "\\n")); // ensures \n is preserved in a String
            matcher.appendReplacement(stringBuffer, replacement);
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    private static URLClassLoader getPathURL(String path) throws MalformedURLException {
        File file = new File(path);
        URL[] urls = {file.toURI().toURL()};
        return new URLClassLoader(urls);
    }

    /**
     * Gets the name of the translation file to be applied to the {@param jsonFormString}
     *
     * @param jsonFormString
     * @return
     */
    public static void getTranslationsFileName(String jsonFormString) {
        Pattern propertiesFileNamePattern = Pattern.compile("\"?" + PROPERTIES_FILE_NAME + "\"?: ?\"([a-zA-Z_0-9\\.]+)\"");
        Matcher matcher = propertiesFileNamePattern.matcher(jsonFormString);
        setTranslatedFileName(matcher.find() ? matcher.group(1) : "");
    }
}

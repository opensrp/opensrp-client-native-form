package com.vijay.jsonwizard.utils;

import org.junit.Test;

import java.io.InputStream;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFomLangUtilsTest {

    @Test
    public void testStringInterpolation() {
        Locale.setDefault(new Locale("id"));
        String expectedJsonForm = getFileContentsAsString("test_translation_in");
        String interpolatedJsonForm = getFileContentsAsString("test_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedJSONForm(interpolatedJsonForm));

        Locale.setDefault(new Locale("en", "US"));
        expectedJsonForm = getFileContentsAsString("test_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedJSONForm(interpolatedJsonForm));
    }

    private String getFileContentsAsString(String filePath) {
        return convertStreamToString(getClass().getClassLoader().getResourceAsStream(filePath));
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

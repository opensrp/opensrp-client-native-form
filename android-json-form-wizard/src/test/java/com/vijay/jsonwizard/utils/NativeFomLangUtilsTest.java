package com.vijay.jsonwizard.utils;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFomLangUtilsTest {

    @Test
    public void testStringInterpolation() throws IOException {
        String interpolatedJsonForm = convertStreamToString(getClass().getClassLoader().getResourceAsStream("test_translation_interpolated"));
        assertEquals("hello_world_str, another_string,yet_another_string", NativeFormLangUtils.getTranslatedJSONForm(interpolatedJsonForm, "strings"));
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

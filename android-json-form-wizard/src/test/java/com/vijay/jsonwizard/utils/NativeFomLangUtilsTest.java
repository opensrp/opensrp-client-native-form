package com.vijay.jsonwizard.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFomLangUtilsTest {

    @Test
    public void testStringInterpolation() {
        assertEquals("hello_world_str, another_string,yet_another_string", NativeFormLangUtils.getTranslatedJSONForm("{{hello_world_str}}, {{another_string}},{{yet_another_string}}"));
    }
}
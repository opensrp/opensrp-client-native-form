package com.vijay.jsonwizard.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by samuelgithengi on 9/8/20.
 */
public class NativeFormsPropertiesTest {

    private NativeFormsProperties nativeFormsProperties;

    @Before
    public void setUp() {
        nativeFormsProperties = new NativeFormsProperties();
        nativeFormsProperties.setProperty("test_key", "true");
    }

    @Test
    public void testGetPropertyBooleanReturnsBoolean() {
        assertTrue(nativeFormsProperties.getPropertyBoolean("test_key"));
        assertFalse(nativeFormsProperties.getPropertyBoolean(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC));
    }

    @Test
    public void testHasPropertyShouldReturnCorrectValue() {
        assertTrue(nativeFormsProperties.hasProperty("test_key"));
        assertFalse(nativeFormsProperties.hasProperty("test_key_1"));
    }


    @Test
    public void testIsShouldReturnCorrectValue() {
        assertTrue(nativeFormsProperties.isTrue("test_key"));
        assertFalse(nativeFormsProperties.isTrue("test_key_1"));
    }


}

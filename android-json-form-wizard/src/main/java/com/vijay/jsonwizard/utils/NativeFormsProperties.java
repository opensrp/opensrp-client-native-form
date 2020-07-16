package com.vijay.jsonwizard.utils;

import java.util.Properties;

/**
 * Created by ndegwamartin on 2020-02-04.
 */
public class NativeFormsProperties extends Properties {

    public Boolean getPropertyBoolean(String key) {
        return Boolean.valueOf(this.getProperty(key));
    }

    public Boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public Boolean isTrue(String key) {
        return hasProperty(key) && getPropertyBoolean(key);
    }

    public final static class KEY {
        //Widgets
        public static final String WIDGET_DATEPICKER_IS_NUMERIC = "widget.datepicker.is.numeric";

    }
}

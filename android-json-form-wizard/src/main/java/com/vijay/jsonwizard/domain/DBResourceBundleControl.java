package com.vijay.jsonwizard.domain;

import java.util.Locale;
import java.util.ResourceBundle;

public class DBResourceBundleControl extends ResourceBundle.Control {

    private String identifier;

    public DBResourceBundleControl(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException,
            InstantiationException {
        return new DBResourceBundle(identifier);
    }

}

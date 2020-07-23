package com.vijay.jsonwizard.domain;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;

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
        return new DBResourceBundle(getPropertiesFromRepository(identifier));
    }

    // Don't cache since the DBResourceBundle handles properties for different Locales
    @Override
    public long getTimeToLive(String arg0, Locale arg1) {
        return TTL_DONT_CACHE;
    }

    private Object[][] getPropertiesFromRepository(String identifier) {
        Object[][] properties = new Object[0][];
        if (StringUtils.isNotBlank(this.identifier)) {
            FormUtils formUtils = new FormUtils();
            String propertiesString = formUtils.getPropertiesFileContentsFromDB(identifier);
            if (StringUtils.isNotBlank(propertiesString)) {
                String[] propertiesArray = propertiesString.split("\n");
                properties = new Object[propertiesArray.length][];
                for (int i = 0; i < propertiesArray.length; i++) {
                    properties[i] = propertiesArray[i].trim().split("\\s*=\\s*");
                }
            }

        }
        return properties;
    }
}

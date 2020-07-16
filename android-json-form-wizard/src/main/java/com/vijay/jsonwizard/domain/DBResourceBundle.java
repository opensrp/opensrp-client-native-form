package com.vijay.jsonwizard.domain;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ListResourceBundle;

public class DBResourceBundle extends ListResourceBundle {

    private String identifier;

    public DBResourceBundle(String identifier) {
        this.identifier = identifier;
    }

    @Override
    protected Object[][] getContents() {
        if (StringUtils.isNotBlank(this.identifier)) {
            FormUtils formUtils = new FormUtils();
            String propertiesString = formUtils.getPropertiesFileContentsFromDB(identifier);
            String[] propertiesArray = propertiesString.split("\n");
            Object[][] properties = new Object[propertiesArray.length][];
            for (int i = 0; i < propertiesArray.length; i++) {
                properties[i] = propertiesArray[i].trim().split("\\s*=\\s*");
            }
            return properties;
        }
        return null;
    }
}

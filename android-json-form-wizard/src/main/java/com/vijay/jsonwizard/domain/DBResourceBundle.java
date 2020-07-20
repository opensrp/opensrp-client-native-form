package com.vijay.jsonwizard.domain;

import java.util.ListResourceBundle;

public class DBResourceBundle extends ListResourceBundle {

    private Object[][] properties;

    public DBResourceBundle(Object[][] properties) {
        this.properties = properties;
    }

    @Override
    protected Object[][] getContents() {
        return this.properties;
    }
}

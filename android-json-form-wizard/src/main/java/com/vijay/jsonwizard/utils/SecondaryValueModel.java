package com.vijay.jsonwizard.utils;

import org.json.JSONArray;

public class SecondaryValueModel {

    private String key;
    private String type;
    private JSONArray values;

    public SecondaryValueModel(String key, String type, JSONArray values) {
        this.key = key;
        this.type = type;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONArray getValues() {
        return values;
    }

    public void setValues(JSONArray values) {
        this.values = values;
    }
}

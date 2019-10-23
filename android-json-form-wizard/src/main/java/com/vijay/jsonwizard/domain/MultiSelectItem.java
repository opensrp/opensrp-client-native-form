package com.vijay.jsonwizard.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class MultiSelectItem {
    private String key;
    private String value;

    public MultiSelectItem() {
    }

    public MultiSelectItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            return jsonObject.put(getKey(), getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

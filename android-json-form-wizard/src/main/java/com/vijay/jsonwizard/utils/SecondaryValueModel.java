package com.vijay.jsonwizard.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class SecondaryValueModel {

    private String key;
    private String type;
    private JSONArray values;
    private JSONArray valuesOpenMRSAttributes;
    private JSONObject openMRSAttributes;

    public SecondaryValueModel(String key, String type, JSONArray values, JSONObject openMRSAttributes,
                               JSONArray valuesOpenMRSAttributes) {
        this.key = key;
        this.type = type;
        this.values = values;
        this.openMRSAttributes = openMRSAttributes;
        this.valuesOpenMRSAttributes = valuesOpenMRSAttributes;
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

    public JSONObject getOpenMRSAttributes() {
        return openMRSAttributes;
    }

    public void setOpenMRSAttributes(JSONObject openMRSAttributes) {
        this.openMRSAttributes = openMRSAttributes;
    }

    public JSONArray getValuesOpenMRSAttributes() {
        return valuesOpenMRSAttributes;
    }

    public void setValuesOpenMRSAttributes(JSONArray valuesOpenMRSAttributes) {
        this.valuesOpenMRSAttributes = valuesOpenMRSAttributes;
    }
}

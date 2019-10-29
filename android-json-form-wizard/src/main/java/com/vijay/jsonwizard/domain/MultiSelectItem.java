package com.vijay.jsonwizard.domain;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    public JSONArray toJson(List<MultiSelectItem> multiSelectItems) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (MultiSelectItem multiSelectItem : multiSelectItems) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JsonFormConstants.KEY, multiSelectItem.getKey());
                jsonObject.put(JsonFormConstants.MultiSelectUtils.PROPERTY, new JSONObject(multiSelectItem.getValue()));
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}

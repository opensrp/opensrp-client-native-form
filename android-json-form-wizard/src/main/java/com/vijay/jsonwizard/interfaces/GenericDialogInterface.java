package com.vijay.jsonwizard.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public interface GenericDialogInterface {
    void addSelectedValues(JSONObject openMRSAttributes, JSONArray valueOpenMRSAttributes, Map<String, String> newValue);

    String getParentKey();

    void setFormIdentity(String formIdentity);

    void setFormLocation(String formLocation);
}
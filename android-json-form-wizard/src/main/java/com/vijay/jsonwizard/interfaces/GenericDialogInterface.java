package com.vijay.jsonwizard.interfaces;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

public interface GenericDialogInterface {
    void addSelectedValues(Map<String, String> newValue);

    String getParentKey();

    void setFormIdentity(String formIdentity);

    void setFormLocation(String formLocation);

    JSONObject getSubFormJson(String formIdentity, String subFormsLocation, Context context);
}
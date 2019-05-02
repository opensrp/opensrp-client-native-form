package com.vijay.jsonwizard.interfaces;

import org.json.JSONArray;

public interface GenericDialogInterface {
    JSONArray getPopUpFields();

    String getParentKey();

    void setFormIdentity(String formIdentity);

    void setFormLocation(String formLocation);
}
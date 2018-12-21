package com.vijay.jsonwizard.interfaces;

import java.util.Map;

public interface GenericDialogInterface {
    void addSelectedValues(Map<String, String> newValue);

    String getParentKey();

    void setFormIdentity(String formIdentity);

    void setFormLocation(String formLocation);
}
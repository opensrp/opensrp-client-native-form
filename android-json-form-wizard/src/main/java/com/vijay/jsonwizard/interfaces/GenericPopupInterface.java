package com.vijay.jsonwizard.interfaces;

import com.vijay.jsonwizard.utils.SecondaryValueModel;

import java.util.Map;

public interface GenericPopupInterface {
    void onGenericDataPass(Map<String, SecondaryValueModel> selectedValues, String parentKey, String stepName, String childKey);
}

package com.vijay.jsonwizard.interfaces;

import com.vijay.jsonwizard.utils.ValidationStatus;

import java.util.Map;

public interface OnFieldsInvalid {
    void passInvalidFields(Map<String, ValidationStatus> invalidFields);
    Map<String, ValidationStatus> getPassedInvalidFields();
}

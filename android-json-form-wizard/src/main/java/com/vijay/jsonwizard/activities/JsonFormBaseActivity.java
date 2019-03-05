package com.vijay.jsonwizard.activities;

import android.support.v7.app.AppCompatActivity;

import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.utils.ValidationStatus;

import java.util.HashMap;
import java.util.Map;

public class JsonFormBaseActivity extends AppCompatActivity implements OnFieldsInvalid {
    private Map<String, ValidationStatus> invalidFields = new HashMap<>();

    public Map<String, ValidationStatus> getInvalidFields() {
        return invalidFields;
    }

    @Override
    public void passInvalidFields(Map<String, ValidationStatus> invalidFields) {
        this.invalidFields = invalidFields;
    }

    @Override
    public Map<String, ValidationStatus> getPassedInvalidFields() {
        return getInvalidFields();
    }
}

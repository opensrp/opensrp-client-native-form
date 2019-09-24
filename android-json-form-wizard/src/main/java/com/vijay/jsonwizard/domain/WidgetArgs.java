package com.vijay.jsonwizard.domain;

import android.content.Context;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;

/**
 * @author Vincent Karuri
 */
public class WidgetArgs {

    private String stepName;
    private Context context;
    private JsonFormFragment formFragment;
    private JSONObject jsonObject;
    private CommonListener listener;
    private boolean popup;

    public String getStepName() {
        return stepName;
    }

    public Context getContext() {
        return context;
    }

    public JsonFormFragment getFormFragment() {
        return formFragment;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public CommonListener getListener() {
        return listener;
    }

    public boolean isPopup() {
        return popup;
    }

    public WidgetArgs withStepName(String stepName) {
        setStepName(stepName);
        return this;
    }

    public WidgetArgs withContext(Context context) {
        setContext(context);
        return this;
    }

    public WidgetArgs withFormFragment(JsonFormFragment formFragment) {
        setFormFragment(formFragment);
        return this;
    }

    public WidgetArgs withJsonObject(JSONObject jsonObject) {
        setJsonObject(jsonObject);
        return this;
    }

    public WidgetArgs withListener(CommonListener listener) {
        setListener(listener);
        return this;
    }

    public WidgetArgs withPopup(boolean popup) {
        setPopup(popup);
        return this;
    }


    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFormFragment(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setListener(CommonListener listener) {
        this.listener = listener;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }
}

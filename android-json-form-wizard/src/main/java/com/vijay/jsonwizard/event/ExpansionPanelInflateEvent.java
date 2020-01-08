package com.vijay.jsonwizard.event;

import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpansionPanelInflateEvent extends BaseEvent {
    private JSONObject value;
    private LinearLayout linearLayout;
    private List<String> previousSelectedValues;

    public ExpansionPanelInflateEvent(JSONObject value, LinearLayout linearLayout) {
        this.value = value;
        this.linearLayout = linearLayout;
        this.previousSelectedValues = new ArrayList<>();
    }

    public JSONObject getValues() {
        return value;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public List<String> getPreviousSelectedValues() {
        return previousSelectedValues;
    }

    public void setPreviousSelectedValues(List<String> previousSelectedValues) {
        this.previousSelectedValues = previousSelectedValues;
    }
}

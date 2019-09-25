package com.vijay.jsonwizard.event;

import android.widget.LinearLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class RefreshExpansionPanelEvent extends BaseEvent {
    private JSONArray values;
    private LinearLayout linearLayout;
    private List<String> previousSelectedValues;

    public RefreshExpansionPanelEvent(JSONArray values, LinearLayout linearLayout) {
        this.values = values;
        this.linearLayout = linearLayout;
        this.previousSelectedValues = new ArrayList<>();
    }

    public JSONArray getValues() {
        return values;
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

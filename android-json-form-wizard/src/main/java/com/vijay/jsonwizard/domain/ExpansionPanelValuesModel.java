package com.vijay.jsonwizard.domain;

import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExpansionPanelValuesModel extends SecondaryValueModel {
    private String label;
    private int index;

    public ExpansionPanelValuesModel(String key, String type, String label, int index, JSONArray values,
                                     JSONObject openmrsAttributes, JSONArray valuesOpenMRSAttributes) {
        super(key, type, values, openmrsAttributes, valuesOpenMRSAttributes);
        this.label = label;
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

package com.vijay.jsonwizard.domain;

public class ExpansionPanelItemModel {

    private String key;
    private String selectedKeys;
    private String selectedValues;

    public ExpansionPanelItemModel(String key, String selectedKeys, String selectedValues) {
        this.key = key;
        this.selectedKeys = selectedKeys;
        this.selectedValues = selectedValues;
    }

    public String getKey() {
        return key;
    }

    public String getSelectedKeys() {
        return selectedKeys;
    }

    public String getSelectedValues() {
        return selectedValues;
    }
}

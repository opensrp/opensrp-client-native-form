package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.interfaces.SubDialog;

public class DialogCheckboxFactory extends CheckBoxFactory implements SubDialog {
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public Object getType() {
        return null;
    }
}

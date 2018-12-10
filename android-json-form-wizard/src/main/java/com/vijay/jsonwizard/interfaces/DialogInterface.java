package com.vijay.jsonwizard.interfaces;

import android.view.View;

import java.util.List;

public interface DialogInterface {
    Object getValues();
    void setValues(Object data);
    List<View> getViews();
}

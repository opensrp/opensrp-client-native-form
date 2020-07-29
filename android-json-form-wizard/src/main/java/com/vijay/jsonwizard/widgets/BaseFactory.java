package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONObject;

/**
 * Created by ndegwamartin on 04/05/2020.
 */
public abstract class BaseFactory implements FormWidgetFactory {


    /**
     * Generic method incase you want to alter or reference the widgets properties
     *
     * @param view the root layout
     */
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {
        // Override this in views if you require a reference to the widget's properties
    }

}

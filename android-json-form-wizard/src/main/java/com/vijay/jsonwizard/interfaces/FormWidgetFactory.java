package com.vijay.jsonwizard.interfaces;

import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONObject;

import java.util.List;

/**
 * ViewFactory to convert JSONObjects to a view list
 */
public interface FormWidgetFactory {
    List<View> getViewsFromJson(String stepName, Context context, NativeViewer formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception;

    List<View> getViewsFromJson(String stepName, Context context, NativeViewer formFragment, JSONObject jsonObject, CommonListener listener) throws Exception;
}

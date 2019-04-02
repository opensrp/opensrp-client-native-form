package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincent Karuri
 */
public class RepeatingGroupFactory implements FormWidgetFactory {

    private String REPEATING_GROUP_LAYOUT = "repeating_group_layout";
    public static Map<Integer, JSONArray> repeatingGroupLayouts = new HashMap<>();

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);

        int rootLayoutId = View.generateViewId();
        rootLayout.setId(rootLayoutId);
        views.add(rootLayout);

        JSONArray repeatingGroupLayout = jsonObject.getJSONArray(REPEATING_GROUP_LAYOUT);
        repeatingGroupLayouts.put(rootLayoutId, repeatingGroupLayout);

        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    protected int getLayout() {
        return R.layout.native_form_repeating_group;
    }
}
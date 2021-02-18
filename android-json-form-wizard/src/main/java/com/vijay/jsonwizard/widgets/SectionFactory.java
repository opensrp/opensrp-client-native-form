package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vijay on 24-05-2015.
 */
public class SectionFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(context, jsonObject);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(Context context, JSONObject jsonObject) throws JSONException {
        List<View> views = new ArrayList<>(1);
        String text = jsonObject.getString(JsonFormConstants.NAME);
        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_section_label, null);
        TextView textView = rootLayout.findViewById(R.id.section_label);
        textView.setText(text);

        updateSection(context, jsonObject, textView, rootLayout);

        views.add(rootLayout);
        return views;
    }

    private void updateSection(Context context, JSONObject jsonObject, TextView textView, LinearLayout relativeLayout) {
        String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, "#000000");
        String backgroundColor = jsonObject.optString(JsonFormConstants.SECTION_BACKGROUND, "#FFFFFF");
        String textSize = jsonObject.optString(JsonFormConstants.TEXT_SIZE, "15px");
        String textStyle = jsonObject.optString(JsonFormConstants.TEXT_STYLE, JsonFormConstants.NORMAL);

        textView.setTextColor(Color.parseColor(textColor));
        textView.setTextSize(FormUtils.getValueFromSpOrDpOrPx(textSize, context));
        FormUtils.setTextStyle(textStyle, textView);
        relativeLayout.setBackgroundColor(Color.parseColor(backgroundColor));
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        return new HashSet<>();
    }
}

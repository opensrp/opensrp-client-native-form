package com.vijay.jsonwizard.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NumberSelectorFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);
        LinearLayout linearLayout = rootLayout.findViewById(R.id.number_selector_layout);

        rootLayout.setId(ViewUtil.generateViewId());
        rootLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        rootLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        rootLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        canvasIds.put(rootLayout.getId());
        rootLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        if (relevance != null && context instanceof JsonApi) {
            rootLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }
        views.add(rootLayout);
        createTextViews(context, jsonObject, linearLayout, listener, rootLayout);

        return views;
    }

    @SuppressLint("NewApi")
    private void createTextViews(Context context, JSONObject jsonObject, LinearLayout linearLayout, CommonListener listener,
                                 LinearLayout rootLayout) throws JSONException {
        String leftPadding = "10dp";
        String rightPadding = "10dp";
        int width = ImageUtils.getDeviceWidth(context);
        width = width - 80;

        int numberOfSelectors = jsonObject.optInt(JsonFormConstants.NUMBER_OF_SELECTORS, 5);
        int startSelectionNumber = jsonObject.optInt(JsonFormConstants.START_SELECTION_NUMBER, 1);
        int textSize = jsonObject.optInt(JsonFormConstants.TEXT_SIZE, (int) context.getResources().getDimension(R.dimen.default_text_size));
        LinearLayout.LayoutParams layoutParams = FormUtils.getLayoutParams(width / numberOfSelectors, FormUtils.WRAP_CONTENT, 1, 1, 1, 1);

        for (int i = 0; i < numberOfSelectors; i++) {
            int text = startSelectionNumber == 0 ? i : i + 1;
            CustomTextView customTextView = FormUtils.getTextViewWith(context, textSize, String.valueOf(text), jsonObject.getString
                    (JsonFormConstants.KEY), jsonObject.getString("type"), "", "", "",
                    "", layoutParams, FormUtils.FONT_BOLD_PATH, 0, null);
            customTextView.setId(R.id.number_selector_textview + i);
            customTextView.setPadding(
                    FormUtils.getValueFromSpOrDpOrPx(leftPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx("5dp", context),
                    FormUtils.getValueFromSpOrDpOrPx(rightPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx("5dp", context));
            if (i == 0) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_left_rounded_background));
            } else if (i == numberOfSelectors - 1) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_right_rounded_background));
            } else {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_normal_background));
            }
            customTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            customTextView.setOnClickListener(listener);
            linearLayout.addView(customTextView);
        }
    }

    protected int getLayout() {
        return R.layout.native_form_item_numbers_selector;
    }
}

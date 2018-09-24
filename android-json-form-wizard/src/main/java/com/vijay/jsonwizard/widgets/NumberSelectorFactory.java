package com.vijay.jsonwizard.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    CustomTextViewClick customTextViewClick = new CustomTextViewClick();

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
        createTextViews(context, jsonObject, linearLayout, listener);

        return views;
    }

    @SuppressLint("NewApi")
    private void createTextViews(Context context, JSONObject jsonObject, LinearLayout linearLayout, CommonListener listener) throws JSONException {
        String leftPadding = "10dp";
        String rightPadding = "10dp";
        int width = ImageUtils.getDeviceWidth(context);
        width = width - 80;

        int numberOfSelectors = jsonObject.optInt(JsonFormConstants.NUMBER_OF_SELECTORS, 5);
        int startSelectionNumber = jsonObject.optInt(JsonFormConstants.START_SELECTION_NUMBER, 1);
        int maxValue = jsonObject.optInt(JsonFormConstants.MAX_SELECTION_VALUE, 20);
        String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);
        int textSize = jsonObject.optInt(JsonFormConstants.TEXT_SIZE, (int) context.getResources().getDimension(R.dimen.default_text_size));
        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(width / numberOfSelectors, FormUtils.WRAP_CONTENT, 1, 1, 1, 1);

        for (int i = 0; i < numberOfSelectors; i++) {
            CustomTextView customTextView = FormUtils.getTextViewWith(context, textSize, getText(i, startSelectionNumber, numberOfSelectors, maxValue), jsonObject.getString(JsonFormConstants.KEY), jsonObject.getString("type"), "", "", "", "", layoutParams, FormUtils.FONT_BOLD_PATH, 0, textColor);
            customTextView.setId(R.id.number_selector_textview + i);
            customTextView.setPadding(
                    FormUtils.getValueFromSpOrDpOrPx(leftPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx("5dp", context),
                    FormUtils.getValueFromSpOrDpOrPx(rightPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx("5dp", context));
            setDefaultColor(context, customTextView, i, numberOfSelectors);
            customTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            customTextView.setClickable(true);
            customTextView.setOnClickListener(customTextViewClick);
            customTextView.setTag(R.id.number_selector_textview_item, i);
            customTextView.setTag(R.id.number_selector_textview_number_of_selectors, numberOfSelectors);
            customTextView.setTag(R.id.number_selector_textview_max_number, maxValue);
            if (i == numberOfSelectors - 1) {
                customTextView.setTag(R.id.number_selector_textview_layout, linearLayout);
                customTextView.setTag(R.id.number_selector_textview_jsonObject, jsonObject);
            }
            linearLayout.addView(customTextView);
        }
    }

    private void createDialogSpinner(LinearLayout linearLayout, Context context, JSONObject jsonObject, int startNumber) {
        int maxValue = jsonObject.optInt(JsonFormConstants.MAX_SELECTION_VALUE, 20);
        Spinner spinner = new Spinner(context, Spinner.MODE_DIALOG);
        linearLayout.addView(spinner);
        List<Integer> numbers = new ArrayList<>();
        for (int i = startNumber; i <= maxValue; i++) {
            numbers.add(i);
        }

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, numbers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.performClick();

    }

    private String getText(int item, int startSelectionNumber, int numberOfSelectors, int maxValue) {
        String text = String.valueOf(startSelectionNumber == 0 ? item : item + 1);
        if (item == numberOfSelectors - 1 && maxValue > Integer.parseInt(text)) {
            text = text + "+";
        }
        return text;
    }

    @SuppressLint("NewApi")
    private void setDefaultColor(Context context, CustomTextView customTextView, int item, int numberOfSelectors) {
        if (item == 0) {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_left_rounded_background));
        } else if (item == numberOfSelectors - 1) {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_right_rounded_background));
        } else {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_normal_background));
        }
    }

    @SuppressLint("NewApi")
    private void setSelectedColor(Context context, CustomTextView customTextView, int item, int numberOfSelectors) {
        if (item == 0) {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_left_rounded_background_selected));
        } else if (item == numberOfSelectors - 1) {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_right_rounded_background_selected));
        } else {
            customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_normal_background_selected));
        }
    }

    protected int getLayout() {
        return R.layout.native_form_item_numbers_selector;
    }

    private class CustomTextViewClick implements View.OnClickListener {

        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            int item = (int) view.getTag(R.id.number_selector_textview_item);
            int numberOfSelectors = (int) view.getTag(R.id.number_selector_textview_number_of_selectors);
            int maxValue = (int) view.getTag(R.id.number_selector_textview_max_number);

            if (item == numberOfSelectors - 1 && numberOfSelectors - 1 < maxValue) {
                LinearLayout linearLayout = (LinearLayout) view.getTag(R.id.number_selector_textview_layout);
                JSONObject jsonObject = (JSONObject) view.getTag(R.id.number_selector_textview_jsonObject);
                createDialogSpinner(linearLayout, view.getContext(), jsonObject, numberOfSelectors);
            }
            setSelectedColor(view.getContext(), (CustomTextView) view, item, numberOfSelectors);
            ((CustomTextView) view).setTextColor(view.getContext().getColor(R.color.white));
        }
    }
}

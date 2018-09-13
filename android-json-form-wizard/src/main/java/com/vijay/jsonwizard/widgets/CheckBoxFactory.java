package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

/**
 * Created by vijay on 24-05-2015.
 */
public class CheckBoxFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        createCheckboxElement(views, jsonObject, context, canvasIds, readOnly);
        addCheckboxOptionsElements(jsonObject, context, readOnly, canvasIds, stepName, views, listener);

        return views;
    }

    private void createCheckboxElement(List<View> views, JSONObject jsonObject, Context context, JSONArray canvasIds, Boolean
            readOnly) throws JSONException {

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        String label = jsonObject.optString(JsonFormConstants.LABEL);
        int labelTextSize = FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString(JsonFormConstants.LABEL_TEXT_SIZE, JsonFormConstants
                .DEFAULT_LABEL_TEXT_SIZE), context);
        String labelTextColor = jsonObject.optString(JsonFormConstants.LABEL_TEXT_COLOR, JsonFormConstants.OPTIONS_DEFAULT_LABEL_TEXT_COLOR);

        if (!label.isEmpty()) {
            CustomTextView textView = getTextViewWith(context, labelTextSize, label, jsonObject.getString(JsonFormConstants.KEY),
                    jsonObject.getString(JsonFormConstants.TYPE), openMrsEntityParent, openMrsEntity, openMrsEntityId, relevance,
                    getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH, 0, labelTextColor);
            canvasIds.put(textView.getId());
            textView.setEnabled(!readOnly);
            views.add(textView);
        }
    }

    private void addCheckboxOptionsElements(JSONObject jsonObject, Context context, Boolean readOnly, JSONArray canvasIds,
                                            String stepName, List<View> views, CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            LinearLayout checkboxLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_checkbox, null);

            String optionTextColor = JsonFormConstants.OPTIONS_DEFAULT_LABEL_TEXT_COLOR;
            String optionTextSize = JsonFormConstants.OPTIONS_DEFAULT_OPTION_TEXT_SIZE;
            if (item.has(JsonFormConstants.TEXT_COLOR)) {
                optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
            }
            if (item.has(JsonFormConstants.TEXT_SIZE)) {
                optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
            }

            TextView checkboxText = checkboxLayout.findViewById(R.id.text1);
            checkboxText.setText(item.getString(JsonFormConstants.TEXT));
            checkboxText.setTextColor(Color.parseColor(optionTextColor));
            checkboxText.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize, context));
            final CheckBox checkBox = checkboxLayout.findViewById(R.id.checkbox);
            checkBoxes.add(checkBox);
            checkBox.setTag(R.id.raw_value, item.getString(JsonFormConstants.TEXT));
            checkBox.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            checkBox.setTag(R.id.type, jsonObject.getString("type"));
            checkBox.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            checkBox.setTag(R.id.openmrs_entity, openMrsEntity);
            checkBox.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            checkBox.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            checkBox.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            checkBox.setOnCheckedChangeListener(listener);
            checkboxLayout.setClickable(true);
            checkboxLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(checkboxLayout.getId());
            checkboxLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.toggle();
                }
            });
            if (!TextUtils.isEmpty(item.optString(JsonFormConstants.VALUE))) {
                checkBox.setChecked(Boolean.valueOf(item.optString(JsonFormConstants.VALUE)));
            }
            checkBox.setEnabled(!readOnly);
            checkBox.setFocusable(!readOnly);
            if (i == options.length() - 1) {
                checkboxLayout.setLayoutParams(FormUtils.getLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }

            views.add(checkboxLayout);
            ((JsonApi) context).addFormDataView(checkBox);

            if (relevance != null && context instanceof JsonApi) {
                checkBox.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(checkBox);
            }

            String constraints = item.optString("constraints");
            if (constraints != null && context instanceof JsonApi) {
                checkBox.setTag(R.id.constraints, constraints);
                ((JsonApi) context).addConstrainedView(checkBox);
            }
        }

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setTag(R.id.canvas_ids, canvasIds.toString());
        }
    }
}

package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vijay on 24-05-2015.
 */
public class CheckBoxFactory implements FormWidgetFactory {
    private void showEditButton(JSONObject jsonObject, List<View> editableViews, ImageView editButton, CommonListener listener) throws JSONException {
        editButton.setTag(R.id.editable_view, editableViews);
        editButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editButton.setTag(R.id.type, jsonObject.getString("type"));
        editButton.setOnClickListener(listener);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws Exception {
        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        Map<String, View> labelViews = FormUtils.createRadioButtonAndCheckBoxLabel(views, jsonObject, context, canvasIds, readOnly, listener);
        ImageView editButton =(ImageView) labelViews.get(JsonFormConstants.EDIT_BUTTON);
        ArrayList<View>  editableCheckBoxes = addCheckBoxOptionsElements(jsonObject, context, readOnly, canvasIds, stepName, views, listener);
        showEditButton(jsonObject,editableCheckBoxes,editButton,listener);

        return views;
    }

    private ArrayList<View> addCheckBoxOptionsElements(JSONObject jsonObject, Context context, Boolean readOnly, JSONArray canvasIds,
                                                           String stepName, List<View> views, CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);


        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        ArrayList<View> checkboxLayouts = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            //Get options for alert dialog
            String labelInfoText = item.optString(JsonFormConstants.LABEL_INFO_TEXT, "");
            String labelInfoTitle = item.optString(JsonFormConstants.LABEL_INFO_TITLE, "");

            RelativeLayout checkboxLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_checkbox, null);
            createCheckBoxText(checkboxLayout, item, context, readOnly);

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
            checkboxLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(checkboxLayout.getId());

            if (!TextUtils.isEmpty(item.optString(JsonFormConstants.VALUE))) {
                checkBox.setChecked(Boolean.valueOf(item.optString(JsonFormConstants.VALUE)));
            }
            checkBox.setEnabled(!readOnly);
            if (i == options.length() - 1) {
                checkboxLayout.setLayoutParams(FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }
            //Displaying optional info alert dialog
            ImageView imageView = checkboxLayout.findViewById(R.id.checkbox_info_icon);
            FormUtils.showInfoIcon(jsonObject, listener, labelInfoText, labelInfoTitle, imageView);

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
            checkboxLayouts.add(checkboxLayout);
        }

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setTag(R.id.canvas_ids, canvasIds.toString());
        }
        return checkboxLayouts;
    }

    /**
     * Inflates and set the checkbox text attributes.
     *
     * @param checkboxLayout
     * @param item
     * @param context
     * @param readOnly
     * @throws JSONException
     */
    private void createCheckBoxText(RelativeLayout checkboxLayout, JSONObject item, Context context, Boolean readOnly) throws JSONException {
        String optionTextColor = JsonFormConstants.DEFAULT_TEXT_COLOR;
        String optionTextSize = String.valueOf(context.getResources().getDimension(R.dimen.options_default_text_size));
        if (item.has(JsonFormConstants.TEXT_COLOR)) {
            optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
        }
        if (item.has(JsonFormConstants.TEXT_SIZE)) {
            optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
        }

        TextView checkboxText = checkboxLayout.findViewById(R.id.text1);
        final CheckBox checkBox = checkboxLayout.findViewById(R.id.checkbox);
        checkboxText.setText(item.getString(JsonFormConstants.TEXT));
        checkboxText.setTextColor(Color.parseColor(optionTextColor));
        checkboxText.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize, context));
        checkboxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });
        checkboxText.setEnabled(!readOnly);
    }
}

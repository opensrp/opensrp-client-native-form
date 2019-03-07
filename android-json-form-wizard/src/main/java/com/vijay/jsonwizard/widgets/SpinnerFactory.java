package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipun on 30/05/15.
 */
public class SpinnerFactory implements FormWidgetFactory {

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            MaterialSpinner spinner) {
        if (!(spinner.getTag(R.id.v_required) instanceof String) || !(spinner.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        Boolean isRequired = Boolean.valueOf((String) spinner.getTag(R.id.v_required));
        if (!isRequired || !spinner.isEnabled()) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        int selectedItemPosition = spinner.getSelectedItemPosition();
        if (selectedItemPosition > 0) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        return new ValidationStatus(false, (String) spinner.getTag(R.id.error), formFragmentView, spinner);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                  boolean popup) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        RelativeLayout spinnerRelativeLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.native_form_item_spinner, null);

        setViewTags(jsonObject, canvasIds, stepName, popup, openMrsEntityParent, openMrsEntity, openMrsEntityId,
                spinnerRelativeLayout);
        spinnerRelativeLayout.setTag(R.id.canvas_ids, canvasIds.toString());

        addSpinner(jsonObject, spinnerRelativeLayout, listener, canvasIds, stepName, popup, context);
        views.add(spinnerRelativeLayout);
        return views;
    }

    private void addSpinner(JSONObject jsonObject, RelativeLayout spinnerRelativeLayout, CommonListener listener,
                            JSONArray canvasIds, String stepName, boolean popup, Context context) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String labelInfoText = jsonObject.optString(JsonFormConstants.LABEL_INFO_TEXT, "");
        String labelInfoTitle = jsonObject.optString(JsonFormConstants.LABEL_INFO_TITLE, "");
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculations = jsonObject.optString(JsonFormConstants.CALCULATION);

        MaterialSpinner spinner = spinnerRelativeLayout.findViewById(R.id.material_spinner);
        ImageView spinnerInfoIconImageView = spinnerRelativeLayout.findViewById(R.id.spinner_info_icon);
        ImageView editButton = spinnerRelativeLayout.findViewById(R.id.spinner_edit_button);
        FormUtils.setEditButtonAttributes(jsonObject, spinner, editButton, listener);
        String hint = jsonObject.optString(JsonFormConstants.HINT);
        if (!TextUtils.isEmpty(hint)) {
            spinner.setHint(jsonObject.getString(JsonFormConstants.HINT));
            spinner.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
        }

        setViewTags(jsonObject, canvasIds, stepName, popup, openMrsEntityParent, openMrsEntity, openMrsEntityId, spinner);

        addSkipLogicTags(context, relevance, constraints, calculations, spinner);

        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(requiredValue)) {
                spinner.setTag(R.id.v_required, requiredValue);
                spinner.setTag(R.id.error, requiredObject.optString(JsonFormConstants.ERR));
            }
        }

        String valueToSelect = "";
        int indexToSelect = -1;
        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))) {
            valueToSelect = jsonObject.optString(JsonFormConstants.VALUE);
        }

        FormUtils.setEditMode(jsonObject, spinner, editButton);

        JSONArray valuesJson = jsonObject.optJSONArray(JsonFormConstants.VALUES);
        String[] values = null;
        if (valuesJson != null && valuesJson.length() > 0) {
            values = new String[valuesJson.length()];
            for (int i = 0; i < valuesJson.length(); i++) {
                values[i] = valuesJson.optString(i);
                if (valueToSelect.equals(values[i])) {
                    indexToSelect = i;
                }
            }
        }

        if (values != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.native_form_simple_list_item_1, values);
            spinner.setAdapter(adapter);
            spinner.setSelection(indexToSelect + 1, true);
            spinner.setOnItemSelectedListener(listener);
        }
        ((JsonApi) context).addFormDataView(spinner);

        FormUtils.showInfoIcon(stepName, jsonObject, listener, labelInfoText, labelInfoTitle, spinnerInfoIconImageView,
                canvasIds);
        spinner.setTag(R.id.canvas_ids, canvasIds.toString());
    }

    private void setViewTags(JSONObject jsonObject, JSONArray canvasIds, String stepName, boolean popup,
                             String openMrsEntityParent, String openMrsEntity, String openMrsEntityId,
                             View view) throws JSONException {
        view.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        view.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        view.setTag(R.id.openmrs_entity, openMrsEntity);
        view.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        view.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        view.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        view.setTag(R.id.extraPopup, popup);
        view.setId(ViewUtil.generateViewId());
        canvasIds.put(view.getId());
    }

    private void addSkipLogicTags(Context context, String relevance, String constraints, String calculations,
                                  MaterialSpinner spinner) {
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            spinner.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(spinner);
        }
        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            spinner.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(spinner);
        }

        if (!TextUtils.isEmpty(calculations) && context instanceof JsonApi) {
            spinner.setTag(R.id.calculation, calculations);
            ((JsonApi) context).addCalculationLogicView(spinner);
        }
    }
}

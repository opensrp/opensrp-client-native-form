package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HiddenTextFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws Exception {
        List<View> views = new ArrayList<>(1);

        RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                getLayout(), null);
        MaterialEditText hiddenText = rootLayout.findViewById(R.id.edit_text);
        attachJson(stepName, context, formFragment, jsonObject, hiddenText);

        JSONArray canvasIds = new JSONArray();
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        hiddenText.setTag(R.id.canvas_ids, canvasIds.toString());

        ((JsonApi) context).addFormDataView(hiddenText);
        rootLayout.setVisibility(View.GONE);
        views.add(rootLayout);
        return views;
    }

    protected void attachJson(String stepName, Context context, JsonFormFragment formFragment,
                              JSONObject jsonObject, MaterialEditText hiddenText)
            throws Exception {

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);

        hiddenText.setId(ViewUtil.generateViewId());
        hiddenText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        hiddenText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        hiddenText.setTag(R.id.openmrs_entity, openMrsEntity);
        hiddenText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        hiddenText.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        hiddenText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));


        hiddenText.setVisibility(View.GONE);

        hiddenText.addTextChangedListener(new GenericTextWatcher(stepName, formFragment, hiddenText));
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            hiddenText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(hiddenText);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            hiddenText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(hiddenText);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            hiddenText.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(hiddenText);
        }

    }

    protected int getLayout() {
        return R.layout.native_form_item_edit_text;
    }

}
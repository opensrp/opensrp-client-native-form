package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentSpacerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonObject, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, Context context, JSONObject jsonObject, boolean popup) throws JSONException {
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_component_spacer, null);
        linearLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(linearLayout.getId());
        linearLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        linearLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        linearLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        linearLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        linearLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        linearLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        linearLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        linearLayout.setTag(R.id.extraPopup, popup);

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            linearLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(linearLayout);
        }

        attachLayout(views, context, jsonObject, linearLayout);
        return views;
    }


    private void attachLayout(List<View> views, Context context, JSONObject jsonObject, LinearLayout linearLayout) {
        String height = jsonObject.optString(JsonFormConstants.SPACER_HEIGHT);
        TextView spacerView = linearLayout.findViewById(R.id.spacer_view);
        int viewHeight = 0;

        if (height != null) {
            viewHeight = FormUtils.getValueFromSpOrDpOrPx(height, context);
        }

        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, viewHeight, 0, 0, 0, 0);
        spacerView.setLayoutParams(layoutParams);
        views.add(linearLayout);
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        return new HashSet<>();
    }
}

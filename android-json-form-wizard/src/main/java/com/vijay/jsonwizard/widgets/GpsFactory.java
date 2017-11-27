package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GpsDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Captures GPS locations
 * <p>
 * Created by Jason Rogena - jrogena@ona.io on 11/24/17.
 */

public class GpsFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context,
                                       JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>();

        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");
        final String constraints = jsonObject.optString("constraints");

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.item_gps, null);
        final int canvasId = ViewUtil.generateViewId();
        rootLayout.setId(canvasId);
        final Button recordButton = (Button) rootLayout.findViewById(R.id.record_button);
        recordButton.setId(ViewUtil.generateViewId());
        if (jsonObject.has(JsonFormConstants.HINT)) {
            recordButton.setText(jsonObject.getString(JsonFormConstants.HINT));
        }
        JSONArray canvasIdsArray = new JSONArray();
        canvasIdsArray.put(canvasId);
        recordButton.setTag(R.id.canvas_ids, canvasIdsArray.toString());
        recordButton.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        recordButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        recordButton.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        recordButton.setTag(R.id.openmrs_entity, openMrsEntity);
        recordButton.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        recordButton.setTag(R.id.type, jsonObject.getString("type"));
        if (relevance != null && context instanceof JsonApi) {
            recordButton.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(recordButton);
        }

        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(requiredValue)) {
                recordButton.setTag(R.id.v_required, requiredValue);
                recordButton.setTag(R.id.error, requiredObject.optString(JsonFormConstants.ERR));
            }
        }

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            recordButton.setEnabled(!readOnly);
            recordButton.setFocusable(!readOnly);
        }

        TextView latitudeTV = (TextView) rootLayout.findViewById(R.id.latitude);
        TextView longitudeTV = (TextView) rootLayout.findViewById(R.id.longitude);
        TextView altitudeTV = (TextView) rootLayout.findViewById(R.id.altitude);
        TextView accuracyTV = (TextView) rootLayout.findViewById(R.id.accuracy);
        latitudeTV.setText(String.format(context.getString(R.string.latitude), ""));
        longitudeTV.setText(String.format(context.getString(R.string.longitude), ""));
        altitudeTV.setText(String.format(context.getString(R.string.altitude), ""));
        accuracyTV.setText(String.format(context.getString(R.string.accuracy), ""));
        final GpsDialog gpsDialog = new GpsDialog(context, recordButton, latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsDialog.show();
            }
        });

        recordButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        ((JsonApi) context).addFormDataView(recordButton);
        views.add(rootLayout);

        return views;
    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            Button recordButton) {
        if (!(recordButton.getTag(R.id.v_required) instanceof String) || !(recordButton.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null, formFragmentView, recordButton);
        }
        Boolean isRequired = Boolean.valueOf((String) recordButton.getTag(R.id.v_required));
        if (!isRequired || !recordButton.isEnabled()) {
            return new ValidationStatus(true, null, formFragmentView, recordButton);
        }

        return new ValidationStatus(false, (String) recordButton.getTag(R.id.error), formFragmentView, recordButton);
    }
}

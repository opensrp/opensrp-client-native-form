package com.vijay.jsonwizard.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Captures GPS locations
 * <p>
 * Created by Jason Rogena - jrogena@ona.io on 11/24/17.
 */

public class GpsFactory implements FormWidgetFactory {

    private GpsDialog gpsDialog;

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

    public static String constructString(Location location) {
        if (location != null) {
            return constructString(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }

        return null;
    }

    public static String constructString(String latitude, String longitude) {
        return latitude + " " + longitude;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonObject, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, jsonObject, false);
    }

    private List<View> attachJson(String stepName, final Context context,
                                  JSONObject jsonObject,
                                  boolean popup) throws JSONException {
        List<View> views = new ArrayList<>();

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.item_gps, null);
        final int canvasId = ViewUtil.generateViewId();
        rootLayout.setId(canvasId);
        final Button recordButton = rootLayout.findViewById(R.id.record_button);
        recordButton.setBackgroundColor(context.getResources().getColor(R.color.primary));
        recordButton.setMinHeight(0);
        recordButton.setMinimumHeight(0);
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
        recordButton.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        recordButton.setTag(R.id.extraPopup, popup);
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
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

        TextView latitudeTV = rootLayout.findViewById(R.id.latitude);
        TextView longitudeTV = rootLayout.findViewById(R.id.longitude);
        TextView altitudeTV = rootLayout.findViewById(R.id.altitude);
        TextView accuracyTV = rootLayout.findViewById(R.id.accuracy);
        //setCoordinates(context, latitudeTV, longitudeTV, altitudeTV, accuracyTV, "", "", "", "");
        attachLayout(context, jsonObject, recordButton, latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        gpsDialog = new GpsDialog(context, recordButton, latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionsForLocation(context);
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

    public void attachLayout(Context context, @NonNull JSONObject jsonObject, @NonNull View dataView, @NonNull TextView latitudeTv, @NonNull TextView longitudeTv, @NonNull TextView altitudeTv, @NonNull TextView accuracyTv) {
        String latitude = "";
        String longitude = "";
        String accuracy = "";
        String altitude = "";
        if (jsonObject.has(JsonFormConstants.VALUE)) {
            String coordinateData = jsonObject.optString(JsonFormConstants.VALUE);

            String[] coordinateElements = coordinateData.split(" ");
            if (coordinateElements.length > 1) {
                latitude = coordinateElements[0];
                longitude = coordinateElements[1];
            }
        }

        latitudeTv.setText(String.format(context.getString(R.string.latitude), latitude));
        longitudeTv.setText(String.format(context.getString(R.string.longitude), longitude));
        altitudeTv.setText(String.format(context.getString(R.string.altitude), altitude));
        accuracyTv.setText(String.format(context.getString(R.string.accuracy), accuracy));

        dataView.setTag(R.id.raw_value, constructString(latitude, longitude));
    }

    private void requestPermissionsForLocation(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Register the RequestPermissionResult listener
                if (activity instanceof JsonApi) {
                    final JsonApi jsonApi = (JsonApi) activity;
                    jsonApi.addOnActivityRequestPermissionResultListener(PermissionUtils.FINE_LOCATION_PERMISSION_REQUEST_CODE, new OnActivityRequestPermissionResultListener() {
                        @Override
                        public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
                            if (PermissionUtils.verifyPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                showGpsDialog();
                            } else {
                                jsonApi.removeOnActivityRequestPermissionResultListener(PermissionUtils.FINE_LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        }
                    });
                }

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtils.FINE_LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                showGpsDialog();
            }
        }
    }

    private void showGpsDialog() {
        gpsDialog.show();
    }
}

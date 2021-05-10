package com.vijay.jsonwizard.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class OptiBPWidgetFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    private List<View> attachJson(String stepName, final Context context, JsonFormFragment formFragment, final JSONObject jsonObject, @SuppressWarnings("unused") CommonListener listener, boolean popup) {
        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        final LinearLayout rootLayout = getRootLayout(context);
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        addViewTags(stepName, rootLayout, jsonObject, popup, canvasIds);
        attachRefreshLogic(context, jsonObject, rootLayout);
        try {
            final TextView label = rootLayout.findViewById(R.id.optibp_label);
            label.setId(ViewUtil.generateViewId());
            initLabelValue(label, jsonObject);
            addViewTags(stepName, label, jsonObject, popup, canvasIds);
            attachRefreshLogic(context, jsonObject, label);
            final Button getStarted = rootLayout.findViewById(R.id.optibp_launch_button);
            getStarted.setId(ViewUtil.generateViewId());
            initOptiBPLaunchButton((Activity) context, rootLayout, getStarted, formFragment, jsonObject, stepName, popup);
            addViewTags(stepName, getStarted, jsonObject, popup, canvasIds);
            attachRefreshLogic(context, jsonObject, getStarted);

            formFragment.getJsonApi().getAppExecutors().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
                            boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
                            if (readOnly) {
                                label.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                                getStarted.setBackgroundDrawable(new ColorDrawable(context.getResources()
                                        .getColor(android.R.color.darker_gray)));
                                getStarted.setClickable(false);
                                getStarted.setEnabled(false);
                                getStarted.setFocusable(false);
                            }
                        }
                    }catch ( JSONException e) {
                        Timber.e(e);
                    }
                }
            });
        } catch (JSONException e) {
            Timber.e(e);
        }
        views.add(rootLayout);
        return views;
    }

    private void initOptiBPLaunchButton(final Activity context, final LinearLayout rootLayout, final Button getStarted,
                                        final JsonFormFragment formFragment, final JSONObject jsonObject,
                                        final String stepName, final boolean popup) throws JSONException {
        setButtonParams(getStarted, jsonObject);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(JsonFormConstants.OptibpConstants.OPTIBP_LAUNCH_INTENT);
                    intent.setType("text/json");
                    intent.putExtra(Intent.EXTRA_TEXT, getInputJson(context, jsonObject));
                    context.startActivityForResult(Intent.createChooser(intent, ""), JsonFormConstants.OptibpConstants.OPTIBP_REQUEST_CODE);
                } catch (Exception e) {
                    Timber.e(e);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (context instanceof JsonApi) {
            final JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(JsonFormConstants.OptibpConstants.OPTIBP_REQUEST_CODE,
                    new OnActivityResultListener() {
                        @Override
                        public void onActivityResult(int requestCode,
                                                     int resultCode, final Intent data) {
                            if (requestCode == JsonFormConstants.OptibpConstants.OPTIBP_REQUEST_CODE && resultCode == RESULT_OK) {
                                try {
                                    if (data != null) {
                                        formFragment.getJsonApi().getAppExecutors().mainThread().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String resultJson = data.getStringExtra(Intent.EXTRA_TEXT);
                                                    Timber.d("Resultant OptiBP JSON: %s ", resultJson);
                                                    populateValues(context, formFragment, resultJson, jsonObject, stepName);
                                                    writeResult(jsonApi, rootLayout, stepName, resultJson, popup);
                                                } catch (JSONException e) {
                                                    Timber.e(e);
                                                }
                                            }
                                        });

                                    } else
                                        Timber.i("NO RESULT FOR QR CODE");
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            } else
                                Toast.makeText(context, context.getString(R.string.optibp_unable_to_receive), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void setButtonParams(Button getStarted, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR)) {
            String colorString = jsonObject.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR);
            applyBgColor(getStarted, colorString);
        }
        if (jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR)) {
            String colorString = jsonObject.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR);
            getStarted.setTextColor(Color.parseColor(colorString));
        }
    }

    private void applyBgColor(Button getStarted, String colorString) {
        Drawable background = getStarted.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(Color.parseColor(colorString));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(Color.parseColor(colorString));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(Color.parseColor(colorString));
        }
    }

    private void writeResult(JsonApi jsonApi, LinearLayout rootLayout, String stepName, String resultJson, boolean popup) throws JSONException {
        // Write result json to widget value
        String key = (String) rootLayout.getTag(R.id.key);
        String openMrsEntityParent = (String) rootLayout.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) rootLayout.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) rootLayout.getTag(R.id.openmrs_entity_id);
        jsonApi.writeValue(stepName, key, resultJson, openMrsEntityParent,
                openMrsEntity, openMrsEntityId, popup);
    }

    private void populateValues(Activity context, JsonFormFragment formFragment, String resultJson, JSONObject jsonObject, String stepName) throws JSONException {
        if (!jsonObject.has(JsonFormConstants.FIELDS_TO_USE_VALUE)) {
            Toast.makeText(context, context.getString(R.string.optibp_values_error), Toast.LENGTH_SHORT).show();
            Timber.e(context.getString(R.string.optibp_values_error));
            return;
        }
        JSONArray fields = jsonObject.getJSONArray(JsonFormConstants.FIELDS_TO_USE_VALUE);
        EditText sbp = getFormObject(formFragment, stepName, fields.get(0).toString());
        sbp.setText(getBPValue(resultJson, true));
        disableView(sbp);
        EditText dbp = getFormObject(formFragment, stepName, fields.get(1).toString());
        dbp.setText(getBPValue(resultJson, false));
        disableView(dbp);
    }

    private void disableView(EditText editText) {
        editText.setClickable(false);
        editText.setEnabled(false);
        editText.setFocusable(false);
    }

    private String getBPValue(String resultData, boolean isSystolic) throws JSONException {
        JSONObject resultJson = new JSONObject(resultData);
        JSONArray result = resultJson.getJSONArray(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_RESULT);
        JSONObject resultObject = result.getJSONObject(0);
        JSONArray component = resultObject.getJSONArray(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_COMPONENT);
        JSONObject bpComponent = ((JSONObject) component.get(isSystolic ? 1 : 0));
        JSONObject valueQuantity = bpComponent.getJSONObject(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_VALUE_QUANITITY);
        int value = valueQuantity.getInt(JsonFormConstants.VALUE);
        return String.valueOf(value);
    }

    private EditText getFormObject(JsonFormFragment formFragment, String stepName, String field) {
        return (EditText) formFragment.getJsonApi().getFormDataView(stepName + ":" + field);
    }

    private String getInputJson(Context context, JSONObject jsonObject) throws Exception {
        if (!jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_DATA))
            throw new JSONException(context.getString(R.string.missing_client_info));
        JSONObject optiBPData = jsonObject.getJSONObject(JsonFormConstants.OptibpConstants.OPTIBP_KEY_DATA);
        if (!optiBPData.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_ID)
                || !optiBPData.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID))
            throw new JSONException(context.getString(R.string.missing_client_info));
        if (TextUtils.isEmpty(optiBPData.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_ID))
                || TextUtils.isEmpty(optiBPData.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID)))
            throw new JSONException(context.getString(R.string.missing_client_info));
        return optiBPData.toString();
    }

    private void initLabelValue(TextView label, JSONObject jsonObject) throws JSONException {
        label.setText(obtainLabelText(label.getContext(), jsonObject));
    }

    private String obtainLabelText(Context context, JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has(JsonFormConstants.LABEL))
            return context.getString(R.string.optibp_label);
        return jsonObject.getString(JsonFormConstants.LABEL);
    }

    private void addViewTags(String stepName, View rootLayout, JSONObject jsonObject, boolean popup, JSONArray canvasIds) {
        try {
            String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
            String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
            String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

            rootLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            rootLayout.setTag(R.id.canvas_ids, canvasIds.toString());
            rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
            rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            rootLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            rootLayout.setTag(R.id.extraPopup, popup);
            rootLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @SuppressLint("InflateParams")
    public LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_optibp_widget, null);
    }

    @Override
    public @NotNull Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL);
        return customTranslatableWidgetFields;
    }

    private void attachRefreshLogic(Context context, JSONObject jsonObject, View rootLayout) {
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            rootLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            rootLayout.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(rootLayout);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            rootLayout.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(rootLayout);
        }

        ((JsonApi) context).addFormDataView(rootLayout);
    }
}

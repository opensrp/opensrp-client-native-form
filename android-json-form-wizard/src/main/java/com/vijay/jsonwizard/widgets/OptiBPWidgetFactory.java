package com.vijay.jsonwizard.widgets;

import static com.vijay.jsonwizard.constants.JsonFormConstants.CALCULATION;
import static com.vijay.jsonwizard.constants.JsonFormConstants.CONSTRAINTS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS_TO_USE_VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.LABEL;
import static com.vijay.jsonwizard.constants.JsonFormConstants.READ_ONLY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RELEVANCE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TYPE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants.OptibpConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class OptiBPWidgetFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withStepName(stepName).withContext(context).withFormFragment(formFragment)
                .withJsonObject(jsonObject).withListener(listener).withPopup(popup);

        JSONArray canvasIds = new JSONArray();

        boolean readOnly = false;
        if (jsonObject.has(READ_ONLY)) {
            readOnly = jsonObject.getBoolean(READ_ONLY);
        }

        LinearLayout rootLayout = getRootLayout(context);
        setWidgetTags(rootLayout, canvasIds, widgetArgs);
        attachRefreshLogic(context, jsonObject, rootLayout);

        initBPFieldsKeys(jsonObject);
        EditText systolicBPEditText = getBPEditTextField(widgetArgs, BPFieldType.SYSTOLIC_BP);
        EditText diastolicBPEditText = getBPEditTextField(widgetArgs, BPFieldType.DIASTOLIC_BP);

        TextView labelTextView = initLabel(rootLayout, widgetArgs, readOnly);
        setWidgetTags(labelTextView, canvasIds, widgetArgs);

        boolean isRepeat = isRepeatMeasurement(BPFieldType.SYSTOLIC_BP, BPFieldType.DIASTOLIC_BP);
        Button launchButton = initLaunchButton(rootLayout, widgetArgs, readOnly, getRequestCode(isRepeat));
        setWidgetTags(launchButton, canvasIds, widgetArgs);

        setGlobalLayoutListener(rootLayout, systolicBPEditText, diastolicBPEditText);
        setUpOptiBpActivityResultListener(widgetArgs, getRequestCode(isRepeat), rootLayout, systolicBPEditText, diastolicBPEditText);

        ((JsonApi) context).addFormDataView(rootLayout);

        rootLayout.setTag(R.id.canvas_ids, canvasIds.toString());

        List<View> views = new ArrayList<>(1);
        views.add(rootLayout);
        return views;
    }

    private void setWidgetTags(View view, JSONArray canvasIds, WidgetArgs widgetArgs) throws JSONException {
        JSONObject jsonObject = widgetArgs.getJsonObject();
        FormUtils.setViewOpenMRSEntityAttributes(jsonObject, view);

        view.setId(ViewUtil.generateViewId());
        view.setTag(R.id.key, jsonObject.getString(KEY));
        view.setTag(R.id.type, widgetArgs.getJsonObject().getString(TYPE));
        view.setTag(R.id.extraPopup, widgetArgs.isPopup());
        view.setTag(R.id.address, widgetArgs.getStepName() + ":" + jsonObject.getString(KEY));
        canvasIds.put(view.getId());
    }

    private void initBPFieldsKeys(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(FIELDS_TO_USE_VALUE)
                && jsonObject.getJSONArray(FIELDS_TO_USE_VALUE).length() == 2) {
            JSONArray fields = jsonObject.getJSONArray(FIELDS_TO_USE_VALUE);
            BPFieldType.SYSTOLIC_BP.setKey(fields.get(0).toString());
            BPFieldType.DIASTOLIC_BP.setKey(fields.get(1).toString());
        } else {
            Timber.e("No field values defined to populate BP values");
        }
    }

    private TextView initLabel(LinearLayout rootLayout, WidgetArgs widgetArgs, boolean readOnly) throws JSONException {
        Context context = widgetArgs.getContext();
        TextView labelTextView = rootLayout.findViewById(R.id.optibp_label);
        labelTextView.setText(getLabelText(context, widgetArgs.getJsonObject()));
        if (readOnly) {
            labelTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
        return labelTextView;
    }

    private Button initLaunchButton(LinearLayout rootLayout, final WidgetArgs widgetArgs, boolean readOnly, final int requestCode) throws JSONException {
        final Context context = widgetArgs.getContext();
        final JSONObject jsonObject = widgetArgs.getJsonObject();
        Button launchButton = rootLayout.findViewById(R.id.optibp_launch_button);
        formatButtonWidget(launchButton, widgetArgs.getJsonObject());
        launchButton.setOnClickListener(view -> {
            try {
                Timber.w(" ONCLICK WITH JSON %s", jsonObject);
                Intent intent = new Intent(OptibpConstants.OPTIBP_LAUNCH_INTENT);
                intent.setType("text/json");
                intent.putExtra(Intent.EXTRA_TEXT, getInputJsonString(context, jsonObject, widgetArgs));
                ((Activity) context).startActivityForResult(Intent.createChooser(intent, ""), requestCode);
            } catch (Exception e) {
                Timber.e(e);
            }
        });

        if (readOnly) {
            launchButton.setBackgroundDrawable(new ColorDrawable(widgetArgs.getContext().getResources()
                    .getColor(android.R.color.darker_gray)));
            launchButton.setClickable(false);
            launchButton.setEnabled(false);
            launchButton.setFocusable(false);
        }

        return launchButton;
    }

    private void formatButtonWidget(Button button, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR)) {
            String colorString = jsonObject.getString(OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR);
            setButtonBgColor(button, colorString);
        }
        if (jsonObject.has(OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR)) {
            String colorString = jsonObject.getString(OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR);
            button.setTextColor(Color.parseColor(colorString));
        }
        if (jsonObject.has(OptibpConstants.OPTIBP_KEY_BUTTON_TEXT)) {
            String buttonText = jsonObject.getString(OptibpConstants.OPTIBP_KEY_BUTTON_TEXT);
            button.setText(buttonText);
        }

    }

    public void setUpOptiBpActivityResultListener(final WidgetArgs widgetArgs, int requestCode, final LinearLayout rootLayout, EditText systolicEditText, final EditText diastolicEditText) {
        final Context context = widgetArgs.getContext();
        if (context instanceof JsonApi) {
            final JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(requestCode, (finalRequestCode, resultCode, data) -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (finalRequestCode == OptibpConstants.OPTIBP_REQUEST_CODE ||
                            finalRequestCode == OptibpConstants.OPTIBP_REPEAT_REQUEST_CODE) {
                        try {
                            if (data != null) {
                                String resultJson = data.getStringExtra(Intent.EXTRA_TEXT);
                                Timber.d("Resultant OptiBP JSON: %s ", resultJson);
                                populateBPEditTextValues(resultJson, systolicEditText, diastolicEditText, widgetArgs);
                                String resultString = getValueString(resultJson);
                                if (StringUtils.isNotBlank(resultString)) {
                                    writeResult(jsonApi, rootLayout, resultString, widgetArgs);
                                } else {
                                    Timber.e("JSON Result  %s , Result String  %s", resultJson, resultString);
                                    Toast.makeText(context, context.getString(R.string.invalid_optibp_data), Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Timber.e("NO RESULT FROM OPTIBP APP finalRequestCode %s, resultCode %s", finalRequestCode, resultCode);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                } else {
                    Timber.e("final Request code: %s,  ResultCode : %s , Data from OptiBP: %s ", finalRequestCode, resultCode, data);
                    Toast.makeText(context, context.getString(R.string.optibp_unable_to_receive), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setGlobalLayoutListener(final LinearLayout rootLayout, final EditText systolicBPEditText, final EditText diastolicBPEditText) {
        if (systolicBPEditText != null && diastolicBPEditText != null) {
            rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (rootLayout.getVisibility() != View.VISIBLE
                        && TextUtils.isEmpty(systolicBPEditText.getText())
                        && TextUtils.isEmpty(diastolicBPEditText.getText())) {
                    Timber.i("OptiBP widget not visible");
                    toggleEditTextEnabled(systolicBPEditText, true);
                    toggleEditTextEnabled(diastolicBPEditText, true);
                }
            });
        }
    }

    private void setButtonBgColor(Button button, String colorString) {
        Drawable background = button.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(Color.parseColor(colorString));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(Color.parseColor(colorString));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(Color.parseColor(colorString));
        }
    }

    private void writeResult(JsonApi jsonApi, LinearLayout rootLayout, String resultJson, WidgetArgs widgetArgs) throws JSONException {
        // Write result JSON to widget value
        String key = (String) rootLayout.getTag(R.id.key);
        String openMrsEntityParent = (String) rootLayout.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) rootLayout.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) rootLayout.getTag(R.id.openmrs_entity_id);
        jsonApi.writeValue(widgetArgs.getStepName(), key, resultJson, openMrsEntityParent,
                openMrsEntity, openMrsEntityId, widgetArgs.isPopup());
    }

    protected void populateBPEditTextValues(String resultJsonString, EditText systolicBPEditText, EditText diastolicBPEditText, WidgetArgs widgetArgs) throws JSONException {
        if (systolicBPEditText != null) {
            systolicBPEditText.setText(getBPValue(resultJsonString, BPFieldType.SYSTOLIC_BP));
            toggleEditTextEnabled(systolicBPEditText, false);
        }
        if (diastolicBPEditText != null) {
            diastolicBPEditText.setText(getBPValue(resultJsonString, BPFieldType.DIASTOLIC_BP));
            toggleEditTextEnabled(diastolicBPEditText, false);
        }
    }

    private void toggleEditTextEnabled(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setClickable(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
    }

    protected String getBPValue(String resultJsonString, BPFieldType field) throws JSONException {
        if (resultJsonString != null) {
            JSONObject jsonObject = new JSONObject(resultJsonString);
            JSONArray result = jsonObject.getJSONArray(OptibpConstants.OPTIBP_REPORT_RESULT);
            JSONObject resultObject = result.getJSONObject(0);
            JSONArray component = resultObject.getJSONArray(OptibpConstants.OPTIBP_REPORT_COMPONENT);
            JSONObject bpComponent = ((JSONObject) component.get(BPFieldType.SYSTOLIC_BP.equals(field) ? 1 : 0));
            JSONObject valueQuantity = bpComponent.getJSONObject(OptibpConstants.OPTIBP_REPORT_VALUE_QUANTITY);
            int value = valueQuantity.getInt(VALUE);
            return String.valueOf(value);
        }
        return null;
    }

    protected String getValueString(String resultJsonString) throws JSONException {
        if (resultJsonString != null) {
            JSONObject jsonObject = new JSONObject(resultJsonString);
            JSONArray result = jsonObject.getJSONArray(OptibpConstants.OPTIBP_REPORT_RESULT);
            JSONObject resultObject = result.getJSONObject(0);
            JSONArray component = resultObject.getJSONArray(OptibpConstants.OPTIBP_REPORT_COMPONENT);
            JSONObject secondIndex = component.optJSONObject(2);
            String valueString = secondIndex.optString(OptibpConstants.OPTIBP_VALUE_STRING);
            Timber.d("Comparative Array from OPtibp: %s", valueString);
            return valueString;
        }
        return null;

    }

    protected EditText getBPEditTextField(WidgetArgs widgetArgs, BPFieldType field) {
        Context context = widgetArgs.getContext();
        EditText bpEditText = (EditText) widgetArgs.getFormFragment().getJsonApi().getFormDataView(widgetArgs.getStepName() + ":" + field.getKey());
        if (bpEditText == null) {
            Toast.makeText(context, context.getString(R.string.optibp_values_error), Toast.LENGTH_SHORT).show();
            Timber.e(context.getString(R.string.optibp_values_error));
            return null;
        }
        return bpEditText;
    }

    private int getRequestCode(boolean isRepeat) {
        return isRepeat ? OptibpConstants.OPTIBP_REPEAT_REQUEST_CODE : OptibpConstants.OPTIBP_REQUEST_CODE;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isRepeatMeasurement(BPFieldType systolicField, BPFieldType diastolicField) {
        return systolicField.getKey().contains("repeat") && diastolicField.getKey().contains("repeat");
    }

    protected String getInputJsonString(Context context, JSONObject jsonObject, WidgetArgs widgetArgs) throws JSONException {
        if (!jsonObject.has(OptibpConstants.OPTIBP_KEY_DATA)) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        JSONObject optiBPData = jsonObject.getJSONObject(OptibpConstants.OPTIBP_KEY_DATA);
        if (!optiBPData.has(OptibpConstants.OPTIBP_KEY_CLIENT_ID)
                || !optiBPData.has(OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID)) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        if (TextUtils.isEmpty(optiBPData.getString(OptibpConstants.OPTIBP_KEY_CLIENT_ID))
                || TextUtils.isEmpty(optiBPData.getString(OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID))) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        /***
         * Adding new calibration data here
         */
        appendHealthData(optiBPData, widgetArgs);
        optiBPData.put(OptibpConstants.CALIBRATION, getCalibrationData(Utils.decompress(optiBPData.optString(OptibpConstants.CALIBRATION),"UTF-8")));
        return optiBPData.toString();
    }

    private JSONArray getCalibrationData(String calibration) {
        try {
            if (Utils.checkIfValidJsonArray(calibration)) {
                return new JSONArray(calibration);
            }
            return StringUtils.isBlank(calibration) ? null : new JSONArray().put(new JSONObject(calibration));
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    private void appendHealthData(JSONObject returnObject, WidgetArgs widgetArgs) {
        try {
            JSONObject currentHeight = getSingleStepJsonObject(widgetArgs, STEP1, OptibpConstants.HEIGHT);
            JSONObject currentWeight = getSingleStepJsonObject(widgetArgs, STEP1, OptibpConstants.CURRENTWEIGHT);
            if (currentHeight != null && currentWeight != null) {
                returnObject.put(OptibpConstants.HEIGHT, Integer.parseInt(currentHeight.optString(VALUE)));
                returnObject.put(OptibpConstants.WEIGHT, Integer.parseInt(currentWeight.optString(VALUE)));
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private static JSONObject getSingleStepJsonObject(WidgetArgs widgetArgs, String stepName, String key) {
        JSONArray jsonArray = widgetArgs.getFormFragment().getJsonApi().getStep(stepName).optJSONArray(FIELDS);
        return Utils.getJsonObjectFromJsonArray(key, jsonArray);

    }

    private String getLabelText(Context context, JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has(LABEL)) {
            return context.getString(R.string.optibp_label);
        }
        return jsonObject.getString(LABEL);
    }

    @SuppressLint("InflateParams")
    public LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_optibp_widget, null);
    }

    @Override
    public @NotNull Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(LABEL);
        return customTranslatableWidgetFields;
    }

    private void attachRefreshLogic(Context context, JSONObject jsonObject, View view) {
        String relevance = jsonObject.optString(RELEVANCE);
        String constraints = jsonObject.optString(CONSTRAINTS);
        String calculation = jsonObject.optString(CALCULATION);

        if (StringUtils.isNotBlank(relevance) && context instanceof JsonApi) {
            view.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(view);
        }

        if (StringUtils.isNotBlank(constraints) && context instanceof JsonApi) {
            view.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(view);
        }

        if (StringUtils.isNotBlank(calculation) && context instanceof JsonApi) {
            view.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(view);
        }
    }

    protected enum BPFieldType {
        DIASTOLIC_BP("bp_diastolic"), SYSTOLIC_BP("bp_systolic");  // TODO -> Add these KEYS to explicit documentation

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        BPFieldType(String key) {
            this.key = key;
        }
    }
}


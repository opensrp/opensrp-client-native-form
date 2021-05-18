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
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class OptiBPWidgetFactory implements FormWidgetFactory, OnActivityResultListener {

    private WidgetArgs widgetArgs;
    private LinearLayout rootLayout;
    private TextView labelTextView;
    private Button launchButton;
    private EditText systolicBPEditText;
    private EditText diastolicBPEditText;
    private int requestCode;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        requestCode = new Random().nextInt(999);
        widgetArgs = new WidgetArgs();
        widgetArgs.withStepName(stepName).withContext(context).withFormFragment(formFragment)
                .withJsonObject(jsonObject).withListener(listener).withPopup(popup);

        rootLayout = getRootLayout(context);
        JSONArray canvasIds = new JSONArray();
        canvasIds.put(rootLayout.getId());
        setWidgetTags(rootLayout, canvasIds);
        observeLayoutChanges(rootLayout);
        attachRefreshLogic(context, jsonObject, rootLayout);

        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        initLabel(rootLayout, context, jsonObject, readOnly);
        initLaunchButton(rootLayout, readOnly);
        initBPEditTexts((Activity) context, formFragment, stepName);
        setWidgetTags(labelTextView, canvasIds);
        attachRefreshLogic(context, jsonObject, labelTextView);
        setWidgetTags(launchButton, canvasIds);
        attachRefreshLogic(context, jsonObject, launchButton);
        setUpOptiBpActivityResultListener();

        ((JsonApi) context).addFormDataView(rootLayout);

        List<View> views = new ArrayList<>(1);
        views.add(rootLayout);
        return views;
    }

    private void setWidgetTags(View view, JSONArray canvasIds) throws JSONException {
        JSONObject jsonObject = widgetArgs.getJsonObject();
        FormUtils.setViewOpenMRSEntityAttributes(jsonObject, view);

        view.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        view.setTag(R.id.canvas_ids, canvasIds.toString());
        view.setTag(R.id.type, widgetArgs.getJsonObject().getString(JsonFormConstants.TYPE));
        view.setTag(R.id.extraPopup, widgetArgs.isPopup());
        view.setTag(R.id.address, widgetArgs.getStepName() + ":" + jsonObject.getString(JsonFormConstants.KEY));
    }

    private void initBPEditTexts(Activity context, JsonFormFragment formFragment, String stepName) throws JSONException {
        initKeys();
        systolicBPEditText = getBPEditTextField(context, formFragment, stepName, BPFieldType.SYSTOLIC_BP);
        diastolicBPEditText = getBPEditTextField(context, formFragment, stepName, BPFieldType.DIASTOLIC_BP);
    }

    private void initKeys() throws JSONException {
        JSONObject jsonObject = widgetArgs.getJsonObject();
        JSONArray fields = jsonObject.getJSONArray(JsonFormConstants.FIELDS_TO_USE_VALUE);
        BPFieldType.SYSTOLIC_BP.setKey(fields.get(0).toString());
        BPFieldType.DIASTOLIC_BP.setKey(fields.get(1).toString());
    }

    private void initLabel(LinearLayout rootLayout, Context context, JSONObject jsonObject, boolean readOnly) throws JSONException {
        labelTextView = rootLayout.findViewById(R.id.optibp_label);
        labelTextView.setId(ViewUtil.generateViewId());
        labelTextView.setText(getLabelText(context, jsonObject));
        if (readOnly) {
            labelTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
    }

    private void initLaunchButton(LinearLayout rootLayout, boolean readOnly) throws JSONException {
        launchButton = rootLayout.findViewById(R.id.optibp_launch_button);
        launchButton.setId(ViewUtil.generateViewId());
        formatButtonWidget(launchButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(JsonFormConstants.OptibpConstants.OPTIBP_LAUNCH_INTENT);
                    intent.setType("text/json");
                    intent.putExtra(Intent.EXTRA_TEXT, getInputJsonString(widgetArgs.getContext(), widgetArgs.getJsonObject()));
                    ((Activity) widgetArgs.getContext()).startActivityForResult(Intent.createChooser(intent, ""), requestCode);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });

        if (readOnly) {
            launchButton.setBackgroundDrawable(new ColorDrawable(widgetArgs.getContext().getResources()
                    .getColor(android.R.color.darker_gray)));
            launchButton.setClickable(false);
            launchButton.setEnabled(false);
            launchButton.setFocusable(false);
        }
    }

    private void formatButtonWidget(Button button) throws JSONException {
        JSONObject jsonObject = widgetArgs.getJsonObject();
        if (jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR)) {
            String colorString = jsonObject.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_BG_COLOR);
            setButtonBgColor(button, colorString);
        }
        if (jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR)) {
            String colorString = jsonObject.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_BUTTON_TEXT_COLOR);
            button.setTextColor(Color.parseColor(colorString));
        }
    }

    public void setUpOptiBpActivityResultListener() {
        Context context = widgetArgs.getContext();
        if (context instanceof JsonApi) {
            final JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(requestCode, this);
        }
    }

    private void observeLayoutChanges(final LinearLayout rootLayout) {
        if (systolicBPEditText != null && diastolicBPEditText != null) {
            rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (rootLayout.getVisibility() != View.VISIBLE
                            && TextUtils.isEmpty(systolicBPEditText.getText())
                            && TextUtils.isEmpty(diastolicBPEditText.getText())) {
                        Timber.i("OptiBP widget not visible");
                        toggleEditTextEnabled(systolicBPEditText, true);
                        toggleEditTextEnabled(diastolicBPEditText, true);
                    }
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

    private void writeResult(JsonApi jsonApi, LinearLayout rootLayout, String stepName, String resultJson, boolean popup) throws JSONException {
        // Write result JSON to widget value
        String key = (String) rootLayout.getTag(R.id.key);
        String openMrsEntityParent = (String) rootLayout.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) rootLayout.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) rootLayout.getTag(R.id.openmrs_entity_id);
        jsonApi.writeValue(stepName, key, resultJson, openMrsEntityParent,
                openMrsEntity, openMrsEntityId, popup);
    }

    private void populateBPEditTextValues(String resultJsonJsonString) throws JSONException {
        if (systolicBPEditText != null) {
            systolicBPEditText.setText(getBPValue(resultJsonJsonString, BPFieldType.SYSTOLIC_BP));
            toggleEditTextEnabled(systolicBPEditText, false);
        }
        if (diastolicBPEditText != null) {
            diastolicBPEditText.setText(getBPValue(resultJsonJsonString, BPFieldType.DIASTOLIC_BP));
            toggleEditTextEnabled(diastolicBPEditText, false);
        }
    }

    private void toggleEditTextEnabled(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setClickable(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
    }

    private String getBPValue(String resultJsonString, BPFieldType field) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJsonString);
        JSONArray result = jsonObject.getJSONArray(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_RESULT);
        JSONObject resultObject = result.getJSONObject(0);
        JSONArray component = resultObject.getJSONArray(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_COMPONENT);
        JSONObject bpComponent = ((JSONObject) component.get(BPFieldType.SYSTOLIC_BP.equals(field) ? 1 : 0));
        JSONObject valueQuantity = bpComponent.getJSONObject(JsonFormConstants.OptibpConstants.OPTIBP_REPORT_VALUE_QUANITITY);
        int value = valueQuantity.getInt(JsonFormConstants.VALUE);
        return String.valueOf(value);
    }

    private EditText getBPEditTextField(Activity context, JsonFormFragment formFragment, String stepName, BPFieldType field) {
        EditText bpEditText = (EditText) formFragment.getJsonApi().getFormDataView(stepName + ":" + field.getKey());
        if (bpEditText == null) {
            Toast.makeText(context, context.getString(R.string.optibp_values_error), Toast.LENGTH_SHORT).show();
            Timber.e(context.getString(R.string.optibp_values_error));
            return null;
        }
        return bpEditText;
    }

    private String getInputJsonString(Context context, JSONObject jsonObject) throws Exception {
        if (!jsonObject.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_DATA)) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        JSONObject optiBPData = jsonObject.getJSONObject(JsonFormConstants.OptibpConstants.OPTIBP_KEY_DATA);
        if (!optiBPData.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_ID)
                || !optiBPData.has(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID)) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        if (TextUtils.isEmpty(optiBPData.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_ID))
                || TextUtils.isEmpty(optiBPData.getString(JsonFormConstants.OptibpConstants.OPTIBP_KEY_CLIENT_OPENSRP_ID))) {
            throw new JSONException(context.getString(R.string.missing_client_info));
        }
        return optiBPData.toString();
    }

    private String getLabelText(Context context, JSONObject jsonObject) throws JSONException {
        if (!jsonObject.has(JsonFormConstants.LABEL)) {
            return context.getString(R.string.optibp_label);
        }
        return jsonObject.getString(JsonFormConstants.LABEL);
    }

    @SuppressLint("InflateParams")
    public LinearLayout getRootLayout(Context context) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_optibp_widget, null);
        layout.setId(ViewUtil.generateViewId());
        return layout;
    }

    @Override
    public @NotNull Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL);
        return customTranslatableWidgetFields;
    }

    private void attachRefreshLogic(Context context, JSONObject jsonObject, View view) {
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        final JsonApi jsonApi = (JsonApi) widgetArgs.getContext();
        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    try {
                        String resultJson = data.getStringExtra(Intent.EXTRA_TEXT);
                        Timber.d("Resultant OptiBP JSON: %s ", resultJson);
                        populateBPEditTextValues(resultJson);
                        writeResult(jsonApi, rootLayout, widgetArgs.getStepName(), resultJson, widgetArgs.isPopup());
                    } catch (JSONException e) {
                        Timber.e(e);
                    }

                } else
                    Timber.i("NO RESULT FROM OPTIBP APP");
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            Toast.makeText(widgetArgs.getContext(), widgetArgs.getContext().getString(R.string.optibp_unable_to_receive), Toast.LENGTH_SHORT).show();
        }
    }

    private enum BPFieldType {
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


package com.vijay.jsonwizard.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CompoundButton;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vijay on 24-05-2015.
 */
public class FormUtils {
    public static final String FONT_BOLD_PATH = "fonts/Roboto-Bold.ttf";
    //public static final String FONT_REGULAR_PATH = "fonts/Roboto-Regular.ttf";
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final String METADATA_PROPERTY = "metadata";
    public static final String LOOK_UP_JAVAROSA_PROPERTY = "look_up";
    public static final String NATIIVE_FORM_DATE_FORMAT_PATTERN = "dd-MM-yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(NATIIVE_FORM_DATE_FORMAT_PATTERN);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String START_JAVAROSA_PROPERTY = "start";
    private static final String END_JAVAROSA_PROPERTY = "end";
    private static final String TODAY_JAVAROSA_PROPERTY = "today";
    private static final String TAG = FormUtils.class.getSimpleName();

    public static LinearLayout.LayoutParams getLinearLayoutParams(int width, int height, int left, int top, int right,
                                                                  int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams getRelativeLayoutParams(int width, int height, int left, int top, int right,
                                                                      int bottom) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static CustomTextView getTextViewWith(Context context, int textSizeInSp, String text,
                                                 String key, String type, String openMrsEntityParent,
                                                 String openMrsEntity, String openMrsEntityId,
                                                 String relevance,
                                                 LinearLayout.LayoutParams layoutParams, String fontPath) {
        return getTextViewWith(context, textSizeInSp, text, key, type, openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance,
                layoutParams, fontPath, 0, null);
    }

    public static CustomTextView getTextViewWith(Context context, int textSizeInSp, String text,
                                                 String key, String type, String openMrsEntityParent,
                                                 String openMrsEntity, String openMrsEntityId,
                                                 String relevance,
                                                 LinearLayout.LayoutParams layoutParams, String fontPath, int bgColor,
                                                 String textColor) {
        CustomTextView textView = new CustomTextView(context);
        textView.setText(text);
        textView.setTag(R.id.key, key);
        textView.setTag(R.id.type, type);
        textView.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        textView.setTag(R.id.openmrs_entity, openMrsEntity);
        textView.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        textView.setId(ViewUtil.generateViewId());
        textView.setTextSize(textSizeInSp);
        textView.setLayoutParams(layoutParams);

        if (bgColor != 0) {
            textView.setBackgroundColor(bgColor);
        }

        if (textColor != null) {
            textView.setTextColor(Color.parseColor(textColor));
        }

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            textView.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(textView);
        }
        return textView;
    }

    public static void updateStartProperties(PropertyManager propertyManager, JSONObject form)
            throws Exception {
        if (form.has(METADATA_PROPERTY)) {
            if (form.getJSONObject(METADATA_PROPERTY).has(START_JAVAROSA_PROPERTY)) {
                Calendar calendar = Calendar.getInstance();
                JSONObject start = form.getJSONObject(METADATA_PROPERTY).getJSONObject(START_JAVAROSA_PROPERTY);
                String value = DATE_TIME_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                start.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) value = "";
                deviceId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) value = "";
                subscriberId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) value = "";
                simSerial.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) value = "";
                simSerial.put(JsonFormConstants.VALUE, value);
            }
        }
    }

    public static void updateEndProperties(PropertyManager propertyManager, JSONObject form)
            throws Exception {
        if (form.has(METADATA_PROPERTY)) {
            if (form.getJSONObject(METADATA_PROPERTY).has(END_JAVAROSA_PROPERTY)) {
                Calendar calendar = Calendar.getInstance();
                JSONObject end = form.getJSONObject(METADATA_PROPERTY).getJSONObject(END_JAVAROSA_PROPERTY);
                String value = DATE_TIME_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                end.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(TODAY_JAVAROSA_PROPERTY)) {
                Calendar calendar = Calendar.getInstance();
                JSONObject today = form.getJSONObject(METADATA_PROPERTY).getJSONObject(TODAY_JAVAROSA_PROPERTY);
                String value = DATE_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                today.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) value = "";
                deviceId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) value = "";
                subscriberId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) value = "";
                simSerial.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) value = "";
                simSerial.put(JsonFormConstants.VALUE, value);
            }
        }
    }

    public static Map<String, View> createRadioButtonAndCheckBoxLabel(String stepName, LinearLayout linearLayout,
                                                                      JSONObject jsonObject, Context context,
                                                                      JSONArray canvasIds, Boolean readOnly,
                                                                      CommonListener listener) throws JSONException {
        Map<String, View> createdViewsMap = new HashMap<>();
        String label = jsonObject.optString(JsonFormConstants.LABEL, "");
        if (!TextUtils.isEmpty(label)) {
            String asterisks = "";
            int labelTextSize = FormUtils
                    .getValueFromSpOrDpOrPx(jsonObject.optString(JsonFormConstants.LABEL_TEXT_SIZE, String.valueOf(context
                            .getResources().getDimension(R.dimen.default_label_text_size))), context);
            String labelTextColor = jsonObject
                    .optString(JsonFormConstants.LABEL_TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            ConstraintLayout labelConstraintLayout = createLabelLinearLayout(stepName, canvasIds, jsonObject, context,
                    listener);

            CustomTextView labelText = labelConstraintLayout.findViewById(R.id.label_text);
            ImageView editButton = labelConstraintLayout.findViewById(R.id.label_edit_button);
            if (requiredObject != null) {
                String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
                if (!TextUtils.isEmpty(requiredValue) && (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue) || Boolean
                        .valueOf(requiredValue))) {
                    asterisks = "<font color=#CF0800> *</font>";
                }
            }

            String combinedLabelText = "<font color=" + labelTextColor + ">" + label + "</font>" + asterisks;

            //Applying textStyle to the text;
            String textStyle = jsonObject.optString(JsonFormConstants.TEXT_STYLE, JsonFormConstants.NORMAL);
            setTextStyle(textStyle, labelText);
            labelText.setText(Html.fromHtml(combinedLabelText));
            labelText.setTag(R.id.original_text, Html.fromHtml(combinedLabelText));
            labelText.setTextSize(labelTextSize);
            canvasIds.put(labelConstraintLayout.getId());
            labelConstraintLayout.setEnabled(!readOnly);
            linearLayout.addView(labelConstraintLayout);
            createdViewsMap.put(JsonFormConstants.EDIT_BUTTON, editButton);
            createdViewsMap.put(JsonFormConstants.CUSTOM_TEXT, labelText);
        }
        return createdViewsMap;
    }

    public static int getValueFromSpOrDpOrPx(String spOrDpOrPx, Context context) {
        int px = 0;
        if (!TextUtils.isEmpty(spOrDpOrPx)) {
            if (spOrDpOrPx.contains("sp")) {
                int unitValues = Integer.parseInt(spOrDpOrPx.replace("sp", ""));
                px = spToPx(context, unitValues);
            } else if (spOrDpOrPx.contains("dp")) {
                int unitValues = Integer.parseInt(spOrDpOrPx.replace("dp", ""));
                px = FormUtils.dpToPixels(context, unitValues);
            } else if (spOrDpOrPx.contains("px")) {
                px = Integer.parseInt(spOrDpOrPx.replace("px", ""));
            } else {
                px = (int) context.getResources().getDimension(R.dimen.default_label_text_size);
            }
        }

        return px;
    }

    public static ConstraintLayout createLabelLinearLayout(String stepName, JSONArray canvasIds, JSONObject jsonObject,
                                                           Context context,
                                                           CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT, null);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY, null);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID, null);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String labelInfoText = jsonObject.optString(JsonFormConstants.LABEL_INFO_TEXT, "");
        String labelInfoTitle = jsonObject.optString(JsonFormConstants.LABEL_INFO_TITLE, "");

        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(context)
                .inflate(R.layout.native_form_labels, null);
        constraintLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(constraintLayout.getId());
        constraintLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        constraintLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        constraintLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        constraintLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        constraintLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        constraintLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        constraintLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        constraintLayout.setId(ViewUtil.generateViewId());
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            constraintLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(constraintLayout);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            constraintLayout.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(constraintLayout);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            constraintLayout.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addCalculationLogicView(constraintLayout);
        }

        ImageView imageView = constraintLayout.findViewById(R.id.label_info);

        showInfoIcon(stepName, jsonObject, listener, labelInfoText, labelInfoTitle, imageView, canvasIds);

        return constraintLayout;
    }

    /**
     * @param textStyle
     * @param view
     */
    public static void setTextStyle(String textStyle, AppCompatTextView view) {
        switch (textStyle) {
            case JsonFormConstants.BOLD:
                view.setTypeface(null, Typeface.BOLD);
                break;
            case JsonFormConstants.ITALIC:
                view.setTypeface(null, Typeface.ITALIC);
                break;
            case JsonFormConstants.NORMAL:
                view.setTypeface(null, Typeface.NORMAL);
                break;
            case JsonFormConstants.BOLD_ITALIC:
                view.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            default:
                view.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    public static int spToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPixels(Context context, float dps) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static void showInfoIcon(String stepName, JSONObject jsonObject, CommonListener listener, String labelInfoText,
                                    String labelInfoTitle, ImageView imageView, JSONArray canvasIds) throws JSONException {
        if (!TextUtils.isEmpty(labelInfoText)) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            imageView.setTag(R.id.type, jsonObject.getString("type"));
            imageView.setTag(R.id.label_dialog_info, labelInfoText);
            imageView.setTag(R.id.label_dialog_title, labelInfoTitle);
            imageView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            imageView.setTag(R.id.canvas_ids, canvasIds.toString());
            imageView.setOnClickListener(listener);
        }
    }

    public static void setEditButtonAttributes(JSONObject jsonObject, View editableView, ImageView editButton,
                                               CommonListener listener) throws JSONException {
        editButton.setTag(R.id.editable_view, editableView);
        editButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editButton.setTag(R.id.type, jsonObject.getString("type"));
        editButton.setOnClickListener(listener);
    }

    /**
     * Checks and uncheck the radio buttons in a linear layout view
     * follows this fix https://stackoverflow.com/a/26961458/5784584
     *
     * @param parent {@link ViewGroup}
     */
    public static void setRadioExclusiveClick(ViewGroup parent) {
        final List<RadioButton> radioButtonList = getRadioButtons(parent);
        for (RadioButton radioButton : radioButtonList) {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton radioButtonView = (RadioButton) view;
                    for (RadioButton button : radioButtonList) {
                        if (button.getId() != radioButtonView.getId()) {
                            button.setChecked(false);
                            try {
                                resetRadioButtonsSpecifyText(button);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

    }

    /**
     * Get the actual radio buttons on the parent view given
     *
     * @param parent {@link ViewGroup}
     * @return radioButtonList
     */
    private static List<RadioButton> getRadioButtons(ViewGroup parent) {
        List<RadioButton> radioButtonList = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof RadioButton) {
                radioButtonList.add((RadioButton) view);
            } else if (view instanceof ViewGroup) {
                List<RadioButton> nestedRadios = getRadioButtons((ViewGroup) view);
                radioButtonList.addAll(nestedRadios);
            }
        }
        return radioButtonList;
    }

    /**
     * Resets the radio buttons specify text in another option is selected
     *
     * @param button {@link CompoundButton}
     * @author kitoto
     */
    private static void resetRadioButtonsSpecifyText(RadioButton button) throws JSONException {
        CustomTextView specifyText = (CustomTextView) button.getTag(R.id.specify_textview);
        CustomTextView reasonsText = (CustomTextView) button.getTag(R.id.specify_reasons_textview);
        CustomTextView extraInfoTextView = (CustomTextView) button
                .getTag(R.id.specify_extra_info_textview);
        JSONObject optionsJson = (JSONObject) button.getTag(R.id.option_json_object);
        String radioButtonText = optionsJson.optString(JsonFormConstants.TEXT);
        button.setText(radioButtonText);

        if (specifyText != null && optionsJson.has(JsonFormConstants.CONTENT_INFO)) {
            String specifyInfo = optionsJson.optString(JsonFormConstants.CONTENT_INFO);
            String newText = "(" + specifyInfo + ")";
            specifyText.setText(newText);
            optionsJson.put(JsonFormConstants.SECONDARY_VALUE, "");
        }

        if (reasonsText != null) {
            LinearLayout reasonTextViewParent = (LinearLayout) reasonsText.getParent();
            LinearLayout radioButtonParent = (LinearLayout) button.getParent().getParent();
            if (reasonTextViewParent.equals(radioButtonParent)) {
                reasonsText.setVisibility(View.GONE);
            }
        }
        if (extraInfoTextView != null) {
            extraInfoTextView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * This method returns a {@link Calendar} object at mid-day corresponding to a date matching
     * the format specified in {@code DATE_FORMAT} or a day in reference to today e.g today,
     * today-1, today+10
     *
     * @param dayString_ The string to be converted to a date
     * @return The calendar object corresponding to the day, or object corresponding to today's
     * date if an error occurred
     */
    public static Calendar getDate(String dayString_) {
        Calendar calendarDate = Calendar.getInstance();

        if (dayString_ != null && dayString_.trim().length() > 0) {
            String dayString = dayString_.trim().toLowerCase();
            if (!"today".equals(dayString)) {
                Pattern pattern = Pattern.compile("today\\s*([-\\+])\\s*(\\d+)([dmyDMY]{1})");
                Matcher matcher = pattern.matcher(dayString);
                if (matcher.find()) {
                    int timeValue = Integer.parseInt(matcher.group(2));
                    if ("-".equals(matcher.group(1))) {
                        timeValue = timeValue * -1;
                    }

                    int field = Calendar.DATE;
                    if (matcher.group(3).equalsIgnoreCase("y")) {
                        field = Calendar.YEAR;
                    } else if (matcher.group(3).equalsIgnoreCase("m")) {
                        field = Calendar.MONTH;
                    }

                    calendarDate.add(field, timeValue);
                } else {
                    try {
                        calendarDate.setTime(DATE_FORMAT.parse(dayString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //set time to mid-day
        calendarDate.set(Calendar.HOUR_OF_DAY, 12);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        return calendarDate;
    }

    public static void setRequiredOnHint(AppCompatEditText editText) {
        if (!TextUtils.isEmpty(editText.getHint())) {
            SpannableString hint = new SpannableString(editText.getHint() + " *");
            hint.setSpan(new ForegroundColorSpan(Color.RED), hint.length() - 1, hint.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setHint(hint);
        }
    }

    public static void setEditMode(JSONObject jsonObject, View editableView, ImageView editButton) throws JSONException {
        if (jsonObject.has(JsonFormConstants.EDITABLE)) {
            boolean editable = jsonObject.getBoolean(JsonFormConstants.EDITABLE);
            if (editable) {
                editButton.setVisibility(View.VISIBLE);
                editableView.setEnabled(false);
            } else {
                editButton.setVisibility(View.GONE);
            }
        } else if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readyOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            editableView.setEnabled(!readyOnly);
            editButton.setVisibility(View.GONE);
        } else if (jsonObject.has(JsonFormConstants.EDITABLE) && jsonObject.has(JsonFormConstants.READ_ONLY)) {
            editButton.setVisibility(View.VISIBLE);
            editableView.setEnabled(false);
        }
    }

    public static JSONObject getSubFormJson(String formIdentity, String subFormsLocation, Context context) throws Exception {
        String defaultSubFormLocation = JsonFormConstants.DEFAULT_SUB_FORM_LOCATION;
        if (!TextUtils.isEmpty(subFormsLocation)) {
            defaultSubFormLocation = subFormsLocation;
        }
        return new JSONObject(loadSubForm(formIdentity, defaultSubFormLocation, context));
    }

    public static String loadSubForm(String formIdentity, String defaultSubFormLocation, Context context)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getAssets().open(defaultSubFormLocation + "/" + formIdentity + ".json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String jsonString;
        while ((jsonString = reader.readLine()) != null) {
            stringBuilder.append(jsonString);
        }
        inputStream.close();


        return stringBuilder.toString();
    }

    public static JSONObject getFieldFromForm(JSONObject jsonForm, String key) throws JSONException {
        JSONObject field = new JSONObject();
        if (jsonForm != null) {
            JSONArray formFields = getMultiStepFormFields(jsonForm);
            if (formFields != null) {
                for (int i = 0; i < formFields.length(); i++) {
                    JSONObject widget = formFields.getJSONObject(i);
                    if (widget.has(JsonFormConstants.KEY) && key.equals(widget.getString(JsonFormConstants.KEY))) {
                        field = widget;
                    }
                }
            }
        }
        return field;
    }

    public static JSONArray getMultiStepFormFields(JSONObject jsonForm) {
        JSONArray fields = new JSONArray();
        try {
            if (jsonForm.has(JsonFormConstants.COUNT)) {
                int stepCount = Integer.parseInt(jsonForm.getString(JsonFormConstants.COUNT));
                for (int i = 0; i < stepCount; i++) {
                    String stepName = "step" + (i + 1);
                    JSONObject step = jsonForm.has(stepName) ? jsonForm.getJSONObject(stepName) : null;
                    if (step != null && step.has(JsonFormConstants.FIELDS)) {
                        JSONArray stepFields = step.getJSONArray(JsonFormConstants.FIELDS);
                        for (int k = 0; k < stepFields.length(); k++) {
                            JSONObject field = stepFields.getJSONObject(k);
                            fields.put(field);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return fields;
    }

    public static JSONArray fields(JSONObject jsonForm, String step) {
        try {
            JSONObject stepJSONObject = jsonForm.has(step) ? jsonForm.getJSONObject(step) : null;
            if (stepJSONObject == null) {
                return null;
            }

            return stepJSONObject.has(JsonFormConstants.FIELDS) ? stepJSONObject
                    .getJSONArray(JsonFormConstants.FIELDS) : null;
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public static JSONObject getFieldJSONObject(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0 || key == null) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, JsonFormConstants.KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            return null;

        }
    }

    public static String getString(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getString(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    public void showGenericDialog(View view) {
        Context context = (Context) view.getTag(R.id.specify_context);
        String specifyContent = (String) view.getTag(R.id.specify_content);
        String specifyContentForm = (String) view.getTag(R.id.specify_content_form);
        String stepName = (String) view.getTag(R.id.specify_step_name);
        CommonListener listener = (CommonListener) view.getTag(R.id.specify_listener);
        JsonFormFragment formFragment = (JsonFormFragment) view.getTag(R.id.specify_fragment);
        JSONArray jsonArray = (JSONArray) view.getTag(R.id.secondaryValues);
        String parentKey = (String) view.getTag(R.id.key);
        String type = (String) view.getTag(R.id.type);
        CustomTextView customTextView = (CustomTextView) view.getTag(R.id.specify_textview);
        CustomTextView reasonsTextView = (CustomTextView) view.getTag(R.id.specify_reasons_textview);
        String childKey;

        if (specifyContent != null) {
            GenericPopupDialog genericPopupDialog = new GenericPopupDialog();
            genericPopupDialog.setCommonListener(listener);
            genericPopupDialog.setFormFragment(formFragment);
            genericPopupDialog.setFormIdentity(specifyContent);
            genericPopupDialog.setFormLocation(specifyContentForm);
            genericPopupDialog.setStepName(stepName);
            genericPopupDialog.setSecondaryValues(jsonArray);
            genericPopupDialog.setParentKey(parentKey);
            genericPopupDialog.setWidgetType(type);
            genericPopupDialog.setContext(context);
            if (customTextView != null && reasonsTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
                genericPopupDialog.setPopupReasonsTextView(reasonsTextView);
            }
            if (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                childKey = (String) view.getTag(R.id.childKey);
                genericPopupDialog.setChildKey(childKey);
            }

            Activity activity = (Activity) context;
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            Fragment prev = activity.getFragmentManager().findFragmentByTag("GenericPopup");
            if (prev != null) {
                ft.remove(prev);
            }

            ft.addToBackStack(null);
            genericPopupDialog.show(ft, "GenericPopup");
        } else {
            Toast.makeText(context, "Please specify the sub form to display ", Toast.LENGTH_LONG).show();
        }


    }

    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType,
                                                String itemText) {
        Map<String, String> value = new HashMap<>();
        switch (itemType) {
            case JsonFormConstants.CHECK_BOX:
                value.put(itemKey, optionKey + ":" + itemText + ":" + keyValue + ";" + itemType);
                break;
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                value.put(itemKey, keyValue + ":" + itemText + ";" + itemType);
                break;
            default:
                value.put(itemKey, keyValue + ";" + itemType);
                break;
        }

        return value;
    }

    public String getRadioButtonText(JSONObject item, String value) {
        String text = "";
        if (item != null && item.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
            try {
                JSONArray options = item.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.getJSONObject(i);
                    if (option != null && option.has(JsonFormConstants.KEY)) {
                        String key = option.getString(JsonFormConstants.KEY);
                        if (key.equals(value)) {
                            text = option.getString(JsonFormConstants.TEXT);
                        }
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }

        }
        return text;
    }

    public String getSpecifyText(JSONArray jsonArray) {
        FormUtils formUtils = new FormUtils();
        StringBuilder specifyText = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    String type = jsonObject.optString(JsonFormConstants.TYPE, null);
                    JSONArray itemArray = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                    for (int j = 0; j < itemArray.length(); j++) {
                        String s = formUtils.getValueFromSecondaryValues(type, itemArray.getString(j));
                        if (!TextUtils.isEmpty(s)) {
                            specifyText.append(s).append(",").append(" ");
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return specifyText.toString().replaceAll(", $", "");
    }

    public String getValueFromSecondaryValues(String type, String itemString) {
        String newString;
        String[] strings = itemString.split(":");
        if (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
            newString = strings[1];
        } else {
            if (strings.length > 1) {
                newString = strings[1];
            } else {
                newString = strings[0];
            }
        }

        return newString;
    }

    public JSONArray getSecondaryValues(JSONObject jsonObject, String type) {
        JSONArray value = new JSONArray();
        String secondaryValues = type
                .equals(JsonFormConstants.EXPANSION_PANEL) ? JsonFormConstants.VALUE : JsonFormConstants.SECONDARY_VALUE;

        if (jsonObject != null && jsonObject.has(secondaryValues)) {
            try {
                value = jsonObject.getJSONArray(secondaryValues);
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }

        return value;
    }

    /**
     * Gets the fields from a specific step in the json form
     *
     * @param stepName
     * @param context
     * @return formFields {JSONArray}
     * @author dubdabasoduba
     */
    public JSONArray getFormFields(String stepName, Context context) {
        Activity activity = (Activity) context;
        JsonApi jsonApi = (JsonApi) activity;
        JSONArray fields = new JSONArray();
        JSONObject mJSONObject = jsonApi.getmJSONObject();
        if (mJSONObject != null) {
            JSONObject parentJson = jsonApi.getStep(stepName);
            try {
                if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson
                        .get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                    JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                    for (int i = 0; i < sections.length(); i++) {
                        JSONObject sectionJson = sections.getJSONObject(i);
                        if (sectionJson.has(JsonFormConstants.FIELDS)) {
                            fields = concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                        }
                    }
                } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson
                        .get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                    fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);

                }
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
        return fields;
    }

    public JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    public JSONObject getOpenMRSAttributes(JSONObject jsonObject) throws JSONException {
        String openmrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openmrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openmrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        JSONObject openmrsAttributes = new JSONObject();
        openmrsAttributes.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openmrsEntityParent);
        openmrsAttributes.put(JsonFormConstants.OPENMRS_ENTITY, openmrsEntity);
        openmrsAttributes.put(JsonFormConstants.OPENMRS_ENTITY_ID, openmrsEntityId);

        return openmrsAttributes;
    }
}

package com.vijay.jsonwizard.utils;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatCheckBox;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.FullScreenGenericPopupDialog;
import com.vijay.jsonwizard.domain.ExpansionPanelItemModel;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.GenericDialogInterface;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.views.CustomTextView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final String START_JAVAROSA_PROPERTY = "start";
    private static final String END_JAVAROSA_PROPERTY = "end";
    private static final String TODAY_JAVAROSA_PROPERTY = "today";
    private static final String DEFAULT_FORM_IMAGES_FOLDER = "image/";
    private static final String TAG = FormUtils.class.getSimpleName();
    private Utils utils = new Utils();
    private GenericDialogInterface genericDialogInterface;

    public static Point getViewLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    public static LinearLayout.LayoutParams getLinearLayoutParams(int width, int height, int left,
                                                                  int top, int right,
                                                                  int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams getRelativeLayoutParams(int width, int height, int left,
                                                                      int top, int right,
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
        return getTextViewWith(context, textSizeInSp, text, key, type, openMrsEntityParent,
                openMrsEntity, openMrsEntityId,
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
                JSONObject start = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(START_JAVAROSA_PROPERTY);
                String value = DATE_TIME_FORMAT.format(calendar.getTime());
                if (value == null) {
                    value = "";
                }
                start.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) {
                    value = "";
                }
                deviceId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) {
                    value = "";
                }
                subscriberId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) {
                    value = "";
                }
                simSerial.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) {
                    value = "";
                }
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
                if (value == null) {
                    value = "";
                }
                end.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(TODAY_JAVAROSA_PROPERTY)) {
                Calendar calendar = Calendar.getInstance();
                JSONObject today = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(TODAY_JAVAROSA_PROPERTY);
                String value = DATE_FORMAT.format(calendar.getTime());
                if (value == null) {
                    value = "";
                }
                today.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) {
                    value = "";
                }
                deviceId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) {
                    value = "";
                }
                subscriberId.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) {
                    value = "";
                }
                simSerial.put(JsonFormConstants.VALUE, value);
            }

            if (form.getJSONObject(METADATA_PROPERTY).has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject(METADATA_PROPERTY)
                        .getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) {
                    value = "";
                }
                simSerial.put(JsonFormConstants.VALUE, value);
            }
        }
    }

    public static Map<String, View> createRadioButtonAndCheckBoxLabel(String stepName,
                                                                      LinearLayout linearLayout,
                                                                      JSONObject jsonObject, Context context,
                                                                      JSONArray canvasIds, Boolean readOnly,
                                                                      CommonListener listener) throws JSONException {
        Map<String, View> createdViewsMap = new HashMap<>();
        String label = jsonObject.optString(JsonFormConstants.LABEL, "");
        if (!TextUtils.isEmpty(label)) {
            String asterisks = "";
            int labelTextSize = FormUtils
                    .getValueFromSpOrDpOrPx(
                            jsonObject.optString(JsonFormConstants.LABEL_TEXT_SIZE, String.valueOf(context
                                    .getResources().getDimension(R.dimen.default_label_text_size))), context);
            String labelTextColor = jsonObject
                    .optString(JsonFormConstants.LABEL_TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            ConstraintLayout labelConstraintLayout = createLabelLinearLayout(stepName, canvasIds,
                    jsonObject, context,
                    listener);

            CustomTextView labelText = labelConstraintLayout.findViewById(R.id.label_text);
            ImageView editButton = labelConstraintLayout.findViewById(R.id.label_edit_button);
            if (requiredObject != null) {
                String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
                if (!TextUtils.isEmpty(requiredValue) && (
                        Boolean.TRUE.toString().equalsIgnoreCase(requiredValue) || Boolean
                                .valueOf(requiredValue))) {
                    asterisks = "<font color=#CF0800> *</font>";
                }
            }

            String combinedLabelText =
                    "<font color=" + labelTextColor + ">" + label + "</font>" + asterisks;

            //Applying textStyle to the text;
            String textStyle = jsonObject
                    .optString(JsonFormConstants.TEXT_STYLE, JsonFormConstants.NORMAL);
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

    public static ConstraintLayout createLabelLinearLayout(String stepName, JSONArray canvasIds,
                                                           JSONObject jsonObject,
                                                           Context context,
                                                           CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject
                .optString(JsonFormConstants.OPENMRS_ENTITY_PARENT, null);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY, null);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID, null);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);

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
        constraintLayout
                .setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
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
        showInfoIcon(stepName, jsonObject, listener, FormUtils.getInfoDialogAttributes(jsonObject),
                imageView,
                canvasIds);

        return constraintLayout;
    }

    /**
     *
     */
    public static void setTextStyle(String textStyle, AppCompatTextView view) {
        switch (textStyle) {
            case JsonFormConstants.BOLD:
                view.setTypeface(null, Typeface.BOLD);
                break;
            case JsonFormConstants.ITALIC:
                view.setTypeface(null, Typeface.ITALIC);
                break;
            case JsonFormConstants.BOLD_ITALIC:
                view.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            case JsonFormConstants.NORMAL:
            default:
                view.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    public static int spToPx(Context context, float sp) {
        return (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPixels(Context context, float dps) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static void showInfoIcon(String stepName, JSONObject jsonObject, CommonListener listener,
                                    @NonNull HashMap<String, String> imageAttributes, ImageView imageView, JSONArray canvasIds)
            throws JSONException {

        //Display custom dialog if has image is true otherwise normal alert dialog is enough
        if (imageAttributes.get(JsonFormConstants.LABEL_INFO_HAS_IMAGE) != null &&
                Boolean.parseBoolean(imageAttributes.get(JsonFormConstants.LABEL_INFO_HAS_IMAGE)) &&
                imageAttributes.get(JsonFormConstants.LABEL_INFO_IMAGE_SRC) != null) {

            imageView.setTag(R.id.label_dialog_image_src,
                    imageAttributes.get(JsonFormConstants.LABEL_INFO_IMAGE_SRC));
            imageView.setVisibility(View.VISIBLE);

        } else if (imageAttributes.get(JsonFormConstants.LABEL_INFO_TEXT) != null) {

            imageView
                    .setTag(R.id.label_dialog_info, imageAttributes.get(JsonFormConstants.LABEL_INFO_TEXT));
            imageView
                    .setTag(R.id.label_dialog_title, imageAttributes.get(JsonFormConstants.LABEL_INFO_TITLE));
            imageView.setVisibility(View.VISIBLE);

        }

        imageView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        imageView.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        imageView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        imageView.setTag(R.id.canvas_ids, canvasIds.toString());
        imageView.setOnClickListener(listener);
    }

    public static HashMap<String, String> getInfoDialogAttributes(JSONObject jsonObject) {
        HashMap<String, String> imageAttributes = new HashMap<>();
        imageAttributes.put(JsonFormConstants.LABEL_INFO_TITLE,
                jsonObject.optString(JsonFormConstants.LABEL_INFO_TITLE, ""));
        imageAttributes.put(JsonFormConstants.LABEL_INFO_TEXT,
                jsonObject.optString(JsonFormConstants.LABEL_INFO_TEXT, null));
        imageAttributes.put(JsonFormConstants.LABEL_INFO_HAS_IMAGE,
                jsonObject.optString(JsonFormConstants.LABEL_INFO_HAS_IMAGE, null));
        imageAttributes.put(JsonFormConstants.LABEL_INFO_IMAGE_SRC,
                jsonObject.optString(JsonFormConstants.LABEL_INFO_IMAGE_SRC, null));
        return imageAttributes;
    }

    public static Drawable readImageFromAsset(Context context, String fileName) throws IOException {
        return Drawable.createFromStream(context.getAssets().open(fileName), null);
    }

    public static void setEditButtonAttributes(JSONObject jsonObject, View editableView,
                                               ImageView editButton,
                                               CommonListener listener) throws JSONException {
        editButton.setTag(R.id.editable_view, editableView);
        editButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editButton.setTag(R.id.type, jsonObject.getString("type"));
        editButton.setOnClickListener(listener);
    }

    /**
     * Checks and uncheck the radio buttons in a linear layout view follows this fix
     * https://stackoverflow.com/a/26961458/5784584
     *
     * @param parent {@link ViewGroup}
     */
    public static void setRadioExclusiveClick(ViewGroup parent) {
        final List<RadioButton> radioButtonList = Utils.getRadioButtons(parent);
        for (RadioButton radioButton : radioButtonList) {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton radioButtonView = (RadioButton) view;
                    for (RadioButton button : radioButtonList) {
                        if (button.getId() != radioButtonView.getId()) {
                            button.setChecked(false);
                            try {
                                Utils.resetRadioButtonsSpecifyText(button);
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
     * This method returns a {@link Calendar} object at mid-day corresponding to a date matching the
     * format specified in {@code DATE_FORMAT} or a day in reference to today e.g today, today-1,
     * today+10
     *
     * @param dayString_ The string to be converted to a date
     * @return The calendar object corresponding to the day, or object corresponding to today's date
     * if an error occurred
     */
    public static Calendar getDate(String dayString_) {
        Calendar calendarDate = Calendar.getInstance();

        if (dayString_ != null && dayString_.trim().length() > 0) {
            String dayString = dayString_.trim().toLowerCase();
            if (!"today".equals(dayString)) {
                Pattern pattern = Pattern.compile("today\\s*([-\\+])\\s*(\\d+)([dmywDMY]{1})");
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
                    } else if (matcher.group(3).equalsIgnoreCase("w")) {
                        field = Calendar.WEEK_OF_MONTH;
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

    public static void setEditMode(JSONObject jsonObject, View editableView, ImageView editButton)
            throws JSONException {
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
        } else if (jsonObject.has(JsonFormConstants.EDITABLE) && jsonObject
                .has(JsonFormConstants.READ_ONLY)) {
            editButton.setVisibility(View.VISIBLE);
            editableView.setEnabled(false);
        }
    }

    public static JSONObject getSubFormJson(String formIdentity, String subFormsLocation,
                                            Context context) throws Exception {
        String defaultSubFormLocation = JsonFormConstants.DEFAULT_SUB_FORM_LOCATION;
        if (!TextUtils.isEmpty(subFormsLocation)) {
            defaultSubFormLocation = subFormsLocation;
        }
        return new JSONObject(loadSubForm(formIdentity, defaultSubFormLocation, context));
    }

    public static String loadSubForm(String formIdentity, String defaultSubFormLocation,
                                     Context context)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getAssets()
                .open(defaultSubFormLocation + "/" + formIdentity + ".json");
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
                    if (widget.has(JsonFormConstants.KEY) && key
                            .equals(widget.getString(JsonFormConstants.KEY))) {
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

    /***
     *  This method adds an attribute to toggle visibility of the edit text
     *  You will find it useful when working with calculations
     *  that require you to check whether the value is empty since hidden widget defaults to '0'
     * @param jsonObject JsonObject that defines the edit Text
     * @param editText The edit text that you want to toggle its visibility
     * @throws JSONException throws JsonException
     * @author ellykits
     */
    public static void toggleEditTextVisibility(JSONObject jsonObject, AppCompatEditText editText)
            throws JSONException {
        if (jsonObject.has(JsonFormConstants.HIDDEN)) {
            boolean hidden = jsonObject.getBoolean(JsonFormConstants.HIDDEN);
            editText.setVisibility(hidden ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Get Bitmap given a location in the assets folder and the name of the file
     *
     * @param context
     * @param folder    The folder within the Assets folder where the image resides
     * @param imageFile Path and the name of the file
     * @return Bitmap
     */
    public static Bitmap getBitmap(Context context, String folder, String imageFile) {
        Bitmap bitmap = null;
        String filePath;
        if (TextUtils.isEmpty(imageFile)) {
            return null;
        }
        try {
            if (TextUtils.isEmpty(folder)) {
                filePath = DEFAULT_FORM_IMAGES_FOLDER + imageFile;
            } else {
                filePath = folder + imageFile;
            }
            InputStream is = context.getAssets().open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString());
        }
        return bitmap;
    }

    public static JSONArray getCheckboxValueJsonArray(HashSet<String> optionValues) {
        return new JSONArray(optionValues);
    }

    public static HashSet<String> getCurrentCheckboxValues(JSONArray optionsArray)
            throws JSONException {
        HashSet<String> result = new HashSet<>();
        for (int i = 0; i < optionsArray.length(); i++) {
            result.add(optionsArray.getString(i));
        }
        return result;
    }

    /**
     * Set view OpenMRS Entity attributes
     *
     * @param jsonObject JSON Object containing OpenMRS entity attributes
     * @param view       View to be set with OpenMRS attributes
     */
    public static void setViewOpenMRSEntityAttributes(JSONObject jsonObject, View view) {
        String key = jsonObject.optString(JsonFormConstants.KEY, "");
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY, "");
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID, "");

        view.setTag(R.id.key, key);
        view.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        view.setTag(R.id.openmrs_entity, openMrsEntity);
        view.setTag(R.id.openmrs_entity_id, openMrsEntityId);
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
        String toolbarHeader = "";
        String container = "";
        LinearLayout rootLayout = (LinearLayout) view.getTag(R.id.main_layout);
        if (type != null && type.equals(JsonFormConstants.EXPANSION_PANEL)) {
            toolbarHeader = (String) view.getTag(R.id.header);
            container = (String) view.getTag(R.id.contact_container);
        }

        if (specifyContent != null) {
            FullScreenGenericPopupDialog genericPopupDialog = new FullScreenGenericPopupDialog();
            genericPopupDialog.setCommonListener(listener);
            genericPopupDialog.setFormFragment(formFragment);
            genericPopupDialog.setFormIdentity(specifyContent);
            genericPopupDialog.setFormLocation(specifyContentForm);
            genericPopupDialog.setStepName(stepName);
            genericPopupDialog.setSecondaryValues(jsonArray);
            genericPopupDialog.setParentKey(parentKey);
            genericPopupDialog.setLinearLayout(rootLayout);
            genericPopupDialog.setContext(context);
            utils.setExpansionPanelDetails(type, toolbarHeader, container, genericPopupDialog);
            genericPopupDialog.setWidgetType(type);
            if (customTextView != null && reasonsTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
                genericPopupDialog.setPopupReasonsTextView(reasonsTextView);
            }
            utils.setChildKey(view, type, genericPopupDialog);

            FragmentTransaction ft = utils.getFragmentTransaction((Activity) context);
            genericPopupDialog.show(ft, "GenericPopup");
            resetFocus(context);
        } else {
            Toast.makeText(context, "Please specify the sub form to display ", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This clears the focus of the whole step when the popups are opened to avoid the scroll to the first edittext after
     * the popup close
     *
     * @param context {@link Context}
     */
    private void resetFocus(Context context) {
        if (context != null) {
            Activity activity = (Activity) context;
            LinearLayout mainLayout = activity.findViewById(R.id.main_layout);
            mainLayout.clearFocus();
        } else {
            Timber.d("The context is empty");
        }
    }

    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType, String itemText) {
        Map<String, String> value;
        if (genericDialogInterface != null && !TextUtils.isEmpty(genericDialogInterface.getWidgetType()) &&
                genericDialogInterface.getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            String[] labels = itemType.split(";");
            String type = "";
            if (labels.length >= 1) {
                type = labels[0];
            }
            value = returnValue(itemKey, optionKey, keyValue, itemType, itemText, type);
        } else {
            value = returnValue(itemKey, optionKey, keyValue, itemType, itemText, itemType);
        }

        return value;
    }

    private Map<String, String> returnValue(String itemKey, String optionKey, String keyValue, String itemType, String itemText, String type) {
        Map<String, String> value = new HashMap<>();
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case JsonFormConstants.CHECK_BOX:
                    value.put(itemKey, optionKey + ":" + itemText + ":" + keyValue + ";" + itemType);
                    break;
                case JsonFormConstants.NATIVE_RADIO_BUTTON:
                case JsonFormConstants.EXTENDED_RADIO_BUTTON:
                    value.put(itemKey, keyValue + ":" + itemText + ";" + itemType);
                    break;
                default:
                    value.put(itemKey, keyValue + ";" + itemType);
                    break;
            }
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
        if (type.equals(JsonFormConstants.CHECK_BOX) || type
                .equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
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
                .equals(JsonFormConstants.EXPANSION_PANEL) ? JsonFormConstants.VALUE
                : JsonFormConstants.SECONDARY_VALUE;

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

    public static ExpansionPanelItemModel getExpansionPanelItem(String key, JSONArray value) throws JSONException {
        ExpansionPanelItemModel result = null;
        for (int j = 0; j < value.length(); j++) {
            JSONObject valueItem = value.getJSONObject(j);
            if (valueItem.getString(JsonFormConstants.KEY).equals(key)) {
                JSONArray valueItemJSONArray = valueItem.getJSONArray(JsonFormConstants.VALUES);
                result = extractExpansionPanelItems(valueItem, valueItemJSONArray);
                break;
            }
        }
        return result;
    }

    private static ExpansionPanelItemModel extractExpansionPanelItems(
            JSONObject valueItem, JSONArray valueItemJSONArray) throws JSONException {
        ExpansionPanelItemModel result;
        String selectedKeys;
        String selectedValues;
        switch (valueItem.getString(JsonFormConstants.TYPE)) {
            case JsonFormConstants.EXTENDED_RADIO_BUTTON:
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                selectedKeys = valueItemJSONArray.getString(0).split(":")[0];
                selectedValues = valueItemJSONArray.getString(0).split(":")[1];
                result = new ExpansionPanelItemModel(valueItem.getString(JsonFormConstants.KEY), selectedKeys, selectedValues);
                break;
            case JsonFormConstants.CHECK_BOX:
                StringBuilder keysStringBuilder = new StringBuilder("[");
                StringBuilder valuesStringBuilder = new StringBuilder();
                selectedKeys = formatCheckboxValues(keysStringBuilder, valueItemJSONArray, 0) + "]";
                selectedValues = formatCheckboxValues(valuesStringBuilder, valueItemJSONArray, 1);
                result = new ExpansionPanelItemModel(valueItem.getString(JsonFormConstants.KEY), selectedKeys, selectedValues);
                break;
            default:
                result = new ExpansionPanelItemModel(valueItem.getString(JsonFormConstants.KEY),
                        valueItemJSONArray.getString(0), valueItemJSONArray.getString(0));
                break;
        }
        return result;
    }

    /**
     * Returns formatted checkbox values in this format:  [item1, item2, item2]
     * Can be used to return list of selected checkbox keys or the list of values  for checkboxes
     *
     * @param sb                 String builder to be used for the formatting
     * @param valueItemJSONArray JsonArray with the selected values from the checkbox
     * @param i                  index flag used to determine whether to get list for keys/values; 0 returns key list, 1 returns values list
     * @return List of selected keys or values
     * @throws JSONException exception thrown
     */
    @NonNull
    public static String formatCheckboxValues(StringBuilder sb, JSONArray valueItemJSONArray, int i) throws JSONException {
        String result;
        for (int index = 0; index < valueItemJSONArray.length(); index++) {
            sb.append(valueItemJSONArray.getString(index).split(":")[i]);
            sb.append(", ");
        }
        result = sb.toString().replaceAll(", $", "");
        return result;
    }

    public void addValuesDisplay(List<String> expansionWidgetValues, LinearLayout contentView, Context context) {
        if (expansionWidgetValues.size() > 0) {
            if (contentView.getChildCount() > 0) {
                contentView.removeAllViews();
            }
            for (int i = 0; i < expansionWidgetValues.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout valuesLayout = (LinearLayout) inflater.inflate(R.layout.native_expansion_panel_list_item, null);
                CustomTextView listHeader = valuesLayout.findViewById(R.id.item_header);
                CustomTextView listValue = valuesLayout.findViewById(R.id.item_value);
                listValue.setTextColor(context.getResources().getColor(R.color.text_color_primary));
                String[] valueObject = expansionWidgetValues.get(i).split(":");
                if (valueObject.length >= 2 && !JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_EARLIER.equals(valueObject[1]) &&
                        !JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_TODAY.equals(valueObject[1])) {
                    listHeader.setText(valueObject[0]);
                    listValue.setText(valueObject[1]);
                }

                contentView.addView(valuesLayout);
            }
        }
    }

    /**
     * Changes the Expansion panel status icon after selection
     *
     * @param imageView {@link ImageView}
     * @param type      {@link String}
     * @param context   {@link Context}
     * @author dubdabasoduba
     */
    public void changeIcon(ImageView imageView, String type, Context context) {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_TODAY:
                case JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_TODAY:
                case JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE:
                case JsonFormConstants.AncRadioButtonOptionTextUtils.DONE:
                case JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_EARLIER:
                case JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_EARLIER:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_done_256));
                    break;
                case JsonFormConstants.AncRadioButtonOptionTypesUtils.ORDERED:
                case JsonFormConstants.AncRadioButtonOptionTextUtils.ORDERED:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_ordered_256));
                    break;
                case JsonFormConstants.AncRadioButtonOptionTypesUtils.NOT_DONE:
                case JsonFormConstants.AncRadioButtonOptionTextUtils.NOT_DONE:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_not_done_256));
                    break;
                default:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_task_256));
                    break;
            }
        }
    }

    /**
     * This updates the expansion panel child values affect the done is selected from the pop up. It also updates the
     * expansion panel status image. It changes it to green when done, yellow when ordered, grey when not done
     *
     * @param values          {@link List<String>}
     * @param statusImageView {@link ImageView}
     * @throws JSONException
     * @author dubdabasoduba
     */
    public void updateExpansionPanelRecyclerView(List<String> values, ImageView statusImageView, Context context)
            throws JSONException {
        JSONArray list = new JSONArray(values);
        for (int k = 0; k < list.length(); k++) {
            String[] stringValues = list.getString(k).split(":");
            if (stringValues.length >= 2) {
                String valueDisplay = list.getString(k).split(":")[1];
                if (valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_TODAY) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_TODAY) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTextUtils.DONE) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_EARLIER) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTextUtils.DONE_EARLIER) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTypesUtils.ORDERED) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTextUtils.ORDERED) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTypesUtils.NOT_DONE) ||
                        valueDisplay.equals(JsonFormConstants.AncRadioButtonOptionTextUtils.NOT_DONE)) {

                    changeIcon(statusImageView, valueDisplay, context);
                    break;
                }
            }
        }
    }

    public static boolean isFieldRequired(JSONObject fieldObject) throws JSONException {
        boolean isValueRequired = false;
        if (fieldObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject valueRequired = fieldObject.getJSONObject(JsonFormConstants.V_REQUIRED);
            String value = valueRequired.getString(JsonFormConstants.VALUE);
            isValueRequired = Boolean.parseBoolean(value);
        }
        //Don't check required for hidden, toaster notes, spacer and label widgets
        return (!fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.SPACER) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.TOASTER_NOTES) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.HIDDEN)) &&
                isValueRequired;
    }

    public Facts getCheckBoxResults(JSONObject jsonObject) throws JSONException {
        Facts result = new Facts();
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int j = 0; j < options.length(); j++) {
            if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                    if (Boolean.valueOf(options.getJSONObject(j).getString(JsonFormConstants.VALUE))) {//Rules engine use only true values
                        result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                                options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                    }
                } else {
                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                            options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                }
            }

            //Backward compatibility Fix
            if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && !jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                    result.put(JsonFormConstants.VALUE, options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                } else {
                    result.put(JsonFormConstants.VALUE, JsonFormConstants.FALSE);
                }
            }
        }
        return result;
    }

    /**
     * @param multiRelevance {@link Boolean}
     * @param object         {@link JSONObject}
     * @return result {@link Facts}
     * @throws JSONException
     */
    public Facts getRadioButtonResults(Boolean multiRelevance, JSONObject object) throws JSONException {
        Facts result = new Facts();
        if (multiRelevance) {
            JSONArray jsonArray = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int j = 0; j < jsonArray.length(); j++) {
                if (object.has(JsonFormConstants.VALUE)) {
                    if (object.getString(JsonFormConstants.VALUE).equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(true));
                    } else {
                        if (!object.has(RuleConstant.IS_RULE_CHECK) || !object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                            result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(false));
                        }
                    }
                } else {
                    Timber.e("option for Key " + jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                }
            }
        } else {
            result.put(utils.getKey(object), utils.getValue(object));
        }

        return result;
    }

    /**
     * This method request focus for views of widgets that are required and have no values added yet.
     *
     * @param jsonObject {@link JSONObject}
     * @param view       {@link View}
     */
    public static void requestFocusForRequiredEmptyFields(JSONObject jsonObject, View view) {
        try {
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);

            if (requiredObject != null) {
                boolean requiredValue = requiredObject.getBoolean(JsonFormConstants.VALUE);
                if (Boolean.TRUE.equals(requiredValue) && FormUtils.shouldRequestFocus(jsonObject)) {
                    getViewFromCompoundWidgets(jsonObject, view);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "EditTextFactory --> requestFocusForRequiredEmptyFields");
        }
    }

    private static void getViewFromCompoundWidgets(JSONObject field, View view) {
        String type = field.optString(JsonFormConstants.TYPE, null);
        if (view != null && type != null) {
            if (JsonFormConstants.NATIVE_RADIO_BUTTON.equals(type) || JsonFormConstants.RADIO_BUTTON.equals(type) || JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(type)) {
                LinearLayout linearLayout = (LinearLayout) view;
                RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(1);
                radioGroup.requestFocus();
                radioGroup.requestFocusFromTouch();
            } else if (JsonFormConstants.CHECK_BOX.equals(type)) {
                LinearLayout mainLayout = (LinearLayout) view;
                LinearLayout checkBoxParent = (LinearLayout) mainLayout.getChildAt(1);
                AppCompatCheckBox checkBox = (AppCompatCheckBox) checkBoxParent.getChildAt(0);
                checkBox.requestFocus();
                checkBox.requestFocusFromTouch();
            } else {
                view.requestFocus();
                view.requestFocusFromTouch();
            }
        }
    }

    /**
     * Checks if the field JSON object has a value
     *
     * @param formField {@link JSONObject}
     * @return isValuePresent {@link Boolean}
     */
    private static boolean shouldRequestFocus(JSONObject formField) {
        boolean isValuePresent = true;
        try {
            if (formField.has(JsonFormConstants.TYPE)) {
                if (JsonFormConstants.EDIT_TEXT.equals(formField.getString(JsonFormConstants.KEY))
                        || JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(formField.getString(JsonFormConstants.KEY))
                        || JsonFormConstants.NATIVE_RADIO_BUTTON.equals(formField.getString(JsonFormConstants.KEY))
                        || JsonFormConstants.NUMBER_SELECTOR.equals(formField.getString(JsonFormConstants.KEY))) {
                    isValuePresent = checkIfValueExists(formField);
                } else if (JsonFormConstants.CHECK_BOX.equals(formField.getString(JsonFormConstants.KEY))) {
                    if (formField.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                        JSONArray jsonArray = formField.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (checkIfValueExists(jsonObject)) {
                                isValuePresent = false;
                                break;
                            }
                        }
                    }
                } else if (JsonFormConstants.EXPANSION_PANEL.equals(formField.getString(JsonFormConstants.KEY))) {
                    if (formField.has(JsonFormConstants.SECOND_VALUE)) {
                        JSONArray values = formField.optJSONArray(JsonFormConstants.SECOND_VALUE);
                        if (values != null && values.length() >= 0) {
                            isValuePresent = false;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "FormUtils --> shouldRequestFocus");
        }
        return isValuePresent;
    }

    /**
     * Checks if the values exists and is not empty
     *
     * @param jsonObject {@link JSONObject}
     * @return isValueFound {@link Boolean}
     */
    private static boolean checkIfValueExists(JSONObject jsonObject) {
        boolean isValueFound = false;
        try {
            if (jsonObject.has(JsonFormConstants.VALUE) && StringUtils.isNotEmpty(jsonObject.getString(JsonFormConstants.KEY))) {
                isValueFound = true;
            }
        } catch (JSONException e) {
            Timber.e(e, "FormUtils --> checkIfValueExists");
        }
        return isValueFound;
    }
}

package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vijay.jsonwizard.utils.FormUtils.showEditButton;
import static com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT;


/**
 * Created by samuelgithengi on 8/16/18.
 */
public class NativeRadioButtonFactory implements FormWidgetFactory {

    private static final String TAG = NativeRadioButtonFactory.class.getCanonicalName();
    private static FormUtils formUtils = new FormUtils();
    private static String hiddenDate;
    private RadioButton radioButton;
    private CustomTextView extraInfoTextView;
    private String secondaryValueDate;

    public static void showDateDialog(View view) {

        Context context = (Context) view.getTag(R.id.specify_context);
        CustomTextView customTextView = (CustomTextView) view.getTag(R.id.specify_textview);
        RadioButton mainTextView = (RadioButton) view.getTag(R.id.native_radio_button);
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        JSONObject jsonObject = (JSONObject) ((View) (mainTextView).getParent().getParent().getParent()).getTag(R.id.json_object);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            datePickerDialog.setCalendarViewShown(false);

            try {
                if (jsonObject != null) {

                    if (jsonObject.has(JsonFormConstants.MIN_DATE)) {
                        Calendar minDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MIN_DATE));
                        minDate.set(Calendar.HOUR_OF_DAY, 0);
                        minDate.set(Calendar.MINUTE, 0);
                        minDate.set(Calendar.SECOND, 0);
                        minDate.set(Calendar.MILLISECOND, 0);
                        datePickerDialog.setMinDate(minDate.getTimeInMillis());
                    }

                    if (jsonObject.has(JsonFormConstants.MAX_DATE)) {
                        Calendar maxDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MAX_DATE));
                        maxDate.set(Calendar.HOUR_OF_DAY, 23);
                        maxDate.set(Calendar.MINUTE, 59);
                        maxDate.set(Calendar.SECOND, 59);
                        maxDate.set(Calendar.MILLISECOND, 999);
                        datePickerDialog.setMaxDate(maxDate.getTimeInMillis());

                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }

        datePickerDialog.setContext(context);
        setDate(datePickerDialog, mainTextView, customTextView, context);
        showDatePickerDialog((Activity) context, datePickerDialog, mainTextView);
    }

    private static void showDatePickerDialog(Activity context,
                                             DatePickerDialog datePickerDialog,
                                             RadioButton radioButton) {
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        Fragment prev = context.getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        datePickerDialog.show(ft, TAG);
        Calendar calendar = getDate(radioButton);
        datePickerDialog.setDate(calendar.getTime());
    }

    private static Calendar getDate(RadioButton radioButton) {
        String[] arrayString = radioButton.getText().toString().split(":");
        String dateString = "";
        if (arrayString.length > 1) {
            dateString = arrayString[1];
        }
        return FormUtils.getDate(dateString);
    }


    private static void setDate(DatePickerDialog datePickerDialog, final RadioButton radioButton, final CustomTextView
            customTextView, final Context context) {
        final String[] arrayString = radioButton.getText().toString().split(":");
        datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarDate.set(Calendar.MONTH, monthOfYear);
                calendarDate.set(Calendar.YEAR, year);
                if (calendarDate.getTimeInMillis() >= view.getMinDate() && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                    radioButton.setText(arrayString[0] + ": " + DATE_FORMAT.format(calendarDate.getTime()));
                    customTextView.setText(createSpecifyText(context.getResources().getString(R.string.radio_button_date_change)));

                    if (context instanceof JsonFormActivity) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(customTextView.getTag(R.id.key).toString(), customTextView.getTag(R.id.childKey) + ":" + DATE_FORMAT.format(calendarDate.getTime()));

                        Intent intent = new Intent(JsonFormConstants.INTENT_ACTION.JSON_FORM_ACTIVITY);
                        intent.putExtra(JsonFormConstants.INTENT_KEY.MESSAGE, map);
                        intent.putExtra(JsonFormConstants.INTENT_KEY.MESSAGE_TYPE, JsonFormConstants.MESSAGE_TYPE.GLOBAL_VALUES);

                        ((JsonFormActivity) context).getLocalBroadcastManager().sendBroadcast(intent);
                    }
                    String key = (String) customTextView.getTag(R.id.key);
                    String childKey = (String) customTextView.getTag(R.id.childKey);
                    String stepName = (String) customTextView.getTag(R.id.specify_step_name);
                    Context context = (Context) customTextView.getTag(R.id.specify_context);


                    onGenericDataPass(key, childKey, stepName, context, calendarDate);

                } else {
                    radioButton.setText(arrayString[0]);
                }
                radioButton.setChecked(false);
                radioButton.performClick();
            }
        });
    }

    private static void onGenericDataPass(String parentKey, String childKey, String stepName, Context context, Calendar calendarDate) {
        Activity activity = (Activity) context;
        JsonApi jsonApi = (JsonApi) activity;
        JSONObject mJSONObject = jsonApi.getmJSONObject();
        if (mJSONObject != null) {
            JSONObject parentJson = jsonApi.getStep(stepName);
            JSONArray fields = new JSONArray();
            try {
                if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                    JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                    for (int i = 0; i < sections.length(); i++) {
                        JSONObject sectionJson = sections.getJSONObject(i);
                        if (sectionJson.has(JsonFormConstants.FIELDS)) {
                            fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                        }
                    }
                } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                    fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);

                }

                if (fields.length() > 0) {
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject widget = fields.getJSONObject(i);
                        if (widget != null && widget.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            radioButtonOptionAssignSecondaryValue(widget, childKey, calendarDate);
                        }
                        if (widget != null && widget.getString(JsonFormConstants.KEY).equals(parentKey + JsonFormConstants.SPECIFY_DATE_HIDDEN_FIELD_SUFFIX)) {
                            assignHiddenDateValue(widget, calendarDate);
                        }
                    }
                }

            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
    }

    private static void assignHiddenDateValue(JSONObject widget, Calendar calendarDate) {
        try {
            widget.put(JsonFormConstants.VALUE, DATE_FORMAT.format(calendarDate.getTime()));
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This assign the secondary value to the radio button options
     *
     * @param jsonObject
     * @param childKey
     * @param calendarDate
     * @throws JSONException
     */
    private static void radioButtonOptionAssignSecondaryValue(JSONObject jsonObject, String childKey, Calendar calendarDate) throws JSONException {
        if (jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject option = jsonArray.getJSONObject(i);
                if (option.has(JsonFormConstants.KEY) && option.getString(JsonFormConstants.KEY).equals(childKey)) {
                    addSecondaryValue(option, calendarDate);
                }
            }
        }
    }

    /**
     * Add the secondary value object
     *
     * @param item
     * @param calendarDate
     * @throws JSONException
     */
    private static void addSecondaryValue(JSONObject item, Calendar calendarDate) throws JSONException {
        JSONObject valueObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        valueObject.put(JsonFormConstants.KEY, item.getString(JsonFormConstants.KEY));
        valueObject.put(JsonFormConstants.TYPE, JsonFormConstants.DATE_PICKER);
        valueObject.put(JsonFormConstants.VALUES, jsonArray.put(DATE_FORMAT.format(calendarDate.getTime())));

        try {
            item.put(JsonFormConstants.SECONDARY_VALUE, new JSONArray().put(valueObject));
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    private static String createSpecifyText(String text) {
        return text == null || text.isEmpty() ? "" : "(" + text + ")";
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws JSONException {

        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }
        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        ImageView editButton;

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);
        Map<String, View> labelViews = FormUtils.createRadioButtonAndCheckBoxLabel(stepName, rootLayout, jsonObject, context, canvasIds,
                readOnly, listener);
        View radioGroup = addRadioButtonOptionsElements(jsonObject, context, readOnly, canvasIds, stepName, rootLayout, listener,
                formFragment, popup);

        radioGroup.setTag(R.id.json_object, jsonObject);

        if (labelViews != null && labelViews.size() > 0) {
            editButton = (ImageView) labelViews.get(JsonFormConstants.EDIT_BUTTON);
            if (editButton != null) {
                showEditButton(jsonObject, radioGroup, editButton, listener);
            }

        }
        rootLayout.setTag(R.id.extraPopup, popup);
        views.add(rootLayout);
        return views;
    }

    protected int getLayout() {
        return R.layout.native_form_compound_button_parent;
    }

    /**
     * Creates the Radio Button options from the JSON definitions
     *
     * @param jsonObject
     * @param context
     * @param readOnly
     * @param canvasIds
     * @param stepName
     * @param linearLayout
     * @param listener
     * @throws JSONException
     */

    protected View addRadioButtonOptionsElements(JSONObject jsonObject, Context context, Boolean readOnly, JSONArray canvasIds,
                                                 String stepName, LinearLayout linearLayout, CommonListener listener, JsonFormFragment
                                                         formFragment, boolean popup) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        Boolean extraRelCheck = jsonObject.optBoolean(JsonFormConstants.EXTRA_REL, false);
        String extraRelArray = null;
        if (extraRelCheck) {
            extraRelArray = jsonObject.optString(JsonFormConstants.HAS_EXTRA_REL, null);
        }

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        radioGroup.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        radioGroup.setTag(R.id.openmrs_entity, openMrsEntity);
        radioGroup.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        radioGroup.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        radioGroup.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        radioGroup.setTag(R.id.extraRelCheck, extraRelCheck);
        radioGroup.setTag(R.id.extraRelArray, extraRelArray);
        radioGroup.setTag(R.id.extraPopup, popup);
        radioGroup.setId(ViewUtil.generateViewId());
        canvasIds.put(radioGroup.getId());

        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            String specifyInfo = item.optString(JsonFormConstants.CONTENT_INFO, null);
            String extraInfo = item.optString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO, null);
            String labelInfoText = item.optString(JsonFormConstants.LABEL_INFO_TEXT, "");
            String labelInfoTitle = item.optString(JsonFormConstants.LABEL_INFO_TITLE, "");

            RelativeLayout radioGroupLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.native_item_radio_button, null);
            radioGroupLayout.setId(ViewUtil.generateViewId());
            radioGroupLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            radioGroupLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            radioGroupLayout.setTag(R.id.openmrs_entity, openMrsEntity);
            radioGroupLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            radioGroupLayout.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.extraPopup, popup);
            canvasIds.put(radioGroupLayout.getId());
            radioGroupLayout.setTag(R.id.canvas_ids, canvasIds.toString());

            //Showing optional info alert dialog on individual radio buttons
            ImageView imageView = radioGroupLayout.findViewById(R.id.info_icon);
            FormUtils.showInfoIcon(stepName, jsonObject, listener, labelInfoText, labelInfoTitle, imageView, canvasIds);


            createRadioButton(radioGroupLayout, jsonObject, readOnly, item, listener, stepName, popup, context, canvasIds);
            if (specifyInfo != null) {
                createSpecifyTextView(context, radioGroupLayout, jsonObject, listener, item, stepName, formFragment, readOnly);
            }

            if (extraInfo != null) {
                createExtraInfo(radioGroupLayout, item, jsonObject, stepName, readOnly, context);
            }

            ((JsonApi) context).addFormDataView(radioGroupLayout);
            radioGroup.addView(radioGroupLayout);
        }

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            radioGroup.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(radioGroup);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            radioGroup.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(radioGroup);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            radioGroup.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(radioGroup);
        }

        FormUtils.setRadioExclusiveClick(radioGroup);
        radioGroup.setLayoutParams(FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.extra_bottom_margin)));
        radioGroup.setTag(R.id.canvas_ids, canvasIds.toString());

        linearLayout.addView(radioGroup);
        return radioGroup;
    }

    private void createRadioButton(RelativeLayout rootLayout, JSONObject jsonObject, Boolean readOnly, JSONObject item,
                                   CommonListener listener, String stepName, boolean popup, Context context, JSONArray canvasIds) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        RadioButton radioButton = rootLayout.findViewById(R.id.mainRadioButton);
        radioButton.setId(ViewUtil.generateViewId());
        radioButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        radioButton.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        radioButton.setTag(R.id.openmrs_entity, openMrsEntity);
        radioButton.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        radioButton.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        radioButton.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
        radioButton.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        radioButton.setTag(R.id.extraPopup, popup);
        radioButton.setTag(jsonObject.getString(JsonFormConstants.TYPE));
        radioButton.setOnCheckedChangeListener(listener);
        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
                && jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
            radioButton.setChecked(true);
        }
        String optionTextColor = JsonFormConstants.DEFAULT_TEXT_COLOR;
        if (item.has(JsonFormConstants.TEXT_COLOR)) {
            optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
        }
        String optionTextSize = String.valueOf(context.getResources().getDimension(R.dimen.options_default_text_size));
        if (item.has(JsonFormConstants.TEXT_SIZE)) {
            optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
        }

        String optionText = item.getString(JsonFormConstants.TEXT);

        if (item.has(JsonFormConstants.SECONDARY_VALUE)) {
            JSONArray secondaryValueArray = item.getJSONArray(JsonFormConstants.SECONDARY_VALUE);
            if (secondaryValueArray != null && secondaryValueArray.length() > 0) {
                JSONObject secondaryValue = secondaryValueArray.getJSONObject(0);
                String secondValueKey = secondaryValue.getString(JsonFormConstants.KEY);
                String secondValueType = secondaryValue.getString(JsonFormConstants.TYPE);
                if (item.getString(JsonFormConstants.KEY).equals(secondValueKey) && secondValueType.equals(JsonFormConstants.DATE_PICKER)) {
                    secondaryValueDate = getSecondaryDateValue(secondaryValue.getJSONArray(JsonFormConstants.VALUES));
                    optionText = item.getString(JsonFormConstants.TEXT) + ":" + secondaryValueDate;
                }
            }
        }

        radioButton.setTextColor(Color.parseColor(optionTextColor));
        radioButton.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize, context));
        radioButton.setText(optionText);
        radioButton.setEnabled(!readOnly);
        canvasIds.put(radioButton.getId());
        radioButton.setTag(R.id.canvas_ids, canvasIds.toString());
        setRadioButton(radioButton);
    }

    private String getSecondaryDateValue(JSONArray values) throws JSONException {
        String date = "";
        if (values != null && values.length() > 0) {
            date = values.getString(0);
        }
        return date;
    }

    private void createSpecifyTextView(Context context, RelativeLayout rootLayout, JSONObject jsonObject, CommonListener listener,
                                       JSONObject item, String stepName, JsonFormFragment formFragment, Boolean readOnly) throws JSONException {

        String text = item.has(JsonFormConstants.SECONDARY_VALUE) ?
                item.has(JsonFormConstants.CONTENT_WIDGET) && (item.has(JsonFormConstants.CONTENT_WIDGET) && item.getString(JsonFormConstants.CONTENT_WIDGET).equals(JsonFormConstants.DATE_PICKER)) ?
                        context.getResources().getString(R.string.radio_button_date_change) :
                        formUtils.getSpecifyText(item.getJSONArray(JsonFormConstants.SECONDARY_VALUE)) : item.getString(JsonFormConstants.CONTENT_INFO);

        String text_color = item.optString(JsonFormConstants.CONTENT_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);
        String specifyWidget = item.optString(JsonFormConstants.CONTENT_WIDGET, "");
        String specifyContent = item.optString(JsonFormConstants.CONTENT_FORM, null);
        String specifyContentForm = item.optString(JsonFormConstants.CONTENT_FORM_LOCATION, null);
        CustomTextView specifyTextView = rootLayout.findViewById(R.id.specifyTextView);
        specifyTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(jsonObject, item, specifyTextView, stepName, text_color);
        specifyTextView.setTag(R.id.specify_type, JsonFormConstants.CONTENT_INFO);
        specifyTextView.setTag(R.id.specify_widget, specifyWidget);
        specifyTextView.setTag(R.id.specify_content, specifyContent);
        specifyTextView.setTag(R.id.specify_content_form, specifyContentForm);
        specifyTextView.setTag(R.id.specify_listener, listener);
        specifyTextView.setTag(R.id.specify_step_name, stepName);
        specifyTextView.setTag(R.id.specify_fragment, formFragment);
        specifyTextView.setTag(R.id.specify_textview, specifyTextView);
        specifyTextView.setTag(R.id.native_radio_button, getRadioButton());
        specifyTextView.setTag(R.id.specify_context, context);
        specifyTextView.setTag(R.id.secondaryValues, formUtils.getSecondaryValues(item, jsonObject.getString(JsonFormConstants.TYPE)));
        specifyTextView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        specifyTextView.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        specifyTextView.setText(createSpecifyText(text));
        specifyTextView.setTextSize(context.getResources().getDimension(R.dimen.specify_date_default_text_size));

        specifyTextView.setId(ViewUtil.generateViewId());
        specifyTextView.setOnClickListener(listener);
        specifyTextView.setEnabled(!readOnly);
    }

    private void createExtraInfo(RelativeLayout rootLayout, JSONObject item, JSONObject jsonObject,
                                 String stepName, boolean readOnly, Context context) throws JSONException {
        String text = item.getString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO);
        String text_color = item.optString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);

        CustomTextView extraInfoTextView = rootLayout.findViewById(R.id.extraInfoTextView);
        extraInfoTextView.setTextSize(context.getResources().getDimension(R.dimen.extra_info_default_text_size));
        extraInfoTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(jsonObject, item, extraInfoTextView, stepName, text_color);
        extraInfoTextView.setText(text);
        extraInfoTextView.setEnabled(!readOnly);
    }

    private void addTextViewAttributes(JSONObject jsonObject, JSONObject item, CustomTextView customTextView,
                                       String stepName, String text_color) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        customTextView.setTextColor(Color.parseColor(text_color));
        customTextView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        customTextView.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        customTextView.setTag(R.id.openmrs_entity, openMrsEntity);
        customTextView.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        customTextView.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        customTextView.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
        customTextView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        setExtraInfoTextView(customTextView);
    }

    public CustomTextView getExtraInfoTextView() {
        return extraInfoTextView;
    }

    private void setExtraInfoTextView(CustomTextView customTextView) {
        extraInfoTextView = customTextView;
    }

    private RadioButton getRadioButton() {
        return radioButton;
    }

    private void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }
}

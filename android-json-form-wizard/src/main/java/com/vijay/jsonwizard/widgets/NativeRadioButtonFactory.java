package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Map;

import static com.vijay.jsonwizard.utils.FormUtils.showEditButton;
import static com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT;


/**
 * Created by samuelgithengi on 8/16/18.
 */
public class NativeRadioButtonFactory implements FormWidgetFactory {
    private static final String TAG = "NativeRadioFactory";

    private CustomTextView customTextView;
    private CustomTextView extraInfoTextView;
    private FormUtils formUtils = new FormUtils();

    public static void showDateDialog(View view) {
        Context context = (Context) view.getTag(R.id.specify_context);
        CustomTextView customTextView = (CustomTextView) view.getTag(R.id.specify_textview);
        CustomTextView mainTextView = (CustomTextView) view.getTag(R.id.main_textview);
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePickerDialog.setCalendarViewShown(false);
        }
        datePickerDialog.setContext(context);
        setDate(datePickerDialog, mainTextView, customTextView, context);
        showDatePickerDialog((Activity) context, datePickerDialog, mainTextView);
    }

    private static void showDatePickerDialog(Activity context,
                                             DatePickerDialog datePickerDialog,
                                             CustomTextView customTextView) {
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        Fragment prev = context.getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        datePickerDialog.show(ft, TAG);
        Calendar calendar = getDate(customTextView);
        datePickerDialog.setDate(calendar.getTime());
    }

    private static Calendar getDate(CustomTextView customTextView) {
        String[] arrayString = customTextView.getText().toString().split(":");
        String dateString = "";
        if (arrayString.length > 1) {
            dateString = arrayString[1];
        }
        return FormUtils.getDate(dateString);
    }


    private static void setDate(DatePickerDialog datePickerDialog, final CustomTextView maintextView, final CustomTextView
            customTextView, final Context context) {
        final String[] arrayString = maintextView.getText().toString().split(":");
        datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarDate.set(Calendar.MONTH, monthOfYear);
                calendarDate.set(Calendar.YEAR, year);
                if (calendarDate.getTimeInMillis() >= view.getMinDate() && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                    maintextView.setText(arrayString[0] + ": " + DATE_FORMAT.format(calendarDate.getTime()));
                    customTextView.setText(createSpecifyText(context.getResources().getString(R.string.radio_button_date_change)));
                } else {
                    maintextView.setText(arrayString[0]);
                }
            }
        });
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

    private List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener, boolean popup) throws JSONException {
        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }
        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        ImageView editButton;

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);
        Map<String, View> labelViews = FormUtils.createRadioButtonAndCheckBoxLabel(rootLayout, jsonObject, context, canvasIds,
                readOnly, listener);
        View radioGroup = addRadioButtonOptionsElements(jsonObject, context, readOnly, canvasIds, stepName, rootLayout, listener,
                formFragment, popup);
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
            radioGroupLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            radioGroupLayout.setTag(R.id.openmrs_entity, openMrsEntity);
            radioGroupLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            radioGroupLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            radioGroupLayout.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            radioGroup.setTag(R.id.extraPopup, popup);
            canvasIds.put(radioGroupLayout.getId());

            //Showing optional info alert dialog on individual radio buttons
            ImageView imageView = radioGroupLayout.findViewById(R.id.info_icon);
            FormUtils.showInfoIcon(jsonObject, listener, labelInfoText, labelInfoTitle, imageView);

            createRadioButton(radioGroupLayout, jsonObject, readOnly, item, listener, stepName, popup);

            createMainTextView(context, radioGroupLayout, jsonObject, item, stepName, readOnly);
            if (specifyInfo != null) {
                createSpecifyTextView(context, radioGroupLayout, jsonObject, listener, item, stepName, formFragment, readOnly);
            }

            if (extraInfo != null) {
                createExtraInfo(context, radioGroupLayout, item, jsonObject, stepName, readOnly);
            }

            ((JsonApi) context).addFormDataView(radioGroupLayout);
            radioGroup.addView(radioGroupLayout);
        }

        if (relevance != null && context instanceof JsonApi) {
            radioGroup.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(radioGroup);
        }

        FormUtils.setRadioExclusiveClick(radioGroup);
        radioGroup.setLayoutParams(FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.extra_bottom_margin)));
        radioGroup.setTag(R.id.canvas_ids, canvasIds.toString());

        linearLayout.addView(radioGroup);
        return radioGroup;
    }

    private void createRadioButton(RelativeLayout rootLayout, JSONObject jsonObject, Boolean readOnly, JSONObject item,
                                   CommonListener listener, String stepName, boolean popup) throws JSONException {
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
        radioButton.setOnCheckedChangeListener(listener);
        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
                && jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
            radioButton.setChecked(true);
        }
        radioButton.setEnabled(!readOnly);
    }

    private void createMainTextView(Context context, RelativeLayout rootLayout, JSONObject jsonObject, JSONObject item,
                                    String stepName, Boolean readOnly) throws JSONException {
        CustomTextView mainTextView = rootLayout.findViewById(R.id.mainRadioButtonTextView);
        String optionTextColor = JsonFormConstants.DEFAULT_TEXT_COLOR;
        if (item.has(JsonFormConstants.TEXT_COLOR)) {
            optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
        }
        addTextViewAttributes(context, jsonObject, item, mainTextView, stepName, optionTextColor);
        mainTextView.setText(item.getString(JsonFormConstants.TEXT));
        mainTextView.setEnabled(!readOnly);
        setMainTextView(mainTextView);
    }

    private void createSpecifyTextView(Context context, RelativeLayout rootLayout, JSONObject jsonObject, CommonListener listener,
                                       JSONObject item, String stepName, JsonFormFragment formFragment, Boolean readOnly) throws JSONException {
        String text;
        if (item.has(JsonFormConstants.SECONDARY_VALUE)) {
            text = formUtils.getSpecifyText(item.getJSONArray(JsonFormConstants.SECONDARY_VALUE));
        } else {
            text = item.getString(JsonFormConstants.CONTENT_INFO);
        }


        String text_color = item.optString(JsonFormConstants.CONTENT_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);
        String specifyWidget = item.optString(JsonFormConstants.CONTENT_WIDGET, "");
        String specifyContent = item.optString(JsonFormConstants.CONTENT_FORM, null);
        String specifyContentForm = item.optString(JsonFormConstants.CONTENT_FORM_LOCATION, null);
        CustomTextView specifyTextView = rootLayout.findViewById(R.id.specifyTextView);
        specifyTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(context, jsonObject, item, specifyTextView, stepName, text_color);
        specifyTextView.setTag(R.id.specify_type, JsonFormConstants.CONTENT_INFO);
        specifyTextView.setTag(R.id.specify_widget, specifyWidget);
        specifyTextView.setTag(R.id.specify_content, specifyContent);
        specifyTextView.setTag(R.id.specify_content_form, specifyContentForm);
        specifyTextView.setTag(R.id.specify_listener, listener);
        specifyTextView.setTag(R.id.specify_step_name, stepName);
        specifyTextView.setTag(R.id.specify_fragment, formFragment);
        specifyTextView.setTag(R.id.specify_textview, specifyTextView);
        specifyTextView.setTag(R.id.main_textview, getMainTextView());
        specifyTextView.setTag(R.id.specify_context, context);
        specifyTextView.setTag(R.id.secondaryValues, formUtils.getSecondaryValues(item));
        specifyTextView.setTag(R.id.specify_textview, specifyTextView);
        specifyTextView.setText(createSpecifyText(text));
        specifyTextView.setId(ViewUtil.generateViewId());
        specifyTextView.setOnClickListener(listener);
        specifyTextView.setEnabled(!readOnly);
    }

    private void createExtraInfo(Context context, RelativeLayout rootLayout, JSONObject item, JSONObject jsonObject,
                                 String stepName, Boolean readOnly) throws JSONException {
        String text = item.getString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO);
        String text_color = item.optString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);
        CustomTextView extraInfoTextView = rootLayout.findViewById(R.id.extraInfoTextView);
        extraInfoTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(context, jsonObject, item, extraInfoTextView, stepName, text_color);
        extraInfoTextView.setText(text);
        extraInfoTextView.setEnabled(!readOnly);
    }

    private void addTextViewAttributes(Context context, JSONObject jsonObject, JSONObject item, CustomTextView customTextView,
                                       String stepName, String text_color) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String optionTextSize = String.valueOf(context.getResources().getDimension(R.dimen.options_default_text_size));
        if (item.has(JsonFormConstants.TEXT_SIZE)) {
            optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
        }

        customTextView.setTextColor(Color.parseColor(text_color));
        customTextView.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize, context));

        customTextView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        customTextView.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        customTextView.setTag(R.id.openmrs_entity, openMrsEntity);
        customTextView.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        customTextView.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        customTextView.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
        customTextView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        setExtraInfoTextView(customTextView);
    }

    private CustomTextView getMainTextView() {
        return customTextView;
    }

    private void setMainTextView(CustomTextView customTextView) {
        this.customTextView = customTextView;
    }

    public CustomTextView getExtraInfoTextView() {
        return extraInfoTextView;
    }

    private void setExtraInfoTextView(CustomTextView customTextView) {
        extraInfoTextView = customTextView;
    }
}

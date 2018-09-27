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

import static com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT;


/**
 * Created by samuelgithengi on 8/16/18.
 */
public class NativeRadioButtonFactory implements FormWidgetFactory {
    private static final String TAG = "NativeRadioButtonFactory";
    private CustomTextView customTextView;
    private CustomTextView extraInfoTextView;

    public static void showDateDialog(View view) {
        Context context = (Context) view.getTag(R.id.native_radio_button_context);
        CustomTextView customTextView = (CustomTextView) view.getTag(R.id.native_radio_button_specify_textview);
        CustomTextView mainTextView = (CustomTextView) view.getTag(R.id.native_radio_button_main_textview);
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePickerDialog.setCalendarViewShown(false);
        }
        datePickerDialog.setContext(context);
        setDate(datePickerDialog, mainTextView);
        showDatePickerDialog((Activity) context, datePickerDialog, mainTextView);
        customTextView.setText(createSpecifyText(context.getResources().getString(R.string.radio_button_date_change)));
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


    private static void setDate(DatePickerDialog datePickerDialog, final CustomTextView customTextView) {
        final String[] arrayString = customTextView.getText().toString().split(":");
        datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarDate.set(Calendar.MONTH, monthOfYear);
                calendarDate.set(Calendar.YEAR, year);
                if (calendarDate.getTimeInMillis() >= view.getMinDate() && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                    customTextView.setText(arrayString[0] + ": " + DATE_FORMAT.format(calendarDate.getTime()));
                } else {
                    customTextView.setText(arrayString[0]);
                }
            }
        });
    }

    private static String createSpecifyText(String text) {
        return text == null || text.isEmpty() ? "" : "(" + text + ")";
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws Exception {
        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        FormUtils.createRadioButtonAndCheckBoxLabel(views, jsonObject, context, canvasIds, readOnly, listener);
        addRadioButtonOptionsElements(jsonObject, context, readOnly, canvasIds, stepName, views, listener);

        return views;
    }

    /**
     * Creates the Radio Button options from the JSON definitions
     *
     * @param jsonObject
     * @param context
     * @param readOnly
     * @param canvasIds
     * @param stepName
     * @param views
     * @param listener
     * @throws JSONException
     */
    protected void addRadioButtonOptionsElements(JSONObject jsonObject, Context context, Boolean readOnly, JSONArray canvasIds,
                                                 String stepName, List<View> views, CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

        ArrayList<RelativeLayout> radioButtonsLayout = new ArrayList<>();
        RadioGroup radioGroup = new RadioGroup(context);

        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            String specifyInfo = item.optString(JsonFormConstants.NATIVE_RADIO_SPECIFY_INFO, null);
            String extraInfo = item.optString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO, null);

            RelativeLayout radioGroupLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.native_item_radio_button, null);
            radioGroupLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            radioGroupLayout.setTag(R.id.openmrs_entity, openMrsEntity);
            radioGroupLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            radioGroupLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            radioGroupLayout.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            radioGroupLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

            createRadioButton(radioGroupLayout, jsonObject, readOnly, item, listener, stepName);
            createMainTextView(context, radioGroupLayout, jsonObject, item, stepName);
            if (specifyInfo != null) {
                createSpecifyTextView(context, radioGroupLayout, jsonObject, listener, item, stepName);
            }

            if (extraInfo != null) {
                createExtraInfo(context, radioGroupLayout, item, jsonObject, stepName);
            }

            ((JsonApi) context).addFormDataView(radioGroupLayout);

            canvasIds.put(radioGroupLayout.getId());
            radioGroup.addView(radioGroupLayout);
            radioButtonsLayout.add(radioGroupLayout);

            if (relevance != null && context instanceof JsonApi) {
                radioGroupLayout.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(radioGroupLayout);
            }
        }

        FormUtils.setRadioExclusiveClick(radioGroup);
        radioGroup.setLayoutParams(FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.extra_bottom_margin)));

        for (RelativeLayout relativeLayout : radioButtonsLayout) {
            relativeLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        }

        views.add(radioGroup);
    }

    private void createRadioButton(RelativeLayout rootLayout, JSONObject jsonObject, Boolean readOnly, JSONObject item,
                                   CommonListener listener, String stepName) throws JSONException {
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
        radioButton.setOnCheckedChangeListener(listener);
        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
                && jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
            radioButton.setChecked(true);
        }
        radioButton.setEnabled(!readOnly);
        radioButton.setFocusable(!readOnly);
    }

    private void createMainTextView(Context context, RelativeLayout rootLayout, JSONObject jsonObject, JSONObject item,
                                    String stepName) throws JSONException {
        CustomTextView mainTextView = rootLayout.findViewById(R.id.mainRadioButtonTextView);
        String optionTextColor = JsonFormConstants.DEFAULT_TEXT_COLOR;
        if (item.has(JsonFormConstants.TEXT_COLOR)) {
            optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
        }
        addTextViewAttributes(context, jsonObject, item, mainTextView, stepName, optionTextColor);
        mainTextView.setText(item.getString(JsonFormConstants.TEXT));
        setMainTextView(mainTextView);
    }

    private void createSpecifyTextView(Context context, RelativeLayout rootLayout, JSONObject jsonObject, CommonListener listener,
                                       JSONObject item, String stepName) throws JSONException {
        String text = item.getString(JsonFormConstants.NATIVE_RADIO_SPECIFY_INFO);
        String text_color = item.optString(JsonFormConstants.NATIVE_RADIO_SPECIFY_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);
        CustomTextView specifyTextView = rootLayout.findViewById(R.id.specifyTextView);
        specifyTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(context, jsonObject, item, specifyTextView, stepName, text_color);
        specifyTextView.setTag(R.id.radio_button_specify_type, JsonFormConstants.NATIVE_RADIO_SPECIFY_INFO);
        specifyTextView.setTag(R.id.native_radio_button_specify_textview, specifyTextView);
        specifyTextView.setTag(R.id.native_radio_button_main_textview, getMainTextView());
        specifyTextView.setTag(R.id.native_radio_button_context, context);
        specifyTextView.setText(createSpecifyText(text));
        specifyTextView.setOnClickListener(listener);
    }

    private void createExtraInfo(Context context, RelativeLayout rootLayout, JSONObject item, JSONObject jsonObject,
                                 String stepName) throws JSONException {
        String text = item.getString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO);
        String text_color = item.optString(JsonFormConstants.NATIVE_RADIO_EXTRA_INFO_COLOR, JsonFormConstants.DEFAULT_HINT_TEXT_COLOR);
        CustomTextView extraInfoTextView = rootLayout.findViewById(R.id.extraInfoTextView);
        extraInfoTextView.setVisibility(View.VISIBLE);
        addTextViewAttributes(context, jsonObject, item, extraInfoTextView, stepName, text_color);
        extraInfoTextView.setText(text);
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

package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.GenericDialogInterface;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.SecondaryValueModel;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class GenericPopupDialog extends DialogFragment implements GenericDialogInterface {
    private final String TAG = this.getClass().getSimpleName();
    private JsonFormInteractor jsonFormInteractor = JsonFormInteractor.getInstance();
    private FormUtils formUtils = new FormUtils();
    private JsonApi jsonApi;
    private Context context;
    private CommonListener commonListener;
    private JsonFormFragment formFragment;
    private String formIdentity;
    private String formLocation;
    private String parentKey;
    private String childKey = null;
    private String stepName;
    private String widgetType;
    private JSONArray secondaryValues;
    private JSONArray newSelectedValues;
    private CustomTextView customTextView;
    private CustomTextView popupReasonsTextView;
    private JSONArray subFormsFields;
    private Map<String, SecondaryValueModel> popAssignedValue = new HashMap<>();
    private Map<String, SecondaryValueModel> secondaryValuesMap = new HashMap<>();
    private String suffix = "";
    private Activity activity;
    private JSONArray specifyContent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
        jsonApi = (JsonApi) activity;
        jsonApi.setGenericPopup(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyVariables();
    }

    private void destroyVariables() {
        popAssignedValue = new HashMap<>();
        secondaryValuesMap = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            throw new IllegalStateException(
                    "The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }

        activity = (Activity) context;
        jsonApi = (JsonApi) activity;

        try {
            loadPartialSecondaryValues();
            createSecondaryValuesMap();
            loadSubForms();
            jsonApi.updateGenericPopupSecondaryValues(specifyContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    protected void loadSubForms() {
        if (!TextUtils.isEmpty(formIdentity)) {
            JSONObject subForm = null;
            try {
                subForm = FormUtils.getSubFormJson(formIdentity, formLocation, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (subForm != null) {
                try {
                    if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                        specifyContent = subForm.getJSONArray(JsonFormConstants.CONTENT_FORM);
                        setSubFormsFields(addFormValues(specifyContent));
                    } else {
                        Utils.showToast(context,
                                context.getApplicationContext().getResources().getString(R.string.please_specify_content));
                        GenericPopupDialog.this.dismiss();
                    }
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    protected void loadPartialSecondaryValues() throws JSONException {
        JSONArray fields = formUtils.getFormFields(getStepName(), context);
        if (fields != null && fields.length() > 0) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY).equals(parentKey) &&
                        item.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    JSONArray options = item.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int k = 0; k < options.length(); k++) {
                        JSONObject option = options.getJSONObject(k);
                        if (option != null && option.has(JsonFormConstants.KEY) &&
                                option.getString(JsonFormConstants.KEY).equals(childKey) &&
                                option.has(JsonFormConstants.SECONDARY_VALUE)) {
                            setSecondaryValues(option.getJSONArray(JsonFormConstants.SECONDARY_VALUE));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.native_form_generic_dialog, container, false);

        Button cancelButton;
        Button okButton;

        new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputManager =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager
                        .hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
            }
        };

        List<View> viewList = initiateViews();
        LinearLayout genericDialogContent = dialogView.findViewById(R.id.generic_dialog_content);
        for (View view : viewList) {
            genericDialogContent.addView(view);
        }

        cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonApi.updateGenericPopupSecondaryValues(null);
                GenericPopupDialog.this.dismiss();
            }
        });

        okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passData();
                jsonApi.updateGenericPopupSecondaryValues(null);
                GenericPopupDialog.this.dismiss();
            }
        });
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        jsonApi.invokeRefreshLogic(null, true, null, null);
        return dialogView;
    }

    protected List<View> initiateViews() {
        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, stepName, formFragment, specifyContent, commonListener, true);
        return listOfViews;
    }

    protected void passData() {
        onGenericDataPass(parentKey, stepName, childKey);
    }

    /**
     * Creates a secondary values map from the secondary values JSONArray on the widget
     */
    protected void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (secondaryValues != null) {
            for (int i = 0; i < secondaryValues.length(); i++) {
                try {
                    jsonObject = secondaryValues.getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    String type = jsonObject.getString(JsonFormConstants.TYPE);
                    JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                    JSONObject openmrsAttributes = new JSONObject();
                    if (jsonObject.has(JsonFormConstants.OPENMRS_ATTRIBUTES)) {
                        openmrsAttributes = jsonObject.getJSONObject(JsonFormConstants.OPENMRS_ATTRIBUTES);
                    }
                    JSONArray valueOpenMRSAttributes = new JSONArray();
                    if (jsonObject.has(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES)) {
                        valueOpenMRSAttributes = jsonObject.getJSONArray(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES);
                    }

                    secondaryValuesMap
                            .put(key, new SecondaryValueModel(key, type, values, openmrsAttributes, valueOpenMRSAttributes));
                    popAssignedValue = secondaryValuesMap;
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    protected JSONArray addFormValues(JSONArray jsonArray) {
        JSONArray subFormFields;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String key = jsonObject.getString(JsonFormConstants.KEY);
                if (secondaryValuesMap != null && secondaryValuesMap.containsKey(key)) {
                    SecondaryValueModel secondaryValueModel = secondaryValuesMap.get(key);
                    String type = secondaryValueModel.getType();
                    if (type != null && (type.equals(JsonFormConstants.CHECK_BOX))) {
                        if (jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                            JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                            JSONArray values = secondaryValueModel.getValues();
                            setCompoundButtonValues(options, values);
                        }
                    } else {
                        JSONArray values = secondaryValueModel.getValues();
                        if (type != null && type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                            for (int k = 0; k < values.length(); k++) {
                                jsonObject.put(JsonFormConstants.VALUE, getValueKey(values.getString(k)));
                            }
                        } else {
                            jsonObject.put(JsonFormConstants.VALUE, setValues(values, type));
                        }
                    }
                }
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
        subFormFields = jsonArray;
        return subFormFields;
    }

    protected void setCompoundButtonValues(JSONArray options, JSONArray secondValues) {
        for (int i = 0; i < options.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = options.getJSONObject(i);
                String mainKey = jsonObject.getString(JsonFormConstants.KEY);
                for (int j = 0; j < secondValues.length(); j++) {
                    String key = getValueKey(secondValues.getString(j));
                    if (mainKey.equals(key)) {
                        jsonObject.put(JsonFormConstants.VALUE, true);
                    }
                }
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
    }

    protected String setValues(JSONArray jsonArray, String type) {
        FormUtils formUtils = new FormUtils();
        String value = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                value = formUtils.getValueFromSecondaryValues(type, jsonArray.getString(i));
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }

        return value.replaceAll(", $", "");
    }

    protected String getValueKey(String value) {
        String key = "";
        String[] strings = value.split(":");
        if (strings.length > 0) {
            key = strings[0];
        }
        return key;
    }

    public JSONArray getSecondaryValues() {
        return secondaryValues;
    }

    public void setSecondaryValues(JSONArray secondaryValues) {
        this.secondaryValues = secondaryValues;
    }

    protected String[] getWidgetType(String value) {
        return value.split(";");
    }

    @Override
    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getChildKey() {
        return childKey;
    }

    public void setChildKey(String childKey) {
        this.childKey = childKey;
    }

    public void setCustomTextView(CustomTextView customTextView) {
        this.customTextView = customTextView;
    }

    public void setPopupReasonsTextView(CustomTextView popupReasonsTextView) {
        this.popupReasonsTextView = popupReasonsTextView;
    }

    /**
     * Receives the generic popup data from Generic Dialog fragment
     *
     * @param parentKey
     * @param stepName
     * @param childKey
     */
    public void onGenericDataPass(String parentKey, String stepName, String childKey) {
        JSONObject mJSONObject = jsonApi.getmJSONObject();
        if (mJSONObject != null) {
            JSONArray fields = formUtils.getFormFields(stepName, context);
            JSONObject item;
            try {
                if (fields.length() > 0) {
                    for (int i = 0; i < fields.length(); i++) {
                        item = fields.getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addSecondaryValues(getJsonObjectToUpdate(item, childKey));
                        }
                    }
                }

                if (newSelectedValues.length() > 0 && customTextView != null && popupReasonsTextView != null) {
                    customTextView.setText("(" + getString(R.string.radio_button_tap_to_change) + ")");
                    popupReasonsTextView.setVisibility(View.VISIBLE);
                    popupReasonsTextView.setText(
                            JsonFormConstants.SECONDARY_PREFIX + formUtils.getSpecifyText(newSelectedValues) + " " + suffix);
                }
                jsonApi.setmJSONObject(mJSONObject);

            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
    }

    /**
     * Finds the actual widget to be updated and secondary values added on
     *
     * @param jsonObject
     * @param childKey
     * @return item {@link JSONObject}
     */
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) ||
                        jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) &&
                        childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) &&
                                    childKey.equals(childItem.getString(JsonFormConstants.KEY))) {
                                item = childItem;
                            }
                        }
                    }
                } else {
                    item = jsonObject;
                }
            } else {
                item = jsonObject;
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return item;
    }

    /**
     * Adding the secondary values on to the specific json widget
     *
     * @param item
     */
    protected void addSecondaryValues(JSONObject item) throws JSONException {
        JSONArray secondaryValuesArray = createValues();
        try {
            item.put(JsonFormConstants.SECONDARY_VALUE, secondaryValuesArray);

            if (item.has(JsonFormConstants.SECONDARY_SUFFIX)) {
                suffix = item.getString(JsonFormConstants.SECONDARY_SUFFIX);
            }
            newSelectedValues = secondaryValuesArray;
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    protected JSONArray createValues() throws JSONException {
        JSONArray selectedValues = new JSONArray();
        JSONArray formFields = getSubFormsFields();
        for (int i = 0; i < formFields.length(); i++) {
            JSONObject field = formFields.getJSONObject(i);
            JSONArray valueOpenMRSAttributes = new JSONArray();
            JSONObject openMRSAttributes = getFieldOpenMRSAttributes(field);
            String key = field.getString(JsonFormConstants.KEY);
            String type = field.getString(JsonFormConstants.TYPE);
            JSONArray values = new JSONArray();
            if (JsonFormConstants.CHECK_BOX.equals(field.getString(JsonFormConstants.TYPE)) &&
                    field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                values = getOptionsValueCheckBox(field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME));
                getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
            } else if ((JsonFormConstants.ANC_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE)) ||
                    JsonFormConstants.NATIVE_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE))) &&
                    field.has(JsonFormConstants.OPTIONS_FIELD_NAME) && field.has(JsonFormConstants.VALUE)) {
                values.put(getOptionsValueRadioButton(field.optString(JsonFormConstants.VALUE),
                        field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME)));
                getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
            } else if (JsonFormConstants.SPINNER.equals(field.getString(JsonFormConstants.TYPE)) && field
                    .has(JsonFormConstants.VALUE)) {
                values.put(field.optString(JsonFormConstants.VALUE));
                getSpinnerValueOpenMRSAttributes(field, valueOpenMRSAttributes);
            } else {
                if (field.has(JsonFormConstants.VALUE)) {
                    values.put(field.optString(JsonFormConstants.VALUE));
                }
            }

            selectedValues.put(createSecondaryValueObject(key, type, values, openMRSAttributes, valueOpenMRSAttributes));
        }

        return selectedValues;
    }

    protected void getOptionsOpenMRSAttributes(JSONObject item, JSONArray valueOpenMRSAttributes) throws JSONException {
        JSONArray options = item.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        if (options.length() > 0) {
            for (int i = 0; i < options.length(); i++) {
                JSONObject itemOption = options.getJSONObject(i);
                if ((JsonFormConstants.NATIVE_RADIO_BUTTON.equals(item.getString(JsonFormConstants.TYPE)) ||
                        JsonFormConstants.ANC_RADIO_BUTTON.equals(item.getString(JsonFormConstants.TYPE))) &&
                        item.has(JsonFormConstants.VALUE)) {
                    String value = item.optString(JsonFormConstants.VALUE);
                    if (itemOption.has(JsonFormConstants.KEY) && value.equals(itemOption.getString(JsonFormConstants.KEY))) {
                        extractOptionOpenMRSAttributes(valueOpenMRSAttributes, itemOption,
                                item.getString(JsonFormConstants.KEY));
                    }
                } else if (JsonFormConstants.CHECK_BOX.equals(item.getString(JsonFormConstants.TYPE)) &&
                        itemOption.has(JsonFormConstants.VALUE) &&
                        JsonFormConstants.TRUE.equals(itemOption.getString(JsonFormConstants.VALUE))) {
                    extractOptionOpenMRSAttributes(valueOpenMRSAttributes, itemOption,
                            item.getString(JsonFormConstants.KEY));
                }
            }
        }
    }

    /**
     * Extracts the openmrs attributes of the Radio button & check box components on popups.
     *
     * @param valueOpenMRSAttributes {@link JSONArray}
     * @param itemOption             {@link JSONObject}
     * @param itemKey                {@link String}
     * @throws JSONException
     */
    protected void extractOptionOpenMRSAttributes(JSONArray valueOpenMRSAttributes, JSONObject itemOption, String itemKey)
            throws JSONException {
        if (itemOption.has(JsonFormConstants.OPENMRS_ENTITY_PARENT) && itemOption.has(JsonFormConstants.OPENMRS_ENTITY) &&
                itemOption.has(JsonFormConstants.OPENMRS_ENTITY_ID)) {
            String openmrsEntityParent = itemOption.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
            String openmrsEntity = itemOption.getString(JsonFormConstants.OPENMRS_ENTITY);
            String openmrsEntityId = itemOption.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

            JSONObject valueOpenMRSObject = new JSONObject();
            valueOpenMRSObject.put(JsonFormConstants.KEY, itemKey);
            valueOpenMRSObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openmrsEntityParent);
            valueOpenMRSObject.put(JsonFormConstants.OPENMRS_ENTITY, openmrsEntity);
            valueOpenMRSObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, openmrsEntityId);

            valueOpenMRSAttributes.put(valueOpenMRSObject);
        }
    }

    protected String getOptionsValueRadioButton(String value, JSONArray options) throws JSONException {
        String secondaryValue = "";
        if (!TextUtils.isEmpty(value)) {
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                if (option.has(JsonFormConstants.KEY) && value.equals(option.getString(JsonFormConstants.KEY))) {
                    String key = option.getString(JsonFormConstants.KEY);
                    String text = option.getString(JsonFormConstants.TEXT);
                    secondaryValue = key + ":" + text;
                }
            }
        }
        return secondaryValue;
    }

    protected JSONArray getOptionsValueCheckBox(JSONArray options) throws JSONException {
        JSONArray secondaryValues = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (option.has(JsonFormConstants.KEY) && option.has(JsonFormConstants.VALUE) &&
                    JsonFormConstants.TRUE.equals(option.getString(JsonFormConstants.VALUE))) {
                String key = option.getString(JsonFormConstants.KEY);
                String text = option.getString(JsonFormConstants.TEXT);
                String secondaryValue = key + ":" + text + ":" + "true";
                secondaryValues.put(secondaryValue);
            }
        }
        return secondaryValues;
    }

    protected void getSpinnerValueOpenMRSAttributes(JSONObject item, JSONArray valueOpenMRSAttributes) throws JSONException {
        if (item.equals(JsonFormConstants.SPINNER) && item.has(JsonFormConstants.OPENMRS_CHOICE_IDS)) {
            JSONObject openMRSChoiceIds = item.getJSONObject(JsonFormConstants.OPENMRS_CHOICE_IDS);
            String value = item.getString(JsonFormConstants.VALUE);
            Iterator<String> keys = openMRSChoiceIds.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (value.equals(key)) {
                    JSONObject jsonObject = new JSONObject();
                    String optionOpenMRSConceptId = openMRSChoiceIds.get(key).toString();
                    jsonObject.put(JsonFormConstants.KEY, item.getString(JsonFormConstants.KEY));
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT,
                            item.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT));
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, item.getString(JsonFormConstants.OPENMRS_ENTITY));
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, optionOpenMRSConceptId);

                    valueOpenMRSAttributes.put(jsonObject);
                }

            }
        }
    }

    protected JSONObject getFieldOpenMRSAttributes(JSONObject item) throws JSONException {
        JSONObject openMRSAttribute = new JSONObject();
        openMRSAttribute
                .put(JsonFormConstants.OPENMRS_ENTITY_PARENT, item.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT));
        openMRSAttribute.put(JsonFormConstants.OPENMRS_ENTITY, item.getString(JsonFormConstants.OPENMRS_ENTITY));
        openMRSAttribute.put(JsonFormConstants.OPENMRS_ENTITY_ID, item.getString(JsonFormConstants.OPENMRS_ENTITY_ID));
        return openMRSAttribute;
    }

    /**
     * @param key
     * @param type
     * @param values
     * @param openMRSAttributes
     * @param valueOpenMRSAttributes
     * @return
     */
    protected JSONObject createSecondaryValueObject(String key, String type, JSONArray values, JSONObject openMRSAttributes,
                                                    JSONArray valueOpenMRSAttributes) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (values.length() > 0) {
                jsonObject.put(JsonFormConstants.KEY, key);
                jsonObject.put(JsonFormConstants.TYPE, type);
                jsonObject.put(JsonFormConstants.VALUES, values);
                jsonObject.put(JsonFormConstants.OPENMRS_ATTRIBUTES, openMRSAttributes);
                if (valueOpenMRSAttributes.length() > 0) {
                    jsonObject.put(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES, valueOpenMRSAttributes);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));

        }
        return jsonObject;
    }

    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }

    public JSONArray getNewSelectedValues() {
        return newSelectedValues;
    }

    public void setNewSelectedValues(JSONArray newSelectedValues) {
        this.newSelectedValues = newSelectedValues;
    }

    public Map<String, SecondaryValueModel> getPopAssignedValue() {
        return popAssignedValue;
    }

    public void setPopAssignedValue(Map<String, SecondaryValueModel> popAssignedValue) {
        this.popAssignedValue = popAssignedValue;
    }

    public Map<String, SecondaryValueModel> getSecondaryValuesMap() {
        return secondaryValuesMap;
    }

    public void setSecondaryValuesMap(Map<String, SecondaryValueModel> secondaryValuesMap) {
        this.secondaryValuesMap = secondaryValuesMap;
    }

    public JsonApi getJsonApi() {
        return jsonApi;
    }

    public void setJsonApi(JsonApi jsonApi) {
        this.jsonApi = jsonApi;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public CommonListener getCommonListener() {
        return commonListener;
    }

    public void setCommonListener(CommonListener commonListener) {
        this.commonListener = commonListener;
    }

    public JsonFormFragment getFormFragment() {
        return formFragment;
    }

    public void setFormFragment(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
    }

    public JSONArray getSpecifyContent() {
        return specifyContent;
    }

    public void setSpecifyContent(JSONArray specifyContent) {
        this.specifyContent = specifyContent;
    }

    public String getFormIdentity() {
        return formIdentity;
    }

    @Override
    public void setFormIdentity(String formIdentity) {
        this.formIdentity = formIdentity;
    }

    public String getFormLocation() {
        return formLocation;
    }

    @Override
    public void setFormLocation(String formLocation) {
        this.formLocation = formLocation;
    }

    public void setContext(Context context) throws IllegalStateException {
        this.context = context;
    }

    public JSONArray getSubFormsFields() {
        return subFormsFields;
    }

    public void setSubFormsFields(JSONArray subFormsFields) {
        this.subFormsFields = subFormsFields;
    }
}

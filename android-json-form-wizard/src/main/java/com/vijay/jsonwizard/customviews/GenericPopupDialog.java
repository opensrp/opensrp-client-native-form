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
    private static JsonFormInteractor jsonFormInteractor = JsonFormInteractor.getInstance();
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
    private Map<String, SecondaryValueModel> popAssignedValue = new HashMap<>();
    private Map<String, SecondaryValueModel> secondaryValuesMap = new HashMap<>();

    private JSONArray specifyContent;
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        jsonApi = (JsonApi) activity;
        jsonApi.invokeRefreshLogic(null, true, null, null);
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
            throw new IllegalStateException("The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }
        jsonApi = (JsonApi) context;

        try {
            loadPartialSecondaryValues();
            createSecondaryValuesMap();
            loadSubForms();
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
                        addFormValues(specifyContent);
                    } else {
                        Utils.showToast(context, context.getApplicationContext().getResources().getString(R.string.please_specify_content));
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
                if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY).equals(parentKey) && item.has
                        (JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    JSONArray options = item.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int k = 0; k < options.length(); k++) {
                        JSONObject option = options.getJSONObject(k);
                        if (option != null && option.has(JsonFormConstants.KEY) && option.getString(JsonFormConstants.KEY).equals
                                (childKey) && option.has(JsonFormConstants.SECONDARY_VALUE)) {
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
        jsonApi.invokeRefreshLogic(null, true, null, null);
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

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
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
            }
        };

        initiateViews(dialogView);

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
        return dialogView;
    }

    protected void initiateViews(ViewGroup dialogView) {
        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, stepName, formFragment, specifyContent, commonListener, true);

        LinearLayout genericDialogContent = dialogView.findViewById(R.id.generic_dialog_content);
        for (View view : listOfViews) {
            genericDialogContent.addView(view);
        }
    }


    protected void passData() {
        onGenericDataPass(popAssignedValue, parentKey, stepName, childKey);
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
                    secondaryValuesMap.put(key, new SecondaryValueModel(key, type, values));
                    popAssignedValue = secondaryValuesMap;
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    protected void addFormValues(JSONArray jsonArray) {
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

    @Override
    public void addSelectedValues(Map<String, String> newValue) {
        if (newValue != null) {
            Iterator newValueIterator = newValue.entrySet().iterator();
            String key = "";
            String type = "";
            String iteratorValue = "";
            String value = "";
            while (newValueIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) newValueIterator.next();
                key = String.valueOf(pair.getKey());
                iteratorValue = String.valueOf(pair.getValue());
            }

            String[] widgetValues = getWidgetType(iteratorValue);
            if (widgetValues.length > 1) {
                type = widgetValues[1];
                value = widgetValues[0];
            }

            createSecondaryValues(key, type, value);
        }
    }

    protected void createSecondaryValues(String key, String type, String value) {
        JSONArray values = new JSONArray();
        values.put(value);
        if (type != null && type.equals(JsonFormConstants.CHECK_BOX)) {
            if (popAssignedValue != null && popAssignedValue.containsKey(key)) {
                SecondaryValueModel valueModel = popAssignedValue.get(key);
                if (valueModel != null) {
                    JSONArray jsonArray = valueModel.getValues();
                    if (!checkSimilarity(jsonArray, value)) {
                        jsonArray.put(value);
                    }

                    valueModel.setValues(removeUnselectedItems(jsonArray, value));
                }
            } else {
                if (popAssignedValue != null) {
                    popAssignedValue.put(key, new SecondaryValueModel(key, type, values));
                }
            }
        } else {
            popAssignedValue.put(key, new SecondaryValueModel(key, type, values));
        }
    }

    protected boolean checkSimilarity(JSONArray values, String value) {
        boolean same = false;
        try {
            for (int i = 0; i < values.length(); i++) {
                String currentValue = values.getString(i);
                if (currentValue.equals(value)) {
                    same = true;
                    break;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return same;
    }

    protected JSONArray removeUnselectedItems(JSONArray jsonArray, String currentValue) {
        JSONArray values;
        ArrayList<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            for (int k = 0; k < list.size(); k++) {
                String value = list.get(k);
                String[] splitValues = value.split(":");
                String[] currentValues = currentValue.split(":");
                if (splitValues.length == 3 && currentValues.length == 3 && splitValues[0].equals(currentValues[0]) && splitValues[1]
                        .equals(currentValues[1]) && currentValues[2].equals("false")) {
                    list.remove(k);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        values = new JSONArray(list);
        return values;
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
     * @param selectedValues
     * @param parentKey
     * @param stepName
     * @param childKey
     */
    public void onGenericDataPass(Map<String, SecondaryValueModel> selectedValues, String parentKey, String stepName, String childKey) {

        JSONObject mJSONObject = jsonApi.getmJSONObject();
        if (mJSONObject != null) {
            JSONArray fields = formUtils.getFormFields(stepName, context);
            try {
                if (fields.length() > 0) {
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject item = fields.getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addSecondaryValues(getJsonObjectToUpdate(item, childKey), selectedValues);
                        }
                    }
                }

                if (newSelectedValues.length() > 0 && customTextView != null && popupReasonsTextView != null) {
                    customTextView.setText("(" + getString(R.string.radio_button_tap_to_change) + ")");
                    popupReasonsTextView.setVisibility(View.VISIBLE);
                    popupReasonsTextView.setText("(" + formUtils.getSpecifyText(newSelectedValues) + ")");
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
     * @return
     */
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || jsonObject.getString
                        (JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) && childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) && childKey.equals(childItem.getString
                                    (JsonFormConstants.KEY))) {
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
     * @param secondaryValueModel
     */
    protected void addSecondaryValues(JSONObject item, Map<String, SecondaryValueModel> secondaryValueModel) {
        JSONObject valueObject;
        JSONArray secondaryValuesArray = new JSONArray();
        SecondaryValueModel secondaryValue;
        for (Object object : secondaryValueModel.entrySet()) {
            Map.Entry pair = (Map.Entry) object;
            secondaryValue = (SecondaryValueModel) pair.getValue();
            valueObject = createSecondaryValueObject(secondaryValue);
            secondaryValuesArray.put(valueObject);
        }
        try {
            item.put(JsonFormConstants.SECONDARY_VALUE, secondaryValuesArray);
            newSelectedValues = secondaryValuesArray;
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Creates the secondary values objects
     *
     * @param value
     * @return
     */
    protected JSONObject createSecondaryValueObject(SecondaryValueModel value) {
        JSONObject jsonObject = new JSONObject();
        try {
            String key = value.getKey();
            String type = value.getType();
            JSONArray values = value.getValues();

            jsonObject.put(JsonFormConstants.KEY, key);
            jsonObject.put(JsonFormConstants.TYPE, type);
            jsonObject.put(JsonFormConstants.VALUES, values);
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

}

package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.GenericPopupInterface;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.SecondaryValueModel;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class GenericPopupDialog extends DialogFragment {
    private static JsonFormInteractor jsonFormInteractor = JsonFormInteractor.getInstance();
    private static GenericPopupDialog genericPopupDialog = new GenericPopupDialog();
    private Activity activity;
    private JsonApi jsonApi;
    private Context context;
    private CommonListener commonListener;
    private JsonFormFragment formFragment;
    private String formIdentity;
    private String formLocation;
    private String parentKey;
    private String childKey = null;
    private String stepName;
    private JSONArray secondaryValues;
    private Map<String, SecondaryValueModel> popAssignedValue = new HashMap<>();
    private Map<String, SecondaryValueModel> secondaryValuesMap = new HashMap<>();
    private GenericPopupInterface genericPopupInterface;
    private JSONArray specifyContent;
    private String TAG = this.getClass().getSimpleName();

    public static GenericPopupDialog getInstance() {
        return genericPopupDialog;
    }

    public void setContext(Context context) throws IllegalStateException {
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        genericPopupInterface = (GenericPopupInterface) context;
        activity = (Activity) context;
        jsonApi = (JsonApi) activity;
        jsonApi.refreshSkipLogic(null, null, true);
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


        activity = (Activity) context;
        jsonApi = (JsonApi) activity;

        createSecondaryValuesMap();
        JSONObject subForm = getSubFormJson(formLocation, context);
        if (subForm != null) {
            try {
                if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                    specifyContent = subForm.getJSONArray(JsonFormConstants.CONTENT_FORM);
                    addFormValues(specifyContent);
                } else {
                    Utils.showToast(activity, activity.getApplicationContext().getResources().getString(R.string.please_specify_content));
                    GenericPopupDialog.this.dismiss();
                }
            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }

        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        jsonApi.refreshSkipLogic(null, null, true);
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
                inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                        HIDE_NOT_ALWAYS);
            }
        };

        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, stepName, formFragment, specifyContent, commonListener, true);

        LinearLayout genericDialogContent = dialogView.findViewById(
                R.id.generic_dialog_content);
        for (View view : listOfViews) {
            genericDialogContent.addView(view);
        }

        cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genericPopupInterface.clearSecondaryFields();
                GenericPopupDialog.this.dismiss();
            }
        });

        okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passData();
                GenericPopupDialog.this.dismiss();
            }
        });

        return dialogView;
    }


    public JSONObject getSubFormJson(String subFormsLocation, Context context) {
        String defaultSubFormLocation = JsonFormConstants.DEFAULT_SUB_FORM_LOCATION;
        if (subFormsLocation != null && !subFormsLocation.equals("")) {
            defaultSubFormLocation = subFormsLocation;
        }

        try {
            return new JSONObject(loadSubForm(defaultSubFormLocation, context));
        } catch (JSONException e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    private void passData() {
        genericPopupInterface.onGenericDataPass(popAssignedValue, parentKey, stepName, childKey);
    }

    private String loadSubForm(String defaultSubFormLocation, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(defaultSubFormLocation + "/" + formIdentity + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String jsonString;
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return stringBuilder.toString();
    }

    private void createSecondaryValuesMap() {
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

    private void addFormValues(JSONArray jsonArray) {
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

    private void setCompoundButtonValues(JSONArray options, JSONArray secondValues) {
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

    private String setValues(JSONArray jsonArray, String type) {
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

    private String getValueKey(String value) {
        String key = "";
        String[] strings = value.split(":");
        if (strings.length > 0) {
            key = strings[0];
        }
        return key;
    }

    public void setCommonListener(CommonListener commonListener) {
        this.commonListener = commonListener;
    }

    public void setFormFragment(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
    }

    public void setFormIdentity(String formIdentity) {
        this.formIdentity = formIdentity;
    }

    public void setFormLocation(String formLocation) {
        this.formLocation = formLocation;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public JSONArray getSecondaryValues() {
        return secondaryValues;
    }

    public void setSecondaryValues(JSONArray secondaryValues) {
        this.secondaryValues = secondaryValues;
    }

    public void addSelectedValues(Map<String, String> newValue) {
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

    private void createSecondaryValues(String key, String type, String value) {
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

    private boolean checkSimilarity(JSONArray values, String value) {
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

    private String[] getWidgetType(String value) {
        return value.split(";");
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
}

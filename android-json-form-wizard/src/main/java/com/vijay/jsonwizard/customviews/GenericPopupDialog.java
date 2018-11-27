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
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.SecondaryValueModel;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.views.GenericPopupDialogInterface;

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
import java.util.List;
import java.util.Map;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class GenericPopupDialog extends DialogFragment {
    private static JsonFormInteractor jsonFormInteractor = new JsonFormInteractor();
    private Context context;
    private CommonListener commonListener;
    private JsonFormFragment formFragment;
    private String formIdentity;
    private String formLocation;
    private String stepName;
    private JSONArray secondValues;
    private Map<String, String> popAssignedValue = new HashMap<>();
    private Map<String, SecondaryValueModel> secondaryValuesMap = new HashMap<>();
    private GenericPopupDialogInterface genericPopupDialogInterface;
    private String TAG = this.getClass().getSimpleName();
    // private Map<String, String> loadedSubForms = new HashMap<>();

    public void setContext(Context context) throws IllegalStateException {
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        genericPopupDialogInterface = (GenericPopupDialogInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (context == null) {
            throw new IllegalStateException("The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.native_form_generic_dialog, container, false);

        Button cancelButton;
        Button okButton;
        JSONArray specifyContent = null;
        Activity activity = (Activity) context;

        JSONObject subForm = getSubFormJson(formLocation, context);
        if (subForm != null) {
            try {
                if (subForm.has(JsonFormConstants.SPECIFY_CONTENT)) {
                    specifyContent = subForm.getJSONArray(JsonFormConstants.SPECIFY_CONTENT);
                    createSecondaryValuesMap();
                    addSecondaryValues(specifyContent);
                } else {
                    Utils.showToast(activity, activity.getApplicationContext().getResources().getString(R.string.please_specify_content));
                    GenericPopupDialog.this.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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

        JsonApi jsonApi = (JsonApi) activity;
        jsonApi.refreshSkipLogic(null, null, true);

        cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericPopupDialog.this.dismiss();
            }
        });

        okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericPopupDialog.this.dismiss();
                passData();
            }
        });

        return dialogView;
    }

    public JSONObject getSubFormJson(String subFormsLocation, Context context) {
        String defaultSubFormLocation = "json/sub_form";
        if (subFormsLocation != null && !subFormsLocation.equals("")) {
            defaultSubFormLocation = subFormsLocation;
        }

        try {
            return new JSONObject(loadSubForm(defaultSubFormLocation, context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void passData() {
        genericPopupDialogInterface.onGenericDataPass(popAssignedValue);
    }

    private String loadSubForm(String defaultSubFormLocation, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(defaultSubFormLocation + "/" + formIdentity + ".json");
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));

            String jsonString;
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (secondValues != null) {
            for (int i = 0; i < secondValues.length(); i++) {
                try {
                    jsonObject = secondValues.getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    String type = jsonObject.optString(JsonFormConstants.TYPE, null);
                    JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                    secondaryValuesMap.put(key, new SecondaryValueModel(key, type, values));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addSecondaryValues(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String key = jsonObject.getString(JsonFormConstants.KEY);
                if (secondaryValuesMap.containsKey(key)) {
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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

    public Map<String, String> getPopAssignedValue() {
        return popAssignedValue;
    }

    public void setPopAssignedValue(Map<String, String> popAssignedValue) {
        this.popAssignedValue = popAssignedValue;
    }

    public JSONArray getSecondValues() {
        return secondValues;
    }

    public void setSecondValues(JSONArray secondValues) {
        this.secondValues = secondValues;
    }

    public void addSelectedValues(Map<String, String> newValue) {
        Map<String, String> selectedValues = getPopAssignedValue();
        String values = String.valueOf(newValue.values());
        Log.i(TAG, values);
    }
}

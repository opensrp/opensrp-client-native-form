package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class GenericPopupDialog extends DialogFragment implements GenericDialogInterface {
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
    private JSONArray mainFormFields;
    private Map<String, SecondaryValueModel> popAssignedValue = new HashMap<>();
    private Map<String, SecondaryValueModel> secondaryValuesMap = new HashMap<>();
    private String suffix = "";
    private Activity activity;
    private JSONArray specifyContent;
    private List<View> viewList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
        jsonApi = (JsonApi) activity;
        jsonApi.setGenericPopup(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            throw new IllegalStateException(
                    "The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }

        activity = (Activity) context;
        setJsonApi((JsonApi) activity);

        try {
            setMainFormFields(formUtils.getFormFields(getStepName(), context));
            loadPartialSecondaryValues();
            createSecondaryValuesMap();
            loadSubForms();
            getJsonApi().updateGenericPopupSecondaryValues(specifyContent);
        } catch (JSONException e) {
            Timber.e(e, " --> onCreate");
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    protected void loadPartialSecondaryValues() throws JSONException {
        if (getMainFormFields() != null && getMainFormFields().length() > 0) {
            for (int i = 0; i < getMainFormFields().length(); i++) {
                JSONObject item = getMainFormFields().getJSONObject(i);
                if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY).equals(parentKey) && item.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    JSONArray options = item.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int k = 0; k < options.length(); k++) {
                        JSONObject option = options.getJSONObject(k);
                        if (option != null && option.has(JsonFormConstants.KEY) && option.getString(JsonFormConstants.KEY).equals(childKey) && option.has(JsonFormConstants.SECONDARY_VALUE)) {
                            setSecondaryValues(option.getJSONArray(JsonFormConstants.SECONDARY_VALUE));
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a secondary values map from the secondary values JSONArray on the widget
     */
    protected void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (getSecondaryValues() != null) {
            for (int i = 0; i < getSecondaryValues().length(); i++) {
                try {
                    jsonObject = getSecondaryValues().getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    String type = jsonObject.getString(JsonFormConstants.TYPE);
                    JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);

                    JSONObject openmrsAttributes = formUtils.getSecondaryOpenMRSAttributes(jsonObject);
                    JSONArray valueOpenMRSAttributes = formUtils.getValueOpenMRSAttributes(jsonObject);

                    getSecondaryValuesMap().put(key, new SecondaryValueModel(key, type, values, openmrsAttributes, valueOpenMRSAttributes));
                    setPopAssignedValue(getSecondaryValuesMap());
                } catch (JSONException e) {
                    Timber.e(e, "GenericPopupDialog --> createSecondaryValuesMap");
                }
            }
        }
    }

    protected void loadSubForms() {
        if (!TextUtils.isEmpty(getFormIdentity())) {
            JSONObject subForm = getSubForm();
            if (subForm != null) {
                try {
                    if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                        setSpecifyContent(subForm.getJSONArray(JsonFormConstants.CONTENT_FORM));
                        setSubFormsFields(addFormValues(getSpecifyContent()));
                    } else {
                        Utils.showToast(context,
                                context.getApplicationContext().getResources().getString(R.string.please_specify_content));
                        GenericPopupDialog.this.dismiss();
                    }
                } catch (JSONException e) {
                    Timber.e(e, "GenericPopupDialog --> loadSubForms");
                }
            }
        }
    }

    public JsonApi getJsonApi() {
        return jsonApi;
    }

    public void setJsonApi(JsonApi jsonApi) {
        this.jsonApi = jsonApi;
    }

    public JSONArray getMainFormFields() {
        return mainFormFields;
    }

    public void setMainFormFields(JSONArray mainFormFields) {
        this.mainFormFields = mainFormFields;
    }

    public JSONArray getSecondaryValues() {
        return secondaryValues;
    }

    public void setSecondaryValues(JSONArray secondaryValues) {
        this.secondaryValues = secondaryValues;
    }

    public Map<String, SecondaryValueModel> getSecondaryValuesMap() {
        return secondaryValuesMap;
    }

    public void setSecondaryValuesMap(Map<String, SecondaryValueModel> secondaryValuesMap) {
        this.secondaryValuesMap = secondaryValuesMap;
    }

    @Nullable
    protected JSONObject getSubForm() {
        JSONObject subForm = new JSONObject();
        try {
            subForm = FormUtils.getSubFormJson(getFormIdentity(), getFormLocation(), context);
            Utils.updateSubFormFields(subForm, getJsonApi().form());

        } catch (Exception e) {
            Timber.e(e, "GenericPopupDialog --> getSubForm");
        }
        return subForm;
    }

    protected JSONArray addFormValues(JSONArray formValues) {
        JSONArray subFormFields;
        for (int i = 0; i < formValues.length(); i++) {
            JSONObject formValue;
            try {
                formValue = formValues.getJSONObject(i);
                String key = formValue.getString(JsonFormConstants.KEY);
                if (getSecondaryValuesMap() != null && getSecondaryValuesMap().containsKey(key)) {
                    SecondaryValueModel secondaryValueModel = getSecondaryValuesMap().get(key);
                    String type = secondaryValueModel.getType();
                    if (type != null && (type.equals(JsonFormConstants.CHECK_BOX))) {
                        if (formValue.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                            JSONArray options = formValue.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                            JSONArray values = secondaryValueModel.getValues();
                            formUtils.setCompoundButtonValues(options, values);
                        }
                    } else {
                        JSONArray values = secondaryValueModel.getValues();
                        if (type != null && type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                            for (int k = 0; k < values.length(); k++) {
                                formValue.put(JsonFormConstants.VALUE, formUtils.getValueKey(values.getString(k)));
                            }
                        } else {
                            formValue.put(JsonFormConstants.VALUE, formUtils.setValues(values, type));
                        }
                    }
                }
            } catch (JSONException e) {
                Timber.e(e, "GenericPopupDialog --> addFormValues");
            }
        }
        subFormFields = formValues;
        return subFormFields;
    }

    public JSONArray getSpecifyContent() {
        return specifyContent;
    }

    public void setSpecifyContent(JSONArray specifyContent) {
        this.specifyContent = specifyContent;
    }

    public String getFormLocation() {
        return formLocation;
    }

    @Override
    public void setFormLocation(String formLocation) {
        this.formLocation = formLocation;
    }

    @Override
    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.native_form_generic_dialog, container, false);

        attachOnShowListener();
        LinearLayout genericDialogContent = dialogView.findViewById(R.id.generic_dialog_content);
        for (View view : getViewList()) {
            genericDialogContent.addView(view);
        }

        attachDialogCancelButton(dialogView);
        attachDialogOkButton(dialogView);

        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        setViewList(initiateViews());
        getJsonApi().invokeRefreshLogic(null, true, null, null,stepName);
        return dialogView;
    }

    private void attachOnShowListener() {
        new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputManager =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager
                        .hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
            }
        };
    }

    protected List<View> initiateViews() {
        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, getStepName(), getFormFragment(), getSpecifyContent(), getCommonListener(), true);
        return listOfViews;
    }

    private void attachDialogCancelButton(ViewGroup dialogView) {
        Button cancelButton;
        cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormFragment(null);
                setFormIdentity(null);
                setFormLocation(null);
                setContext(null);
                getJsonApi().setGenericPopup(null);
                getJsonApi().updateGenericPopupSecondaryValues(null);
                GenericPopupDialog.this.dismiss();
            }
        });
    }

    private void attachDialogOkButton(ViewGroup dialogView) {
        Button okButton;
        okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passData();
                getJsonApi().setGenericPopup(null);
                getJsonApi().updateGenericPopupSecondaryValues(null);
                GenericPopupDialog.this.dismiss();
            }
        });
    }

    public JsonFormFragment getFormFragment() {
        return formFragment;
    }

    public void setFormFragment(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
    }

    public CommonListener getCommonListener() {
        return commonListener;
    }

    public void setCommonListener(CommonListener commonListener) {
        this.commonListener = commonListener;
    }

    public void setContext(Context context) throws IllegalStateException {
        this.context = context;
    }

    protected void passData() {
        onGenericDataPass(getParentKey(), getChildKey());
    }

    /**
     * Receives the generic popup data from Generic Dialog fragment
     *
     * @param parentKey
     * @param childKey
     */
    public void onGenericDataPass(String parentKey, String childKey) {
        JSONObject mJSONObject = getJsonApi().getmJSONObject();
        if (mJSONObject != null) {
            JSONObject item;
            try {
                if (getMainFormFields().length() > 0) {
                    for (int i = 0; i < getMainFormFields().length(); i++) {
                        item = getMainFormFields().getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addSecondaryValues(getJsonObjectToUpdate(item, childKey));
                        }
                    }
                }

                if (getNewSelectedValues().length() > 0 && customTextView != null && popupReasonsTextView != null) {
                    customTextView.setText("(" + getString(R.string.radio_button_tap_to_change) + ")");
                    popupReasonsTextView.setVisibility(View.VISIBLE);
                    popupReasonsTextView.setText(
                            JsonFormConstants.SECONDARY_PREFIX + formUtils
                                    .getSpecifyText(getNewSelectedValues()) + " " + suffix);
                }
                getJsonApi().setmJSONObject(mJSONObject);

            } catch (JSONException e) {
                Timber.e(e, "GenericPopupDialog --> onGenericDataPass");
            }
        }
    }

    public String getChildKey() {
        return childKey;
    }

    public void setChildKey(String childKey) {
        this.childKey = childKey;
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
            setNewSelectedValues(secondaryValuesArray);
        } catch (Exception e) {
            Timber.e(e, "GenericPopupDialog --> addSecondaryValues");
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
                        jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.EXTENDED_RADIO_BUTTON)) &&
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
            Timber.e(e, "GenericPopupDialog --> getJsonObjectToUpdate");
        }

        return item;
    }

    public JSONArray getNewSelectedValues() {
        return newSelectedValues;
    }

    public void setNewSelectedValues(JSONArray newSelectedValues) {
        this.newSelectedValues = newSelectedValues;
    }

    protected JSONArray createValues() throws JSONException {
        JSONArray selectedValues = new JSONArray();
        JSONArray formFields = getSubFormsFields();
        for (int i = 0; i < formFields.length(); i++) {
            JSONObject field = formFields.getJSONObject(i);
            if (field != null && field.has(JsonFormConstants.TYPE) && !JsonFormConstants.LABEL
                    .equals(field.getString(JsonFormConstants.TYPE)) && !JsonFormConstants.SECTIONS
                    .equals(field.getString(JsonFormConstants.TYPE))) {
                JSONArray valueOpenMRSAttributes = new JSONArray();
                JSONObject openMRSAttributes = formUtils.getFieldOpenMRSAttributes(field);
                String key = field.getString(JsonFormConstants.KEY);
                String type = field.getString(JsonFormConstants.TYPE);
                JSONArray values = new JSONArray();
                if (JsonFormConstants.CHECK_BOX.equals(field.getString(JsonFormConstants.TYPE)) &&
                        field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    values = formUtils.getOptionsValueCheckBox(field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME));
                    formUtils.getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if ((JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE)) ||
                        JsonFormConstants.NATIVE_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE))) &&
                        field.has(JsonFormConstants.OPTIONS_FIELD_NAME) && field.has(JsonFormConstants.VALUE)) {
                    values.put(formUtils.getOptionsValueRadioButton(field.optString(JsonFormConstants.VALUE),
                            field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME)));
                    formUtils.getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if (JsonFormConstants.SPINNER.equals(field.getString(JsonFormConstants.TYPE)) && field
                        .has(JsonFormConstants.VALUE)) {
                    values.put(field.optString(JsonFormConstants.VALUE));
                    formUtils.getSpinnerValueOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else {
                    if (field.has(JsonFormConstants.VALUE)) {
                        values.put(field.optString(JsonFormConstants.VALUE));
                    }
                }

                if (values.length() > 0) {
                    selectedValues
                            .put(formUtils.createSecondaryValueObject(key, type, values, openMRSAttributes, valueOpenMRSAttributes));
                }
            }
        }

        return selectedValues;
    }

    public JSONArray getSubFormsFields() {
        return subFormsFields;
    }

    public void setSubFormsFields(JSONArray subFormsFields) {
        this.subFormsFields = subFormsFields;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyVariables();
    }

    private void destroyVariables() {
        setPopAssignedValue(new HashMap<String, SecondaryValueModel>());
        setSecondaryValuesMap(new HashMap<String, SecondaryValueModel>());
    }

    protected String[] getWidgetType(String value) {
        return value.split(";");
    }

    @Override
    public JSONArray getPopUpFields() {
        return getSubFormsFields();
    }

    @Override
    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    @Override
    public String getFormIdentity() {
        return formIdentity;
    }

    @Override
    public void setFormIdentity(String formIdentity) {
        this.formIdentity = formIdentity;
    }

    public void setCustomTextView(CustomTextView customTextView) {
        this.customTextView = customTextView;
    }

    public void setPopupReasonsTextView(CustomTextView popupReasonsTextView) {
        this.popupReasonsTextView = popupReasonsTextView;
    }

    public Map<String, SecondaryValueModel> getPopAssignedValue() {
        return popAssignedValue;
    }

    public void setPopAssignedValue(Map<String, SecondaryValueModel> popAssignedValue) {
        this.popAssignedValue = popAssignedValue;
    }
}

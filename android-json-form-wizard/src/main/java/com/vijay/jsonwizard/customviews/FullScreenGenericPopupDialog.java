package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.ExpansionPanelValuesModel;
import com.vijay.jsonwizard.event.RefreshExpansionPanelEvent;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.SecondaryValueModel;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class FullScreenGenericPopupDialog extends GenericPopupDialog {
    protected Toolbar mToolbar;
    protected String container;
    private Map<String, ExpansionPanelValuesModel> secondaryValuesMap = new HashMap<>();
    private FormUtils formUtils = new FormUtils();
    private Activity activity;
    private Context context;
    private String header;
    private LinearLayout linearLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity) context;
        setGenericPopUpDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            throw new IllegalStateException(
                    "The Context is not set. Did you forget to set context with Anc Generic Dialog setContext method?");
        }

        this.activity = (Activity) context;
        setJsonApi((JsonApi) activity);

        try {
            setMainFormFields(formUtils.getFormFields(getStepName(), context));
            getJsonApi().setGenericPopup(this);
            setGenericPopUpDialog();
            loadPartialSecondaryValues();
            createSecondaryValuesMap();
            loadSubForms();
            getJsonApi().updateGenericPopupSecondaryValues(getSpecifyContent());
        } catch (JSONException e) {
            Timber.e(e, "FullScreenGenericPopupDialog --> onCreate");
        }

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    protected void loadPartialSecondaryValues() throws JSONException {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            if (getMainFormFields() != null && getMainFormFields().length() > 0) {
                for (int i = 0; i < getMainFormFields().length(); i++) {
                    JSONObject item = getMainFormFields().getJSONObject(i);
                    if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY).equals(getParentKey()) && item.has(JsonFormConstants.VALUE)) {
                        setSecondaryValues(item.getJSONArray(JsonFormConstants.VALUE));
                    }
                }
            }
        } else {
            super.loadPartialSecondaryValues();
        }
    }

    @Override
    protected void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            if (getSecondaryValues() != null) {
                for (int i = 0; i < getSecondaryValues().length(); i++) {
                    if (!getSecondaryValues().isNull(i)) {
                        try {
                            jsonObject = getSecondaryValues().getJSONObject(i);
                            String key = jsonObject.getString(JsonFormConstants.KEY);
                            String type = jsonObject.getString(JsonFormConstants.TYPE);
                            String label = jsonObject.getString(JsonFormConstants.LABEL);
                            JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                            int index = jsonObject.optInt(JsonFormConstants.INDEX);

                            JSONObject openmrsAttributes = getOpenMRSAttributes(jsonObject);
                            JSONArray valueOpenMRSAttributes = getValueOpenMRSAttributes(jsonObject);

                            secondaryValuesMap.put(key,
                                    new ExpansionPanelValuesModel(key, type, label, index, values, openmrsAttributes,
                                            valueOpenMRSAttributes));
                        } catch (JSONException e) {
                            Timber.e(e, "FullScreenGenericPopupDialog --> createSecondaryValuesMap");
                        }
                    }
                }
            }
        } else {
            super.createSecondaryValuesMap();
        }

    }

    @Override
    protected void loadSubForms() {
        if (!TextUtils.isEmpty(getFormIdentity())) {
            JSONObject subForm = getSubForm();
            if (subForm != null) {
                try {
                    if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                        setSpecifyContent(subForm.getJSONArray(JsonFormConstants.CONTENT_FORM));
                        setSubFormsFields(addFormValues(getSpecifyContent()));
                    } else {
                        Utils.showToast(activity, activity.getApplicationContext().getResources()
                                .getString(com.vijay.jsonwizard.R.string.please_specify_content));
                        FullScreenGenericPopupDialog.this.dismiss();
                    }
                } catch (JSONException e) {
                    Timber.e(e, "FullScreenGenericPopupDialog --> loadSubForms");
                }

            }
        }
    }

    @Override
    protected JSONArray addFormValues(JSONArray formValues) {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            for (int i = 0; i < formValues.length(); i++) {
                JSONObject formValue;
                try {
                    formValue = formValues.getJSONObject(i);
                    String key = formValue.getString(JsonFormConstants.KEY);
                    formValue.put(JsonFormConstants.INDEX, String.valueOf(i));
                    if (secondaryValuesMap != null && secondaryValuesMap.containsKey(key)) {
                        SecondaryValueModel secondaryValueModel = secondaryValuesMap.get(key);
                        String type = secondaryValueModel.getType();
                        if (type != null && (type.equals(JsonFormConstants.CHECK_BOX))) {
                            if (formValue.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                                JSONArray options = formValue.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                                JSONArray values = secondaryValueModel.getValues();
                                setCompoundButtonValues(options, values);
                            }
                        } else {
                            JSONArray values = secondaryValueModel.getValues();
                            if (type != null && (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) ||
                                    type.equals(JsonFormConstants.EXTENDED_RADIO_BUTTON))) {
                                for (int k = 0; k < values.length(); k++) {
                                    formValue.put(JsonFormConstants.VALUE, getValueKey(values.getString(k)));
                                }
                            } else {
                                formValue.put(JsonFormConstants.VALUE, setValues(values, type));
                            }
                        }
                    }
                } catch (JSONException e) {
                    Timber.e(e, "FullScreenGenericPopupDialog --> loadSubForms");
                }
            }
            return formValues;
        } else {
            super.addFormValues(formValues);
        }
        return formValues;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.fragment_generic_dialog, container, false);
            mToolbar = dialogView.findViewById(R.id.generic_toolbar);
            changeToolbarColor();

            TextView toolBar = mToolbar.findViewById(R.id.txt_title_label);
            if (!TextUtils.isEmpty(header)) {
                toolBar.setText(header);
            }
            AppCompatImageButton cancelButton;
            Button okButton;

            new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager inputManager =
                            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                            HIDE_NOT_ALWAYS);
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
                    getJsonApi().updateGenericPopupSecondaryValues(new JSONArray());
                    setFormFragment(null);
                    setFormIdentity(null);
                    setFormLocation(null);
                    setContext(null);
                    getJsonApi().setGenericPopup(null);
                    FullScreenGenericPopupDialog.this.dismissAllowingStateLoss();
                }
            });

            okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passData();
                    getJsonApi().setGenericPopup(null);
                    getJsonApi().updateGenericPopupSecondaryValues(new JSONArray());
                    FullScreenGenericPopupDialog.this.dismissAllowingStateLoss();
                }
            });
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
            getJsonApi().invokeRefreshLogic(null, true, null, null);
            return dialogView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyVariables();
        JsonApi ancJsonApi = (JsonApi) activity;
        ancJsonApi.setGenericPopup(null);
    }

    private void destroyVariables() {
        setSecondaryValuesMap(new HashMap<String, SecondaryValueModel>());
    }

    @Override
    public void setContext(Context context) throws IllegalStateException {
        super.setContext(context);
        this.context = context;
    }

    @Override
    protected void passData() {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            onDataPass(getParentKey(), getChildKey());
        } else {
            onGenericDataPass(getParentKey(), getChildKey());
        }

    }

    /**
     * Finds the actual widget to be updated and secondary values added on
     *
     * @param jsonObject {@link JSONObject}
     * @param childKey   {@link String}
     * @return item {@link JSONObject}
     */
    @Override
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) && childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) && childKey.equals(childItem.getString(JsonFormConstants.KEY))) {
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
            Timber.e(e, "FullScreenGenericPopupDialog --> getJsonObjectToUpdate");
        }

        return item;
    }

    @Override
    protected JSONArray createValues() throws JSONException {
        JSONArray selectedValues = new JSONArray();
        JSONArray formFields = getSubFormsFields();
        String dateField = "";
        for (int i = 0; i < formFields.length(); i++) {
            JSONObject field = formFields.getJSONObject(i);
            if (field != null && field.has(JsonFormConstants.TYPE) &&
                    !JsonFormConstants.LABEL.equals(field.getString(JsonFormConstants.TYPE)) &&
                    !JsonFormConstants.SECTIONS.equals(field.getString(JsonFormConstants.TYPE)) &&
                    !JsonFormConstants.SPACER.equals(field.getString(JsonFormConstants.TYPE)) &&
                    !JsonFormConstants.TOASTER_NOTES.equals(field.getString(JsonFormConstants.TYPE))) {
                JSONArray valueOpenMRSAttributes = new JSONArray();
                JSONObject openMRSAttributes = getFieldOpenMRSAttributes(field);
                String key = field.getString(JsonFormConstants.KEY);
                String type = field.getString(JsonFormConstants.TYPE);
                String label = JsonFormConstants.HIDDEN.equals(type) ? JsonFormConstants.HIDDEN : getWidgetLabel(field);
                JSONArray values = new JSONArray();
                if (JsonFormConstants.CHECK_BOX.equals(field.getString(JsonFormConstants.TYPE)) && field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    values = getOptionsValueCheckBox(field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME));
                    formUtils.getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if ((JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE)) || JsonFormConstants.NATIVE_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE))) && field.has(JsonFormConstants.OPTIONS_FIELD_NAME) && field.has(JsonFormConstants.VALUE)) {
                    values.put(getOptionsValueRadioButton(field.optString(JsonFormConstants.VALUE), field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME)));
                    formUtils.getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if (JsonFormConstants.SPINNER.equals(field.getString(JsonFormConstants.TYPE)) && field.has(JsonFormConstants.VALUE)) {
                    values.put(field.optString(JsonFormConstants.VALUE));
                    getSpinnerValueOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else {
                    if (field.has(JsonFormConstants.VALUE)) {
                        values.put(field.optString(JsonFormConstants.VALUE));
                        if (JsonFormConstants.HIDDEN.equals(type) && key.contains(JsonFormConstants.DATE_TODAY_HIDDEN)) {
                            dateField = key + ":" + field.optString(JsonFormConstants.VALUE);
                        }
                    } else {
                        if (JsonFormConstants.DATE_PICKER.equals(type) && dateField.contains(key)) {
                            String[] datePickerValues = dateField.split(":");
                            if (datePickerValues.length > 1 && !datePickerValues[1].equals("0")) {
                                values.put(datePickerValues[1]);
                            }
                        }
                    }
                }

                if (values.length() > 0) {
                    if (!TextUtils.isEmpty(label) && field.has(JsonFormConstants.INDEX)) {
                        int index = field.optInt(JsonFormConstants.INDEX);
                        if (JsonFormConstants.HIDDEN.equals(type)) {
                            label = "";
                        }
                        selectedValues.put(createValueObject(key, type, label, index, values, openMRSAttributes,
                                valueOpenMRSAttributes));
                    } else {
                        selectedValues.put(createSecondaryValueObject(key, type, values, openMRSAttributes,
                                valueOpenMRSAttributes));
                    }
                }
            }
        }
        return selectedValues;
    }

    private void changeToolbarColor() {
        if (!TextUtils.isEmpty(getContainer())) {
            switch (getContainer()) {
                case JsonFormConstants.JsonFormConstantsUtils.ANC_TEST:
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.contact_tests_actionbar));
                    break;
                case JsonFormConstants.JsonFormConstantsUtils.ANC_COUNSELLING_TREATMENT:
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.contact_counselling_actionbar));
                    break;
                default:
                    break;
            }
        }
    }

    public String getContainer() {
        return container;
    }

    /**
     * Receives the generic popup data from Generic Dialog fragment
     *
     * @param parentKey
     * @param childKey
     */
    public void onDataPass(String parentKey, String childKey) {
        JSONObject mJSONObject = getJsonApi().getmJSONObject();
        if (mJSONObject != null) {
            try {
                if (getMainFormFields().length() > 0) {
                    for (int i = 0; i < getMainFormFields().length(); i++) {
                        JSONObject item = getMainFormFields().getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addValues(getJsonObjectToUpdate(item, childKey));
                        }
                    }
                }
                getJsonApi().setmJSONObject(mJSONObject);
            } catch (JSONException e) {
                Timber.e(e, "FullScreenGenericPopupDialog --> onDataPass");
            }
        }
    }

    protected void addValues(JSONObject item) throws JSONException {
        JSONArray secondaryValuesArray = createValues();
        try {
            JSONArray orderedValues = orderExpansionPanelValues(secondaryValuesArray);
            item.remove(JsonFormConstants.VALUE);
            item.put(JsonFormConstants.VALUE, orderedValues);
            setNewSelectedValues(orderedValues);
            Utils.postEvent(new RefreshExpansionPanelEvent(orderedValues, linearLayout));
            addRequiredFields(item);
        } catch (Exception e) {
            Timber.e(e, "FullScreenGenericPopupDialog --> addValues");
        }
    }

    private JSONArray orderExpansionPanelValues(JSONArray expansionPanelValues) throws JSONException {
        JSONArray formattedArray = new JSONArray();
        if (expansionPanelValues != null && expansionPanelValues.length() > 0) {
            JSONArray sortedItemsWithNulls = new JSONArray();
            for (int i = 0; i < expansionPanelValues.length(); i++) {
                JSONObject valueItem = expansionPanelValues.getJSONObject(i);
                if (valueItem.has(JsonFormConstants.INDEX)) {
                    int itemIndex = valueItem.getInt(JsonFormConstants.INDEX);
                    sortedItemsWithNulls.put(itemIndex, valueItem);
                }
            }

            for (int k = 0; k < sortedItemsWithNulls.length(); k++) {
                if (!sortedItemsWithNulls.isNull(k)) {
                    formattedArray.put(sortedItemsWithNulls.getJSONObject(k));
                }
            }
        }

        return formattedArray;
    }

    /**
     * This methods adds a new attribute field called required_fields to the accordion object that it
     * has been passed. It does so by getting only visible that are required;
     *
     * @param theAccordion accordion/Expansion panel that is to be updated with the new attribute
     * @throws JSONException Exception thrown
     */
    private void addRequiredFields(JSONObject theAccordion) throws JSONException {
        //Clear the current required fields first
        if (theAccordion.has(JsonFormConstants.REQUIRED_FIELDS)) {
            theAccordion.remove(JsonFormConstants.REQUIRED_FIELDS);
        }

        JSONArray requiredFieldsList = new JSONArray();
        JSONArray formFields = getSubFormsFields();

        for (int index = 0; index < formFields.length(); index++) {
            JSONObject fieldObject = formFields.getJSONObject(index);
            boolean isFieldVisible = !fieldObject.has(JsonFormConstants.IS_VISIBLE) ||
                    fieldObject.getBoolean(JsonFormConstants.IS_VISIBLE);
            if (FormUtils.isFieldRequired(fieldObject)) {
                if (!isFieldVisible) {
                    continue;
                }
                requiredFieldsList.put(fieldObject.getString(JsonFormConstants.KEY));
            }
        }

        if (requiredFieldsList.length() > 0) {
            theAccordion.put(JsonFormConstants.REQUIRED_FIELDS, requiredFieldsList);
        }
    }

    private String getWidgetLabel(JSONObject jsonObject) throws JSONException {
        String label = "";
        String widgetType = jsonObject.getString(JsonFormConstants.TYPE);
        if (!TextUtils.isEmpty(widgetType) && getWidgetType().equals(JsonFormConstants.EXPANSION_PANEL)) {
            switch (widgetType) {
                case JsonFormConstants.EDIT_TEXT:
                case JsonFormConstants.DATE_PICKER:
                    label = jsonObject.optString(JsonFormConstants.HINT, "");
                    break;
                default:
                    label = jsonObject.optString(JsonFormConstants.LABEL, "");
                    break;
            }
        }
        return label;
    }

    protected JSONObject createValueObject(String key, String type, String label, int index, JSONArray values, JSONObject openMRSAttributes, JSONArray valueOpenMRSAttributes) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (values.length() > 0) {
                jsonObject.put(JsonFormConstants.KEY, key);
                jsonObject.put(JsonFormConstants.TYPE, type);
                jsonObject.put(JsonFormConstants.LABEL, label);
                jsonObject.put(JsonFormConstants.INDEX, index);
                jsonObject.put(JsonFormConstants.VALUES, values);
                jsonObject.put(JsonFormConstants.OPENMRS_ATTRIBUTES, openMRSAttributes);
                if (valueOpenMRSAttributes.length() > 0) {
                    jsonObject.put(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES, valueOpenMRSAttributes);
                }
            }
        } catch (Exception e) {
            Timber.e(e, "FullScreenGenericPopupDialog --> createValueObject");

        }
        return jsonObject;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    private void setGenericPopUpDialog() {
        JsonApi ancJsonApi = (JsonApi) activity;
        ancJsonApi.setGenericPopup(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

}

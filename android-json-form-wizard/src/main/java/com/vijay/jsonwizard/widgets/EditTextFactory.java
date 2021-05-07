package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.edittext.CumulativeTotalValidator;
import com.vijay.jsonwizard.validators.edittext.MaxLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;
import com.vijay.jsonwizard.validators.edittext.ReferenceValidator;
import com.vijay.jsonwizard.validators.edittext.RelativeNumericValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.DEFAULT_CUMULATIVE_VALIDATION_ERR;
import static com.vijay.jsonwizard.constants.JsonFormConstants.DEFAULT_RELATIVE_MAX_VALIDATION_ERR;
import static com.vijay.jsonwizard.constants.JsonFormConstants.DEFAULT_RELATIVE_MIN_VALIDATION_ERR;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RELATED_FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RELATIVE_VALIDATION_EXCEPTION;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.V_CUMULATIVE_TOTAL;
import static com.vijay.jsonwizard.constants.JsonFormConstants.V_RELATIVE_MAX;
import static com.vijay.jsonwizard.constants.JsonFormConstants.V_RELATIVE_MIN;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class EditTextFactory implements FormWidgetFactory {
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 100;
    private FormUtils formUtils = new FormUtils();

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            MaterialEditText editText) {
        if (editText.isEnabled()) {
            boolean validate = editText.validate();
            if (!validate) {
                String errorString = null;
                if (editText != null && editText.getError() != null) {
                    errorString = editText.getError().toString();
                }
                return new ValidationStatus(false, errorString, formFragmentView, editText);
            }
        }
        return new ValidationStatus(true, null, formFragmentView, editText);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener
                                               listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                    CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);

        RelativeLayout rootLayout = getRelativeLayout(context);
        RelativeLayout editTextLayout = rootLayout.findViewById(R.id.edit_text_layout);
        MaterialEditText editText = editTextLayout.findViewById(R.id.edit_text);
        ImageView editButton = editTextLayout.findViewById(R.id.material_edit_text_edit_button);

        FormUtils.setEditButtonAttributes(jsonObject, editText, editButton, listener);
        attachLayout(stepName, context, formFragment, jsonObject, editText, editButton);

        JSONArray canvasIds = new JSONArray();
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        editText.setTag(R.id.canvas_ids, canvasIds.toString());
        editText.setTag(R.id.extraPopup, popup);

        attachInfoIcon(stepName, jsonObject, rootLayout, canvasIds, listener);

        ((JsonApi) context).addFormDataView(editText);
        views.add(rootLayout);
        return views;
    }

    public RelativeLayout getRelativeLayout(Context context) {
        return (RelativeLayout) LayoutInflater.from(context).inflate(getLayout(), null);
    }

    protected int getLayout() {
        return R.layout.native_form_item_edit_text;
    }

    protected void attachLayout(String stepName, Context context, JsonFormFragment formFragment,
                                final JSONObject jsonObject, final MaterialEditText editText, ImageView editButton)
            throws Exception {

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))) {
            formFragment.getJsonApi().getAppExecutors().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        editText.setText(jsonObject.optString(JsonFormConstants.VALUE));
                        editText.setSelection(editText.getText().length());
                    } catch (Exception e) {
                        Timber.e(e, "Catching an error throw on spannable, when loading report form");
                    }
                }
            });
        }
        if (jsonObject.has(JsonFormConstants.HINT)) {
            editText.setHint(jsonObject.getString(JsonFormConstants.HINT));
            editText.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
        }
        FormUtils.setEditMode(jsonObject, editText, editButton);
        FormUtils.toggleEditTextVisibility(jsonObject, editText);

        addRequiredValidator(jsonObject, editText);
        addLengthValidator(jsonObject, editText);
        addRegexValidator(jsonObject, editText);
        addEmailValidator(jsonObject, editText);
        addUrlValidator(jsonObject, editText);
        addNumericValidator(jsonObject, editText);
        addNumericIntegerValidator(jsonObject, editText);
        addRelativeNumericIntegerValidator(jsonObject, formFragment, editText, true);
        addRelativeNumericIntegerValidator(jsonObject, formFragment, editText, false);
        addCumulativeTotalValidator(jsonObject, formFragment, editText, stepName, (JsonApi) context);
        // edit type check
        String editType = jsonObject.optString(JsonFormConstants.EDIT_TYPE);
        if (!TextUtils.isEmpty(editType)) {
            if (JsonFormConstants.NUMBER.equals(editType)) {
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if (JsonFormConstants.NAME.equals(editType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            }
        }

        editText.setSingleLine(false);
        editText.addTextChangedListener(new GenericTextWatcher(stepName, formFragment, editText));
        attachRefreshLogic(context, jsonObject, editText);
    }

    private void attachInfoIcon(String stepName, JSONObject jsonObject, RelativeLayout rootLayout, JSONArray canvasIds,
                                CommonListener listener) throws JSONException {
        if (jsonObject.has(JsonFormConstants.LABEL_INFO_TEXT) || jsonObject.has(JsonFormConstants.LABEL_INFO_HAS_IMAGE)) {
            ImageView infoIcon = rootLayout.findViewById(R.id.info_icon);
            formUtils.showInfoIcon(stepName, jsonObject, listener, FormUtils.getInfoDialogAttributes(jsonObject), infoIcon, canvasIds);
        }

    }

    private void addRequiredValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            boolean requiredValue = requiredObject.getBoolean(JsonFormConstants.VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }
        }
    }

    private void addLengthValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        int minLength = MIN_LENGTH;
        int maxLength = MAX_LENGTH;
        JSONObject minLengthObject = jsonObject.optJSONObject(JsonFormConstants.V_MIN_LENGTH);
        if (minLengthObject != null) {
            String minLengthValue = minLengthObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(minLengthValue)) {
                minLength = Integer.parseInt(minLengthValue);
                editText.addValidator(new MinLengthValidator(minLengthObject.getString(JsonFormConstants.ERR),
                        Integer.parseInt(minLengthValue)));
            }
        }

        JSONObject maxLengthObject = jsonObject.optJSONObject(JsonFormConstants.V_MAX_LENGTH);
        if (maxLengthObject != null) {
            String maxLengthValue = maxLengthObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(maxLengthValue)) {
                maxLength = Integer.parseInt(maxLengthValue);
                editText.addValidator(new MaxLengthValidator(maxLengthObject.getString(JsonFormConstants.ERR),
                        Integer.parseInt(maxLengthValue)));
                boolean iSFixedSize = maxLengthObject.optBoolean(JsonFormConstants.IS_FIXED_SIZE, false);
                if (iSFixedSize) {
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                }

            }
        }

        editText.setMaxCharacters(maxLength);
        editText.setMinCharacters(minLength);
    }

    private void addRegexValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject regexObject = jsonObject.optJSONObject(JsonFormConstants.V_REGEX);
        if (regexObject != null) {
            String regexValue = regexObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(regexValue)) {
                editText.addValidator(new RegexpValidator(regexObject.getString(JsonFormConstants.ERR), regexValue));
            }
        }
    }

    private void addEmailValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject emailObject = jsonObject.optJSONObject(JsonFormConstants.V_EMAIL);
        if (emailObject != null) {
            String emailValue = emailObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(emailValue) && Boolean.TRUE.toString().equalsIgnoreCase(emailValue)) {
                editText.addValidator(
                        new RegexpValidator(emailObject.getString(JsonFormConstants.ERR), android.util.Patterns.EMAIL_ADDRESS
                                .toString()));
            }

        }

    }

    private void addUrlValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject urlObject = jsonObject.optJSONObject(JsonFormConstants.V_URL);
        if (urlObject != null) {
            String urlValue = urlObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(urlValue) && Boolean.TRUE.toString().equalsIgnoreCase(urlValue)) {
                editText.addValidator(
                        new RegexpValidator(urlObject.getString(JsonFormConstants.ERR), Patterns.WEB_URL.toString()));
            }

        }
    }

    private void addNumericValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject numericObject = jsonObject.optJSONObject(JsonFormConstants.V_NUMERIC);
        if (numericObject != null) {
            String numericValue = numericObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(numericValue) && Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addValidator(new RegexpValidator(numericObject.getString(JsonFormConstants.ERR),
                        "[0-9]*\\.?[0-9]*"));

                if (jsonObject.has(JsonFormConstants.V_MIN)) {
                    JSONObject minValidation = jsonObject.getJSONObject(JsonFormConstants.V_MIN);
                    editText.addValidator(new MinNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }

                if (jsonObject.has(JsonFormConstants.V_MAX)) {
                    JSONObject minValidation = jsonObject.getJSONObject(JsonFormConstants.V_MAX);
                    editText.addValidator(new MaxNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }
            }
        }
    }

    private void addNumericIntegerValidator(JSONObject jsonObject, MaterialEditText editText) throws JSONException {
        JSONObject numericIntegerObject = jsonObject.optJSONObject(JsonFormConstants.V_NUMERIC_INTEGER);
        if (numericIntegerObject != null) {
            String numericValue = numericIntegerObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(numericValue) && Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.addValidator(new RegexpValidator(numericIntegerObject.getString(JsonFormConstants.ERR),
                        "\\d*"));

                if (jsonObject.has(JsonFormConstants.V_MIN)) {
                    JSONObject minValidation = jsonObject.getJSONObject(JsonFormConstants.V_MIN);
                    editText.addValidator(new MinNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }

                if (jsonObject.has(JsonFormConstants.V_MAX)) {
                    JSONObject minValidation = jsonObject.getJSONObject(JsonFormConstants.V_MAX);
                    editText.addValidator(new MaxNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }
            }
        }
    }

    private void addRelativeNumericIntegerValidator(JSONObject editTextJSONObject, JsonFormFragment formFragment,
                                                    MaterialEditText editText, boolean isMaxValidator) throws JSONException {
        JSONObject relativeMaxValidationJSONObject = editTextJSONObject.optJSONObject(isMaxValidator ? V_RELATIVE_MAX : V_RELATIVE_MIN);
        if (relativeMaxValidationJSONObject != null) {
            // validate that the relative max field exists
            String relativeMaxValidationKey = relativeMaxValidationJSONObject.optString(JsonFormConstants.VALUE, null);
            JSONObject formJSONObject = new JSONObject(formFragment.getCurrentJsonState());
            JSONArray formFields = fields(formJSONObject, STEP1);
            JSONObject relativeMaxFieldJSONObject = getFieldJSONObject(formFields, relativeMaxValidationKey);
            if (relativeMaxFieldJSONObject != null) {
                // RELATIVE_MAX_VALIDATION_EXCEPTION, should never be set to Integer.MIN_VALUE in the native form json
                int relativeMaxValidationException = relativeMaxValidationJSONObject
                        .optInt(RELATIVE_VALIDATION_EXCEPTION, 0);
                if (relativeMaxValidationException != Integer.MIN_VALUE) {
                    // add validator
                    String relativeMaxValidationErrorMsg = relativeMaxValidationJSONObject
                            .optString(JsonFormConstants.ERR, null);
                    String defaultErrMsg = String.format(isMaxValidator ? DEFAULT_RELATIVE_MAX_VALIDATION_ERR : DEFAULT_RELATIVE_MIN_VALIDATION_ERR, relativeMaxValidationKey);
                    relativeMaxValidationException = relativeMaxValidationJSONObject
                            .optInt(RELATIVE_VALIDATION_EXCEPTION, Integer.MIN_VALUE);
                    editText.addValidator(new RelativeNumericValidator(
                            relativeMaxValidationErrorMsg == null ? defaultErrMsg : relativeMaxValidationErrorMsg,
                            formFragment,
                            relativeMaxValidationKey,
                            relativeMaxValidationException,
                            STEP1,
                            isMaxValidator));
                }
            }
        }
    }

    private void addCumulativeTotalValidator(JSONObject editTextJSONObject, JsonFormFragment formFragment,
                                             final MaterialEditText editText, String stepName, JsonApi jsonApi) throws JSONException {
        JSONObject validationJSONObject = editTextJSONObject.optJSONObject(V_CUMULATIVE_TOTAL);
        if (validationJSONObject != null) {
            String totalValueKey = validationJSONObject.optString(JsonFormConstants.VALUE, null);
            JSONArray formFields = fields(new JSONObject(formFragment.getCurrentJsonState()), stepName);
            JSONObject totalFieldJSONObject = getFieldJSONObject(formFields, totalValueKey);
            if (totalFieldJSONObject != null) {
                String validationErrorMsg = validationJSONObject
                        .optString(JsonFormConstants.ERR, null);
                JSONArray relatedFieldsJson = validationJSONObject.optJSONArray(RELATED_FIELDS);

                if (relatedFieldsJson != null) {
                    String errorMessage = String
                            .format(DEFAULT_CUMULATIVE_VALIDATION_ERR, editTextJSONObject.get(KEY), relatedFieldsJson.join(", "),
                                    totalValueKey);

                    final CumulativeTotalValidator cumulativeTotalValidator = new CumulativeTotalValidator(
                            validationErrorMsg == null ? errorMessage : validationErrorMsg,
                            formFragment,
                            stepName,
                            totalValueKey,
                            relatedFieldsJson,
                            jsonApi);
                    editText.addValidator(cumulativeTotalValidator);
                    for (int i = 0; i < relatedFieldsJson.length(); i++) {
                        MaterialEditText relatedEditText = getViewUsingAddress(stepName, relatedFieldsJson.getString(i), jsonApi);
                        if (relatedEditText != null) {
                            ReferenceValidator referenceValidator = new ReferenceValidator(
                                    cumulativeTotalValidator.getErrorMessage(), cumulativeTotalValidator, relatedEditText, editText);
                            relatedEditText.
                                    addValidator(referenceValidator);
                            cumulativeTotalValidator.getReferenceValidators().add(referenceValidator);
                        }
                    }

                    MaterialEditText totalValueView = getViewUsingAddress(stepName, totalValueKey, jsonApi);

                    totalValueView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {//do nothing
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {//do nothing
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!cumulativeTotalValidator.isValid(editText.getText(), TextUtils.isEmpty(s))
                                    && !TextUtils.isEmpty(editText.getText())) {
                                editText.setError(cumulativeTotalValidator.getErrorMessage());
                            }
                        }
                    });
                }
            }
        }

    }

    private void attachRefreshLogic(Context context, JSONObject jsonObject, MaterialEditText editText) {
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            editText.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(editText);
        }
    }

    private MaterialEditText getViewUsingAddress(String stepName, String fieldKey, JsonApi jsonApi) {
        View view = jsonApi.getFormDataView(stepName + ":" + fieldKey);
        if (view instanceof MaterialEditText)
            return (MaterialEditText) view;
        else
            return null;
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL_INFO_TITLE);
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL_INFO_TEXT);
        return customTranslatableWidgetFields;
    }
}
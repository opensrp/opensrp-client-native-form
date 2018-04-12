package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
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
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.edittext.MaxLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class EditTextFactory implements FormWidgetFactory {
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 100;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);

        RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                getLayout(), null);
        MaterialEditText editText = (MaterialEditText) rootLayout.findViewById(R.id.edit_text);

        attachJson(stepName, context, formFragment, jsonObject, editText);

        JSONArray canvasIds = new JSONArray();
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        editText.setTag(R.id.canvas_ids, canvasIds.toString());

        ((JsonApi) context).addFormDataView(editText);
        views.add(rootLayout);
        return views;
    }

    protected void attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, MaterialEditText editText) throws Exception {

        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");
        String constraints = jsonObject.optString("constraints");

        int minLength = MIN_LENGTH;
        int maxLength = MAX_LENGTH;


        editText.setHint(jsonObject.getString(JsonFormConstants.HINT));
        editText.setFloatingLabelText(jsonObject.getString(JsonFormConstants.HINT));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.type, jsonObject.getString("type"));
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))) {
            editText.setText(jsonObject.optString(JsonFormConstants.VALUE));
        }

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readyOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            editText.setEnabled(!readyOnly);
            editText.setFocusable(!readyOnly);
        }

        //add validators
        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(requiredValue) && Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
            }

        }

        JSONObject minLengthObject = jsonObject.optJSONObject("v_min_length");
        if (minLengthObject != null) {
            String minLengthValue = minLengthObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(minLengthValue)) {
                minLength = Integer.parseInt(minLengthValue);
                editText.addValidator(new MinLengthValidator(minLengthObject.getString(JsonFormConstants.ERR), Integer.parseInt(minLengthValue)));
            }
        }

        JSONObject maxLengthObject = jsonObject.optJSONObject("v_max_length");
        if (maxLengthObject != null) {
            String maxLengthValue = maxLengthObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(maxLengthValue)) {
                maxLength = Integer.parseInt(maxLengthValue);
                editText.addValidator(new MaxLengthValidator(maxLengthObject.getString(JsonFormConstants.ERR), Integer.parseInt(maxLengthValue)));
            }
        }

        editText.setMaxCharacters(maxLength);
        editText.setMinCharacters(minLength);

        JSONObject regexObject = jsonObject.optJSONObject("v_regex");
        if (regexObject != null) {
            String regexValue = regexObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(regexValue)) {
                editText.addValidator(new RegexpValidator(regexObject.getString(JsonFormConstants.ERR), regexValue));
            }
        }

        JSONObject emailObject = jsonObject.optJSONObject("v_email");
        if (emailObject != null) {
            String emailValue = emailObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(emailValue) && Boolean.TRUE.toString().equalsIgnoreCase(emailValue)) {
                editText.addValidator(new RegexpValidator(emailObject.getString(JsonFormConstants.ERR), android.util.Patterns.EMAIL_ADDRESS.toString()));
            }

        }

        JSONObject urlObject = jsonObject.optJSONObject("v_url");
        if (urlObject != null) {
            String urlValue = urlObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(urlValue) && Boolean.TRUE.toString().equalsIgnoreCase(urlValue)) {
                editText.addValidator(new RegexpValidator(urlObject.getString(JsonFormConstants.ERR), Patterns.WEB_URL.toString()));
            }

        }

        JSONObject numericObject = jsonObject.optJSONObject("v_numeric");
        if (numericObject != null) {
            String numericValue = numericObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(numericValue) && Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addValidator(new RegexpValidator(numericObject.getString(JsonFormConstants.ERR),
                        "[0-9]*\\.?[0-9]*"));

                if (jsonObject.has("v_min")) {
                    JSONObject minValidation = jsonObject.getJSONObject("v_min");
                    editText.addValidator(new MinNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }

                if (jsonObject.has("v_max")) {
                    JSONObject minValidation = jsonObject.getJSONObject("v_min");
                    editText.addValidator(new MaxNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }
            }
        }

        JSONObject numericIntegerObject = jsonObject.optJSONObject("v_numeric_integer");
        if (numericIntegerObject != null) {
            String numericValue = numericIntegerObject.optString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(numericValue) && Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.addValidator(new RegexpValidator(numericIntegerObject.getString(JsonFormConstants.ERR),
                        "\\d*"));

                if (jsonObject.has("v_min")) {
                    JSONObject minValidation = jsonObject.getJSONObject("v_min");
                    editText.addValidator(new MinNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }

                if (jsonObject.has("v_max")) {
                    JSONObject minValidation = jsonObject.getJSONObject("v_min");
                    editText.addValidator(new MaxNumericValidator(minValidation.getString(JsonFormConstants.ERR),
                            Double.parseDouble(minValidation.getString(JsonFormConstants.VALUE))));
                }
            }
        }

        // edit type check
        String editType = jsonObject.optString("edit_type");
        if (!TextUtils.isEmpty(editType)) {
            if ("number".equals(editType)) {
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if ("name".equals(editType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            }
        }

        editText.addTextChangedListener(new GenericTextWatcher(stepName, formFragment, editText));
        if (relevance != null && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }

        if (constraints != null && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }

    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            MaterialEditText editText) {
        if (editText.isEnabled()) {
            boolean validate = editText.validate();
            if (!validate) {
                return new ValidationStatus(false, editText.getError().toString(), formFragmentView, editText);
            }
        }
        return new ValidationStatus(true, null, formFragmentView, editText);
    }

    protected int getLayout() {
        return R.layout.native_form_item_edit_text;
    }
}

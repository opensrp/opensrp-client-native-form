package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

/**
 * Created by samuelgithengi on 3/4/19.
 */
public class CumulativeTotalValidator extends METValidator {


    private final String TAG = CumulativeTotalValidator.class.getName();

    private String relatedFieldsKey;
    private JsonFormFragment formFragment;
    private String step;
    private String totalValueFieldKey;

    public CumulativeTotalValidator(@NonNull String errorMessage, @NonNull JsonFormFragment formFragment,
                                    String step, String totalValueField, String relatedFieldsKey) {
        super(errorMessage);
        this.formFragment = formFragment;
        this.step = step;
        this.totalValueFieldKey = totalValueField;
        this.relatedFieldsKey = relatedFieldsKey;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        if (!isEmpty) {
            try {
                JSONObject formJSONObject = new JSONObject(formFragment.getCurrentJsonState());
                JSONArray formFields = fields(formJSONObject, step);
                int cumulativeTotal = Integer.parseInt(text.toString());

                int totalMaxFieldValue = getFieldJSONObject(formFields, totalValueFieldKey).optInt(JsonFormConstants.VALUE);
                JSONArray relatedFields = getFieldJSONObject(formFields, relatedFieldsKey).names();

                for (int i = 0; i < relatedFields.length(); i++) {
                    cumulativeTotal += relatedFields.getJSONObject(i).optInt(JsonFormConstants.VALUE);
                }

                if (cumulativeTotal != totalMaxFieldValue) {
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        return true;
    }
}

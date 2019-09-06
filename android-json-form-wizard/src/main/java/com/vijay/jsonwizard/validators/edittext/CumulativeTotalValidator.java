package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

/**
 * Created by samuelgithengi on 3/4/19.
 */
public class CumulativeTotalValidator extends METValidator {


    private final String TAG = CumulativeTotalValidator.class.getName();

    private JSONArray relatedFields;
    private JsonApi jsonApi;
    private JsonFormFragment formFragment;
    private String step;
    private String totalValueFieldKey;
    private MaterialEditText referenceEditText;
    private List<ReferenceValidator> referenceValidators = new ArrayList<>();


    public CumulativeTotalValidator(@NonNull String errorMessage, @NonNull JsonFormFragment formFragment,
                                    String step, String totalValueField, JSONArray relatedFields, JsonApi jsonApi) {
        super(errorMessage);
        this.formFragment = formFragment;
        this.step = step;
        this.totalValueFieldKey = totalValueField;
        this.relatedFields = relatedFields;
        this.jsonApi = jsonApi;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        if (!isEmpty) {

            try {
                JSONObject formJSONObject = new JSONObject(formFragment.getCurrentJsonState());
                JSONArray formFields = fields(formJSONObject, step);
                int cumulativeTotal = Integer.parseInt(text.toString());

                int totalMaxFieldValue = getFieldJSONObject(formFields, totalValueFieldKey).optInt(JsonFormConstants.VALUE);

                for (int i = 0; i < relatedFields.length(); i++) {
                    View relatedView = jsonApi.getFormDataView(step + ":" + relatedFields.getString(i));
                    if (relatedView instanceof MaterialEditText) {
                        MaterialEditText editText = (MaterialEditText) relatedView;
                        CharSequence value = editText.getText();

                        if (!TextUtils.isEmpty(value))
                            cumulativeTotal += Integer.parseInt(value.toString());
                    }

                }

                if (cumulativeTotal != totalMaxFieldValue) {
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            for (ReferenceValidator validator : referenceValidators) {
                disableValidator(validator.getEditText());
            }
            disableValidator(referenceEditText);
        }
        return true;
    }

    private void disableValidator(MaterialEditText editText) {
        editText.setError(null);
        editText.postInvalidate();
    }


    public boolean isValid(@NonNull MaterialEditText referenceEditText, boolean isEmpty) {
        this.referenceEditText = referenceEditText;
        return isValid(TextUtils.isEmpty(referenceEditText.getText()) ? "0" : referenceEditText.getText(), isEmpty);
    }

    public List<ReferenceValidator> getReferenceValidators() {
        return referenceValidators;
    }
}

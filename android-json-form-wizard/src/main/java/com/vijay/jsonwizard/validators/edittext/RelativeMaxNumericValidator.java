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
 * @author Vincent Karuri
 */
public class RelativeMaxNumericValidator extends METValidator {

        private JsonFormFragment formFragment;
        private String bindMaxValTo;

        private final String TAG = RelativeMaxNumericValidator.class.getName();

        public RelativeMaxNumericValidator(@NonNull String errorMessage, JsonFormFragment formFragment, @NonNull String bindMaxValTo) {
            super(errorMessage);
            this.formFragment = formFragment;
            this.bindMaxValTo = bindMaxValTo;
        }

        public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
            if (!isEmpty) {
                try {
                    JSONObject formJSONObject = new JSONObject(formFragment.getCurrentJsonState());
                    JSONArray formFields = fields(formJSONObject);
                    int relativeMaxFieldValue = getFieldJSONObject(formFields, bindMaxValTo).optInt(JsonFormConstants.VALUE);
                    if (Integer.parseInt(text.toString()) > relativeMaxFieldValue) {
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


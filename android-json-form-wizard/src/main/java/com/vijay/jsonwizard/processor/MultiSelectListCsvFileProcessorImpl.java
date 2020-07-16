package com.vijay.jsonwizard.processor;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/***
 * @deprecated should now use MultiSelectListRepository
 */
public class MultiSelectListCsvFileProcessorImpl implements MultiSelectListFileProcessor {

    @Override
    public Object process(@NonNull String content) {
        try {
            if (!StringUtils.isBlank(content)) {
                JSONArray jsonOptionsArray = new JSONArray();
                String[] lines = content.split("\n");
                String[] headers = lines[0].split(",");
                String[] property = new String[headers.length - 5];
                for (int j = 0; j < property.length; j++) {
                    property[j] = headers[j + 5].replace(JsonFormConstants.MultiSelectUtils.PROPERTY.concat("::"), "");
                }

                for (int i = 1; i < lines.length; i++) {
                    String[] segments = lines[i].split(",");
                    JSONObject jsonOptionObject = new JSONObject();
                    jsonOptionObject.put(JsonFormConstants.KEY, segments[0]);
                    jsonOptionObject.put(JsonFormConstants.MultiSelectUtils.TEXT, segments[1]);
                    jsonOptionObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, resolveEmptySlots(2, segments));
                    jsonOptionObject.put(JsonFormConstants.OPENMRS_ENTITY, resolveEmptySlots(3, segments));
                    jsonOptionObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, resolveEmptySlots(4, segments));
                    jsonOptionObject.put(JsonFormConstants.MultiSelectUtils.IS_HEADER, false);

                    JSONObject jsonPropertyObject = new JSONObject();
                    for (int k = 0; k < property.length; k++) {
                        jsonPropertyObject.put(property[k], resolveEmptySlots((k + 5), segments));
                    }

                    jsonOptionObject.put(JsonFormConstants.MultiSelectUtils.PROPERTY, jsonPropertyObject);
                    jsonOptionsArray.put(jsonOptionObject);
                }
                return jsonOptionsArray;
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    private String resolveEmptySlots(int i, String[] segments) {
        if (i >= segments.length) {
            return " ";
        } else {
            return segments[i];
        }
    }
}

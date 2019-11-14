package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.reader.MultiSelectFileReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MultiSelectListUtils {

    public static void saveMultiSelectListOptions(@NonNull Context context, @NonNull MultiSelectFileReader multiSelectFileReader) throws IOException {
        String[] files = context.getAssets().list(JsonFormConstants.MultiSelectUtils.FILES_LOCATION);
        if (files != null) {
            for (String file : files) {
                multiSelectFileReader.initMultiSelectFileReader(file);
            }
        }
    }

    public static List<MultiSelectItem> loadOptionsFromJsonForm(@NonNull JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME) ? jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME) : null;
            if (jsonArray != null) {
                return processOptionsJsonArray(jsonArray);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    public static List<MultiSelectItem> processOptionsJsonArray(@NonNull JSONArray jsonArray) throws JSONException {
        List<MultiSelectItem> multiSelectItems = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonDataObject = jsonArray.getJSONObject(i);
            multiSelectItems.add(new MultiSelectItem(
                    jsonDataObject.getString(JsonFormConstants.KEY),
                    jsonDataObject.getString(JsonFormConstants.TEXT),
                    jsonDataObject.has(JsonFormConstants.MultiSelectUtils.PROPERTY) ? jsonDataObject.getString(JsonFormConstants.MultiSelectUtils.PROPERTY) : null,
                    jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY),
                    jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID),
                    jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT)));
        }
        return multiSelectItems;
    }

    public static void addGroupings(@NonNull List<MultiSelectItem> unsortedMultiSelectItems, @NonNull JSONArray jsonGroupingsArray) {
        for (int i = 0; i < jsonGroupingsArray.length(); i++) {
            MultiSelectItem multiSelectItem = new MultiSelectItem(jsonGroupingsArray.optString(i), jsonGroupingsArray.optString(i), null, null, null, null);
            unsortedMultiSelectItems.add(multiSelectItem);
        }
    }

}

package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageWithTextFactory implements FormWidgetFactory {

    private static String TAG = ImageWithTextFactory.class.getCanonicalName();
    private View rootLayout;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        setWidgetTags(jsonObject, context);
        views.add(rootLayout);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private int getLayout() {
        return R.layout.native_form_text_and_image;
    }

    private void setWidgetTags(JSONObject jsonObject, Context context) throws JSONException {
        String key = jsonObject.getString(JsonFormConstants.KEY);
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String descriptionText = jsonObject.getString(JsonFormConstants.TEXT);
        String imageFile = jsonObject.getString(JsonFormConstants.IMAGE_FILE_NAME);

        if (!TextUtils.isEmpty(descriptionText)) {
            TextView descriptionTextView = rootLayout.findViewById(R.id.text);
            if (jsonObject.has(JsonFormConstants.TEXT_COLOR)) {
                descriptionTextView.setTextColor(Color.parseColor(JsonFormConstants.TEXT_COLOR));
            }
            descriptionTextView.setText(descriptionText);
        }

        if (!TextUtils.isEmpty(imageFile)) {
            String folderName = null;
            if (jsonObject.has(JsonFormConstants.IMAGE_FOLDER)) {
                folderName = jsonObject.getString(JsonFormConstants.IMAGE_FOLDER);
            }
            Bitmap bitmap = FormUtils.getBitmap(context, folderName, imageFile);
            if (bitmap != null) {
                ImageView imageView = rootLayout.findViewById(R.id.image);
                imageView.setImageBitmap(bitmap);
            }
        }

        rootLayout.setTag(R.id.key, key);
        rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
    }

}

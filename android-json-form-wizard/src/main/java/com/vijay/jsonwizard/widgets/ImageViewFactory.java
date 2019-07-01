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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageViewFactory implements FormWidgetFactory {

    private View rootLayout;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        setWidgetTags(jsonObject);
        setViewConfigs(jsonObject, context);
        views.add(rootLayout);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private int getLayout() {
        return R.layout.native_form_image_view;
    }

    private void setWidgetTags(JSONObject jsonObject) {
        String key = jsonObject.optString(JsonFormConstants.KEY, "");
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY, "");
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID, "");

        rootLayout.setTag(R.id.key, key);
        rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);

    }

    private void setViewConfigs(JSONObject jsonObject, Context context) {
        String descriptionText = jsonObject.optString(JsonFormConstants.TEXT, "");
        String imageFile = jsonObject.optString(JsonFormConstants.IMAGE_FILE, "");

        if (!TextUtils.isEmpty(descriptionText)) {
            TextView descriptionTextView = rootLayout.findViewById(R.id.text);
            descriptionTextView.setText(descriptionText);
            String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, "#000000");
            descriptionTextView.setTextColor(Color.parseColor(textColor));
            String textSize = jsonObject.optString(JsonFormConstants.TEXT_SIZE, String.valueOf(context.getResources().getDimension(R.dimen.label_text_size)));
            descriptionTextView.setTextSize(FormUtils.getValueFromSpOrDpOrPx(textSize, context));
        }

        if (!TextUtils.isEmpty(imageFile)) {
            String folderName = jsonObject.optString(JsonFormConstants.IMAGE_FOLDER, "");
            Bitmap bitmap = FormUtils.getBitmap(context, folderName, imageFile);
            if (bitmap != null) {
                ImageView imageView = rootLayout.findViewById(R.id.image);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}

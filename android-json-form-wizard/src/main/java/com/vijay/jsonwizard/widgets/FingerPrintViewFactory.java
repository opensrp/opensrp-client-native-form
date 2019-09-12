package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FingerPrintViewFactory implements FormWidgetFactory {

    private View rootLayout;
    private TextView descriptionTextView;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);
        descriptionTextView = rootLayout.findViewById(R.id.imageViewLabel);
        setWidgetTags(jsonObject, stepName);
        setViewConfigs(jsonObject, context);

        rootLayout.setOnClickListener(listener);
        List<View> views = new ArrayList<>(1);
        views.add(rootLayout);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private int getLayout() {
        return R.layout.native_form_finger_print_view;
    }

    private void setWidgetTags(JSONObject jsonObject, String stepName) {
        FormUtils.setViewOpenMRSEntityAttributes(jsonObject, rootLayout);
        FormUtils.setViewOpenMRSEntityAttributes(jsonObject, descriptionTextView);

        JSONArray canvasIds = new JSONArray();
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        rootLayout.setTag(R.id.canvas_ids, canvasIds);
        rootLayout.setTag(R.id.type, jsonObject.optString(JsonFormConstants.TYPE));
        rootLayout.setTag(R.id.address, stepName + ":" + jsonObject.optString(JsonFormConstants.KEY));
        rootLayout.setTag(R.id.extraPopup, false);
    }
    


    private void setViewConfigs(JSONObject jsonObject, Context context) {
        String descriptionText = jsonObject.optString(JsonFormConstants.TEXT, "");
        String imageFile = jsonObject.optString(JsonFormConstants.IMAGE_FILE, "");

        if (!TextUtils.isEmpty(descriptionText)) {
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

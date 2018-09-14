package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ToasterNotesFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws JSONException {

        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_toaster_notes, null);
        linearLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(linearLayout.getId());
        linearLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        linearLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        linearLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        linearLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        linearLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        linearLayout.setTag(R.id.type, jsonObject.getString("type"));
        linearLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (relevance != null && context instanceof JsonApi) {
            linearLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(linearLayout);
        }

        attachJson(views, context, jsonObject, linearLayout);
        return views;
    }

    private void attachJson(List<View> views, Context context, JSONObject jsonObject, LinearLayout
            linearLayout) {
        String type = jsonObject.optString(JsonFormConstants.TOASTER_TYPE, JsonFormConstants.TOASTER_INFO);
        String text = jsonObject.optString(JsonFormConstants.TEXT, "");
        String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);

        LinearLayout toasterNotesLayout = linearLayout.findViewById(R.id.toaster_notes_layout);
        ImageView toasterNoteImageView = linearLayout.findViewById(R.id.toaster_notes_image);
        TextView toasterNotesTextView = linearLayout.findViewById(R.id.toaster_notes_text);

        switch (type) {
            case JsonFormConstants.TOASTER_INFO:
                toasterNotesLayout.setBackgroundResource(R.drawable.toaster_notes_info);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_info));
                break;
            case JsonFormConstants.TOASTER_POSITIVE:
                toasterNotesLayout.setBackgroundResource(R.drawable.toaster_notes_positive);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_positivity));
                break;
            case JsonFormConstants.TOASTER_PROBLEM:
                toasterNotesLayout.setBackgroundResource(R.drawable.toaster_notes_danger);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_danger));
                break;
            case JsonFormConstants.TOASTER_WARNING:
                toasterNotesLayout.setBackgroundResource(R.drawable.toaster_notes_warning);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_warning));
                break;
            default:
                break;
        }

        toasterNotesTextView.setText(text);
        toasterNotesTextView.setTextColor(Color.parseColor(textColor));
        views.add(linearLayout);
    }
}

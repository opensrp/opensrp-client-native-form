package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.native_form_toaster_notes, null);
        relativeLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(relativeLayout.getId());
        relativeLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        relativeLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        relativeLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        relativeLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        relativeLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        relativeLayout.setTag(R.id.type, jsonObject.getString("type"));
        relativeLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (relevance != null && context instanceof JsonApi) {
            relativeLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(relativeLayout);
        }

        attachJson(views, context, jsonObject, relativeLayout, listener);
        return views;
    }

    private void attachJson(List<View> views, Context context, JSONObject jsonObject, RelativeLayout relativeLayout, CommonListener listener)
            throws JSONException {
        String type = jsonObject.optString(JsonFormConstants.TOASTER_TYPE, JsonFormConstants.TOASTER_INFO);
        String text = jsonObject.optString(JsonFormConstants.TEXT, "");
        String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);
        String infoText = jsonObject.optString(JsonFormConstants.TOASTER_INFO_TEXT, null);

        ImageView toasterNoteImageView = relativeLayout.findViewById(R.id.toaster_notes_image);
        TextView toasterNotesTextView = relativeLayout.findViewById(R.id.toaster_notes_text);

        switch (type) {
            case JsonFormConstants.TOASTER_INFO:
                relativeLayout.setBackgroundResource(R.drawable.toaster_notes_info);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_info));
                break;
            case JsonFormConstants.TOASTER_POSITIVE:
                relativeLayout.setBackgroundResource(R.drawable.toaster_notes_positive);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_positivity));
                break;
            case JsonFormConstants.TOASTER_PROBLEM:
                relativeLayout.setBackgroundResource(R.drawable.toaster_notes_danger);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_danger));
                break;
            case JsonFormConstants.TOASTER_WARNING:
                relativeLayout.setBackgroundResource(R.drawable.toaster_notes_warning);
                toasterNoteImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_warning));
                break;
            default:
                break;
        }

        toasterNotesTextView.setText(text);
        toasterNotesTextView.setTextColor(Color.parseColor(textColor));

        if (infoText != null) {
            addToasterInfo(views, jsonObject, relativeLayout, listener);
        } else {
            views.add(relativeLayout);
        }
    }

    private void addToasterInfo(List<View> views, JSONObject jsonObject, RelativeLayout relativeLayout, CommonListener listener) throws
            JSONException {
        String infoTitle = jsonObject.optString(JsonFormConstants.TOASTER_INFO_TITLE, "");
        String infoText = jsonObject.optString(JsonFormConstants.TOASTER_INFO_TEXT, "");

        ImageView toasterNoteInfo = relativeLayout.findViewById(R.id.toaster_notes_info);
        toasterNoteInfo.setVisibility(View.VISIBLE);
        toasterNoteInfo.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        toasterNoteInfo.setTag(R.id.type, jsonObject.getString("type"));
        toasterNoteInfo.setTag(R.id.label_dialog_info, infoText);
        toasterNoteInfo.setTag(R.id.label_dialog_title, infoTitle);
        toasterNoteInfo.setOnClickListener(listener);
        views.add(relativeLayout);
    }
}

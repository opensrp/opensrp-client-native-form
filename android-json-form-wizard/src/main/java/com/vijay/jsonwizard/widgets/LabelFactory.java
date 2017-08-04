package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class LabelFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        List<View> views = new ArrayList<>(1);
        LinearLayout.LayoutParams layoutParams = com.vijay.jsonwizard.utils.FormUtils.getLayoutParams(com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT, com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.default_bottom_margin));
        views.add(com.vijay.jsonwizard.utils.FormUtils.getTextViewWith(context, 27, jsonObject.getString("text"), jsonObject.getString("key"),
                jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance, layoutParams, com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH));
        return views;
    }

}

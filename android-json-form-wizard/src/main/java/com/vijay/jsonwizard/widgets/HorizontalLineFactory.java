package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This widget just creates a horizontal line by default. However, this can be converted to a vertical
 * line by explicitly setting a height & width
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 12/04/2018.
 */
public class HorizontalLineFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(context, jsonObject, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(Context context, JSONObject jsonObject, boolean popup) throws JSONException {
        // Create the view
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String key = jsonObject.getString(JsonFormConstants.KEY);

        String topMargin = jsonObject.optString(JsonFormConstants.TOP_MARGIN, "0dp");
        String bottomMargin = jsonObject.optString(JsonFormConstants.BOTTOM_MARGIN, "0dp");

        String leftMargin = jsonObject.optString(JsonFormConstants.LEFT_MARGIN, "0dp");
        String rightMargin = jsonObject.optString("right_margin", "0dp");

        String height = jsonObject.optString("height", "1dp");
        String width = jsonObject.optString("width", null);

        int absWidth = (TextUtils.isEmpty(width)) ? FormUtils.MATCH_PARENT : FormUtils.getValueFromSpOrDpOrPx(width, context);

        View horizontalLine = new View(context);

        // Add linear layout params & custom properties - height, bottom_margin, top_margin, left_margin, right_margin (enable sp, dp, px)
        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(absWidth
                , FormUtils.getValueFromSpOrDpOrPx(height, context)
                , FormUtils.getValueFromSpOrDpOrPx(leftMargin, context)
                , FormUtils.getValueFromSpOrDpOrPx(topMargin, context)
                , FormUtils.getValueFromSpOrDpOrPx(rightMargin, context)
                , FormUtils.getValueFromSpOrDpOrPx(bottomMargin, context));

        if (absWidth > 0) {
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        }
        horizontalLine.setLayoutParams(layoutParams);

        // Attach json -> id, key, canvasIds(empty) other values such as openmrs
        JSONArray jsonArray = new JSONArray();

        horizontalLine.setTag(R.id.key, key);
        horizontalLine.setTag(R.id.canvas_ids, jsonArray.toString());
        horizontalLine.setTag(R.id.openmrs_entity, openMrsEntity);
        horizontalLine.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        horizontalLine.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        horizontalLine.setTag(R.id.relevance, relevance);
        horizontalLine.setTag(R.id.extraPopup, popup);

        // Add other custom properties such as bg_color
        String bgColorHex = jsonObject.optString("bg_color", null);
        int bgColor = (TextUtils.isEmpty(bgColorHex)) ? context.getResources().getColor(R.color.horizontal_line_default_bg) : Color.parseColor(bgColorHex);

        horizontalLine.setBackgroundColor(bgColor);

        // Add view to list
        ArrayList<View> views = new ArrayList<>();
        views.add(horizontalLine);

        return views;
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        return new HashSet<>();
    }
}

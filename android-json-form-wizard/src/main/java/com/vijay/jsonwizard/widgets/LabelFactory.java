package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class LabelFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String text = jsonObject.getString(JsonFormConstants.TEXT);

        boolean hintOnText = jsonObject.optBoolean("hint_on_text", false);

        boolean hasBg = jsonObject.optBoolean("has_bg", false);
        String textSize = jsonObject.optString("text_size", null);
        String textColor = jsonObject.optString("text_color", null);

        String bgColor = null;
        String topMargin = null;
        String bottomMargin = null;
        String topPadding = null;
        String bottomPadding = null;
        String leftPadding = null;
        String rightPadding = null;
        if (hasBg) {
            bgColor = jsonObject.optString("bg_color", "#F3F3F3");
            topMargin = jsonObject.optString("top_margin", "0dp");
            bottomMargin = jsonObject.optString("bottom_margin", "0dp");

            topPadding = jsonObject.optString("top_padding", "5dp");
            bottomPadding = jsonObject.optString("bottom_padding", "5dp");
            leftPadding = jsonObject.optString("left_padding", "5dp");
            rightPadding = jsonObject.optString("right_padding", "5dp");
        }

        List<View> views = new ArrayList<>(1);

        int bgColorInt = 0;
        int topMarginInt = 0;
        int bottomMarginInt = (int) context.getResources().getDimension(R.dimen.default_bottom_margin);
        if (hasBg) {
            topMarginInt = FormUtils.getValueFromSpOrDpOrPx(topMargin, context);
            bottomMarginInt = FormUtils.getValueFromSpOrDpOrPx(bottomMargin, context);
            bgColorInt = Color.parseColor(bgColor);
        }

        LinearLayout.LayoutParams layoutParams = FormUtils.getLayoutParams(FormUtils.MATCH_PARENT,
                FormUtils.WRAP_CONTENT,
                0,
                topMarginInt,
                0,
                bottomMarginInt);

        CustomTextView textView = FormUtils.getTextViewWith(context, 27, text, jsonObject.getString(JsonFormConstants.KEY),
                jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance, layoutParams, com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH, bgColorInt);
        textView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (hasBg) {

            textView.setPadding(
                    FormUtils.getValueFromSpOrDpOrPx(leftPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(topPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(rightPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(bottomPadding, context)
            );
        }

        if (textSize != null) {
            textView.setTextSize(FormUtils.getValueFromSpOrDpOrPx(textSize, context));
        }

        if (textColor != null) {
            textView.setTextColor(Color.parseColor(textColor));
        }

        textView.setEnabled(!jsonObject.optBoolean(JsonFormConstants.READ_ONLY, false));
        textView.setHintOnText(hintOnText);

        // Set the id for the view
        JSONArray canvasIds = new JSONArray();
        textView.setId(ViewUtil.generateViewId());
        canvasIds.put(textView.getId());
        textView.setTag(R.id.canvas_ids, canvasIds.toString());

        views.add(textView);
        return views;
    }

}

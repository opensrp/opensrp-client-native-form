package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            topMarginInt = getValueFromSpOrDp(topMargin, context);
            bottomMarginInt = getValueFromSpOrDp(bottomMargin, context);
            bgColorInt = Color.parseColor(bgColor);
        }

        LinearLayout.LayoutParams layoutParams = com.vijay.jsonwizard.utils.FormUtils.getLayoutParams(com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT,
                com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT,
                0,
                topMarginInt,
                0,
                bottomMarginInt);

        CustomTextView textView = com.vijay.jsonwizard.utils.FormUtils.getTextViewWith(context, 27, text, jsonObject.getString(JsonFormConstants.KEY),
                jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance, layoutParams, com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH, bgColorInt);
        textView.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (hasBg) {

            textView.setPadding(
                    getValueFromSpOrDp(leftPadding, context),
                    getValueFromSpOrDp(topPadding, context),
                    getValueFromSpOrDp(rightPadding, context),
                    getValueFromSpOrDp(bottomPadding, context)
            );
        }

        if (textSize != null) {
            textView.setTextSize(getValueFromSpOrDp(textSize, context));
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

    public static int spToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private int getValueFromSpOrDp(String spOrDp, Context context) {
        int px = 0;
        if (!TextUtils.isEmpty(spOrDp)) {
            if (spOrDp.contains("sp")) {
                int unitValues = Integer.parseInt(spOrDp.replace("sp", ""));
                px = spToPx(context, unitValues);
            } else if (spOrDp.contains("dp")) {
                int unitValues = Integer.parseInt(spOrDp.replace("dp", ""));
                px = FormUtils.dpToPixels(context, unitValues);
            }
        }

        return px;
    }

}

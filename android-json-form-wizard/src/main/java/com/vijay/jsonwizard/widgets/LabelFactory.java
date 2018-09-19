package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class LabelFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener
            listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        RelativeLayout relativeLayout = FormUtils.createLabelRelativeLayout(jsonObject, context, listener);

        relativeLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        boolean hasBg = jsonObject.optBoolean("has_bg", false);
        String topMargin = jsonObject.optString("top_margin", "0dp");
        String bottomMargin = null;
        if (hasBg) {
            bottomMargin = jsonObject.optString("bottom_margin", "0dp");
        }

        int topMarginInt = FormUtils.getValueFromSpOrDpOrPx(topMargin, context);
        int bottomMarginInt = (int) context.getResources().getDimension(R.dimen.default_bottom_margin);
        if (hasBg) {
            bottomMarginInt = FormUtils.getValueFromSpOrDpOrPx(bottomMargin, context);
        }

        RelativeLayout.LayoutParams layoutParams = FormUtils.getRelativeLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 0,
                topMarginInt, 0, bottomMarginInt);
        relativeLayout.setLayoutParams(layoutParams);

        createLabelTextView(jsonObject, context, relativeLayout);

        // Set the id for the view
        JSONArray canvasIds = new JSONArray();
        relativeLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(relativeLayout.getId());
        relativeLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        views.add(relativeLayout);
        return views;
    }

    /**
     * Instantiates the label Custom TextView and adds in its attributes
     *
     * @param jsonObject
     * @param context
     * @param relativeLayout
     * @throws JSONException
     */
    private void createLabelTextView(JSONObject jsonObject, Context context, RelativeLayout relativeLayout) throws JSONException {
        boolean hintOnText = jsonObject.optBoolean("hint_on_text", false);
        boolean hasBg = jsonObject.optBoolean("has_bg", false);

        String bgColor = null;
        String topPadding = null;
        String bottomPadding = null;
        String leftPadding = null;
        String rightPadding = null;
        if (hasBg) {
            bgColor = jsonObject.optString("bg_color", "#F3F3F3");

            topPadding = jsonObject.optString("top_padding", "5dp");
            bottomPadding = jsonObject.optString("bottom_padding", "5dp");
            leftPadding = jsonObject.optString("left_padding", "5dp");
            rightPadding = jsonObject.optString("right_padding", "5dp");
        }

        int bgColorInt = 0;
        if (hasBg) {
            bgColorInt = Color.parseColor(bgColor);
        }
        int labelTextSize = FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("text_size", JsonFormConstants.DEFAULT_LABEL_TEXT_SIZE), context);

        CustomTextView labelText = relativeLayout.findViewById(R.id.label_text);

        if (bgColorInt != 0) {
            labelText.setBackgroundColor(bgColorInt);
        }

        if (hasBg) {
            labelText.setPadding(
                    FormUtils.getValueFromSpOrDpOrPx(leftPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(topPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(rightPadding, context),
                    FormUtils.getValueFromSpOrDpOrPx(bottomPadding, context)
            );
        }

        labelText.setTextSize(labelTextSize);
        labelText.setEnabled(!jsonObject.optBoolean(JsonFormConstants.READ_ONLY, false));//Gotcha: Should be set before createLabelText is used
        labelText.setHintOnText(hintOnText);//Gotcha: Should be set before createLabelText is used
        labelText.setText(createLabelText(jsonObject));

    }

    /**
     * Generates the spanned texted to be passed to the label Custom TextView
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private Spanned createLabelText(JSONObject jsonObject) throws JSONException {
        String text = jsonObject.getString(JsonFormConstants.TEXT);
        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        Boolean required = jsonObject.optBoolean(JsonFormConstants.READ_ONLY);
        String asterisks = "";
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(requiredValue) && Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                asterisks = "<font color=" + "#CF0800" + "> *</font>";
            }
        }

        String labelTextColor = required ? "#737373" : jsonObject.optString(JsonFormConstants.TEXT_COLOR, null);

        String combinedLabelText = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? "<font color=" + labelTextColor + ">" + Html
                .escapeHtml(text) + "</font>" + asterisks : "<font color=" + labelTextColor + ">" + TextUtils.htmlEncode(text) + "</font>" +
                asterisks;

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(combinedLabelText, Html.FROM_HTML_MODE_LEGACY) : Html
                .fromHtml(combinedLabelText);

    }

}

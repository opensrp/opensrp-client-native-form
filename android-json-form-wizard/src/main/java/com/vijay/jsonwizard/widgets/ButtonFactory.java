package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.vijay.jsonwizard.constants.JsonFormConstants.BTN_BG_COLOR;
import static com.vijay.jsonwizard.constants.JsonFormConstants.BTN_TEXT_COLOR;
import static com.vijay.jsonwizard.constants.JsonFormConstants.BTN_TEXT_SIZE;

/**
 * Created by Jason Rogena - jrogena@ona.io on 07/07/2017.
 */

public class ButtonFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       final JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, final Context context, final JsonFormFragment formFragment, JSONObject jsonObject, final CommonListener listener, boolean popup) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        final Button button = createButton(context);

        prepareButtonProperties(stepName, jsonObject, popup, openMrsEntityParent, openMrsEntity, openMrsEntityId, canvasIds, button);

        setUpButtonActions((JsonApi) context, formFragment, jsonObject, listener, button);

        ((JsonApi) context).addFormDataView(button);
        views.add(button);
        button.setTag(R.id.canvas_ids, canvasIds.toString());
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            button.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(button);
        }

        return views;
    }

    private void prepareButtonProperties(String stepName, JSONObject jsonObject, boolean popup, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, JSONArray canvasIds, Button button) throws JSONException {
        // set text properties
        String hint = jsonObject.optString(JsonFormConstants.HINT);
        if (!TextUtils.isEmpty(hint)) {
            button.setText(hint);
            // btn text color
            String textColorStr = jsonObject.optString(BTN_TEXT_COLOR, null);
            if (textColorStr != null) {
                int textColor = Color.parseColor(textColorStr);
                button.setTextColor(textColor);
            }
            // btn text size
            int textSize = jsonObject.optInt(BTN_TEXT_SIZE, -1);
            if (textSize != -1) {
                button.setTextSize(textSize);
            }
        }

        // set btn background color
        String bgColorStr = jsonObject.optString(BTN_BG_COLOR, null);
        if (bgColorStr != null) {
            int bgColor = Color.parseColor(bgColorStr);
            Drawable background = button.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(bgColor);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(bgColor);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(bgColor);
            }
        }

        button.setId(ViewUtil.generateViewId());
        canvasIds.put(button.getId());

        addViewTags(stepName, jsonObject, popup, openMrsEntityParent, openMrsEntity, openMrsEntityId, button);

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            button.setEnabled(!jsonObject.getBoolean(JsonFormConstants.READ_ONLY));
            button.setFocusable(!jsonObject.getBoolean(JsonFormConstants.READ_ONLY));
        }
    }

    private void setUpButtonActions(final JsonApi context, final JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, final Button button) throws JSONException {
        JSONObject action = jsonObject.optJSONObject(JsonFormConstants.ACTION);
        final Boolean confirmationBtnSkipValidation = !jsonObject.isNull(JsonFormConstants.SKIP_VALIDATION) ? jsonObject.getBoolean(JsonFormConstants.SKIP_VALIDATION) : false;
        if (action != null) {
            jsonObject.put(JsonFormConstants.VALUE, Boolean.FALSE.toString());
            jsonObject.getJSONObject(JsonFormConstants.ACTION).put(JsonFormConstants.RESULT, false);
            final String behaviour = action.optString(JsonFormConstants.BEHAVIOUR);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String addressString = (String) v.getTag(R.id.address);
                        if (!TextUtils.isEmpty(addressString)) {
                            String[] address = addressString.split(":");
                            JSONObject jsonObject = context
                                    .getObjectUsingAddress(address, false);
                            jsonObject.put(JsonFormConstants.VALUE, Boolean.TRUE.toString());

                            switch (behaviour) {
                                case JsonFormConstants.BEHAVIOUR_FINISH_FORM:
                                    button.setTag(R.id.raw_value, Boolean.TRUE.toString());
                                    formFragment.save(confirmationBtnSkipValidation);
                                    break;
                                case JsonFormConstants.BEHAVIOUR_NEXT_STEP:
                                    formFragment.next();
                                    break;
                                default:
                                    jsonObject.getJSONObject(JsonFormConstants.ACTION)
                                            .put(JsonFormConstants.RESULT, false);
                                    jsonObject.put(JsonFormConstants.VALUE, Boolean.FALSE.toString());
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            button.setOnClickListener(listener);
        }
    }

    private void addViewTags(String stepName, JSONObject jsonObject, boolean popup, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, Button button) throws JSONException {
        button.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        button.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        button.setTag(R.id.openmrs_entity, openMrsEntity);
        button.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        button.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        button.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        button.setTag(R.id.extraPopup, popup);
        button.setTag(R.id.raw_value, jsonObject.optString(JsonFormConstants.VALUE));
    }

    protected Button createButton(Context context) {
        Button button = new Button(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.extra_bottom_margin);
        button.setLayoutParams(layoutParams);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.button_text_size));
        button.setTextColor(context.getResources().getColor(android.R.color.white));
        button.setHeight(context.getResources().getDimensionPixelSize(R.dimen.button_height));
        button.setBackgroundResource(R.drawable.btn_bg);

        return button;
    }

    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        return new HashSet<>();
    }
}

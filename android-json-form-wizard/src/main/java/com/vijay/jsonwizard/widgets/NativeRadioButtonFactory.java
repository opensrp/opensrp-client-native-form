package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

/**
 * Created by samuelgithengi on 8/16/18.
 */
public class NativeRadioButtonFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        createRadioButtonElement(views,jsonObject,context,canvasIds,readOnly);
        addRadioButtonOptionsElements(jsonObject,context,readOnly,canvasIds,stepName, views,listener);

        return views;
    }

    /**
     * Create the Radio Button from the JSON definition
     * @param view
     * @param jsonObject
     * @param context
     * @param canvasIds
     * @param readOnly
     */
    public void createRadioButtonElement(List<View> view, JSONObject jsonObject, Context context, JSONArray canvasIds, Boolean
            readOnly) throws JSONException {

        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        String label = jsonObject.optString("label");
        int labelTextSize = FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("label_text_size", JsonFormConstants
                .NATIVE_RADIO_BUTTON_DEFAULT_LABEL_TEXT_SIZE),context);
        String labelTextColor = jsonObject.optString("label_text_color", "#212121");


        if (!label.isEmpty()) {
            CustomTextView textView = null;
            try {
                textView = getTextViewWith(context, labelTextSize, label, jsonObject.getString(JsonFormConstants.KEY),
                        jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId, relevance,
                        getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH,0, labelTextColor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert textView != null;
            canvasIds.put(textView.getId());
            textView.setEnabled(!readOnly);
            view.add(textView);
        }
    }

    /**
     * Creates the Radio Button options from the JSON definitions
     * @param jsonObject
     * @param context
     * @param readOnly
     * @param canvasIds
     * @param stepName
     * @param views
     * @param listener
     * @throws JSONException
     */
    protected void addRadioButtonOptionsElements(JSONObject jsonObject, Context context, Boolean readOnly, JSONArray canvasIds,
                                               String stepName, List<View> views, CommonListener listener) throws JSONException {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        ArrayList<RadioButton> radioButtons = new ArrayList<>();
        RadioGroup radioGroup = new RadioGroup(context);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            RadioButton radioButton = (RadioButton) LayoutInflater.from(context).inflate(R.layout.item_radio_button,
                    null);

            String optionTextColor = "#212121";
            String optionTextSize = "15sp";

            try {
                if (item.getString(JsonFormConstants.TEXT_COLOR) != null) {
                    optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
                }
            } catch (JSONException exception) {
                Log.d("nativeRadioButtonColor", exception.getLocalizedMessage());
            }

            try {
                if (item.getString(JsonFormConstants.TEXT_SIZE) != null) {
                    optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
                }
            } catch (JSONException exception) {
                Log.d("nativeRadioButtonSize", exception.getLocalizedMessage());
            }


            radioButton.setId(ViewUtil.generateViewId());
            radioButton.setText(item.getString(JsonFormConstants.TEXT));
            radioButton.setTextColor(Color.parseColor(optionTextColor));
            radioButton.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize,context));
            radioButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            radioButton.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            radioButton.setTag(R.id.openmrs_entity, openMrsEntity);
            radioButton.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            radioButton.setTag(R.id.type, jsonObject.getString("type"));
            radioButton.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            radioButton.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            radioButton.setOnCheckedChangeListener(listener);
            if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
                    && jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
                radioButton.setChecked(true);
            }
            radioButton.setEnabled(!readOnly);
            radioButton.setFocusable(!readOnly);

            ((JsonApi) context).addFormDataView(radioButton);

            canvasIds.put(radioButton.getId());
            radioButtons.add(radioButton);
            radioGroup.addView(radioButton);


            if (relevance != null && context instanceof JsonApi) {
                radioButton.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(radioButton);
            }
        }
        radioGroup.setLayoutParams(getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.extra_bottom_margin)));
        views.add(radioGroup);

        for (RadioButton radioButton : radioButtons) {
            radioButton.setTag(R.id.canvas_ids, canvasIds.toString());
        }
    }
}

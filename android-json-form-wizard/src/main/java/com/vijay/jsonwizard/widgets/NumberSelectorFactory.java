package com.vijay.jsonwizard.widgets;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.NativeViewer;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.CustomTextView;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberSelectorFactory implements FormWidgetFactory {
    private static CustomTextView selectedTextView;
    private static int selectedItem = -1;
    private static HashMap<ViewParent, CustomTextView> selectedTextViews = new HashMap<>();
    private static ArrayAdapter<String> dataAdapter;
    private SelectedNumberClickListener selectedNumberClickListener = new SelectedNumberClickListener();
    private Spinner spinner;
    private Context context;
    private CommonListener listener;
    public static final String TAG = NumberSelectorFactory.class.getCanonicalName();
    private static NumberSelectorFactoryReceiver receiver;
    private Map<String, JSONObject> jsonObjectMap = new HashMap<>();
    private Map<String, LinearLayout> rootLayoutMap = new HashMap<>();

    @SuppressLint("NewApi")

    private static void setSelectedColor(Context context, CustomTextView customTextView, int item, int numberOfSelectors, String textColor) {
        if (customTextView != null && item > -1) {
            if (item == 0) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_left_rounded_background_selected));
            } else if (item == numberOfSelectors - 1) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_right_rounded_background_selected));
            } else {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_normal_background_selected));
            }

            customTextView.setTextColor(Color.parseColor(textColor));
        }
    }

    @SuppressLint("NewApi")
    private static void setDefaultColor(Context context, CustomTextView customTextView, int item, int numberOfSelectors, String textColor) {
        if (customTextView != null && item > -1) {
            if (item == 0) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_left_rounded_background));
            } else if (item == numberOfSelectors - 1) {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_right_rounded_background));
            } else {
                customTextView.setBackground(context.getResources().getDrawable(R.drawable.number_selector_normal_background));
            }
            customTextView.setTextColor(Color.parseColor(textColor));
        }
    }

    /**
     * Sets backgrounds according to the selected textviews
     *
     * @param textView
     */
    public static void setBackgrounds(CustomTextView textView) {
        String defaultColor = (String) textView.getTag(R.id.number_selector_default_text_color);
        String selectedColor = (String) textView.getTag(R.id.number_selector_selected_text_color);
        int item = (int) textView.getTag(R.id.number_selector_item);
        int numberOfSelectors = (int) textView.getTag(R.id.number_selector_number_of_selectors);
        ViewParent textViewParent = textView.getParent();

        if (!textView.equals(selectedTextView)) {
            if (selectedTextViews.size() == 0) {
                setSelectedColor(textView.getContext(), textView, item, numberOfSelectors, selectedColor);
                setDefaultColor(textView.getContext(), selectedTextView, selectedItem, numberOfSelectors, defaultColor);
            } else {
                for (HashMap.Entry<ViewParent, CustomTextView> entry : selectedTextViews.entrySet()) {
                    if (textViewParent == entry.getKey()) {
                        if (textView != entry.getValue()) {
                            setSelectedColor(textView.getContext(), textView, item, numberOfSelectors, selectedColor);
                            setDefaultColor(textView.getContext(), entry.getValue(), selectedItem, numberOfSelectors, defaultColor);
                        }
                    } else {
                        if (textView != entry.getValue()) {
                            setSelectedColor(textView.getContext(), textView, item, numberOfSelectors, selectedColor);
                        }
                    }

                }
            }
            selectedTextViews.put(textViewParent, textView);
        }
    }

    public static void setSelectedTextViews(CustomTextView customTextView) {
        int item = (int) customTextView.getTag(R.id.number_selector_item);
        selectedTextView = customTextView;
        selectedItem = item;
    }

    /**
     * Create the spinner with the numbers starting from the last number in hte selectors
     *
     * @param context
     * @param jsonObject
     */
    private static Spinner createDialogSpinner(Context context, JSONObject jsonObject, int spinnerStartNumber, final CommonListener listener, String stepName) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        int maxValue = jsonObject.optInt(JsonFormConstants.MAX_SELECTION_VALUE, 20);
        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(10, FormUtils.WRAP_CONTENT, 0, 0, 0, 0);
        final Spinner spinner = new Spinner(context, Spinner.MODE_DIALOG);

        List<String> numbers = new ArrayList<>();
        for (int i = spinnerStartNumber; i <= maxValue; i++) {
            numbers.add(String.valueOf(i));
        }
        numbers.add(0, context.getResources().getString(R.string.select_one)); //This is to enable the first item in the spinner selection.

        dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, numbers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setDropDownWidth(100);
        }
        spinner.setLayoutParams(layoutParams);
        spinner.setId(ViewUtil.generateViewId());
        spinner.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY) + JsonFormConstants.SUFFIX.SPINNER);
        spinner.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        spinner.setTag(R.id.openmrs_entity, openMrsEntity);
        spinner.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        spinner.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        spinner.setTag(R.id.address, stepName + ":" + spinner.getTag(R.id.key));
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(listener);
            }
        });

        return spinner;
    }

    public static void setSelectedTextViewText(String viewText) {
        selectedTextView.setText(viewText);
    }

    public static ValidationStatus validate(NativeViewer formFragmentView,
                                            CustomTextView customTextView) {
        if (!(customTextView.getTag(R.id.v_required) instanceof String) || !(customTextView.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null, formFragmentView, customTextView);
        }
        Boolean isRequired = Boolean.valueOf((String) customTextView.getTag(R.id.v_required));
        if (!isRequired || !customTextView.isEnabled()) {
            return new ValidationStatus(true, null, formFragmentView, customTextView);
        }
        String selectedNumber = String.valueOf(customTextView.getText());
        if (!selectedNumber.isEmpty()) {
            return new ValidationStatus(true, null, formFragmentView, customTextView);
        } else {
            return new ValidationStatus(false, (String) customTextView.getTag(R.id.error), formFragmentView, customTextView);
        }
    }

    private void createNumberSelector(CustomTextView textView) {
        final Spinner spinner = (Spinner) textView.getTag(R.id.number_selector_spinner);

        LinearLayout parent = ((LinearLayout) textView.getTag(R.id.toolbar_parent_layout));

        if (spinner != null && parent.findViewById(spinner.getId()) == null) {
            parent.addView(spinner);
        }
        spinner.performClick();

    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, NativeViewer formFragment, JSONObject jsonObject, CommonListener
            listener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, NativeViewer formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, Context context, JSONObject jsonObject, CommonListener
            listener, boolean popup) throws JSONException {
        this.context = context;
        this.listener = listener;

        jsonObjectMap.put(jsonObject.getString(JsonFormConstants.KEY), jsonObject);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);

        LinearLayout rootLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 1, 2, 1, 2);

        rootLayout.setLayoutParams(layoutParams);
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        rootLayout.setId(ViewUtil.generateViewId());
        rootLayout.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        rootLayout.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        rootLayout.setTag(R.id.extraPopup, popup);
        rootLayout.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        canvasIds.put(rootLayout.getId());
        rootLayout.setTag(R.id.canvas_ids, canvasIds.toString());
        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            rootLayout.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            rootLayout.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(rootLayout);
        }

        views.add(rootLayout);
        createTextViews(context, jsonObject, rootLayout, listener, stepName, popup);

        rootLayoutMap.put(jsonObject.getString(JsonFormConstants.KEY), rootLayout);

        receiver = new NumberSelectorFactoryReceiver();

        return views;
    }

    @SuppressLint("NewApi")
    private void createTextViews(Context context, JSONObject jsonObject, LinearLayout linearLayout, CommonListener listener, String stepName, boolean popup) throws JSONException {
        int startSelectionNumber = jsonObject.optInt(JsonFormConstants.START_SELECTION_NUMBER, 1);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int numberOfSelectors = jsonObject.optInt(JsonFormConstants.NUMBER_OF_SELECTORS, 5);

        if (linearLayout.getTag(R.id.is_automatic) == null) {
            jsonObject.put(JsonFormConstants.NUMBER_OF_SELECTORS_ORIGINAL, numberOfSelectors);
        }

        int maxValue = jsonObject.optInt(JsonFormConstants.MAX_SELECTION_VALUE, 20);
        for (int i = 0; i < numberOfSelectors; i++) {
            CustomTextView customTextView = createCustomView(context, jsonObject, width, numberOfSelectors, listener, linearLayout, i, popup);
            if (i == (numberOfSelectors - 1) && (numberOfSelectors < maxValue)) {
                spinner = createDialogSpinner(context, jsonObject, (startSelectionNumber + (numberOfSelectors - 1)), listener, stepName);
                spinner.setTag(R.id.number_selector_textview, customTextView);
                spinner.setTag(R.id.extraPopup, popup);
                customTextView.setTag(R.id.number_selector_spinner, spinner);
                customTextView.setTag(R.id.toolbar_parent_layout, linearLayout);
                customTextView.setOnClickListener(selectedNumberClickListener);
            } else {
                customTextView.setOnClickListener(listener);
            }
            linearLayout.addView(customTextView);
            showSelectedTextView(jsonObject, customTextView);
        }
    }

    public String getText(int item, int startSelectionNumber, int numberOfSelectors, int maxValue) {
        String text = startSelectionNumber == 0 ? String.valueOf(item) : startSelectionNumber == 1 ? String.valueOf(item + 1) : String.valueOf(startSelectionNumber + item);
        if ((item == (numberOfSelectors - 1)) && (maxValue - 1) > Integer.parseInt(text)) {
            text = text + "+";
        }
        return text;
    }

    @SuppressLint("NewApi")
    private CustomTextView createCustomView(Context context, JSONObject jsonObject, int width, int numberOfSelectors, CommonListener listener, LinearLayout linearLayout, int item, boolean popup) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        int startSelectionNumber = jsonObject.optInt(JsonFormConstants.START_SELECTION_NUMBER, 1);
        int maxValue = jsonObject.optInt(JsonFormConstants.MAX_SELECTION_VALUE, 20);
        String textColor = jsonObject.optString(JsonFormConstants.TEXT_COLOR, JsonFormConstants.DEFAULT_TEXT_COLOR);
        String selectedTextColor = jsonObject.optString(JsonFormConstants.NUMBER_SELECTOR_SELCTED_TEXT_COLOR, JsonFormConstants.DEFAULT_NUMBER_SELECTOR_TEXT_COLOR);
        String textSize = jsonObject.getString(JsonFormConstants.TEXT_SIZE);
        textSize = textSize == null ? String.valueOf(context.getResources().getDimension(R.dimen.default_label_text_size)) : String.valueOf(FormUtils.getValueFromSpOrDpOrPx(textSize, context));
        LinearLayout.LayoutParams layoutParams = FormUtils.getLinearLayoutParams(width / numberOfSelectors, FormUtils.WRAP_CONTENT, 1, 2, 1, 2);

        CustomTextView customTextView = FormUtils.getTextViewWith(context, Integer.parseInt(textSize), getText(item, startSelectionNumber,
                numberOfSelectors, maxValue), jsonObject.getString(JsonFormConstants.KEY) + JsonFormConstants.SUFFIX.TEXT_VIEW,
                jsonObject.getString(JsonFormConstants.TYPE), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                "", layoutParams, FormUtils.FONT_BOLD_PATH, 0, textColor);

        customTextView.setId(ViewUtil.generateViewId());
        customTextView.setPadding(0, 15, 0, 15);
        setDefaultColor(context, customTextView, item, numberOfSelectors, textColor);
        customTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        customTextView.setClickable(true);
        customTextView.setTag(R.id.number_selector_item, item);
        customTextView.setTag(R.id.number_selector_number_of_selectors, numberOfSelectors);
        customTextView.setTag(R.id.number_selector_max_number, maxValue);
        customTextView.setTag(R.id.number_selector_default_text_color, textColor);
        customTextView.setTag(R.id.number_selector_selected_text_color, selectedTextColor);
        customTextView.setTag(R.id.number_selector_start_selection_number, startSelectionNumber);
        customTextView.setTag(R.id.number_selector_listener, listener);
        customTextView.setTag(R.id.v_required, addRequiredTag(jsonObject));
        customTextView.setTag(R.id.extraPopup, popup);
        if (item == numberOfSelectors - 1) {
            customTextView.setTag(R.id.number_selector_layout, linearLayout);
        }
        customTextView.setTag(R.id.json_object, jsonObject);

        return customTextView;
    }

    private void showSelectedTextView(JSONObject jsonObject, CustomTextView customTextView) {
        String text = customTextView.getText().toString();
        String numberValue = jsonObject.optString(JsonFormConstants.VALUE);
        if (text.contains("+")) {
            text = text.replace("+", "");
        }

        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(numberValue)) {
            if (customTextView.getText().toString().contains("+") && (Integer.valueOf(numberValue) > Integer.valueOf(text))) {
                customTextView.setText(jsonObject.optString(JsonFormConstants.VALUE));
                setBackgrounds(customTextView);
                setSelectedTextViews(customTextView);
            }

            if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE)) && text.equals(jsonObject.optString(JsonFormConstants.VALUE))) {
                customTextView.callOnClick();

            }

        }
    }

    private String addRequiredTag(JSONObject jsonObject) throws JSONException {
        String required = "false";
        JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString(JsonFormConstants.VALUE);
            if (!TextUtils.isEmpty(requiredValue) && Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                required = "true";
            }
        }

        return required;
    }

    private class SelectedNumberClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            createNumberSelector((CustomTextView) view);
        }
    }

    public static NumberSelectorFactoryReceiver getNumberSelectorsReceiver() {
        return receiver;
    }


    public class NumberSelectorFactoryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context contextInner, Intent intent) {
            try {
                int maxValue = intent.getIntExtra(JsonFormConstants.MAX_SELECTION_VALUE, 0);

                JSONObject jsonObject = jsonObjectMap.get(intent.getStringExtra(JsonFormConstants.JSON_OBJECT_KEY));
                boolean isPopUp = intent.getBooleanExtra(JsonFormConstants.IS_POPUP, false);

                jsonObject.put(JsonFormConstants.MAX_SELECTION_VALUE, maxValue);
                if (jsonObject.has(JsonFormConstants.NUMBER_OF_SELECTORS_ORIGINAL)) {
                    jsonObject.put(JsonFormConstants.NUMBER_OF_SELECTORS, maxValue < jsonObject.getInt(JsonFormConstants.NUMBER_OF_SELECTORS_ORIGINAL) ? maxValue : jsonObject.getInt(JsonFormConstants.NUMBER_OF_SELECTORS_ORIGINAL));
                } else {
                    jsonObject.put(JsonFormConstants.NUMBER_OF_SELECTORS, maxValue < jsonObject.getInt(JsonFormConstants.NUMBER_OF_SELECTORS) ? maxValue : jsonObject.getInt(JsonFormConstants.NUMBER_OF_SELECTORS));

                }

                LinearLayout rootLayout = rootLayoutMap.get(intent.getStringExtra(JsonFormConstants.JSON_OBJECT_KEY));
                rootLayout.removeAllViews();
                rootLayout.setTag(R.id.is_automatic, true);
                createTextViews(context, jsonObject, rootLayout, listener, intent.getStringExtra(JsonFormConstants.STEPNAME), isPopUp);
                rootLayout.setTag(R.id.is_automatic, null);

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}

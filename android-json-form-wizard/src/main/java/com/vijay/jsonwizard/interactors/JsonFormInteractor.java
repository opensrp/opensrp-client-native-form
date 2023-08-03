package com.vijay.jsonwizard.interactors;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.widgets.BarcodeFactory;
import com.vijay.jsonwizard.widgets.ButtonFactory;
import com.vijay.jsonwizard.widgets.CheckBoxFactory;
import com.vijay.jsonwizard.widgets.ComponentSpacerFactory;
import com.vijay.jsonwizard.widgets.CountDownTimerFactory;
import com.vijay.jsonwizard.widgets.DatePickerFactory;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.ExpansionWidgetFactory;
import com.vijay.jsonwizard.widgets.ExtendedRadioButtonWidgetFactory;
import com.vijay.jsonwizard.widgets.FingerPrintFactory;
import com.vijay.jsonwizard.widgets.GpsFactory;
import com.vijay.jsonwizard.widgets.HiddenTextFactory;
import com.vijay.jsonwizard.widgets.HorizontalLineFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.ImageViewFactory;
import com.vijay.jsonwizard.widgets.LabelFactory;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;
import com.vijay.jsonwizard.widgets.NativeEditTextFactory;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;
import com.vijay.jsonwizard.widgets.NumberSelectorFactory;
import com.vijay.jsonwizard.widgets.RDTCaptureFactory;
import com.vijay.jsonwizard.widgets.RadioButtonFactory;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;
import com.vijay.jsonwizard.widgets.SectionFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;
import com.vijay.jsonwizard.widgets.TimePickerFactory;
import com.vijay.jsonwizard.widgets.ToasterNotesFactory;
import com.vijay.jsonwizard.widgets.TreeViewFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vijay on 5/19/15.
 */
public class JsonFormInteractor {

    private static final String TAG = "JsonFormInteractor";
    protected static JsonFormInteractor INSTANCE;
    public Map<String, FormWidgetFactory> map;

    public JsonFormInteractor() {
        this(null);
    }

    public JsonFormInteractor(@Nullable Map<String, FormWidgetFactory> additionalWidgetsMap) {
        registerWidgets();
        if (additionalWidgetsMap != null) {
            for (Map.Entry<String, FormWidgetFactory> widgetFactoryEntry : additionalWidgetsMap.entrySet()) {
                map.put(widgetFactoryEntry.getKey(), widgetFactoryEntry.getValue());
            }
        }
    }


    public static JsonFormInteractor getInstance(@Nullable Map<String, FormWidgetFactory> additionalWidgetsMap) {
        if (INSTANCE == null) {
            INSTANCE = new JsonFormInteractor(additionalWidgetsMap);
        }

        return INSTANCE;
    }

    public static JsonFormInteractor getInstance() {
        return getInstance(null);
    }

    protected void registerWidgets() {
        map = new HashMap<>();
        map.put(JsonFormConstants.SECTION_LABEL, new SectionFactory());
        map.put(JsonFormConstants.EDIT_TEXT, new EditTextFactory());
        map.put(JsonFormConstants.HIDDEN, new HiddenTextFactory());
        map.put(JsonFormConstants.LABEL, new LabelFactory());
        map.put(JsonFormConstants.CHECK_BOX, new CheckBoxFactory());
        map.put(JsonFormConstants.RADIO_BUTTON, new RadioButtonFactory());
        map.put(JsonFormConstants.CHOOSE_IMAGE, new ImagePickerFactory());
        map.put(JsonFormConstants.FINGER_PRINT, new FingerPrintFactory());
        map.put(JsonFormConstants.SPINNER, new SpinnerFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
        map.put(JsonFormConstants.TREE, new TreeViewFactory());
        map.put(JsonFormConstants.BARCODE, new BarcodeFactory());
        map.put(JsonFormConstants.BUTTON, new ButtonFactory());
        map.put(JsonFormConstants.GPS, new GpsFactory());
        map.put(JsonFormConstants.HORIZONTAL_LINE, new HorizontalLineFactory());
        map.put(JsonFormConstants.NATIVE_RADIO_BUTTON, new NativeRadioButtonFactory());
        map.put(JsonFormConstants.NUMBER_SELECTOR, new NumberSelectorFactory());
        map.put(JsonFormConstants.TOASTER_NOTES, new ToasterNotesFactory());
        map.put(JsonFormConstants.SPACER, new ComponentSpacerFactory());
        map.put(JsonFormConstants.NATIVE_EDIT_TEXT, new NativeEditTextFactory());
        map.put(JsonFormConstants.TIME_PICKER, new TimePickerFactory());
        map.put(JsonFormConstants.REPEATING_GROUP, new RepeatingGroupFactory());
        map.put(JsonFormConstants.RDT_CAPTURE, new RDTCaptureFactory());
        map.put(JsonFormConstants.COUNTDOWN_TIMER, new CountDownTimerFactory());
        map.put(JsonFormConstants.IMAGE_VIEW, new ImageViewFactory());
        map.put(JsonFormConstants.EXTENDED_RADIO_BUTTON, new ExtendedRadioButtonWidgetFactory());
        map.put(JsonFormConstants.EXPANSION_PANEL, new ExpansionWidgetFactory());
        map.put(JsonFormConstants.MULTI_SELECT_LIST, new MultiSelectListFactory());

    }

    public List<View> fetchFormElements(String stepName, JsonFormFragment formFragment,
                                        JSONObject parentJson, CommonListener listener, Boolean popup) {
        List<View> viewsFromJson = new ArrayList<>(5);
        try {

            if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson
                    .get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                fetchSections(viewsFromJson, stepName, formFragment, sections, listener, popup);

            } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson
                    .get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                JSONArray fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                fetchFields(viewsFromJson, stepName, formFragment, fields, listener, popup);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json exception occurred : " + e.getMessage());
        }
        return viewsFromJson;
    }

    private void fetchSections(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                               JSONArray sections, CommonListener listener, Boolean popup) {
        try {
            if (sections == null || sections.length() == 0) {
                return;
            }

            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionJson = sections.getJSONObject(i);

                if (sectionJson.has(JsonFormConstants.NAME)) {
                    fetchViews(viewsFromJson, stepName, formFragment, JsonFormConstants.SECTION_LABEL, sectionJson, listener,
                            popup);
                }

                if (sectionJson.has(JsonFormConstants.FIELDS)) {
                    JSONArray fields = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                    fetchFields(viewsFromJson, stepName, formFragment, fields, listener, popup);
                }


            }
        } catch (JSONException e) {
            Log.e(TAG, "Json exception occurred : " + e.getMessage());
        }
    }

    public void fetchFields(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                            JSONArray fields, CommonListener listener, Boolean popup) {

        try {
            if (fields == null || fields.length() == 0) {
                return;
            }

            for (int i = 0; i < fields.length(); i++) {
                JSONObject childJson = fields.getJSONObject(i);
                fetchViews(viewsFromJson, stepName, formFragment, childJson.getString(JsonFormConstants.TYPE), childJson,
                        listener, popup);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json exception occurred : " + e.getMessage());
        }
    }

    private void fetchViews(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                            String type, JSONObject jsonObject, CommonListener listener, Boolean popup) {

        try {
            List<View> views = map
                    .get(type)
                    .getViewsFromJson(stepName, formFragment.getActivity(), formFragment, jsonObject, listener, popup);
            if (views.size() > 0) {
                viewsFromJson.addAll(views);
            }
        } catch (Exception e) {
            Log.e(TAG,
                    "Exception occurred in making view : Exception is : "
                            + e.getMessage());
            e.printStackTrace();
        }

    }
}
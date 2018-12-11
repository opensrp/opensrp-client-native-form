package com.vijay.jsonwizard.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.comparisons.Comparison;
import com.vijay.jsonwizard.comparisons.EqualToComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.LessThanComparison;
import com.vijay.jsonwizard.comparisons.LessThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.NotEqualToComparison;
import com.vijay.jsonwizard.comparisons.RegexComparison;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.customviews.TextableView;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineHelper;
import com.vijay.jsonwizard.utils.ExObjectResult;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.PropertyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class JsonFormActivity extends AppCompatActivity implements JsonApi {

    private static final String TAG = JsonFormActivity.class.getSimpleName();
    private static final String JSON_STATE = "jsonState";
    private static final String FORM_STATE = "formState";
    private GenericPopupDialog genericPopupDialog = GenericPopupDialog.getInstance();
    private FormUtils formUtils = new FormUtils();
    private Toolbar mToolbar;
    private JSONObject mJSONObject;
    private PropertyManager propertyManager;
    private HashMap<String, View> skipLogicViews;
    private HashMap<String, View> calculationLogicViews;
    private HashMap<String, View> constrainedViews;
    private ArrayList<View> formDataViews;
    private String functionRegex;
    private HashMap<String, Comparison> comparisons;
    private HashMap<Integer, OnActivityResultListener> onActivityResultListeners;
    private HashMap<Integer, OnActivityRequestPermissionResultListener> onActivityRequestPermissionResultListeners;
    private String confirmCloseTitle;
    private String confirmCloseMessage;
    private Map<String, List<String>> ruleKeys = new HashMap<>();
    private RulesEngineHelper rulesEngineHelper = null;
    private final Set<Character> JAVA_OPERATORS = new HashSet<>(Arrays.asList(new Character[]{'(', '!', ',', '?', '+', '-', '*', '/', '%', '+', '-', '.', '^', ')', '<', '>', '=', '{', '}', ':', ';'}));
    private JSONArray extraFieldsWithValues;
    private Form form;

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
            if (!mJSONObject.has("encounter_type")) {
                mJSONObject = new JSONObject();
                throw new JSONException("Form encounter_type not set");
            }
            Map<String, String> globalValues = null;
            //populate them global values
            if (mJSONObject.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson().fromJson(mJSONObject.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(), new TypeToken<HashMap<String, String>>() {
                }.getType());
            }

            rulesEngineHelper = new RulesEngineHelper(this, globalValues);

            confirmCloseTitle = getString(R.string.confirm_form_close);
            confirmCloseMessage = getString(R.string.confirm_form_close_explanation);

        } catch (JSONException e) {
            Log.d(TAG, "Initialization error. Json passed is invalid : " + e.getMessage(), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_form_activity_json_form);
        mToolbar = findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
        skipLogicViews = new HashMap<>();
        calculationLogicViews = new HashMap<>();
        onActivityResultListeners = new HashMap<>();
        onActivityRequestPermissionResultListeners = new HashMap<>();
        if (savedInstanceState == null) {
            init(getIntent().getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON));
            initializeFormFragment();
            onFormStart();
            this.form = extractForm(getIntent().getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM));
        } else {
            init(savedInstanceState.getString(JSON_STATE));
            this.form = extractForm(savedInstanceState.getSerializable(FORM_STATE));
        }

    }

    public void initializeFormFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, JsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onActivityResultListeners.containsKey(requestCode)) {
            onActivityResultListeners.get(requestCode).onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (onActivityRequestPermissionResultListeners.containsKey(requestCode)) {
            onActivityRequestPermissionResultListeners.get(requestCode).onRequestPermissionResult(requestCode, permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public synchronized JSONObject getStep(String name) {
        synchronized (mJSONObject) {
            try {
                return mJSONObject.getJSONObject(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent,
                           String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey,
                           String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup)
            throws JSONException {
        checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, popup);
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, false);
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, false);
    }

    private void widgetsWriteValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = item.has(JsonFormConstants.TYPE) ? item.getString(JsonFormConstants.TYPE) : "";
                keyAtIndex = itemType.equals(JsonFormConstants.NUMBER_SELECTORS) ? keyAtIndex + "_spinner" : keyAtIndex;
                if (key.equals(keyAtIndex)) {
                    if (item.has(JsonFormConstants.TEXT)) {
                        item.put(JsonFormConstants.TEXT, value);
                    } else {
                        if (popup) {
                            String itemText = "";
                            if (itemType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                                itemText = formUtils.getRadioButtonText(item, value);
                            }
                            genericPopupDialog.addSelectedValues(formUtils.addAssignedValue(keyAtIndex, "", value, itemType, itemText));
                            extraFieldsWithValues = fields;
                        }
                        item.put(JsonFormConstants.VALUE, value);
                    }
                    item.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openMrsEntityParent);
                    item.put(JsonFormConstants.OPENMRS_ENTITY, openMrsEntity);
                    item.put(JsonFormConstants.OPENMRS_ENTITY_ID, openMrsEntityId);
                    refreshCalculationLogic(key, null, popup);
                    refreshSkipLogic(key, null, popup);
                    refreshConstraints(key, null);
                    refreshMediaLogic(key, value);
                    return;
                }
            }
        }
    }

    private void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, boolean popup) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = "";
                if (popup) {
                    itemType = item.getString(JsonFormConstants.TYPE);
                }
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString(JsonFormConstants.KEY);
                        String itemText = "";
                        if (itemType.equals(JsonFormConstants.CHECK_BOX)) {
                            itemText = innerItem.getString(JsonFormConstants.TEXT);
                        }
                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put(JsonFormConstants.VALUE, value);
                            if (popup) {
                                genericPopupDialog.addSelectedValues(formUtils.addAssignedValue(keyAtIndex, childKey, value, itemType, itemText));
                                extraFieldsWithValues = fields;
                            }
                            refreshCalculationLogic(parentKey, childKey, popup);
                            refreshSkipLogic(parentKey, childKey, popup);
                            refreshConstraints(parentKey, childKey);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeMetaDataValue(String metaDataKey, Map<String, String> values) throws JSONException {
        synchronized (mJSONObject) {
            if (mJSONObject.has(FormUtils.METADATA_PROPERTY) && !values.isEmpty() && (mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).has(metaDataKey))) {
                JSONObject metaData = mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).getJSONObject(metaDataKey);
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value == null) value = "";
                    metaData.put(key, value);
                }
            }

        }
    }

    @Override
    public String currentJsonState() {
        synchronized (mJSONObject) {
            return mJSONObject.toString();
        }
    }

    @Override
    public String getCount() {
        synchronized (mJSONObject) {
            return mJSONObject.optString("count");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(JSON_STATE, mJSONObject.toString());
        outState.putSerializable(FORM_STATE, form);
    }

    @Override
    public void onFormStart() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(this);
            }
            FormUtils.updateStartProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFormFinish() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(this);
            }
            FormUtils.updateEndProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearSkipLogicViews() {
        skipLogicViews.clear();
    }

    @Override
    public void clearCalculationLogicViews() {
        calculationLogicViews.clear();
    }

    @Override
    public void clearConstrainedViews() {
        constrainedViews = new HashMap<>();
    }

    @Override
    public void clearFormDataViews() {
        formDataViews = new ArrayList<>();
        clearSkipLogicViews();
        clearConstrainedViews();
        clearCalculationLogicViews();
    }

    @Override
    public void refreshHiddenViews(boolean popup) {
        for (View curView : formDataViews) {
            String addressString = (String) curView.getTag(R.id.address);
            String[] address = addressString.split(":");
            try {
                JSONObject viewData = getObjectUsingAddress(address, popup);
                if (viewData.has("hidden") && viewData.getBoolean("hidden")) {
                    toggleViewVisibility(curView, false, popup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addSkipLogicView(View view) {
        skipLogicViews.put(getViewKey(view), view);
    }

    @Override
    public void addCalculationLogicView(View view) {
        calculationLogicViews.put(getViewKey(view), view);
    }

    @Override
    public void addConstrainedView(View view) {
        constrainedViews.put(getViewKey(view), view);
    }

    private String getViewKey(View view) {
        String key = (String) view.getTag(R.id.key);
        if (view.getTag(R.id.childKey) != null) {
            key = key + ":" + view.getTag(R.id.childKey);
        }

        return key;
    }

    @Override
    public void addFormDataView(View view) {
        formDataViews.add(view);
    }

    @Override
    public ArrayList<View> getFormDataViews() {
        return formDataViews;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog)
                .setTitle(confirmCloseTitle)
                .setMessage(confirmCloseMessage)
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JsonFormActivity.this.finish();
                    }
                })
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "No button on dialog in " + JsonFormActivity.class.getCanonicalName());
                    }
                })
                .create();

        dialog.show();
    }

    @Override
    public void refreshSkipLogic(String parentKey, String childKey, boolean popup) {
        initComparisons();
        for (View curView : skipLogicViews.values()) {
            addRelevance(curView, popup);
        }
    }


    private void addRelevance(View view, Boolean popup) {
        String relevanceTag = (String) view.getTag(R.id.relevance);
        if (relevanceTag != null && relevanceTag.length() > 0) {
            try {
                JSONObject relevance = new JSONObject(relevanceTag);
                Iterator<String> keys = relevance.keys();
                boolean ok = true;
                while (keys.hasNext()) {
                    String curKey = keys.next();

                    JSONObject curRelevance = relevance.has(curKey) ? relevance.getJSONObject(curKey) : null;

                    String[] address = curKey.contains(":") ? curKey.split(":") : new String[]{curKey, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE), view.getTag(R.id.address).toString().replace(':', '_')};

                    if (address.length > 1) {
                        Map<String, String> curValueMap = getValueFromAddress(address, popup);

                        try {
                            boolean comparison = isRelevant(curValueMap, curRelevance);

                            ok = ok && comparison;
                            if (!ok) break;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                    }
                }
                toggleViewVisibility(view, ok, popup);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }


    @Override
    public void refreshCalculationLogic(String parentKey, String childKey, boolean popup) {
        Collection<View> views = calculationLogicViews.values();
        for (View curView : views) {
            String calculationTag = (String) curView.getTag(R.id.calculation);
            if (calculationTag != null && calculationTag.length() > 0) {
                try {
                    JSONObject calculation = new JSONObject(calculationTag);
                    Iterator<String> keys = calculation.keys();

                    while (keys.hasNext()) {
                        String curKey = keys.next();

                        JSONObject curRelevance = calculation.getJSONObject(curKey);

                        String[] address = new String[]{curKey, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE), curView.getTag(R.id.address).toString().replace(':', '_')};

                        Map<String, String> curValueMap = getValueFromAddress(address, popup);

                        updateCalculation(curValueMap, curView, address[1]);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);

                }
            }
        }
    }

    protected void toggleViewVisibility(View view, boolean visible, boolean popup) {
        try {
            JSONArray canvasViewIds = new JSONArray((String) view.getTag(R.id.canvas_ids));
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);
            boolean enabled = visible;
            if (object.has(JsonFormConstants.READ_ONLY) && object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }

            updateCanvas(view, visible, canvasViewIds);
            setReadOnlyAndFocus(view, visible, popup);
        } catch (Exception e) {
            Log.e(TAG, view.toString());
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void setReadOnlyAndFocus(View view, boolean visible, boolean popup) {
        try {
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);

            boolean enabled = visible;
            if (object != null && object.has(JsonFormConstants.READ_ONLY) && object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if all views being watched for constraints enforce those constraints
     * This library currently only supports constraints on views that store the value in {@link MaterialEditText}
     * (ie TreeViews, DatePickers, and EditTexts), and {@link CheckBox}
     */
    @Override
    public void refreshConstraints(String parentKey, String childKey) {
        initComparisons();

        // Priorities constraints on the view that has just been changed
        String changedViewKey = parentKey;
        if (changedViewKey != null && childKey != null) {
            changedViewKey = changedViewKey + ":" + childKey;
        }

        if (changedViewKey != null && constrainedViews.containsKey(changedViewKey)) {
            checkViewConstraints(constrainedViews.get(changedViewKey), false);
        }

        for (View curView : constrainedViews.values()) {
            if (changedViewKey == null || !getViewKey(curView).equals(changedViewKey)) {
                checkViewConstraints(curView, false);
            }
        }
    }

    @Override
    public void addOnActivityResultListener(final Integer requestCode,
                                            OnActivityResultListener onActivityResultListener) {
        onActivityResultListeners.put(requestCode, onActivityResultListener);
    }

    @Override
    public void addOnActivityRequestPermissionResultListener(Integer
                                                                     requestCode, OnActivityRequestPermissionResultListener
                                                                     onActivityRequestPermissionResultListener) {
        onActivityRequestPermissionResultListeners.put(requestCode, onActivityRequestPermissionResultListener);
    }

    @Override
    public void removeOnActivityRequestPermissionResultListener(Integer requestCode) {
        onActivityRequestPermissionResultListeners.remove(requestCode);
    }

    @Override
    public void resetFocus() {
        EditText defaultFocusView = findViewById(R.id.default_focus_view);
        defaultFocusView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputManager != null && getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
        }
    }

    private void checkViewConstraints(View curView, boolean popup) {
        String constraintTag = (String) curView.getTag(R.id.constraints);
        if (constraintTag != null && constraintTag.length() > 0) {
            try {
                String addressString = (String) curView.getTag(R.id.address);
                String[] address = addressString.split(":");

                JSONArray constraint = new JSONArray(constraintTag);
                String errorMessage = null;
                for (int i = 0; i < constraint.length(); i++) {
                    JSONObject curConstraint = constraint.getJSONObject(i);
                    if (address.length == 2) {
                        String value = getValueFromAddress(address, popup).get(JsonFormConstants.VALUE);
                        errorMessage = enforceConstraint(value, curView, curConstraint);
                        if (errorMessage != null) break;
                    }
                }

                if (errorMessage != null) {
                    if (curView instanceof MaterialEditText) {
                        ((MaterialEditText) curView).setText(null);
                        ((MaterialEditText) curView).setError(errorMessage);
                    } else if (curView instanceof CheckBox) {
                        ((CheckBox) curView).setChecked(false);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        String checkBoxKey = (String) curView.getTag(R.id.childKey);

                        JSONObject questionObject = getObjectUsingAddress(address, popup);
                        for (int i = 0; i < questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                            JSONObject curOption = questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                            if (curOption.getString(JsonFormConstants.KEY).equals(checkBoxKey)) {
                                curOption.put(JsonFormConstants.VALUE, "false");
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, String> getValueFromAddress(String[] address, boolean popup) throws Exception {
        Map<String, String> result = new HashMap<>();

        JSONObject object = getObjectUsingAddress(address, popup);

        if (object != null) {

            if (object.has(RuleConstant.RESULT)) {
                JSONArray jsonArray = object.getJSONArray(RuleConstant.RESULT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject formObject = jsonArray.getJSONObject(i);

                    formObject.put(RuleConstant.IS_RULE_CHECK, true);
                    formObject.put(RuleConstant.STEP, formObject.getString(RuleConstant.STEP));

                    result.putAll(getValueFromAddressCore(formObject));
                }

                result.put(RuleConstant.SELECTED_RULE, address[2]);

            } else {

                result = getValueFromAddressCore(object);

            }
        }


        return result;
    }

    private Map<String, String> getValueFromAddressCore(JSONObject object) throws JSONException {
        Map<String, String> result = new HashMap<>();

        if (object != null) {
            switch (object.getString(JsonFormConstants.TYPE)) {
                case JsonFormConstants.CHECK_BOX:
                    JSONArray options = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int j = 0; j < options.length(); j++) {
                        if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                            if (object.has(RuleConstant.IS_RULE_CHECK)) {
                                if (Boolean.valueOf(options.getJSONObject(j).getString(JsonFormConstants.VALUE))) {
                                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                                }
                            } else {
                                result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                            }
                        } else {
                            Log.e(TAG, "option for Key " + options.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                        }
                        //Backward compatibility Fix
                        if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                            result.put(JsonFormConstants.VALUE, options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                        } else {
                            result.put(JsonFormConstants.VALUE, "false");
                        }
                    }
                    break;

                case JsonFormConstants.NATIVE_RADIO_BUTTON:
                    Boolean multiRelevance = object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
                    if (multiRelevance) {
                        JSONArray jsonArray = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            if (object.has(JsonFormConstants.VALUE)) {
                                if (object.getString(JsonFormConstants.VALUE).equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                                    result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(true));
                                } else {
                                    if (!object.has(RuleConstant.IS_RULE_CHECK)) {
                                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(false));
                                    }
                                }
                            } else {
                                Log.e(TAG, "option for Key " + jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                            }
                        }
                    } else {
                        result.put(getKey(object), getValue(object));
                    }
                    break;

                default:
                    result.put(getKey(object), getValue(object));
                    break;
            }

            if (object.has(RuleConstant.IS_RULE_CHECK) && (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) && object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false)))) {
                List<String> selectedValues = new ArrayList<>(result.keySet());
                result.clear();
                result.put(getKey(object), selectedValues.toString());
            }
        }
        return result;
    }

    @Override
    public JSONObject getObjectUsingAddress(String[] address, boolean popup) throws JSONException {
        if (address != null && address.length > 1) {

            if (RuleConstant.RULES_ENGINE.equals(address[0])) {

                String fieldKey = address[2];

                List<String> rulesList = getRules(address[1], fieldKey);
                if (rulesList != null) {
                    JSONObject result = new JSONObject();

                    JSONArray rulesArray = new JSONArray();

                    for (Integer h = 1; h < mJSONObject.getInt(JsonFormConstants.COUNT) + 1; h++) {
                        JSONArray fields = fetchFields(mJSONObject.getJSONObject(RuleConstant.STEP + h), popup);
                        for (int i = 0; i < fields.length(); i++) {
                            if (rulesList.contains(RuleConstant.STEP + h + "_" + fields.getJSONObject(i).getString(JsonFormConstants.KEY))) {

                                JSONObject fieldObject = fields.getJSONObject(i);
                                fieldObject.put(RuleConstant.STEP, RuleConstant.STEP + h);

                                rulesArray.put(fieldObject);
                            }
                        }
                    }
                    result.put(RuleConstant.RESULT, rulesArray);
                    return result;
                }


            } else {


                JSONArray fields = fetchFields(mJSONObject.getJSONObject(address[0]), popup);
                for (int i = 0; i < fields.length(); i++) {
                    if (fields.getJSONObject(i).getString(JsonFormConstants.KEY).equals(address[1])) {
                        return fields.getJSONObject(i);
                    }
                }
            }
        }

        return null;
    }

    private void initComparisons() {
        if (comparisons == null) {
            functionRegex = "";
            comparisons = new HashMap<>();

            LessThanComparison lessThanComparison = new LessThanComparison();
            functionRegex += lessThanComparison.getFunctionName();
            comparisons.put(lessThanComparison.getFunctionName(), lessThanComparison);

            LessThanEqualToComparison lessThanEqualToComparison = new LessThanEqualToComparison();
            functionRegex += "|" + lessThanEqualToComparison.getFunctionName();
            comparisons.put(lessThanEqualToComparison.getFunctionName(), lessThanEqualToComparison);

            EqualToComparison equalToComparison = new EqualToComparison();
            functionRegex += "|" + equalToComparison.getFunctionName();
            comparisons.put(equalToComparison.getFunctionName(), equalToComparison);

            NotEqualToComparison notEqualToComparer = new NotEqualToComparison();
            functionRegex += "|" + notEqualToComparer.getFunctionName();
            comparisons.put(notEqualToComparer.getFunctionName(), notEqualToComparer);

            GreaterThanComparison greaterThanComparison = new GreaterThanComparison();
            functionRegex += "|" + greaterThanComparison.getFunctionName();
            comparisons.put(greaterThanComparison.getFunctionName(), greaterThanComparison);

            GreaterThanEqualToComparison greaterThanEqualToComparison = new GreaterThanEqualToComparison();
            functionRegex += "|" + greaterThanEqualToComparison.getFunctionName();
            comparisons.put(greaterThanEqualToComparison.getFunctionName(), greaterThanEqualToComparison);

            RegexComparison regexComparison = new RegexComparison();
            functionRegex += "|" + regexComparison.getFunctionName();
            comparisons.put(regexComparison.getFunctionName(), regexComparison);
        }
    }

    private boolean doComparison(String value, JSONObject comparison) throws Exception {
        String type = comparison.getString("type").toLowerCase();
        String ex = comparison.getString("ex");

        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);//functions arguments should be two, and should either be addresses or values (enclosed using "")
            String[] args = getFunctionArgs(b, value);
            return comparisons.get(functionName).compare(args[0], type, args[1]);
        }

        return false;
    }

    private String[] getFunctionArgs(String functionArgs, String value) {
        String[] args = new String[2];
        String[] splitArgs = functionArgs.split(",");
        if (splitArgs.length == 2) {
            Pattern valueRegex = Pattern.compile("\"(.*)\"");
            for (int i = 0; i < splitArgs.length; i++) {
                String curArg = splitArgs[i].trim();

                if (".".equals(curArg)) {
                    args[i] = value;
                } else {
                    Matcher valueMatcher = valueRegex.matcher(curArg);
                    if (valueMatcher.find()) {
                        args[i] = valueMatcher.group(1);
                    } else {
                        try {
                            args[i] = getValueFromAddress(curArg.split(":"), false).get(JsonFormConstants.VALUE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return args;
    }

    private void refreshMediaLogic(String key, String value) {
        try {
            JSONObject object = getStep("step1");
            JSONArray fields = object.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject questionGroup = fields.getJSONObject(i);
                if ((questionGroup.has("key") && questionGroup.has("has_media_content"))
                        && (questionGroup.getString("key").equalsIgnoreCase(key))
                        && (questionGroup.getBoolean("has_media_content"))) {
                    JSONArray medias = questionGroup.getJSONArray("media");
                    for (int j = 0; j < medias.length(); j++) {
                        JSONObject media = medias.getJSONObject(j);
                        mediaDialog(media, value);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void mediaDialog(JSONObject media, String value) {
        try {
            if (media.getString("media_trigger_value").equalsIgnoreCase(value)) {
                String mediatype = media.getString("media_type");
                String medialink = media.getString("media_link");
                String mediatext = media.getString("media_text");

                infoDialog(mediatype, medialink, mediatext);
            }
        } catch (Exception e) {

        }
    }

    private void infoDialog(String mediatype, String medialink, String mediatext) {
        final FancyAlertDialog.Builder builder = new FancyAlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setBackgroundColor(Color.parseColor("#208CC5")).setPositiveBtnBackground(Color.parseColor("#208CC5"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("OK").setAnimation(Animation.SLIDE)
                .isCancellable(true)
                .setIcon(com.shashank.sony.fancydialoglib.R.drawable.ic_person_black_24dp, Icon.Visible);
        builder.setMessage(mediatext);
        if (mediatype.equalsIgnoreCase("image")) {
            builder.setImagetoshow(medialink);
        } else if (mediatype.equalsIgnoreCase("video")) {
            builder.setVideopath(medialink);
        }
        builder.build();
    }


    /**
     * This method checks whether a constraint has been enforced and returns an error message if not
     * The error message should be displayable to the user
     *
     * @param value      The value to be checked
     * @param constraint The constraint expression to use
     * @return An error message if constraint has not been enfored or NULL if constraint enforced
     * @throws Exception
     */
    private String enforceConstraint(String value, View view, JSONObject constraint) throws
            Exception {
        String type = constraint.getString("type").toLowerCase();
        String ex = constraint.getString("ex");
        String errorMessage = constraint.getString(JsonFormConstants.ERR);
        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);
            String[] args = getFunctionArgs(b, value);

            boolean viewDoesntHaveValue = TextUtils.isEmpty(value);
            if (view instanceof CheckBox) {
                viewDoesntHaveValue = !((CheckBox) view).isChecked();
            }

            if (viewDoesntHaveValue
                    || TextUtils.isEmpty(args[0])
                    || TextUtils.isEmpty(args[1])
                    || comparisons.get(functionName).compare(args[0], type, args[1])) {
                return null;
            }
        } else {
            Log.d(TAG, "Matcher didn't work with function");
        }

        return errorMessage;
    }

    private JSONArray fetchFields(JSONObject parentJson, Boolean popup) {
        JSONArray fields = new JSONArray();
        try {
            if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);

                for (int i = 0; i < sections.length(); i++) {
                    JSONObject sectionJson = sections.getJSONObject(i);
                    if (sectionJson.has(JsonFormConstants.FIELDS)) {
                        if (popup) {
                            JSONArray jsonArray = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject item = jsonArray.getJSONObject(k);
                                if (item.getString(JsonFormConstants.KEY).equals(genericPopupDialog.getParentKey())) {
                                    if (item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                                        fields = formUtils.concatArray(fields, specifyFields(item));
                                    }
                                }
                            }
                        } else {
                            fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                        }

                    }
                }
            } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                if (popup) {
                    JSONArray jsonArray = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject item = jsonArray.getJSONObject(k);
                        if (item.getString(JsonFormConstants.KEY).equals(genericPopupDialog.getParentKey()) && item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                            fields = specifyFields(item);
                        }
                    }
                } else {
                    fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fields;
    }

    private JSONArray specifyFields(JSONObject parentJson) {
        JSONArray fields = new JSONArray();
        if (parentJson.has(JsonFormConstants.HAS_EXTRA_REL)) {
            String optionKey;
            try {
                optionKey = (String) parentJson.get(JsonFormConstants.HAS_EXTRA_REL);
                JSONArray options = parentJson.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                if (options.length() > 0) {
                    for (int j = 0; j < options.length(); j++) {
                        JSONObject jsonObject = options.getJSONObject(j);
                        String objectKey = (String) jsonObject.get(JsonFormConstants.KEY);
                        if (objectKey.equals(optionKey) && jsonObject.has(JsonFormConstants.CONTENT_FORM)) {
                            if (extraFieldsWithValues != null) {
                                fields = extraFieldsWithValues;
                            } else {
                                fields = getSubFormFields(jsonObject.get(JsonFormConstants.CONTENT_FORM).toString(), jsonObject.get(JsonFormConstants.CONTENT_FORM_LOCATION).toString(), fields);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    private JSONArray getSubFormFields(String subFormName, String subFormLocation, JSONArray
            fields) {
        JSONArray fieldArray = new JSONArray();
        genericPopupDialog.setFormIdentity(subFormName);
        genericPopupDialog.setFormLocation(subFormLocation);
        JSONObject jsonObject = genericPopupDialog.getSubFormJson("", getApplicationContext());
        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.CONTENT_FORM);
                if (jsonArray != null && jsonArray.length() > 0) {
                    fieldArray = formUtils.concatArray(fields, jsonArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fieldArray;
    }


    public String getConfirmCloseTitle() {
        return confirmCloseTitle;
    }

    public void setConfirmCloseTitle(String confirmCloseTitle) {
        this.confirmCloseTitle = confirmCloseTitle;
    }

    public String getConfirmCloseMessage() {
        return confirmCloseMessage;
    }

    public void setConfirmCloseMessage(String confirmCloseMessage) {
        this.confirmCloseMessage = confirmCloseMessage;
    }

    public Form getForm() {
        return form;
    }

    private boolean isRelevant(Map<String, String> curValueMap, JSONObject curRelevance) throws
            Exception {


        if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_RULES))

        {

            return curValueMap.size() == 0 ? false : rulesEngineHelper.getRelevance(curValueMap, curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE));

        } else if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_CHECKBOX))

        {


            JSONArray exArray = curRelevance.getJSONArray(JsonFormConstants.JSON_FORM_KEY.EX_CHECKBOX);

            for (int i = 0; i < exArray.length(); i++) {
                ExObjectResult exObjectResult = isExObjectRelevant(curValueMap, exArray.getJSONObject(i));
                if (exObjectResult.isRelevant()) {
                    return true;
                } else if (!exObjectResult.isRelevant() && exObjectResult.isFinal()) {
                    return false;
                }

            }
            return false;
        } else

        {
            return doComparison(curValueMap.get(JsonFormConstants.VALUE), curRelevance);
        }

    }

    private ExObjectResult isExObjectRelevant
            (Map<String, String> curValueMap, JSONObject object) throws Exception {
        if (object.has(JsonFormConstants.JSON_FORM_KEY.NOT)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.NOT);

            for (int i = 0; i < orArray.length(); i++) {
                if (!Boolean.valueOf(curValueMap.get(orArray.getString(i)))) {
                    return new ExObjectResult(true, false);
                } else {
                    return new ExObjectResult(false, true);
                }
            }
        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.OR)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.OR);

            for (int i = 0; i < orArray.length(); i++) {
                if (Boolean.valueOf(curValueMap.get(orArray.getString(i)))) {
                    return new ExObjectResult(true, true);
                }

            }

        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.AND)) {
            JSONArray andArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.AND);

            for (int i = 0; i < andArray.length(); i++) {
                if (!Boolean.valueOf(curValueMap.get(andArray.getString(i)))) {
                    return new ExObjectResult(false, false);
                }
            }
            return new ExObjectResult(true, false);

        }

        return new ExObjectResult(false, false);
    }


    private List<String> getRules(String filename, String fieldKey) {

        List<String> rules = ruleKeys.get(filename + ":" + fieldKey);

        try {

            if (rules == null) {

                Yaml yaml = new Yaml();
                InputStreamReader inputStreamReader = new InputStreamReader(this.getAssets().open((rulesEngineHelper.getRulesFolderPath() + filename)));
                Iterable<Object> ruleObjects = yaml.loadAll(inputStreamReader);

                for (Object object : ruleObjects) {
                    Map<String, Object> map = ((Map<String, Object>) object);

                    String name = map.get(RuleConstant.NAME).toString();

                    List<String> actions = new ArrayList<>();

                    String conditionString = map.get(RuleConstant.CONDITION).toString();

                    List<String> fields = (List<String>) map.get(RuleConstant.ACTIONS);
                    if (fields != null) {
                        for (String field : fields) {
                            if (field.trim().startsWith(RuleConstant.CALCULATION)) {
                                conditionString += " " + field;
                            }
                        }

                    }

                    actions.addAll(getConditionKeys(conditionString));
                    ruleKeys.put(filename + ":" + name, actions);

                    if (name.equals(fieldKey)) {
                        break;
                    }

                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return ruleKeys.get(filename + ":" + fieldKey);
    }

    @Override
    public JSONObject getmJSONObject() {
        return mJSONObject;
    }

    @Override
    public void setmJSONObject(JSONObject mJSONObject) {
        this.mJSONObject = mJSONObject;
    }

    private Form extractForm(Serializable serializable) {
        if (serializable != null && serializable instanceof Form) {
            return (Form) serializable;
        } else {
            return null;
        }
    }

    private List<String> getConditionKeys(String condition) {
        String[] conditionTokens = condition.split(" ");
        Map<String, Boolean> conditionKeys = new HashMap<>();

        for (int i = 0; i < conditionTokens.length; i++) {

            if (conditionTokens[i].contains(RuleConstant.STEP) || conditionTokens[i].contains(RuleConstant.PREFIX.GLOBAL)) {
                String conditionToken = cleanToken(conditionTokens[i]);

                conditionKeys.put(conditionToken, true);
            }

        }

        return new ArrayList<>(conditionKeys.keySet());


    }

    @NonNull
    private String cleanToken(String conditionTokenRaw) {

        String conditionToken = conditionTokenRaw.trim();


        for (int i = 0; i < conditionToken.length(); i++) {

            if (JAVA_OPERATORS.contains(conditionToken.charAt(i))) {

                if (i == 0) {
                    conditionToken = cleanToken(conditionToken.substring(1));
                } else {

                    conditionToken = conditionToken.substring(0, conditionToken.indexOf(conditionToken.charAt(i)));

                    break;
                }
            }

        }

        return conditionToken;
    }

    private void updateCalculation(Map<String, String> valueMap, View view, String rulesFile) {

        try {


            String calculation = rulesEngineHelper.getCalculation(valueMap, rulesFile);

            if (view instanceof TextableView) {
                TextableView textView = ((TextableView) view);
                textView.setText(calculation.charAt(0) == '{' ? getRenderText(calculation, textView.getTag(R.id.original_text).toString()) : calculation);
            } else if (view instanceof EditText) {
                ((EditText) view).setText(calculation);
                view.setTag(R.id.is_first_time, true);

            } else {

                ((TextView) view).setText(calculation);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Log.d(TAG, "calling updateCalculation on Non TextView or Text View decendant");
        }

    }

    private String getRenderText(String calculation, String textTemplate) throws Exception {
        JSONObject jsonObject = new JSONObject(calculation);
        Map<String, String> valueMap = new Gson().fromJson((jsonObject).toString(), new TypeToken<HashMap<String, String>>() {
        }.getType());

        return stringFormat(textTemplate, valueMap);
    }

    public String stringFormat(String string, Map<String, String> valueMap) {
        String resString = string;
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            resString = resString.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return resString;
    }

    private String getValue(JSONObject object) throws JSONException {
        String value = object.optString(JsonFormConstants.VALUE);

        if (object.has(JsonFormConstants.EDIT_TYPE) && object.getString(JsonFormConstants.EDIT_TYPE).equals(JsonFormConstants.EDIT_TEXT_TYPE.NUMBER) && TextUtils.isEmpty(object.optString(JsonFormConstants.VALUE))) {
            value = "0";
        }

        return value;
    }

    private String getKey(JSONObject object) throws JSONException {
        return object.has(RuleConstant.IS_RULE_CHECK) ? object.get(RuleConstant.STEP) + "_" + object.get(JsonFormConstants.KEY) : JsonFormConstants.VALUE;
    }

    private void updateCanvas(View view, boolean visible, JSONArray canvasViewIds) throws JSONException {
        for (int i = 0; i < canvasViewIds.length(); i++) {
            int curId = canvasViewIds.getInt(i);
            View curCanvasView = view.getRootView().findViewById(curId);
            if (visible) {
                if (curCanvasView != null) {
                    curCanvasView.setEnabled(true);
                    curCanvasView.setVisibility(View.VISIBLE);
                }
                if (curCanvasView instanceof RelativeLayout || view instanceof LinearLayout) {
                    curCanvasView.setFocusable(true);
                }
                if (view instanceof EditText) {
                    view.setFocusable(true);
                }
            } else {
                if (curCanvasView != null) {
                    curCanvasView.setEnabled(false);
                    curCanvasView.setVisibility(View.GONE);
                }
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setText("");
                    }

                }
            }
        }
    }

    @Override
    public void updateGenericPopupSecondaryValues(JSONArray jsonArray) {
        extraFieldsWithValues = jsonArray;
    }
}

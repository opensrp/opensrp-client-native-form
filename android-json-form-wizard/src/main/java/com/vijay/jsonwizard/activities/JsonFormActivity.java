package com.vijay.jsonwizard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.TextableView;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.GenericDialogInterface;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.LifeCycleListener;
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineFactory;
import com.vijay.jsonwizard.utils.ExObjectResult;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.PropertyManager;
import com.vijay.jsonwizard.widgets.NumberSelectorFactory;

import org.jeasy.rules.api.Facts;
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
import java.util.LinkedHashMap;
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
    private final Set<Character> JAVA_OPERATORS = new HashSet<>(
            Arrays.asList('(', '!', ',', '?', '+', '-', '*', '/', '%', '+', '-', '.', '^', ')', '<', '>', '=', '{', '}', ':',
                    ';', '[', ']'));
    private final List<String> PREFICES_OF_INTEREST = Arrays.asList(RuleConstant.PREFIX.GLOBAL, RuleConstant.STEP);
    private FormUtils formUtils = new FormUtils();
    private Toolbar mToolbar;
    private JSONObject mJSONObject;
    private PropertyManager propertyManager;
    private Map<String, View> skipLogicViews;
    private Map<String, View> calculationLogicViews;
    private Map<String, View> constrainedViews;
    private ArrayList<View> formDataViews;
    private String functionRegex;
    private HashMap<String, Comparison> comparisons;
    private HashMap<Integer, OnActivityResultListener> onActivityResultListeners;
    private HashMap<Integer, OnActivityRequestPermissionResultListener> onActivityRequestPermissionResultListeners;
    private List<LifeCycleListener> lifeCycleListeners;
    private String confirmCloseTitle;
    private String confirmCloseMessage;
    private Map<String, List<String>> ruleKeys = new HashMap<>();
    private GenericDialogInterface genericDialogInterface;
    private JSONArray extraFieldsWithValues;
    private Form form;
    private Map<String, String> globalValues = null;
    private RulesEngineFactory rulesEngineFactory = null;
    private LocalBroadcastManager localBroadcastManager;
    private Map<String, String> formValuesCacheMap = new HashMap<>();
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String messageType = intent.getStringExtra(JsonFormConstants.INTENT_KEY.MESSAGE_TYPE);

            switch (messageType) {
                case JsonFormConstants.MESSAGE_TYPE.GLOBAL_VALUES:

                    Map<String, String> map =
                            (Map<String, String>) intent.getSerializableExtra(JsonFormConstants.INTENT_KEY.MESSAGE);
                    globalValues.putAll(map);

                    break;
                default:
                    break;

            }
            // Log.d(TAG, "Received Broadcast Message Type " + messageType);
        }
    };

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
            if (!mJSONObject.has("encounter_type")) {
                mJSONObject = new JSONObject();
                throw new JSONException("Form encounter_type not set");
            }

            //populate them global values
            if (mJSONObject.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson()
                        .fromJson(mJSONObject.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(),
                                new TypeToken<HashMap<String, String>>() {
                                }.getType());
            } else {
                globalValues = new HashMap<>();
            }

            rulesEngineFactory = new RulesEngineFactory(this, globalValues);

            confirmCloseTitle = getString(R.string.confirm_form_close);
            confirmCloseMessage = getString(R.string.confirm_form_close_explanation);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);

        } catch (JSONException e) {
            Log.e(TAG, "Initialization error. Json passed is invalid : " + e.getMessage(), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_form_activity_json_form);
        mToolbar = findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
        skipLogicViews = new LinkedHashMap<>();
        calculationLogicViews = new LinkedHashMap<>();
        onActivityResultListeners = new HashMap<>();
        onActivityRequestPermissionResultListeners = new HashMap<>();
        lifeCycleListeners = new ArrayList<>();
        if (savedInstanceState == null) {
            init(getIntent().getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON));
            initializeFormFragment();
            onFormStart();
            this.form = extractForm(getIntent().getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM));
        } else {
            init(savedInstanceState.getString(JSON_STATE));
            this.form = extractForm(savedInstanceState.getSerializable(FORM_STATE));
        }
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onCreate(savedInstanceState);
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
            onActivityRequestPermissionResultListeners.get(requestCode)
                    .onRequestPermissionResult(requestCode, permissions, grantResults);
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
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup) throws JSONException {
        if (invokeRefreshLogic(stepName, null, key, value)) {
            cacheFormMapValues(stepName, null, key, value);
            widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup)
            throws JSONException {
        if (invokeRefreshLogic(stepName, parentKey, childKey, value)) {
            cacheFormMapValues(stepName, parentKey, childKey, value);
            checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, popup);

        }
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId) throws JSONException {

        if (invokeRefreshLogic(stepName, null, key, value)) {
            cacheFormMapValues(stepName, null, key, value);
            widgetsWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, false);
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        if (invokeRefreshLogic(stepName, parentKey, childKey, value)) {
            cacheFormMapValues(stepName, parentKey, childKey, value);
            checkBoxWriteValue(stepName, parentKey, childObjectKey, childKey, value, false);

        }
    }

    protected void widgetsWriteValue(String stepName, String key, String value, String openMrsEntityParent,
                                     String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = item.has(JsonFormConstants.TYPE) ? item.getString(JsonFormConstants.TYPE) : "";
                boolean isSpecialWidget = isSpecialWidget(itemType);
                String cleanKey = isSpecialWidget ? cleanWidgetKey(key, itemType) : key;

                if (cleanKey.equals(keyAtIndex)) {
                    if (item.has(JsonFormConstants.TEXT)) {
                        item.put(JsonFormConstants.TEXT, value);
                    } else {
                        performPopupOperations(value, popup, item, keyAtIndex, itemType);
                        widgetWriteItemValue(value, item, itemType);
                    }
                    addOpenMrsAttributes(openMrsEntityParent, openMrsEntity, openMrsEntityId, item);

                    invokeRefreshLogic(value, popup, cleanKey, null);
                    return;
                }
            }
        }
    }

    private void addOpenMrsAttributes(String openMrsEntityParent, String openMrsEntity, String openMrsEntityId,
                                      JSONObject item) throws JSONException {
        item.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openMrsEntityParent);
        item.put(JsonFormConstants.OPENMRS_ENTITY, openMrsEntity);
        item.put(JsonFormConstants.OPENMRS_ENTITY_ID, openMrsEntityId);
    }

    private void widgetWriteItemValue(String value, JSONObject item, String itemType) throws JSONException {
        item.put(JsonFormConstants.VALUE, itemType.equals(JsonFormConstants.HIDDEN) && TextUtils.isEmpty(value) ?
                item.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(item.getString(JsonFormConstants.VALUE)) ?
                        item.getString(JsonFormConstants.VALUE) : value : value);
    }

    private void performPopupOperations(String value, boolean popup, JSONObject item, String keyAtIndex, String itemType) {
        if (popup) {
            String itemText = "";
            if (itemType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
                itemText = formUtils.getRadioButtonText(item, value);
            }
            genericDialogInterface.addSelectedValues(formUtils.addAssignedValue(keyAtIndex, "", value, itemType, itemText));
            //extraFieldsWithValues = fields;
        }
    }

    private boolean checkPopUpValidity(String[] curKey, boolean popup) throws JSONException {
        boolean validity = false;
        if (popup) {
            String parentKey = "";
            if (curKey.length == 2) {
                parentKey = curKey[1];
            } else if (curKey.length == 3) {
                parentKey = curKey[2].substring(curKey[2].indexOf("_") + 1);
            }

            if (extraFieldsWithValues != null && extraFieldsWithValues.length() > 0) {
                for (int i = 0; i < extraFieldsWithValues.length(); i++) {
                    JSONObject jsonObject = extraFieldsWithValues.getJSONObject(i);
                    if (jsonObject.has(JsonFormConstants.KEY) &&
                            jsonObject.getString(JsonFormConstants.KEY).equals(parentKey)) {
                        validity = true;
                        break;
                    }
                }
            }
        }
        return validity;
    }

    @Override
    public void invokeRefreshLogic(String value, boolean popup, String parentKey, String childKey) {
        refreshCalculationLogic(parentKey, childKey, popup);
        refreshSkipLogic(parentKey, childKey, popup);
        refreshConstraints(parentKey, childKey, popup);
        refreshMediaLogic(parentKey, value);
    }

    protected boolean isSpecialWidget(String itemType) {
        return isNumberSelector(itemType);
    }

    protected String cleanWidgetKey(String itemKey, String itemType) {
        String key = itemKey;

        if (isNumberSelector(itemType) && itemKey.endsWith(JsonFormConstants.SUFFIX.TEXT_VIEW) ||
                itemKey.endsWith(JsonFormConstants.SUFFIX.SPINNER)) {
            key = itemKey.endsWith(JsonFormConstants.SUFFIX.TEXT_VIEW) ?
                    itemKey.substring(0, itemKey.indexOf(JsonFormConstants.SUFFIX.TEXT_VIEW)) :
                    itemKey.substring(0, itemKey.indexOf(JsonFormConstants.SUFFIX.SPINNER));
        }

        return key;
    }

    private boolean isNumberSelector(String itemType) {
        return itemType.equals(JsonFormConstants.NUMBER_SELECTOR);
    }

    protected void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey,
                                      String value, boolean popup) throws JSONException {
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
                                genericDialogInterface.addSelectedValues(
                                        formUtils.addAssignedValue(keyAtIndex, childKey, value, itemType, itemText));
                            }

                            invokeRefreshLogic(value, popup, parentKey, childKey);

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
            if (mJSONObject.has(FormUtils.METADATA_PROPERTY) && !values.isEmpty() &&
                    (mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).has(metaDataKey))) {
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
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onSaveInstanceState(outState);
        }
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
        constrainedViews = new LinkedHashMap<>();
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
                if (viewData.has(JsonFormConstants.HIDDEN) && viewData.getBoolean(JsonFormConstants.HIDDEN)) {
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
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                .setMessage(confirmCloseMessage).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JsonFormActivity.this.finish();
                    }
                }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "No button on dialog in " + JsonFormActivity.class.getCanonicalName());
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void refreshSkipLogic(String parentKey, String childKey, boolean popup) {
        initComparisons();
        for (View curView : skipLogicViews.values()) {
            addRelevance(curView, popup);
        }
    }

    protected void addRelevance(View view, boolean popup) {
        String relevanceTag = (String) view.getTag(R.id.relevance);
        if (relevanceTag != null && relevanceTag.length() > 0) {
            try {
                boolean isPopup = popup;
                JSONObject relevance = new JSONObject(relevanceTag);
                Iterator<String> keys = relevance.keys();
                boolean ok = true;
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    JSONObject curRelevance = relevance.has(curKey) ? relevance.getJSONObject(curKey) : null;

                    String[] address = getAddress(view, curKey, curRelevance);
                    isPopup = checkPopUpValidity(address, popup);
                    if (address.length > 1) {
                        Facts curValueMap = getValueFromAddress(address, isPopup);
                        try {
                            boolean comparison = isRelevant(curValueMap, curRelevance);

                            ok = ok && comparison;
                            if (!ok) break;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                    }
                }
                toggleViewVisibility(view, ok, isPopup);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private String[] getAddress(View view, String curKey, JSONObject curRelevance) throws JSONException {
        return curKey.contains(":") ? curKey.split(":") : new String[]{curKey,
                curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE),
                view.getTag(R.id.address).toString().replace(':', '_')};
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

                        String[] address = new String[]{curKey,
                                curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(
                                        RuleConstant.RULES_FILE), curView.getTag(R.id.address).toString().replace(':', '_')};

                        Facts curValueMap = getValueFromAddress(address, popup);

                        if (address.length > 2 && RuleConstant.RULES_ENGINE.equals(address[0]) &&
                                (!JsonFormConstants.TOASTER_NOTES.equals(curView.getTag(R.id.type)) &&
                                        !JsonFormConstants.NATIVE_RADIO_BUTTON.equals(curView.getTag(R.id.type)))) {

                            //check for integrity of values
                            updateCalculation(curValueMap, curView, address[1]);
                        } else {
                            updateCalculation(curValueMap, curView, address[1]);
                        }
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
            if (object != null && object.has(JsonFormConstants.READ_ONLY) &&
                    object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }

            updateCanvas(view, visible, canvasViewIds,addressString,object);
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
            if (object != null && object.has(JsonFormConstants.READ_ONLY) &&
                    object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
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
     * This method checks if all views being watched for constraints enforce those constraints This library currently only
     * supports constraints on views that store the value in {@link MaterialEditText} (ie TreeViews, DatePickers, and
     * EditTexts), and {@link CheckBox}
     *
     * @param parentKey
     * @param childKey
     */
    @Override
    public void refreshConstraints(String parentKey, String childKey, boolean popup) {
        initComparisons();

        // Priorities constraints on the view that has just been changed
        String changedViewKey = parentKey;
        if (changedViewKey != null && childKey != null) {
            changedViewKey = changedViewKey + ":" + childKey;
        }

        if (changedViewKey != null && (constrainedViews != null && constrainedViews.containsKey(changedViewKey))) {
            checkViewConstraints(constrainedViews.get(changedViewKey), popup);
        }

        for (View curView : constrainedViews.values()) {
            String viewKey = getViewKey(curView);
            if (changedViewKey == null || (!TextUtils.isEmpty(viewKey) && !viewKey.equals(changedViewKey))) {
                checkViewConstraints(curView, popup);
            }
        }
    }

    @Override
    public void addOnActivityResultListener(final Integer requestCode, OnActivityResultListener onActivityResultListener) {
        onActivityResultListeners.put(requestCode, onActivityResultListener);
    }

    @Override
    public void addOnActivityRequestPermissionResultListener(Integer requestCode,
                                                             OnActivityRequestPermissionResultListener onActivityRequestPermissionResultListener) {
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

                String errorMessage = null;
                String[] address = null;

                if (constraintTag.charAt(0) == '[') {
                    String addressString = (String) curView.getTag(R.id.address);
                    address = addressString.split(":");

                    JSONArray constraint = new JSONArray(constraintTag);
                    for (int i = 0; i < constraint.length(); i++) {
                        JSONObject curConstraint = constraint.getJSONObject(i);
                        if (address.length == 2) {
                            String value = getValueFromAddress(address, popup).get(JsonFormConstants.VALUE);
                            errorMessage = enforceConstraint(value, curView, curConstraint);
                            if (errorMessage != null) break;
                        }
                    }

                } else {
                    //Rules Engine
                    JSONObject constraint = new JSONObject(constraintTag);
                    Iterator<String> keys = constraint.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();

                        JSONObject curConstraint = constraint.getJSONObject(curKey);

                        address = getAddress(curView, curKey, curConstraint);

                        Facts curValueMap = getValueFromAddress(address, popup);

                        errorMessage = enforceConstraint(curValueMap, curConstraint);

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
                        for (int i = 0;
                             i < questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                            JSONObject curOption =
                                    questionObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                            if (curOption.getString(JsonFormConstants.KEY).equals(checkBoxKey)) {
                                curOption.put(JsonFormConstants.VALUE, "false");
                                break;
                            }
                        }
                    } else if (curView.getTag(R.id.type).toString().equals(JsonFormConstants.NUMBER_SELECTOR) &&
                            !TextUtils.isEmpty(errorMessage) &&
                            (curView.getTag(R.id.previous) == null || !curView.getTag(R.id.previous).equals(errorMessage))) {

                        if (!"false".equals(errorMessage)) {
                            Intent localIntent = new Intent(JsonFormConstants.INTENT_ACTION.NUMBER_SELECTOR_FACTORY);
                            localIntent.putExtra(JsonFormConstants.MAX_SELECTION_VALUE, Integer.valueOf(errorMessage));
                            localIntent.putExtra(JsonFormConstants.JSON_OBJECT_KEY, curView.getTag(R.id.key).toString());
                            localIntent.putExtra(JsonFormConstants.STEPNAME, address[0]);
                            localIntent.putExtra(JsonFormConstants.IS_POPUP, popup);
                            localBroadcastManager.sendBroadcast(localIntent);
                            curView.setTag(R.id.previous, errorMessage); //Store value to avoid re-fires
                        }


                    } else if (curView instanceof RadioGroup &&
                            curView.getTag(R.id.type).toString().equals(JsonFormConstants.NATIVE_RADIO_BUTTON) &&
                            !TextUtils.isEmpty(errorMessage) &&
                            (curView.getTag(R.id.previous) == null || !curView.getTag(R.id.previous).equals(errorMessage))) {

                        JSONObject jsonObject = (JSONObject) curView.getTag(R.id.json_object);
                        JSONObject jsonObjectNew = new JSONObject(errorMessage);
                        Iterator<String> keys = jsonObjectNew.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            jsonObject.put(key, jsonObjectNew.getString(key));
                        }

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private Facts getValueFromAddress(String[] address, boolean popup) throws Exception {
        Facts result = new Facts();

        JSONObject object = getObjectUsingAddress(address, popup);

        if (object != null) {
            //reset the rules check value
            object.put(RuleConstant.IS_RULE_CHECK, false);
            if (object.has(RuleConstant.RESULT)) {
                JSONArray jsonArray = object.getJSONArray(RuleConstant.RESULT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject formObject = jsonArray.getJSONObject(i);

                    formObject.put(RuleConstant.IS_RULE_CHECK, true);
                    formObject.put(RuleConstant.STEP, formObject.getString(RuleConstant.STEP));

                    result.asMap().putAll(getValueFromAddressCore(formObject).asMap());
                }

                result.put(RuleConstant.SELECTED_RULE, address[2]);
            } else {
                result = getValueFromAddressCore(object);
            }
        }

        return result;
    }

    protected Facts getValueFromAddressCore(JSONObject object) throws JSONException {
        Facts result = new Facts();

        if (object != null) {
            switch (object.getString(JsonFormConstants.TYPE)) {
                case JsonFormConstants.CHECK_BOX:
                    JSONArray options = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int j = 0; j < options.length(); j++) {
                        if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                            if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                                if (Boolean.valueOf(options.getJSONObject(j)
                                        .getString(JsonFormConstants.VALUE))) {//Rules engine useth only
                                    // true values
                                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                                            options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                                }
                            } else {
                                result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                                        options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                            }
                        }

                        //Backward compatibility Fix
                        if (object.has(RuleConstant.IS_RULE_CHECK) && !object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                            if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                                result.put(JsonFormConstants.VALUE,
                                        options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                            } else {
                                result.put(JsonFormConstants.VALUE, "false");
                            }
                        }
                    }
                    break;

                case JsonFormConstants.NATIVE_RADIO_BUTTON:
                    boolean multiRelevance = object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
                    if (multiRelevance) {
                        JSONArray jsonArray = object.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            if (object.has(JsonFormConstants.VALUE)) {
                                if (object.getString(JsonFormConstants.VALUE)
                                        .equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                                    result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY),
                                            String.valueOf(true));
                                } else {
                                    if (!object.has(RuleConstant.IS_RULE_CHECK) ||
                                            !object.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY),
                                                String.valueOf(false));
                                    }
                                }
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

            if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) &&
                    (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) ||
                            (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) &&
                                    object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false)))) {
                List<String> selectedValues = new ArrayList<>(result.asMap().keySet());
                result = new Facts();
                result.put(getKey(object), selectedValues);
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
                            if (rulesList.contains(RuleConstant.STEP + h + "_" +
                                    fields.getJSONObject(i).getString(JsonFormConstants.KEY))) {

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
            String b = matcher.group(
                    2);//functions arguments should be two, and should either be addresses or values (enclosed using "")
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

    protected void refreshMediaLogic(String key, String value) {
        try {
            JSONObject object = getStep("step1");
            JSONArray fields = object.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject questionGroup = fields.getJSONObject(i);
                if ((questionGroup.has("key") && questionGroup.has("has_media_content")) &&
                        (questionGroup.getString("key").equalsIgnoreCase(key)) &&
                        (questionGroup.getBoolean("has_media_content"))) {
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
        builder.setBackgroundColor(Color.parseColor("#208CC5"))
                .setPositiveBtnBackground(Color.parseColor("#208CC5"))  //Don't pass R
                // .color.colorvalue
                .setPositiveBtnText("OK").setAnimation(Animation.SLIDE).isCancellable(true)
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
     * This method checks whether a constraint has been enforced and returns an error message if not The error message should
     * be displayable to the user
     *
     * @param value      {@link String} The value to be checked
     * @param view       {@link View} The value to be checked
     * @param constraint {@link JSONObject} The constraint expression to use
     * @return An error message if constraint has not been enforced or NULL if constraint enforced
     * @throws Exception
     */
    private String enforceConstraint(String value, View view, JSONObject constraint) throws Exception {

        String type = constraint.getString("type").toLowerCase();
        String ex = constraint.getString("ex");
        String errorMessage = type.equals(JsonFormConstants.NUMBER_SELECTOR) ? constraint.optString(JsonFormConstants.ERR) :
                constraint.getString(JsonFormConstants.ERR);
        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);
            String[] args = getFunctionArgs(b, value);

            boolean viewDoesNotHaveValue = TextUtils.isEmpty(value);
            if (view instanceof CheckBox) {
                viewDoesNotHaveValue = !((CheckBox) view).isChecked();
            } else if (isNumberSelectorConstraint(view) || isDatePickerNativeRadio(view)) {
                return args.length > 1 ? args[1] : "";//clever fix to pass back the max value for number selectors

            }

            if (checkViewValues(type, functionName, args, viewDoesNotHaveValue)) return null;
        } else {
            Log.d(TAG, "Matcher didn't work with function");
        }

        return errorMessage;
    }

    private boolean checkViewValues(String type, String functionName, String[] args, boolean viewDoesNotHaveValue) {
        return viewDoesNotHaveValue || TextUtils.isEmpty(args[0]) || TextUtils.isEmpty(args[1]) ||
                comparisons.get(functionName).compare(args[0], type, args[1]);
    }

    private String enforceConstraint(Facts curValueMap, JSONObject constraint) throws Exception {
        return curValueMap.asMap().size() == 0 ? "0" : rulesEngineFactory.getConstraint(curValueMap,
                constraint.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES).getString(RuleConstant.RULES_FILE));
    }

    private boolean isNumberSelectorConstraint(View view) {
        return JsonFormConstants.NUMBER_SELECTOR.equals(view.getTag(R.id.type));
    }

    private boolean isDatePickerNativeRadio(View view) {
        return JsonFormConstants.NATIVE_RADIO_BUTTON.equals(view.getTag(R.id.type));
    }

    protected JSONArray fetchFields(JSONObject parentJson, boolean popup) {
        JSONArray fields = new JSONArray();
        try {
            if (parentJson.has(JsonFormConstants.SECTIONS) &&
                    parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                for (int i = 0; i < sections.length(); i++) {
                    JSONObject sectionJson = sections.getJSONObject(i);
                    fields = returnFormWithSectionFields(sectionJson, popup);
                }
            } else if (parentJson.has(JsonFormConstants.FIELDS) &&
                    parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                fields = returnWithFormFields(parentJson, popup);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fields;
    }

    /**
     * Get form fields from JSON forms that have sections in the form steps. The JSONObject {@link JSONObject} argument is
     * the object after getting the section in the specified step name The popup {@link Boolean} argument is a boolean value
     * to let the function know that the form is being executed on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the section for the given step
     *
     * @param sectionJson
     * @param popup
     * @return
     * @throws JSONException
     * @author dubdabasoduba
     */
    protected JSONArray returnFormWithSectionFields(JSONObject sectionJson, boolean popup) throws JSONException {
        JSONArray fields = new JSONArray();
        if (sectionJson.has(JsonFormConstants.FIELDS)) {
            if (popup) {
                JSONArray jsonArray = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject item = jsonArray.getJSONObject(k);
                    if (genericDialogInterface != null &&
                            item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey())) {
                        if (item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                            fields = formUtils.concatArray(fields, specifyFields(item));
                        }
                    }
                }
            } else {
                fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
            }
        }
        return fields;
    }

    /**
     * Get the form fields for the JSON forms that do not use the sections in the steps The JSONObject {@link JSONObject}
     * argument is the object after getting the step name The popup {@link boolean} argument is a boolean value to let the
     * function know that the form is being executed on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the step
     *
     * @param parentJson
     * @param popup
     * @return fields
     * @throws JSONException
     * @author dubdabasoduba
     */
    protected JSONArray returnWithFormFields(JSONObject parentJson, boolean popup) throws JSONException {
        JSONArray fields = new JSONArray();
        if (popup) {
            JSONArray jsonArray = parentJson.getJSONArray(JsonFormConstants.FIELDS);
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject item = jsonArray.getJSONObject(k);
                if (genericDialogInterface != null &&
                        item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey()) &&
                        item.has(JsonFormConstants.EXTRA_REL) && item.has(JsonFormConstants.HAS_EXTRA_REL)) {
                    fields = specifyFields(item);
                }
            }
        } else {
            fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
        }

        return fields;
    }

    protected JSONArray specifyFields(JSONObject parentJson) {
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
                                String formLocation = jsonObject.has(JsonFormConstants.CONTENT_FORM_LOCATION) ?
                                        jsonObject.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "";
                                fields = getSubFormFields(jsonObject.get(JsonFormConstants.CONTENT_FORM).toString(),
                                        formLocation, fields);
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

    protected JSONArray getSubFormFields(String subFormName, String subFormLocation, JSONArray fields) {
        JSONArray fieldArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            jsonObject = FormUtils.getSubFormJson(subFormName, subFormLocation, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private boolean isRelevant(Facts curValueMap, JSONObject curRelevance) throws Exception {
        if (curRelevance != null) {
            if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_RULES)) {
                return curValueMap.asMap().size() != 0 && rulesEngineFactory.getRelevance(curValueMap,
                        curRelevance.getJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES)
                                .getString(RuleConstant.RULES_FILE));
            } else if (curRelevance.has(JsonFormConstants.JSON_FORM_KEY.EX_CHECKBOX)) {
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
            } else {
                String curValue = String.valueOf(curValueMap.get(JsonFormConstants.VALUE));
                return doComparison(curValue != null ? curValue : "", curRelevance);
            }
        }
        return false;
    }

    private ExObjectResult isExObjectRelevant(Facts curValueMap, JSONObject object) throws Exception {
        if (object.has(JsonFormConstants.JSON_FORM_KEY.NOT)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.NOT);

            for (int i = 0; i < orArray.length(); i++) {

                String curValue = curValueMap.get(orArray.getString(i));

                if (curValue != null && !Boolean.valueOf(curValue)) {
                    return new ExObjectResult(true, false);
                } else {
                    return new ExObjectResult(false, true);
                }
            }
        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.OR)) {
            JSONArray orArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.OR);

            for (int i = 0; i < orArray.length(); i++) {
                String curValue = curValueMap.get(orArray.getString(i));
                if (curValue != null && Boolean.valueOf(curValue)) {
                    return new ExObjectResult(true, true);
                }

            }

        }

        if (object.has(JsonFormConstants.JSON_FORM_KEY.AND)) {
            JSONArray andArray = object.getJSONArray(JsonFormConstants.JSON_FORM_KEY.AND);

            for (int i = 0; i < andArray.length(); i++) {
                String curValue = curValueMap.get(andArray.getString(i));
                if (curValue != null && !Boolean.valueOf(curValue)) {
                    return new ExObjectResult(false, false);
                }
            }
            return new ExObjectResult(true, false);

        }

        return new ExObjectResult(false, false);
    }

    private List<String> getRules(String filename, String fieldKey) {

        List<String> rules = ruleKeys.get(filename + ":" + fieldKey);


        if (rules == null) {
            try {

                Yaml yaml = new Yaml();
                InputStreamReader inputStreamReader =
                        new InputStreamReader(this.getAssets().open((rulesEngineFactory.getRulesFolderPath() + filename)));
                Iterable<Object> ruleObjects = yaml.loadAll(inputStreamReader);

                for (Object object : ruleObjects) {

                    Map<String, Object> map = ((Map<String, Object>) object);

                    String name = map.get(RuleConstant.NAME).toString();
                    if (ruleKeys.containsKey(filename + ":" + name)) {
                        continue;
                    }

                    List<String> actions = new ArrayList<>();

                    StringBuilder conditionString = new StringBuilder();
                    conditionString.append(map.get(RuleConstant.CONDITION).toString());

                    List<String> fields = (List<String>) map.get(RuleConstant.ACTIONS);
                    if (fields != null) {
                        for (String field : fields) {
                            if (field.trim().startsWith(RuleConstant.CALCULATION) ||
                                    field.trim().startsWith(RuleConstant.CONSTRAINT)) {
                                conditionString.append(" " + field);
                            }
                        }

                    }

                    actions.addAll(getConditionKeys(conditionString.toString()));
                    ruleKeys.put(filename + ":" + name, actions);

                    if (name.equals(fieldKey)) {
                        break;
                    }

                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }


            return ruleKeys.get(filename + ":" + fieldKey);
        } else {

            return rules;
        }
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
        String cleanString = cleanConditionString(condition);
        String[] conditionTokens = cleanString.split(" ");
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

    private void updateCalculation(Facts valueMap, View view, String rulesFile) {

        try {

            String calculation = rulesEngineFactory.getCalculation(valueMap, rulesFile);

            if (calculation != null) {
                if (view instanceof CheckBox) {
                    //For now were only handling checkbox titles only
                    TextView checkboxLabel = ((View) view.getParent().getParent()).findViewById(R.id.label_text);
                    if (checkboxLabel != null) {
                        checkboxLabel
                                .setText(getRenderText(calculation, checkboxLabel.getTag(R.id.original_text).toString()));
                    }

                } else if (view instanceof TextableView) {
                    TextableView textView = ((TextableView) view);
                    if (!TextUtils.isEmpty(calculation)) {
                        textView.setText(calculation.charAt(0) == '{' ?
                                getRenderText(calculation, textView.getTag(R.id.original_text).toString()) :
                                (textView.getTag(R.id.original_text) != null && "0".equals(calculation)) ?
                                        textView.getTag(R.id.original_text).toString() : calculation);
                    }
                } else if (view instanceof EditText) {
                    String type = (String) view.getTag(R.id.type);
                    if (JsonFormConstants.HIDDEN.equals(type) && TextUtils.isEmpty(calculation)) {
                        calculation = "0";
                    }

                    if (!TextUtils.isEmpty(calculation)) {
                        ((EditText) view).setText(calculation);
                    }

                } else if (view instanceof RadioGroup) {
                    RadioGroup radioButton = (RadioGroup) view;
                    int count = radioButton.getChildCount();
                    for (int i = 0; i < count; i++) {
                        TextView renderView = radioButton.getChildAt(i).findViewById(R.id.extraInfoTextView);

                        // if (((AppCompatRadioButton) ((ViewGroup) radioButton.getChildAt(i).findViewById(R.id
                        // .radioContentLinearLayout))
                        // .getChildAt(0)).isChecked()) {

                        if (renderView.getTag(R.id.original_text) == null) {
                            renderView.setTag(R.id.original_text, renderView.getText());
                        }
                        renderView.setText(calculation.charAt(0) == '{' ?
                                getRenderText(calculation, renderView.getTag(R.id.original_text).toString()) : calculation);

                        renderView.setVisibility(renderView.getText().toString().contains("{") ||
                                renderView.getText().toString().equals("0") ? View.GONE : View.VISIBLE);
                        // break;
                        //} else {
                        //  renderView.setVisibility(renderView.getText().toString().contains("{") ? View.GONE : View
                        // .VISIBLE);
                        //}

                    }

                } else {

                    ((TextView) view).setText(calculation);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "calling updateCalculation on Non TextView or Text View decendant", e);
        }

    }

    private String getRenderText(String calculation, String textTemplate) {
        Map<String, Object> valueMap = new Gson().fromJson(calculation, new TypeToken<HashMap<String, Object>>() {
        }.getType());

        return stringFormat(textTemplate, valueMap);
    }

    public String stringFormat(String string, Map<String, Object> valueMap) {
        String resString = string;
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            resString = resString.replace("{" + entry.getKey() + "}", getTemplateValue(entry.getValue()));
        }

        return resString;
    }

    protected Object getValue(JSONObject object) throws JSONException {
        Object value;

        if (object.has(JsonFormConstants.VALUE)) {

            value = object.opt(JsonFormConstants.VALUE);

            if (isNumberWidget(object)) {
                value = TextUtils.isEmpty(object.optString(JsonFormConstants.VALUE)) ? 0 :
                        processNumberValues(object.optString(JsonFormConstants.VALUE));
            } else if (value != null && !TextUtils.isEmpty(object.getString(JsonFormConstants.VALUE)) &&
                    canHaveNumber(object)) {
                value = processNumberValues(value);
            }

        } else {
            value = isNumberWidget(object) ? 0 : "";
        }

        return value;
    }

    protected Object processNumberValues(Object object) {
        Object jsonObject = object;
        try {
            if (jsonObject.toString().contains(".")) {
                jsonObject = String.valueOf((float) Math.round(Float.valueOf(jsonObject.toString()) * 100) / 100);
            } else {
                jsonObject = Integer.valueOf(jsonObject.toString());
            }
        } catch (NumberFormatException e) {
            //Log.e(TAG, "Error trying to convert " + object + " to a number ", e);
        }
        return jsonObject;
    }

    protected String getKey(JSONObject object) throws JSONException {
        return object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) ?
                object.get(RuleConstant.STEP) + "_" + object.get(JsonFormConstants.KEY) : JsonFormConstants.VALUE;
    }

    private void clearHiddenViewsValues(JSONObject object, String addressString) {
        if (object != null) {
            String objectKey = addressString.replace(":", "_");
            formValuesCacheMap.remove(objectKey);
            formValuesCacheMap.put(objectKey, "");
            if (object.has(JsonFormConstants.VALUE)) {
                object.remove(JsonFormConstants.VALUE);
            }
        }
    }

    private void updateCanvas(View view, boolean visible, JSONArray canvasViewIds, String addressString, JSONObject object) throws JSONException {
        for (int i = 0; i < canvasViewIds.length(); i++) {
            int curId = canvasViewIds.getInt(i);

            View curCanvasView = view.getRootView().findViewById(curId);

            if (curCanvasView == null) {
                continue;
            }

            if (visible) {
                curCanvasView.setEnabled(true);
                curCanvasView.setVisibility(View.VISIBLE);

                if (curCanvasView instanceof RelativeLayout || view instanceof LinearLayout) {
                    curCanvasView.setFocusable(true);
                }
                if (view instanceof EditText) {
                    view.setFocusable(true);
                }
            } else {
                clearHiddenViewsValues(object,addressString);
                curCanvasView.setEnabled(false);
                curCanvasView.setVisibility(View.GONE);
                refreshViews(curCanvasView);
            }
        }
    }

    private void refreshViews(View childElement) {
        if (childElement instanceof ViewGroup) {
            childElement.setFocusable(true);
            ViewGroup group = (ViewGroup) childElement;
            for (int id = 0; id < group.getChildCount(); id++) {
                View child = group.getChildAt(id);
                if (child instanceof CheckBox) {
                    ((CheckBox) child).setChecked(false);
                } else if (child instanceof RadioButton) {
                    ((RadioButton) child).setChecked(false);
                } else if (child instanceof EditText) {
                    EditText editText = (EditText) child;
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setText("");
                    }
                } else if (child instanceof MaterialSpinner) {
                    MaterialSpinner spinner = (MaterialSpinner) child;
                    spinner.setSelected(false);
                }
                refreshViews(group.getChildAt(id));
            }
        }
    }

    @Override
    public void updateGenericPopupSecondaryValues(JSONArray jsonArray) {
        setExtraFieldsWithValues(jsonArray);
    }

    public JSONArray getExtraFieldsWithValues() {
        return extraFieldsWithValues;
    }

    public void setExtraFieldsWithValues(JSONArray extraFieldsWithValues) {
        this.extraFieldsWithValues = extraFieldsWithValues;
    }

    private String cleanConditionString(String conditionStringRaw) {
        String conditionString = conditionStringRaw;

        for (String token : PREFICES_OF_INTEREST) {

            conditionString = conditionString.replaceAll(token, " " + token);
        }

        return conditionString.replaceAll("  ", " ");
    }

    @Override
    public void registerLifecycleListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener);
    }

    @Override
    public void unregisterLifecycleListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener);
    }

    @Override
    public void setGenericPopup(GenericPopupDialog context) {
        genericDialogInterface = context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager
                .registerReceiver(messageReceiver, new IntentFilter(JsonFormConstants.INTENT_ACTION.JSON_FORM_ACTIVITY));
        localBroadcastManager.registerReceiver(NumberSelectorFactory.getNumberSelectorsReceiver(),
                new IntentFilter(JsonFormConstants.INTENT_ACTION.NUMBER_SELECTOR_FACTORY));

        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onResume();
        }
    }

    @Override
    protected void onPause() {
        localBroadcastManager.unregisterReceiver(NumberSelectorFactory.getNumberSelectorsReceiver());
        localBroadcastManager.unregisterReceiver(messageReceiver);
        super.onPause();
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onStop();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onLowMemory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onDestroy();
        }
    }

    private String getTemplateValue(Object object) {
        StringBuilder result = new StringBuilder();
        if (object instanceof List) {
            List<String> valueList = (List<String>) object;
            for (int i = 0; i < valueList.size(); i++) {
                result.append(valueList.get(i));
                if (i != (valueList.size() - 1)) {
                    result.append(", ");
                }
            }
        } else {
            result.append(
                    object.toString().contains(".0") ? object.toString().substring(0, object.toString().indexOf(".0")) :
                            object.toString()); //Fix automatic conversion float bug
        }

        return result.toString();
    }

    public LocalBroadcastManager getLocalBroadcastManager() {
        return localBroadcastManager;
    }

    private void cacheFormMapValues(String stepName, String parentKey, String childKey, String value) {

        formValuesCacheMap.put(stepName + "_" + (parentKey != null ? parentKey + "_" : "") + childKey, value);

    }

    private boolean invokeRefreshLogic(String stepName, String parentKey, String childKey, String value) {
        String oldValue = formValuesCacheMap.get(stepName + "_" + (parentKey != null ? parentKey + "_" : "") + childKey);

        return !value.equals(oldValue);

    }

    protected boolean canHaveNumber(JSONObject object) throws JSONException {
        return isNumberWidget(object) || object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.HIDDEN) ||
                object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.SPINNER);
    }

    protected boolean isNumberWidget(JSONObject object) throws JSONException {
        return object.has(JsonFormConstants.EDIT_TYPE) &&
                object.getString(JsonFormConstants.EDIT_TYPE).equals(JsonFormConstants.EDIT_TEXT_TYPE.NUMBER) ||
                object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NUMBER_SELECTOR);
    }
}

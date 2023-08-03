package com.vijay.jsonwizard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.LifeCycleListener;
import com.vijay.jsonwizard.interfaces.OnActivityRequestPermissionResultListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.rules.RulesEngineFactory;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.PropertyManager;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class JsonFormBaseActivity extends MultiLanguageActivity implements OnFieldsInvalid {
    protected static final String TAG = JsonFormActivity.class.getSimpleName();
    protected static final String JSON_STATE = "jsonState";
    protected static final String FORM_STATE = "formState";
    protected final HashSet<String> invisibleRequiredFields = new HashSet<>();
    protected JSONObject mJSONObject;
    protected PropertyManager propertyManager;
    protected Map<String, View> skipLogicViews;
    protected Map<String, View> calculationLogicViews;
    protected HashMap<Integer, OnActivityResultListener> onActivityResultListeners;
    protected HashMap<Integer, OnActivityRequestPermissionResultListener> onActivityRequestPermissionResultListeners;
    protected List<LifeCycleListener> lifeCycleListeners;
    protected String confirmCloseTitle;
    protected String confirmCloseMessage;
    protected Form form;
    protected Map<String, String> globalValues = null;
    protected RulesEngineFactory rulesEngineFactory = null;
    protected LocalBroadcastManager localBroadcastManager;
    protected boolean isFormFragmentInitialized;
    private Toolbar mToolbar;
    private Map<String, ValidationStatus> invalidFields = new HashMap<>();

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
        isFormFragmentInitialized = false;
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

    public void init(String json) {
        try {
            setmJSONObject(new JSONObject(json));
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

    public synchronized void initializeFormFragment() {
        isFormFragmentInitialized = true;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, JsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commitAllowingStateLoss();
    }

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

    private Form extractForm(Serializable serializable) {
        if (serializable instanceof Form) {
            return (Form) serializable;
        } else {
            return null;
        }
    }

    public void setmJSONObject(JSONObject mJSONObject) {
        this.mJSONObject = mJSONObject;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
    public void passInvalidFields(Map<String, ValidationStatus> invalidFields) {
        this.invalidFields = invalidFields;
    }

    @Override
    public Map<String, ValidationStatus> getPassedInvalidFields() {
        return getInvalidFields();
    }

    public Map<String, ValidationStatus> getInvalidFields() {
        return invalidFields;
    }

    public RulesEngineFactory getRulesEngineFactory() {
        return rulesEngineFactory;
    }

    public void setRulesEngineFactory(RulesEngineFactory rulesEngineFactory) {
        this.rulesEngineFactory = rulesEngineFactory;
    }
}

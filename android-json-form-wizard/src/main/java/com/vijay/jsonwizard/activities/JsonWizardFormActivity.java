package com.vijay.jsonwizard.activities;

import com.vijay.jsonwizard.delegates.JsonWizardFormDelegate;

import org.json.JSONException;

/**
 * Created by keyman on 04/12/2018.
 */
public class JsonWizardFormActivity extends JsonFormActivity {

    private final JsonWizardFormDelegate delegate = new JsonWizardFormDelegate();

    @Override
    public synchronized void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        callSuperWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
    }

    @Override
    public void onFormFinish() {
        callSuperFinish();
    }

    protected void callSuperFinish() {
        super.onFormFinish();
    }

    protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
    }

    protected void initializeFormFragmentCore() {
        delegate.initializeFormFragmentCore(this.getSupportFragmentManager());
    }
}


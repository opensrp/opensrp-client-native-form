package com.vijay.jsonwizard;

import android.content.Intent;
import android.os.Build;

import com.vijay.jsonwizard.application.TestApplication;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import timber.log.Timber;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1, application = TestApplication.class)
public abstract class BaseTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    protected static final int CONSTANT_INT_50 = 50;
    protected static final int CONSTANT_INT_20 = 20;
    protected static final int CONSTANT_INT_16 = 16;
    protected static final int CONSTANT_INT_0 = 0;
    protected static final int CONSTANT_INT_1 = 1;
    protected static final String DEFAULT_ERROR_MSG = "Default Error Message";
    protected static final String DEFAULT_TEST_MESSAGE = "Native Form Test";

    protected static long TIMEOUT=2000;

    public Intent getJsonFormActivityIntent() {
        Intent intent = new Intent();
        try {
            JSONObject mJSONObject = new JSONObject();
            mJSONObject.put(JsonFormConstants.STEP1, new JSONObject());
            mJSONObject.put(JsonFormConstants.ENCOUNTER_TYPE, "encounter_type");

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, mJSONObject.toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
        return intent;
    }
}
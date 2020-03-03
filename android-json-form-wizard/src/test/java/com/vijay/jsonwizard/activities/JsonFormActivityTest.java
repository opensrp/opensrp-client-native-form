package com.vijay.jsonwizard.activities;

import android.content.Intent;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

public class JsonFormActivityTest extends BaseActivityTest {

    private JsonFormActivity activity;
    private ActivityController<JsonFormActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, DUMMY_JSON_FORM_STRING);
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();

        Assert.assertNotNull(activity);
    }

    @Test
    public void testSetConfirmationTitleUpdatesConfirmationTitleCorrectly() {
        Assert.assertNotNull(activity.getConfirmCloseTitle());

        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close), activity.getConfirmCloseTitle());
        activity.setConfirmCloseTitle(DUMMY_TEST_STRING);
        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseTitle());
    }

    @Test
    public void testSetConfirmationMessageUpdatesConfirmationMessageCorrectly() {
        Assert.assertNotNull(activity.getConfirmCloseMessage());
        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close_explanation), activity.getConfirmCloseMessage());
        activity.setConfirmCloseMessage(DUMMY_TEST_STRING);
        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseMessage());
    }


}

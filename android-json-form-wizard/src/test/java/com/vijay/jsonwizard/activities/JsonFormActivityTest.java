package com.vijay.jsonwizard.activities;

import android.content.Intent;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import java.util.HashSet;
import java.util.Set;

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

    @Test
    public void testInitiateFormUpdateShouldUpdateFieldsToBeHiddenOrDisabled() {
        String sampleForm = "{\"count\":\"1\",\"step1\":{\"title\":\"Basic Form One\",\"fields\":[{\"key\":\"user_image\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"finger_print\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"finger_print\",\"project_id\":\"tZqJnw0ajK04LMYdZzyw\",\"user_id\":\"test_user\",\"module_id\":\"mpower\",\"finger_print_option\":\"register\",\"uploadButtonText\":\"Take finger print\",\"image_file\":\"\",\"relevance\":{\"step1:user_first_name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"test\\\")\"}}},{\"key\":\"user_qr_code\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"User ID\",\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":false,\"err\":\"Please enter the user ID\"}},{\"key\":\"user_age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User age\",\"edit_type\":\"number\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"user_gps\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"gps\"}]}}";
        try {
            JSONObject jsonObject = new JSONObject(sampleForm);
            Form form = new Form();
            Set<String> disabledKeys = new HashSet<>();
            disabledKeys.add("user_image");
            Set<String> hiddenKeys = new HashSet<>();
            hiddenKeys.add("finger_print");
            form.setDisabledFields(disabledKeys);
            form.setHiddenFields(hiddenKeys);
            activity.form = form;
            activity.initiateFormUpdate(jsonObject);
            JSONObject stepObject = jsonObject.optJSONObject("step1");
            Assert.assertEquals("hidden", FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "user_image").optString("type"));
            Assert.assertTrue(FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "user_image").optBoolean("disabled"));
            Assert.assertEquals("hidden", FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "finger_print").optString("type"));
            Assert.assertFalse(FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "finger_print").has("disabled"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

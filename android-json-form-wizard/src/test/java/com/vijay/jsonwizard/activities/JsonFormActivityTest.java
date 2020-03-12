package com.vijay.jsonwizard.activities;

import android.content.Intent;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
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

    @Test
    public void testGetValueFromAddressCoreForEditTexts() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"pregest_weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"5090\",\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"edit_type\":\"number\",\"relevance\":{\"step1:pregest_weight_unknown\":{\"ex-checkbox\":[{\"not\":[\"pregest_weight_unknown\"]}]}},\"v_numeric\":{\"value\":\"true\",\"err\":\"\"},\"v_min\":{\"value\":\"30\",\"err\":\"Weight must be equal or greater than 30\"},\"v_max\":{\"value\":\"180\",\"err\":\"Weight must be equal or less than 180\"},\"v_required\":{\"value\":\"true\",\"err\":\"Pre-gestational weight is required\"},\"step\":\"step1\",\"is-rule-check\":true}");
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("step1_pregest_weight"));
        Assert.assertEquals(0, facts.asMap().get("step1_pregest_weight"));
    }

    @Test
    public void testGetValueFromAddressCoreForRadioButtons() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Blood type test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"Blood type status is required\"},\"index\":\"0\",\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"done_today\"}");
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("step1_blood_type_test_status"));
        Assert.assertEquals("done_today", facts.asMap().get("step1_blood_type_test_status"));
    }

    @Test
    public void testGetValueFromAddressCoreForCheckBoxes() throws Exception{
        JSONObject jsonObject = new JSONObject("{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"check_box\",\"label\":\"Abnormal\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"options\":[{\"key\":\"rapid_breathing\",\"text\":\"Rapid breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"TACHYPNEA\",\"openmrs_entity_id\":\"125061\",\"value\":true},{\"key\":\"slow_breathing\",\"text\":\"Slow breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false}],\"value\":[\"rapid_breathing\"],\"is-rule-check\":false}");
        Facts facts = Whitebox.invokeMethod(activity,"getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("slow_breathing"));
        Assert.assertEquals("false",facts.asMap().get("slow_breathing"));
        Assert.assertTrue(facts.asMap().containsKey("rapid_breathing"));
        Assert.assertEquals("true",facts.asMap().get("rapid_breathing"));
    }
}

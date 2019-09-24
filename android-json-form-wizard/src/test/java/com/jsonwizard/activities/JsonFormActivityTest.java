package com.jsonwizard.activities;

import android.content.Intent;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
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
        intent.putExtra(JsonFormConstants.JsonFormKeyUtils.JSON, DUMMY_JSON_FORM_STRING);
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();

        Assert.assertNotNull(activity);


    }

    @Test
    public void testSetConfirmationTitleUpdatesConfirmationTitleCorrectly() {

        Assert.assertNotNull(activity.getConfirmCloseTitle());

        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close),
                activity.getConfirmCloseTitle());


        activity.setConfirmCloseTitle(DUMMY_TEST_STRING);


        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseTitle());


    }

    @Test
    public void testSetConfirmationMessageUpdatesConfirmationMessageCorrectly() {

        Assert.assertNotNull(activity.getConfirmCloseMessage());

        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close_explanation),
                activity.getConfirmCloseMessage());


        activity.setConfirmCloseMessage(DUMMY_TEST_STRING);


        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseMessage());


    }

    @Test
    public void testExtractOptionOpenMRSAttributes() throws Exception {
        String optionItem = "\n" +
                "        {\n" +
                "          \"key\": \"1\",\n" +
                "          \"text\": \"Not done\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"concept\",\n" +
                "          \"openmrs_entity_id\": \"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },";
        JSONObject optionItemJson = new JSONObject(optionItem);
        JSONArray valuesArray = new JSONArray();
        String itemKey = "respiratory_exam_radio_button";

        Whitebox.invokeMethod(activity, "extractOptionOpenMRSAttributes", valuesArray, optionItemJson, itemKey);
        Assert.assertEquals(valuesArray.length(), 1);

    }

    @Test
    public void testGetRadioButtonOptionsOpenMRSAttributes() throws Exception {
        String radioButtonItem = " {\n" +
                "      \"key\": \"respiratory_exam_radio_button\",\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_entity_id\": \"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"type\": \"native_radio\",\n" +
                "      \"label\": \"Respiratory exam\",\n" +
                "      \"label_text_style\": \"bold\",\n" +
                "      \"text_color\": \"#000000\",\n" +
                "      \"extra_rel\": true,\n" +
                "      \"has_extra_rel\": \"3\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"key\": \"1\",\n" +
                "          \"text\": \"Not done\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"concept\",\n" +
                "          \"openmrs_entity_id\": \"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"2\",\n" +
                "          \"text\": \"Normal\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"concept\",\n" +
                "          \"openmrs_entity_id\": \"165230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"3\",\n" +
                "          \"text\": \"Abnormal\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"concept\",\n" +
                "          \"openmrs_entity_id\": \"165231AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";
        JSONObject itemJson = new JSONObject(radioButtonItem);
        String value = "2";
        JSONArray valuesArray = new JSONArray();

        Whitebox.invokeMethod(activity, "getOptionsOpenMRSAttributes", value, itemJson, valuesArray);
        Assert.assertEquals(valuesArray.length(), 1);
    }

    @Test
    public void testGetCheckBoxOptionsOpenMRSAttributes() throws Exception {
        String checkboxItem = "{\n" +
                "      \"key\": \"respiratory_exam_abnormal\",\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"\",\n" +
                "      \"openmrs_entity_id\": \"\",\n" +
                "      \"type\": \"check_box\",\n" +
                "      \"label\": \"Abnormal\",\n" +
                "      \"label_text_style\": \"bold\",\n" +
                "      \"text_color\": \"#000000\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"key\": \"rapid_breathing\",\n" +
                "          \"text\": \"Rapid breathing\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"TACHYPNEA\",\n" +
                "          \"openmrs_entity_id\": \"125061\",\n" +
                "          \"value\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"slow_breathing\",\n" +
                "          \"text\": \"Slow breathing\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"\",\n" +
                "          \"openmrs_entity_id\": \"\",\n" +
                "          \"value\": false\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"other\",\n" +
                "          \"text\": \"Other (specify)\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"\",\n" +
                "          \"openmrs_entity_id\": \"\",\n" +
                "          \"value\": false\n" +
                "        }\n" +
                "      ]\n" +
                "    }";

        String valueAttribute = "{\n" +
                "      \"key\": \"respiratory_exam_abnormal\",\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"TACHYPNEA\",\n" +
                "      \"openmrs_entity_id\": \"125061\"\n" +
                "    }";

        JSONObject valueAttributeJson = new JSONObject(valueAttribute);
        JSONObject itemJson = new JSONObject(checkboxItem);

        String value = "rapid_breathing";
        JSONArray valuesArray = new JSONArray();

        Whitebox.invokeMethod(activity, "getOptionsOpenMRSAttributes", value, itemJson, valuesArray);
        Assert.assertEquals(valuesArray.length(), 1);
        Assert.assertEquals(valueAttributeJson.getString(JsonFormConstants.KEY),
                valuesArray.getJSONObject(0).getString(JsonFormConstants.KEY));

    }
}

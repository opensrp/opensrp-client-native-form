package com.vijay.jsonwizard.widgets;

import android.util.Pair;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 03/04/2020
 */
public class SpinnerFactoryTest extends BaseTest {

    private final String spinnerWidgetString = "{\n" +
            "        \"key\": \"response_spinner_with_options\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"spinner\",\n" +
            "        \"hint\": \"Response Spinners\",\n" +
            "        \"options\": [\n" +
            "          {\n" +
            "            \"key\": \"yes\",\n" +
            "            \"text\": \"Yes\",\n" +
            "            \"value\": false,\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"key\": \"no\",\n" +
            "            \"text\": \"No\",\n" +
            "            \"value\": false,\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"key\": \"maybe\",\n" +
            "            \"text\": \"Maybe\",\n" +
            "            \"value\": false,\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter response\"\n" +
            "        },\n" +
            "        \"openmrs_choice_ids\": {\n" +
            "          \"user_one\": \"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"user_two\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        }\n" +
            "      }";

    private SpinnerFactory spinnerFactory;
    private JSONArray spinnerWidgetOptions;

    @Before
    public void setUp() throws JSONException {
        spinnerFactory = new SpinnerFactory();
        JSONObject spinnerWidget = new JSONObject(spinnerWidgetString);
        spinnerWidgetOptions = spinnerWidget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
    }

    @Test
    public void testGetOptionsKeyValPairsShouldExtractCorrectPairs() throws Exception {
        Pair<JSONArray, JSONArray> keyValPairs = Whitebox.invokeMethod(spinnerFactory, "getOptionsKeyValPairs", spinnerWidgetOptions);
        JSONArray actualKeys = keyValPairs.first;
        JSONArray actualVals = keyValPairs.second;

        JSONArray expectedKeys = new JSONArray();
        expectedKeys.put("yes");
        expectedKeys.put("no");
        expectedKeys.put("maybe");

        JSONArray expectedVals = new JSONArray();
        expectedVals.put("Yes");
        expectedVals.put("No");
        expectedVals.put("Maybe");
        for (int i = 0; i < actualKeys.length(); i++) {
            assertEquals(actualKeys.getString(i), expectedKeys.getString(i));
            assertEquals(actualVals.getString(i), expectedVals.getString(i));
        }
    }
}

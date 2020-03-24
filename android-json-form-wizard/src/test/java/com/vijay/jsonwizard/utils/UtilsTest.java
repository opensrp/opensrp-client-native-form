package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFormConfigShouldReturnFormConfig() throws IOException, JSONException {
        Context context = Mockito.mock(Context.class);
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        String configFileContent = "[{\"form_name\":\"anc_quick_check\",\"hidden_fields\":[],\"disabled_fields\":[\"leg_cramps\"]}]";
        Mockito.when(assetManager.open("json.form.config.json")).thenReturn(new ByteArrayInputStream(configFileContent.getBytes()));
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        JSONObject jsonResult = Utils.getFormConfig("anc_quick_check", "json.form.config.json", context);
        assertEquals(3, jsonResult.length());
        assertTrue(jsonResult.has("form_name"));
        assertTrue(jsonResult.has("hidden_fields"));
        assertTrue(jsonResult.has("disabled_fields"));

    }

    @Test
    public void testConvertJsonArrayToSetShouldReturnASet() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("test");
        jsonArray.put("tester");
        jsonArray.put("tested");
        jsonArray.put("testing");
        jsonArray.put("test");
        Set<String> strings = Utils.convertJsonArrayToSet(jsonArray);
        assertEquals(4, strings.size());
    }

    @Test
    public void testConvertJsonArrayToSetShouldReturnNull() {
        assertNull(Utils.convertJsonArrayToSet(null));
    }

    @Test
    public void testBuildRulesWithUniqueIdShouldUpdateRelevanceRulesEngineObjectAccordingly() throws JSONException, IOException {
        String ruleType = "relevance";
        JSONObject element = new JSONObject();
        element.put(ruleType, new JSONObject("{\"rules-engine\":{\"ex-rules\":{\"rules-dynamic\":\"diagnose_and_treat_relevance.yml\"}}}"));
        String unique_id = "c29afdf9-843e-4c90-9a79-3dafd70e045b";
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String contentOfRelevanceFile = "---\n" +
                "name: step1_diagnostic_test_result_spinner\n" +
                "description: diagnostic_test_result_spinner\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Pregnancy Test' || step1_diagnostic_test == 'Malaria test'\n" +
                "|| step1_diagnostic_test == 'HIV test' || step1_diagnostic_test == 'Syphilis test'\n" +
                "|| step1_diagnostic_test == 'Hep B test' || step1_diagnostic_test == 'Hep C test'\n" +
                " || step1_diagnostic_test == 'TB Screening' || step1_diagnostic_test == 'Midstream urine Gram-staining'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_specify\n" +
                "description: diagnostic_test_result_specify\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Ultra sound'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_glucose\n" +
                "description: diagnostic_test_result_glucose\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test.startsWith('Blood Glucose test')\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_spinner_blood_type\n" +
                "description: diagnostic_test_result_spinner_blood_type\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Blood Type test'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"";
        InputStream inputStream = new ByteArrayInputStream(contentOfRelevanceFile.getBytes());
        Mockito.when(assetManager.open("rule/diagnose_and_treat_relevance.yml")).thenReturn(inputStream);
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();
        Utils.buildRulesWithUniqueId(element, unique_id, ruleType, context, rulesFileMap);
        String expected = "{\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-dynamic\":[{\"key\":\"c29afdf9-843e-4c90-9a79-3dafd70e045b\"},{\"condition\":\"step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Pregnancy Test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Malaria test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'HIV test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Syphilis test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Hep B test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Hep C test' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'TB Screening' || step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Midstream urine Gram-staining'\",\"name\":\"step1_diagnostic_test_result_spinner_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"description\":\"diagnostic_test_result_spinner_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"priority\":1,\"actions\":\"isRelevant = true\"},{\"condition\":\"step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Ultra sound'\",\"name\":\"step1_diagnostic_test_result_specify_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"description\":\"diagnostic_test_result_specify_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"priority\":1,\"actions\":\"isRelevant = true\"},{\"condition\":\"step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b.startsWith('Blood Glucose test')\",\"name\":\"step1_diagnostic_test_result_glucose_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"description\":\"diagnostic_test_result_glucose_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"priority\":1,\"actions\":\"isRelevant = true\"},{\"condition\":\"step1_diagnostic_test_c29afdf9-843e-4c90-9a79-3dafd70e045b == 'Blood Type test'\",\"name\":\"step1_diagnostic_test_result_spinner_blood_type_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"description\":\"diagnostic_test_result_spinner_blood_type_c29afdf9-843e-4c90-9a79-3dafd70e045b\",\"priority\":1,\"actions\":\"isRelevant = true\"}]}}}}";
        Assert.assertEquals(expected, element.toString());
    }

    @Test
    public void testBuildRulesWithUniqueIdShouldUpdateRelevanceInlineObjectAccordingly() throws JSONException {
        String ruleType = "relevance";
        JSONObject element = new JSONObject();
        element.put(ruleType, new JSONObject("{\"step1:dob_unknown\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"false\\\")\"}}"));
        String unique_id = "c29afdf9-843e-4c90-9a79-3dafd70e045b";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();
        Utils.buildRulesWithUniqueId(element, unique_id, ruleType, context, rulesFileMap);
        String expected = "{\"relevance\":{\"step1:dob_unknown_c29afdf9-843e-4c90-9a79-3dafd70e045b\":{\"ex\":\"equalTo(., \\\"false\\\")\",\"type\":\"string\"}}}";
        Assert.assertEquals(expected, element.toString());
    }

    @Test
    public void testProcessNumberValuesShouldReturnCorrectValues() {
        Utils utils = new Utils();
        Assert.assertEquals(String.valueOf(4.7), utils.processNumberValues("4.7"));//test float
        Assert.assertEquals(String.valueOf(0.05), utils.processNumberValues("0.047"));//test rounding off
        Assert.assertEquals(47, utils.processNumberValues("47"));//test integer
        Assert.assertEquals(String.valueOf(Long.MAX_VALUE), utils.processNumberValues(String.valueOf(Long.MAX_VALUE)));//test when exception
    }

    @Test
    public void testGetDateFromStringShouldReturnCorrectDate() throws ParseException {
        String date = "20-12-1997";
        Assert.assertEquals(DatePickerFactory.DATE_FORMAT.parse(date), Utils.getDateFromString("20-12-1997"));
        Assert.assertNull(Utils.getDateFromString("20/12/1997"));
        Assert.assertNull(Utils.getDateFromString(""));
    }

    @Test
    public void testGetStringFromDateShouldReturnCorrectString() throws ParseException {
        String strDate = "20-12-1997";
        Date date = DatePickerFactory.DATE_FORMAT.parse(strDate);
        Assert.assertEquals(strDate, Utils.getStringFromDate(date));
        Assert.assertNull(Utils.getStringFromDate(null));
    }

    @Test
    public void testReverseDateString() {
        String date = "20-12-1997";
        Assert.assertEquals("1997-12-20", Utils.reverseDateString(date, "-"));
    }

    @Test
    public void testGetStringValue() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUES, new JSONArray().put("yes").put("no").put("don't know"));
        Utils utils = new Utils();
        String expected = "yes, no, don't know";
        Assert.assertEquals(expected, WhiteboxImpl.invokeMethod(utils, "getStringValue", jsonObject));
    }
}
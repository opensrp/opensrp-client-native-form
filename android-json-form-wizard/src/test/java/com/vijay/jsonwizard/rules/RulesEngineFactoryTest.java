package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
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
import org.robolectric.util.ReflectionHelpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

@RunWith(MockitoJUnitRunner.class)
public class RulesEngineFactoryTest {

    private RulesEngineFactory rulesEngineFactory;

    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rulesEngineFactory = new RulesEngineFactory();
    }

    @Test
    public void testGetDynamicRulesFromJsonArrayShouldReturnNonEmptyRulesList() throws Exception {
        String expected = "[" +
                "{\"key\":\"c29afdf9843e4c909a793dafd70e045b\"}," +
                "{" +
                "\"condition\":\"step1_diagnostic_test_c29afdf9843e4c909a793dafd70e045b == 'Pregnancy Test'\"," +
                "\"name\":\"step1_diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"description\":\"diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"priority\":1," +
                "\"actions\":\"isRelevant = true\"" +
                "}" +
                "]";
        try {
            JSONArray jsonArray = new JSONArray(expected);
            rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
            Map<String, Rules> ruleMap = new HashMap<>();
            WhiteboxImpl.setInternalState(rulesEngineFactory, "ruleMap", ruleMap);
            Rules result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "getDynamicRulesFromJsonArray", jsonArray, JsonFormConstants.RELEVANCE);
            Rule ruleObject = result.iterator().next();
            Assert.assertEquals("step1_diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b", ruleObject.getName());
            Assert.assertEquals("diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b", ruleObject.getDescription());
            Assert.assertEquals(1, ruleObject.getPriority());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Test
    public void testGetDynamicRulesFromJsonArrayShouldReturnNullIfKeyElementIsMissing() throws Exception {
        String expected = "[" +
                "{" +
                "\"condition\":\"step1_diagnostic_test_c29afdf9843e4c909a793dafd70e045b == 'Pregnancy Test'\"," +
                "\"name\":\"step1_diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"description\":\"diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"priority\":1," +
                "\"actions\":\"isRelevant = true\"" +
                "}" +
                "]";
        try {
            JSONArray jsonArray = new JSONArray(expected);
            rulesEngineFactory = new RulesEngineFactory();
            Map<String, Rules> ruleMap = new HashMap<>();
            WhiteboxImpl.setInternalState(rulesEngineFactory, "ruleMap", ruleMap);
            Rules result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "getDynamicRulesFromJsonArray", jsonArray, JsonFormConstants.RELEVANCE);
            Assert.assertNull(result);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Test
    public void testGetValueShouldReturnBooleanTrue() {
        Assert.assertTrue((Boolean) rulesEngineFactory.getValue("true"));
    }

    @Test
    public void testGetValueShouldReturnInteger() {
        Assert.assertEquals(1, rulesEngineFactory.getValue("1"));
    }

    @Test
    public void testGetValueShouldReturnFloat() {
        Assert.assertEquals(1.00f, rulesEngineFactory.getValue("1.000"));
    }

    @Test
    public void testGetValueShouldReturnValuePassed() {
        Assert.assertEquals("kilo", rulesEngineFactory.getValue("kilo"));
    }

    @Test
    public void testGetValueShouldReturnStringArrayList() {
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("kg");
        strings.add("mg");
        Assert.assertEquals(strings, rulesEngineFactory.getValue("[kg,mg]"));
    }

    @Test
    public void testGetRelevanceShouldReturnFalse() throws IOException {
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \"isRelevant = true\"";
        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.when(assetManager.open("rule/test")).thenReturn(inputStream);
        boolean result = rulesEngineFactory.getRelevance(relevanceFacts, "test");
        Assert.assertFalse(result);
    }

    @Test
    public void testGetRelevanceShouldReturnTrue() throws IOException {
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \"isRelevant = true\"";
        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.when(assetManager.open("rule/test")).thenReturn(inputStream);
        relevanceFacts.put("step1_first_Name", "Doe");
        boolean result = rulesEngineFactory.getRelevance(relevanceFacts, "test");
        Assert.assertFalse(result);
    }

    @Test
    public void testGetDynamicRelevanceShouldReturnFalse() throws JSONException {
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        String rulesStrObject = "[" +
                "{\"key\":\"c29afdf9843e4c909a793dafd70e045b\"}," +
                "{" +
                "\"condition\":\"step1_diagnostic_test_c29afdf9843e4c909a793dafd70e045b == 'Pregnancy Test'\"," +
                "\"name\":\"step1_diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"description\":\"diagnostic_test_result_spinner_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"priority\":1," +
                "\"actions\":\"isRelevant = true\"" +
                "}" +
                "]";
        JSONArray jsonArray = new JSONArray(rulesStrObject);
        boolean result = rulesEngineFactory.getDynamicRelevance(relevanceFacts, jsonArray);
        Assert.assertFalse(result);
    }

    @Test
    public void testGetCalculationShouldReturnEmptyString() throws IOException {
        String specifiedString = "1";
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \"calculation = " + specifiedString + "\"";
        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.when(assetManager.open("rule/test")).thenReturn(inputStream);
        relevanceFacts.put(RuleConstant.SELECTED_RULE, "step1_last_name");
        relevanceFacts.put("step1_first_Name", "Doe");
        String result = rulesEngineFactory.getCalculation(relevanceFacts, "test");
        Assert.assertEquals(specifiedString, result);
    }

    @Test
    public void testGetDynamicCalculationShouldReturnSpecifiedString() throws JSONException {
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        String specifiedString = "test";
        String rulesStrObject = "[" +
                "{\"key\":\"c29afdf9843e4c909a793dafd70e045b\"}," +
                "{" +
                "\"condition\":\"step1_test_field_c29afdf9843e4c909a793dafd70e045b == 'test'\"," +
                "\"name\":\"step1_test_field_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"description\":\"test_field_c29afdf9843e4c909a793dafd70e045b\"," +
                "\"priority\":1," +
                "\"actions\":\"calculation = '" + specifiedString + "'\"" +
                "}" +
                "]";
        JSONArray jsonArray = new JSONArray(rulesStrObject);
        relevanceFacts.put(RuleConstant.SELECTED_RULE, "step1_test_field_c29afdf9843e4c909a793dafd70e045b");
        relevanceFacts.put("step1_test_field_c29afdf9843e4c909a793dafd70e045b", "test");
        String result = rulesEngineFactory.getDynamicCalculation(relevanceFacts, jsonArray);
        Assert.assertEquals(specifiedString, result);
    }

    @Test
    public void testGetConstraintShouldReturnSpecifiedString() throws IOException {
        String specifiedString = "1";
        rulesEngineFactory = new RulesEngineFactory(context, new HashMap<String, String>());
        Facts relevanceFacts = new Facts();
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \"constraint = " + specifiedString + "\"";
        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.when(assetManager.open("rule/test")).thenReturn(inputStream);
        relevanceFacts.put(RuleConstant.SELECTED_RULE, "step1_last_name");
        relevanceFacts.put("step1_first_Name", "Doe");
        String result = rulesEngineFactory.getConstraint(relevanceFacts, "test");
        Assert.assertEquals(specifiedString, result);
    }

    @Test
    public void testFormatCalculationShouldReturnEmptyString() throws Exception {
        String result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "formatCalculationReturnValue", "");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFormatCalculationShouldReturnJsonString() throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        String result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "formatCalculationReturnValue", hashMap);
        try {
            JSONObject jsonObject = new JSONObject(result);
            Assert.assertNotNull(jsonObject);
        } catch (JSONException e) {
            throw new JSONException("Not a json object");
        }
    }

    @Test
    public void testFormatCalculationShouldReturnFloatTo2dp() throws Exception {
        String result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "formatCalculationReturnValue", "34.789");
        Assert.assertEquals("34.79", result);
    }

    @Test
    public void testGetRulesFromAssetShouldCallActivityHandleError() throws IOException {
        String ruleFileName = "rules/calculation_file.yml";
        JsonFormActivity jsonFormActivity = Mockito.mock(JsonFormActivity.class);

        rulesEngineFactory = new RulesEngineFactory(jsonFormActivity, new HashMap<String, String>());
        ReflectionHelpers.callInstanceMethod(rulesEngineFactory, "getRulesFromAsset", ReflectionHelpers.ClassParameter.from(String.class, ruleFileName));

        Mockito.verify(jsonFormActivity).handleFormError(true, ruleFileName);
    }
}
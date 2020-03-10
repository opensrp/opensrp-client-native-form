package com.vijay.jsonwizard.rules;

import android.content.Context;

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

@RunWith(MockitoJUnitRunner.class)
public class RulesEngineFactoryTest {

    private RulesEngineFactory rulesEngineFactory;

    @Before
    public void setUp() {
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
            rulesEngineFactory = new RulesEngineFactory(Mockito.mock(Context.class), new HashMap<String, String>());
            Map<String, Rules> ruleMap = new HashMap<>();
            WhiteboxImpl.setInternalState(rulesEngineFactory, "ruleMap", ruleMap);
            Rules result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "getDynamicRulesFromJsonArray", jsonArray);
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
            Rules result = WhiteboxImpl.invokeMethod(rulesEngineFactory, "getDynamicRulesFromJsonArray", jsonArray);
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
        rulesEngineFactory = new RulesEngineFactory(Mockito.mock(Context.class), new HashMap<String, String>());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("kg");
        strings.add("mg");
        Assert.assertEquals(strings, rulesEngineFactory.getValue("[kg,mg]"));
    }
}
package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {
    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
}
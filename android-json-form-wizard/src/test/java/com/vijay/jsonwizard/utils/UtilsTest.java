package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UtilsTest extends BaseTest {

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
    public void testBuildRulesWithUniqueId() throws JSONException {
        JSONObject formElement = new JSONObject("{\"key\":\"date_larvae_collection\",\"type\":\"edit_text\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Date of larvae collection\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-dynamic\":\"repeating_groups_calculation_rules.yml\"}}}}");
        Assert.assertNotNull(formElement);

        String uniqueId = "33d56473a1de41e9986f952337c664ee";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();

        Utils.buildRulesWithUniqueId(formElement, uniqueId, JsonFormConstants.CALCULATION, RuntimeEnvironment.application, rulesFileMap);
        Assert.assertNotNull(rulesFileMap);
        Assert.assertEquals(1, rulesFileMap.size());
        Assert.assertEquals("step2_larvae_total != ''", rulesFileMap.get("rule/repeating_groups_calculation_rules.yml").get(0).get("condition"));
    }

    @Test
    public void testBuildRulesWithUniqueIdWithoutRulesEngine() throws JSONException {
        JSONObject formElement = new JSONObject("{\"key\":\"task_business_status\",\"label\":\"Status\",\"type\":\"native_radio\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"options\":[{\"key\":\"In Progress\",\"text\":\"In Progress\"},{\"key\":\"Incomplete\",\"text\":\"Incomplete\"},{\"key\":\"Not Eligible\",\"text\":\"Not Eligible\"},{\"key\":\"Complete\",\"text\":\"Complete\"}],\"relevance\":{\"step2:moz_type\":{\"ex-checkbox\":[{\"or\":[\"An. funestus\"]}]}},\"v_required\":{\"value\":true,\"err\":\"Please specify the task status\"}}");
        Assert.assertNotNull(formElement);

        String uniqueId = "33d56473a1de41e9986f952337c664ee";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();

        Utils.buildRulesWithUniqueId(formElement, uniqueId, JsonFormConstants.RELEVANCE, RuntimeEnvironment.application, rulesFileMap);
        Assert.assertNotNull(rulesFileMap);
        Assert.assertEquals(0, rulesFileMap.size());
    }

    @Test
    public void testCreateExpansionPanelChildren() throws JSONException {
        JSONArray fields = new JSONArray("[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"index\":4,\"values\":[\"ab:AB\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163117AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"rh_factor\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"index\":5,\"values\":[\"positive:Positive\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}]");
        Assert.assertNotNull(fields);

        List<String> strings = new Utils().createExpansionPanelChildren(fields);
        Assert.assertNotNull(strings);
        Assert.assertEquals(4, strings.size());
        Assert.assertEquals("Blood type test date:10-03-2020", strings.get(1));
    }

    @Test
    public void testGetDurationWithYears() {
        String duration = Utils.getDuration("10-03-2012", "30-05-2020");
        Assert.assertNotNull(duration);
        Assert.assertEquals("8y 2m", duration);
    }

    @Test
    public void testGetDurationWithWeeks() {
        String duration = Utils.getDuration("10-03-2020", "30-05-2020");
        Assert.assertNotNull(duration);
        Assert.assertEquals("11w 4d", duration);
    }

    @Test
    public void testGetDurationWithDays() {
        String duration = Utils.getDuration("1992-09-19T03:00:00.000+03:00", "1992-09-19T23:23:10.100+03:00");
        Assert.assertNotNull(duration);
        Assert.assertEquals("0d", duration);
    }
}
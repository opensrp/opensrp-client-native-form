package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

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
}
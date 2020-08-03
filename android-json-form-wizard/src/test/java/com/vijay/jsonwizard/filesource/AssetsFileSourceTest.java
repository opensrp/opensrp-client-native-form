package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.rules.RuleConstant;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
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
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class AssetsFileSourceTest {
    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    private AssetsFileSource assetsFileSource = AssetsFileSource.INSTANCE;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInstanceIsNoNull() {
        Assert.assertNotNull(AssetsFileSource.INSTANCE);
    }

    @Test
    public void testGetRulesFromFileConvertsFileToRules() throws Exception {
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_name.equalsIgnoreCase('Linet')\"\n" +
                "actions:\n" +
                "    - \"isRelevant = true\"";

        Mockito.when(context.getAssets()).thenReturn(assetManager);
        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.when(assetManager.open("rule/test")).thenReturn(inputStream);

        // rules are returned
        Rules rules = assetsFileSource.getRulesFromFile(context, "rule/test");
        Assert.assertFalse(rules.isEmpty());


        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        RulesEngine defaultRulesEngine = new DefaultRulesEngine(parameters);
        Facts facts = new Facts();
        facts.put("step1_first_name", "Linet");

        defaultRulesEngine.fire(rules, facts);
        Assert.assertTrue((Boolean) facts.get(RuleConstant.IS_RELEVANT));
    }

    @Test
    public void testGetFormFromFileReadsFile() throws Exception {
        String expected = "{\n" +
                "        \"key\": \"user_image\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"choose_image\",\n" +
                "        \"uploadButtonText\": \"Take a photo of the child\"\n" +
                "      }";
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("json.form/test.json")).thenReturn(inputStream);
        JSONObject jsonObject = assetsFileSource.getFormFromFile(context, "test");
        Assert.assertNotNull(jsonObject);
        Assert.assertEquals("user_image", jsonObject.getString("key"));
        Assert.assertEquals("choose_image", jsonObject.getString("type"));
        Assert.assertEquals("Take a photo of the child", jsonObject.getString("uploadButtonText"));
    }

    @Test
    public void testGetFileInputStream() throws Exception {
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        assetsFileSource.getFileInputStream(context, "test");
        Mockito.verify(assetManager).open("test");
    }
}

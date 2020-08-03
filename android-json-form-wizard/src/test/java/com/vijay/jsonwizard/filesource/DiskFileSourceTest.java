package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.os.Environment;

import org.jeasy.rules.api.Rules;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class DiskFileSourceTest {

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    @Mock
    private Context context;

    private DiskFileSource diskFileSource = Mockito.spy(DiskFileSource.INSTANCE);

    @Mock
    private File externalFile;

    @Before
    public void setUp() {
        Mockito.doReturn("/tmp/downloads").when(externalFile).toString();

        PowerMockito.mockStatic(Environment.class);
        Mockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).thenReturn(externalFile);
    }

    @Test
    public void testGetRulesFromFileConvertsFileToRules() throws Exception {

        String specifiedString = "1";
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \" calculation = " + specifiedString + "\"";

        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.doReturn(inputStream).when(diskFileSource).getInputStream(Mockito.any(File.class));

        Rules rules = diskFileSource.getRulesFromFile(context, "test_rule");
        Assert.assertFalse(rules.isEmpty());

        org.jeasy.rules.api.Rule rule = rules.iterator().next();
        Assert.assertEquals(rule.getName(), "step1_last_name");
        Assert.assertEquals(rule.getDescription(), "last_name");
        Assert.assertEquals(rule.getPriority(), 1);
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
        Mockito.doReturn(inputStream).when(diskFileSource).getInputStream(Mockito.any(File.class));

        JSONObject jsonObject = diskFileSource.getFormFromFile(context, "test");
        Assert.assertNotNull(jsonObject);
        Assert.assertEquals("user_image", jsonObject.getString("key"));
        Assert.assertEquals("choose_image", jsonObject.getString("type"));
        Assert.assertEquals("Take a photo of the child", jsonObject.getString("uploadButtonText"));
    }
}

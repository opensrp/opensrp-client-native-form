package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 16/03/2020
 */
public class JsonFormInterpolationToolTest {

    private final TestUtils testUtils = new TestUtils();
    private JsonFormInterpolationTool jsonFormInterpolationTool;
    private final String formName = "basic_form";

    @Before
    public void setUp() {
        jsonFormInterpolationTool = new JsonFormInterpolationTool();
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformation() throws IOException {
        String interpolatedFormPath = File.separator + "tmp" + File.separator + formName + "_interpolated" + ".json";
        String propertiesFilePath = File.separator + "tmp" + File.separator + formName + "_interpolated" + ".properties";

        jsonFormInterpolationTool.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");
        testUtils.copyFilesIntoResourcesFolder(interpolatedFormPath);
        testUtils.copyFilesIntoResourcesFolder(propertiesFilePath);

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString(formName + "_interpolated" + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));

        testUtils.deleteFile(testUtils.getResourcesFilePath() + File.separator + formName + "_interpolated" + ".json");
        testUtils.deleteFile(testUtils.getResourcesFilePath() + File.separator + formName +  "_interpolated" + ".properties");
    }
}

package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 16/03/2020
 */
public class JsonFormMLSAssetGeneratorTest {

    private final TestUtils testUtils = new TestUtils();
    private JsonFormMLSAssetGenerator jsonFormMLSAssetGenerator;
    private final String formName = "basic_form";

    @Before
    public void setUp() {
        jsonFormMLSAssetGenerator = new JsonFormMLSAssetGenerator();
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformation() {
        jsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + "tmp" + File.separator + formName  + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, File.separator + "tmp" + File.separator));

        testUtils.deleteFile(File.separator + "tmp" + File.separator + formName  + ".json");
        testUtils.deleteFile(File.separator + "tmp" + File.separator + formName  + ".properties");
    }
}

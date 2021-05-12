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

    @Before
    public void setUp() {
        jsonFormMLSAssetGenerator = new JsonFormMLSAssetGenerator();
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformationForJsonForm() throws Exception {
        String formName = "basic_form";
        jsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, null, File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator));

        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".properties");
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformationForJsonFormWithDynamicLabel() throws Exception {
        String formName = "sample_spinner_form";
        jsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        String actualJsonForm = NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, null, File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator);

        assertEquals(expectedJsonForm, actualJsonForm);

        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".properties");
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformationForJsonSubForm() throws Exception {
        String formName = "expansion_panel_sub_form";
        jsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, null, File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator));

        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        testUtils.deleteFile(File.separator + jsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".properties");
    }
}

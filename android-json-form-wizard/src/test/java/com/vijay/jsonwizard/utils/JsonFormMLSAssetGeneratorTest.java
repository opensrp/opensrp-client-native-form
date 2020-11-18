package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.TestUtils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 16/03/2020
 */
public class JsonFormMLSAssetGeneratorTest {

    private final TestUtils testUtils = new TestUtils();

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformationForJsonForm() throws Exception {
        String formName = "basic_form";
        JsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, null, File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator));

        testUtils.deleteFile(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        testUtils.deleteFile(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".properties");
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformationForJsonSubForm() throws Exception {
        String formName = "expansion_panel_sub_form";
        JsonFormMLSAssetGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = Utils.getFileContentsAsString(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm, null, File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator));

        testUtils.deleteFile(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".json");
        testUtils.deleteFile(File.separator + JsonFormMLSAssetGenerator.getMLSAssetsFolder() + File.separator + formName + ".properties");
    }
}

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
public class JsonFormPlaceholderGeneratorTest {

    private final TestUtils testUtils = new TestUtils();
    private JsonFormPlaceholderGenerator jsonFormPlaceholderGenerator;
    private final String formName = "basic_form";

    @Before
    public void setUp() {
        jsonFormPlaceholderGenerator = new JsonFormPlaceholderGenerator();
    }

    @Test
    public void testFormInterpolationShouldPerformCorrectTransformation() throws IOException {
        String placeholderInjectedFormPath = File.separator + "tmp" + File.separator + "placeholder_injected_" +  formName  + ".json";
        String propertiesFilePath = File.separator + "tmp" + File.separator + "placeholder_injected_" + formName  + ".properties";

        jsonFormPlaceholderGenerator.processForm(testUtils.getResourcesFilePath() + File.separator + formName + ".json");
        testUtils.copyFilesIntoResourcesFolder(placeholderInjectedFormPath);
        testUtils.copyFilesIntoResourcesFolder(propertiesFilePath);

        String expectedJsonForm = testUtils.getResourceFileContentsAsString(formName + ".json");
        String placeholderInjectedJsonForm = testUtils.getResourceFileContentsAsString("placeholder_injected_" + formName  + ".json");

        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(placeholderInjectedJsonForm));

        testUtils.deleteFile(testUtils.getResourcesFilePath() + File.separator + "placeholder_injected_" + formName  + ".json");
        testUtils.deleteFile(testUtils.getResourcesFilePath() + File.separator + "placeholder_injected_" + formName + ".properties");
    }
}

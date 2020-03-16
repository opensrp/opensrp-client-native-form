package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.TestUtils;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static com.vijay.jsonwizard.utils.Utils.convertStreamToString;
import static com.vijay.jsonwizard.utils.Utils.getTranslatedYamlFile;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFormLangUtilsTest {

    private final TestUtils testUtils = new TestUtils();

    @Test
    public void testJsonFormTranslationShouldTranslateForm() {
        Locale.setDefault(new Locale("id"));
        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));

        Locale.setDefault(new Locale("en", "US"));
        expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));
    }

    @Test
    public void testYamlFileTranslationShouldTranslateYamlFile() throws IOException {
        Context context = mockAssetManager("test_yaml_translation_interpolated");
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("file_name", context);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_en_US"), translatedYamlStr);
    }

    @Test
    public void testJsonFormTranslationShouldReturnUntranslatedForm() {
        Locale.setDefault(new Locale("id"));
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated_missing_translations");
        assertEquals(interpolatedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));
    }

    @Test
    public void testYamlFileTranslationShouldReturnUntranslatedYamlFile() throws IOException {
        Context context = mockAssetManager("test_yaml_translation_interpolated_missing_translations");
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("file_name", context);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_interpolated_missing_translations"), translatedYamlStr);
    }

    private Context mockAssetManager(String yamlFilePath) throws IOException {
        Context context = mock(Context.class);
        AssetManager assetManager = mock(AssetManager.class);
        doReturn(testUtils.getTestResource(yamlFilePath))
                .when(assetManager)
                .open(eq("file_name"));
        doReturn(assetManager).when(context).getAssets();
        return context;
    }
}

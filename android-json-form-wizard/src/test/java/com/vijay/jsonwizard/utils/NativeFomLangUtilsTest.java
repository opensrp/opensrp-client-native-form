package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.AssetManager;

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
public class NativeFomLangUtilsTest {

    @Test
    public void testJsonFormTranslation() {
        Locale.setDefault(new Locale("id"));
        String expectedJsonForm = getFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = getFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));

        Locale.setDefault(new Locale("en", "US"));
        expectedJsonForm = getFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm));
    }

    @Test
    public void testYamlFileTranslation() throws IOException {
        Context context = mock(Context.class);
        AssetManager assetManager = mock(AssetManager.class);
        doReturn(getTestResource("interpolated_yaml"))
                .when(assetManager)
                .open(eq("file_name"));
        doReturn(assetManager).when(context).getAssets();

        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("file_name", context);
        assertEquals(getFileContentsAsString("test_yaml_translation_en_US"), translatedYamlStr);
    }

    private InputStream getTestResource(String filePath) {
        return getClass().getClassLoader().getResourceAsStream(filePath);
    }

    private String getFileContentsAsString(String filePath) {
        return convertStreamToString(getTestResource(filePath));
    }
}

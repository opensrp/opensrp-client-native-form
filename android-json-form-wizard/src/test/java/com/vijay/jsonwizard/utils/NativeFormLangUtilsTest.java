package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.NativeFormLibrary;
import com.vijay.jsonwizard.TestUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.client.utils.contract.ClientFormContract;

import java.util.Locale;
import java.util.ResourceBundle;

import static com.vijay.jsonwizard.utils.Utils.getTranslatedYamlFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFormLangUtilsTest extends BaseTest {

    private final TestUtils testUtils = new TestUtils();

    @Test
    public void testJsonFormTranslationShouldTranslateForm() {
        Locale.setDefault(new Locale("id"));
        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));

        Locale.setDefault(new Locale("en", "US"));
        expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonFormTranslationShouldTranslateFormUsingLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application));
        allSharedPreferences.saveLanguagePreference(new Locale("id").toLanguageTag());
        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));

        allSharedPreferences.saveLanguagePreference(new Locale("en", "US").toLanguageTag());
        expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonFormTranslationShouldReturnUntranslatedForm() {
        Locale.setDefault(new Locale("id"));
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated_missing_translations");
        assertEquals(interpolatedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonSubFormTranslationShouldTranslateJsonSubForm() throws Exception {
        Locale.setDefault(new Locale("id"));
        String expectedSubFormJson = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedSubFormJson = FormUtils.loadSubForm("test_form_translation_interpolated", JsonFormConstants.DEFAULT_SUB_FORM_LOCATION, RuntimeEnvironment.application, true);
        assertEquals(expectedSubFormJson, interpolatedSubFormJson);

        Locale.setDefault(new Locale("en", "US"));
        interpolatedSubFormJson = FormUtils.loadSubForm("test_form_translation_interpolated", JsonFormConstants.DEFAULT_SUB_FORM_LOCATION, RuntimeEnvironment.application, true);
        expectedSubFormJson = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedSubFormJson, interpolatedSubFormJson);
    }

    @Test
    public void testYamlFileTranslationShouldTranslateYamlFile() {
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("test_yaml_translation_interpolated", RuntimeEnvironment.application);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_en_US"), translatedYamlStr);
    }

    @Test
    public void testYamlFileTranslationShouldReturnUntranslatedYamlFile() {
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("test_yaml_translation_interpolated_missing_translations", RuntimeEnvironment.application);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_interpolated_missing_translations"), translatedYamlStr);
    }

    @Test
    public void testCanGetResourceBundleWithPropertiesFromRepository() {
        ClientFormContract.Dao clientFormRepository = Mockito.mock(ClientFormContract.Dao.class);
        NativeFormLibrary.getInstance().setClientFormDao(clientFormRepository);
        Context context = RuntimeEnvironment.application;
        String enProperties = "step1.title = New client record\nstep1.previous_label = SAVE AND EXIT";
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");

        ClientFormContract.Model clientForm = new TestClientForm();

        clientForm.setJson(enProperties);
        Mockito.doReturn(clientForm).when(clientFormRepository).getActiveClientFormByIdentifier(Mockito.eq("form_strings.properties"));
        assertEquals("SAVE AND EXIT",  NativeFormLangUtils.getResourceBundleFromRepository(RuntimeEnvironment.application, interpolatedJsonForm).getString("step1.previous_label"));

        String swProperties = "step1.title = Rekodi mpya\nstep1.previous_label = WEKA ALAFU ONDOKA";
        clientForm.setJson(swProperties);
        NativeFormLangUtils.setAppLocale(context, "sw");
        Mockito.doReturn(clientForm).when(clientFormRepository).getActiveClientFormByIdentifier(Mockito.eq("form_strings_sw.properties"));
        assertEquals("Rekodi mpya",  NativeFormLangUtils.getResourceBundleFromRepository(RuntimeEnvironment.application, interpolatedJsonForm).getString("step1.title"));
    }

    @Test
    public void testResourceBundleWithPropertiesFromDbIsEmptyWhenClientFormDoesntExist() {
        ClientFormContract.Dao clientFormRepository = Mockito.mock(ClientFormContract.Dao.class);
        NativeFormLibrary.getInstance().setClientFormDao(clientFormRepository);
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");

        Mockito.doReturn(null).when(clientFormRepository).getActiveClientFormByIdentifier(Mockito.eq("form_strings.properties"));

        ResourceBundle mlsResourceBundle = NativeFormLangUtils.getResourceBundleFromRepository(RuntimeEnvironment.application, interpolatedJsonForm);
        assertFalse(mlsResourceBundle.getKeys().hasMoreElements());
    }
}

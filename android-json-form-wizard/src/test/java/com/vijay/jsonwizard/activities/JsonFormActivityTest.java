package com.vijay.jsonwizard.activities;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.util.Pair;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.NativeFormLibrary;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.TestConstants;
import com.vijay.jsonwizard.TestUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.event.RefreshExpansionPanelEvent;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.Utils;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class JsonFormActivityTest extends BaseActivityTest {
    private JsonFormActivity activity;
    private ActivityController<JsonFormActivity> controller;
    private final TestUtils testUtils = new TestUtils();

    @Mock
    private Context context;
    private Intent intent;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, DUMMY_JSON_FORM_STRING);
        activity = getActivityWithIntent(intent);
        activity.getmJSONObject().put(JsonFormConstants.SKIP_BLANK_STEPS, true);
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
    }

    @Test
    public void setmJsonObjectShouldSkipMissingStep() throws JSONException {
        activity = Mockito.spy(activity);
        Mockito.doNothing().when(activity).refreshHiddenViews(ArgumentMatchers.anyBoolean());
        activity.getFormFieldsMap().clear();
        ((JsonFormFragment) activity.getSupportFragmentManager().findFragmentById(R.id.container)).setmJsonApi(activity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.COUNT, 1);

        activity.setmJSONObject(jsonObject);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Assert.assertTrue(activity.getFormFieldsMap().isEmpty());
    }

    @Test
    public void testSetConfirmationTitleUpdatesConfirmationTitleCorrectly() {
        Assert.assertNotNull(activity.getConfirmCloseTitle());

        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close), activity.getConfirmCloseTitle());
        activity.setConfirmCloseTitle(DUMMY_TEST_STRING);
        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseTitle());
    }

    @Test
    public void testJsonFormShouldTranslationWhenGlobalConfigIsTrue() {
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");

        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, interpolatedJsonForm);
        NativeFormLibrary.getInstance().setPerformFormTranslation(true);
        Locale.setDefault(new Locale("en", "US"));
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();
        Assert.assertNotNull(activity);

        String jsonForm = activity.getJsonForm();

        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, jsonForm);
    }

    @Test
    public void testJsonFormShouldNotTranslationWhenGlobalConfigValueIsFalse() {
        String inputJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");

        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, inputJsonForm);
        NativeFormLibrary.getInstance().setPerformFormTranslation(false);
        Locale.setDefault(new Locale("en", "US"));
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();
        Assert.assertNotNull(activity);

        String jsonForm = activity.getJsonForm();

        assertEquals(inputJsonForm, jsonForm);
    }

    @Test
    public void testJsonFormShouldNotTranslateWhenIntentValueIsFalse() {
        String inputJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");

        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, inputJsonForm);
        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, false);
        NativeFormLibrary.getInstance().setPerformFormTranslation(true);
        Locale.setDefault(new Locale("en", "US"));
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();
        Assert.assertNotNull(activity);

        String jsonForm = activity.getJsonForm();

        assertEquals(inputJsonForm, jsonForm);
    }

    @Test
    public void testJsonFormShouldTranslateWhenIntentValueIsTrue() {
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");

        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, interpolatedJsonForm);
        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
        NativeFormLibrary.getInstance().setPerformFormTranslation(false);
        Locale.setDefault(new Locale("en", "US"));
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();
        Assert.assertNotNull(activity);

        String jsonForm = activity.getJsonForm();

        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, jsonForm);
    }

    @Test
    public void testSetConfirmationMessageUpdatesConfirmationMessageCorrectly() {
        Assert.assertNotNull(activity.getConfirmCloseMessage());
        //default message
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.confirm_form_close_explanation), activity.getConfirmCloseMessage());
        activity.setConfirmCloseMessage(DUMMY_TEST_STRING);
        Assert.assertEquals(DUMMY_TEST_STRING, activity.getConfirmCloseMessage());
    }

    @Test
    public void testInitiateFormUpdateShouldUpdateFieldsToBeHiddenOrDisabled() {
        String sampleForm = "{\"count\":\"1\",\"step1\":{\"title\":\"Basic Form One\",\"fields\":[{\"key\":\"user_image\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"finger_print\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"finger_print\",\"project_id\":\"tZqJnw0ajK04LMYdZzyw\",\"user_id\":\"test_user\",\"module_id\":\"mpower\",\"finger_print_option\":\"register\",\"uploadButtonText\":\"Take finger print\",\"image_file\":\"\",\"relevance\":{\"step1:user_first_name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"test\\\")\"}}},{\"key\":\"user_qr_code\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"User ID\",\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":false,\"err\":\"Please enter the user ID\"}},{\"key\":\"user_age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User age\",\"edit_type\":\"number\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"user_gps\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"gps\"}]}}";
        try {
            JSONObject jsonObject = new JSONObject(sampleForm);
            Form form = new Form();
            Set<String> disabledKeys = new HashSet<>();
            disabledKeys.add("user_image");
            Set<String> hiddenKeys = new HashSet<>();
            hiddenKeys.add("finger_print");
            form.setDisabledFields(disabledKeys);
            form.setHiddenFields(hiddenKeys);
            activity.form = form;
            activity.initiateFormUpdate(jsonObject);
            JSONObject stepObject = jsonObject.optJSONObject("step1");
            Assert.assertEquals("hidden", FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "user_image").optString("type"));
            Assert.assertTrue(FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "user_image").optBoolean("disabled"));
            Assert.assertEquals("hidden", FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "finger_print").optString("type"));
            Assert.assertFalse(FormUtils.getFieldJSONObject(stepObject.optJSONArray("fields"), "finger_print").has("disabled"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetValueFromAddressCoreForEditTexts() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"pregest_weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"5090\",\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"edit_type\":\"number\",\"relevance\":{\"step1:pregest_weight_unknown\":{\"ex-checkbox\":[{\"not\":[\"pregest_weight_unknown\"]}]}},\"v_numeric\":{\"value\":\"true\",\"err\":\"\"},\"v_min\":{\"value\":\"30\",\"err\":\"Weight must be equal or greater than 30\"},\"v_max\":{\"value\":\"180\",\"err\":\"Weight must be equal or less than 180\"},\"v_required\":{\"value\":\"true\",\"err\":\"Pre-gestational weight is required\"},\"step\":\"step1\",\"is-rule-check\":true}");
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("step1_pregest_weight"));
        Assert.assertEquals(0, facts.asMap().get("step1_pregest_weight"));
    }

    @Test
    public void testGetValueFromAddressCoreForRadioButtons() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Blood type test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"Blood type status is required\"},\"index\":\"0\",\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"done_today\"}");
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("step1_blood_type_test_status"));
        Assert.assertEquals("done_today", facts.asMap().get("step1_blood_type_test_status"));
    }

    @Test
    public void testGetValueFromAddressCoreForCheckBoxes() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"check_box\",\"label\":\"Abnormal\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"options\":[{\"key\":\"rapid_breathing\",\"text\":\"Rapid breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"TACHYPNEA\",\"openmrs_entity_id\":\"125061\",\"value\":true},{\"key\":\"slow_breathing\",\"text\":\"Slow breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false}],\"value\":[\"rapid_breathing\"],\"is-rule-check\":false}");
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddressCore", jsonObject);
        Assert.assertNotNull(facts);
        Assert.assertTrue(facts.asMap().containsKey("slow_breathing"));
        Assert.assertEquals("false", facts.asMap().get("slow_breathing"));
        Assert.assertTrue(facts.asMap().containsKey("rapid_breathing"));
        Assert.assertEquals("true", facts.asMap().get("rapid_breathing"));
    }


    @Test
    public void testStringFormatShouldFormatStringTemplateCorrectly() {
        String str = "{bmi} translates {bmi_meaning}";
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("bmi", "24.1");
        List<String> strings = new ArrayList<>();
        strings.add("Overweight 25-29.0");
        strings.add("Underweight < 18.5");
        strings.add("Normal 18.5 - 24.9");
        valueMap.put("bmi_meaning", strings);
        Spanned result = activity.stringFormat(str, valueMap, true);
        Spanned expectedResult = Html.fromHtml("<b>24.1</b> translates <b>Overweight 25-29.0, Underweight < 18.5, Normal 18.5 - 24.9</b>");
        Assert.assertEquals(expectedResult.toString(), result.toString());
    }

    @Test
    public void testGetSubFormFieldsShouldReturnFieldsFromSubForm() {
        String subFormName = "expansion_panel_sub_form";
        String subFormLocation = "form.sub_form";
        JSONArray fields = new JSONArray();
        JSONArray result = activity.getSubFormFields(subFormName, subFormLocation, fields);
        Assert.assertEquals(7, result.length());
    }

    @Test
    public void testSetFilterTouchesWhenObscuredSetToTrueForActivityLayout() {
        Assert.assertTrue(activity.findViewById(R.id.native_form_activity).getFilterTouchesWhenObscured());
    }

    @Test
    public void testSetRadioButtonOptionsDisabled() throws Exception {
        AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(activity.getBaseContext());
        appCompatRadioButton.setEnabled(true);

        LinearLayout radioButtonMainLayout = new LinearLayout(activity.getBaseContext());
        LinearLayout linearLayout = new LinearLayout(activity.getBaseContext());
        RelativeLayout radioGroupChildLayout = new RelativeLayout(activity.getBaseContext());

        radioButtonMainLayout.addView(appCompatRadioButton);
        linearLayout.addView(radioButtonMainLayout);
        radioGroupChildLayout.addView(linearLayout);
        RadioGroup radioGroup = new RadioGroup(activity.getBaseContext());
        radioGroup.addView(radioGroupChildLayout);

        Whitebox.invokeMethod(activity, "setReadOnlyRadioButtonOptions", radioGroup, false);
        Assert.assertFalse(appCompatRadioButton.isEnabled());
    }

    @Test
    public void testSetRadioButtonOptionsEnabled() throws Exception {
        AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(activity.getBaseContext());
        appCompatRadioButton.setEnabled(false);

        LinearLayout radioButtonMainLayout = new LinearLayout(activity.getBaseContext());
        LinearLayout linearLayout = new LinearLayout(activity.getBaseContext());
        RelativeLayout radioGroupChildLayout = new RelativeLayout(activity.getBaseContext());

        radioButtonMainLayout.addView(appCompatRadioButton);
        linearLayout.addView(radioButtonMainLayout);
        radioGroupChildLayout.addView(linearLayout);
        RadioGroup radioGroup = new RadioGroup(activity.getBaseContext());
        radioGroup.addView(radioGroupChildLayout);

        Whitebox.invokeMethod(activity, "setReadOnlyRadioButtonOptions", radioGroup, true);
        Assert.assertTrue(appCompatRadioButton.isEnabled());
    }

    @Test
    public void testSetRadioButtonOptionsEnabledWithTheWrongView() throws Exception {
        AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(activity.getBaseContext());
        appCompatRadioButton.setEnabled(false);

        LinearLayout radioButtonMainLayout = new LinearLayout(activity.getBaseContext());
        LinearLayout linearLayout = new LinearLayout(activity.getBaseContext());
        RelativeLayout radioGroupChildLayout = new RelativeLayout(activity.getBaseContext());

        radioButtonMainLayout.addView(appCompatRadioButton);
        linearLayout.addView(radioButtonMainLayout);
        radioGroupChildLayout.addView(linearLayout);
        RelativeLayout radioGroup = new RelativeLayout(activity.getBaseContext());
        radioGroup.addView(radioGroupChildLayout);

        Whitebox.invokeMethod(activity, "setReadOnlyRadioButtonOptions", radioGroup, true);
        Assert.assertFalse(appCompatRadioButton.isEnabled());
    }

    @Test
    public void testToggleViewVisibilityShouldMakeViewVisible() {
        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, TestConstants.BASIC_FORM);
        activity = getActivityWithIntent(intent);
        int id = ViewUtil.generateViewId();
        LinearLayout rootLayout = new LinearLayout(activity.getApplicationContext());
        MaterialEditText view = Mockito.spy(new MaterialEditText(activity.getApplicationContext()));
        view.setId(id);
        rootLayout.addView(view);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(id);
        view.setTag(R.id.canvas_ids, jsonArray.toString());
        view.setTag(R.id.address, "step1:user_last_name");
        activity.toggleViewVisibility(view, true, false);
        Mockito.verify(view, Mockito.times(1)).setVisibility(Mockito.eq(View.VISIBLE));
        Mockito.verify(view, Mockito.times(3)).setFocusable(Mockito.eq(true));
        Mockito.verify(view, Mockito.times(2)).setFocusableInTouchMode(Mockito.eq(true));
        Mockito.verify(view, Mockito.times(3)).setEnabled(Mockito.eq(true));
        Mockito.verify(view, Mockito.times(1)).invalidate();
        Mockito.verify(view, Mockito.times(1))
                .setTag(Mockito.eq(R.id.relevance_decided), Mockito.eq(true));
    }

    @Test
    public void testToggleViewVisibilityShouldMakeViewInVisible() {
        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, TestConstants.BASIC_FORM);
        activity = getActivityWithIntent(intent);
        int id = ViewUtil.generateViewId();
        LinearLayout rootLayout = new LinearLayout(activity.getApplicationContext());
        MaterialEditText view = Mockito.spy(new MaterialEditText(activity.getApplicationContext()));
        view.setId(id);
        rootLayout.addView(view);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(id);
        view.setTag(R.id.canvas_ids, jsonArray.toString());
        view.setTag(R.id.address, "step1:user_last_name");
        activity.toggleViewVisibility(view, false, false);
        Mockito.verify(view, Mockito.times(1)).setVisibility(Mockito.eq(View.GONE));
        Mockito.verify(view, Mockito.times(2)).setFocusable(Mockito.eq(false));
        Mockito.verify(view, Mockito.times(2)).setFocusableInTouchMode(Mockito.eq(false));
        Mockito.verify(view, Mockito.times(3)).setEnabled(Mockito.eq(false));
        Mockito.verify(view, Mockito.times(1)).invalidate();
        Mockito.verify(view, Mockito.times(1))
                .setTag(Mockito.eq(R.id.relevance_decided), Mockito.eq(false));
    }

    private JsonFormActivity getActivityWithIntent(Intent intent) {
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        return controller.get();
    }

    @Test
    public void testRefreshExpansionPanelShouldInvokeExpectedMethods() throws JSONException {
        activity = Mockito.spy(activity);

        Utils mockUtils = Mockito.mock(Utils.class);
        FormUtils mockFormUtils = Mockito.mock(FormUtils.class);

        ReflectionHelpers.setField(activity, "formUtils", mockFormUtils);
        ReflectionHelpers.setField(activity, "utils", mockUtils);

        JSONArray jsonArray = new JSONArray();
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        RefreshExpansionPanelEvent event = new RefreshExpansionPanelEvent(jsonArray, linearLayout);

        //Layout
        RelativeLayout layoutHeader = new RelativeLayout(activity);
        ImageView status = new ImageView(activity);
        status.setId(R.id.statusImageView);
        layoutHeader.addView(status, 0);
        linearLayout.addView(layoutHeader);

        LinearLayout contentLayout = (new LinearLayout(activity));
        linearLayout.addView(contentLayout, 1);
        LinearLayout mainContentView = new LinearLayout(activity);
        mainContentView.setId(R.id.contentView);
        contentLayout.addView(mainContentView);

        LinearLayout buttonLayout = (new LinearLayout(activity));
        buttonLayout.setId(R.id.accordion_bottom_navigation);
        contentLayout.addView(buttonLayout);

        Button undoButton = new Button(activity);
        undoButton.setId(R.id.undo_button);
        buttonLayout.addView(undoButton);
        //

        List<String> values = Arrays.asList("Done", "Not Done");
        Mockito.doReturn(values)
                .when(mockUtils)
                .createExpansionPanelChildren(ArgumentMatchers.any(JSONArray.class));

        activity.refreshExpansionPanel(event);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Mockito.verify(mockFormUtils)
                .updateExpansionPanelRecyclerView(ArgumentMatchers.eq(values), ArgumentMatchers.eq(status), ArgumentMatchers.any(Context.class));

        Mockito.verify(mockFormUtils)
                .addValuesDisplay(ArgumentMatchers.eq(values), ArgumentMatchers.eq(mainContentView), ArgumentMatchers.any(Context.class));

        Mockito.verify(mockUtils)
                .enableExpansionPanelViews(ArgumentMatchers.eq(linearLayout));
    }

    @Test
    public void testGetRelevanceAddressReturnsExpectedAddressAndRelevancePair() throws JSONException {
        View view = new View(activity.getBaseContext());
        JSONObject curRelevance = new JSONObject();
        try {
            curRelevance = new JSONObject("{ex-checkbox:[{\"or\":[\"other\"]}]}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        view.setTag(R.id.relevance,
                "{\"step1:medications\":{ex-checkbox:[{\"or\":[\"other\"]}]}}");
        view.setTag(R.id.key, JsonFormConstants.SIMPRINTS_OPTION_REGISTER);
        view.setTag(R.id.address, "step1:medications_other");
        view.setTag(R.id.extraPopup, false);
        String[] address = new String[]{"step1", "medications"};

        Pair pair = new Pair<>(address, curRelevance);
        Pair activityPair = activity.getRelevanceAddress(view, false);
        // assert that the right relevance condition is returned
        Assert.assertTrue(pair.second.toString().equals(activityPair.second.toString()));
        String[] activityAddress = (String[]) pair.first;
        // assert address array contains expected elements
        Assert.assertTrue(address[0].equals(activityAddress[0]));
        Assert.assertTrue(address[1].equals(activityAddress[1]));

    }

    @Test
    public void testGetValueFromAddressReturnsNull() throws Exception {
        String[] address = {"address1:one", "address2:two", "address3:three"};
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        activity = new JsonFormActivity();
        Facts facts = Whitebox.invokeMethod(activity, "getValueFromAddress", address, true, jsonObject);
        assertEquals(0, facts.asMap().size());

    }


    @Test
    public void testUpdateUiByCalculationReturnsNullPair() throws JSONException {
        activity = getActivityWithIntent(intent);
        int id = ViewUtil.generateViewId();
        String calculation = "{\"user_last_name\":" + new JSONObject("{\"user_last_name\":\"button\"}") + "}:user_last_name";
        MaterialEditText view = Mockito.spy(new MaterialEditText(activity.getApplicationContext()));
        view.setId(id);
        view.setTag(R.id.calculation, calculation);
        view.setTag(R.id.key, "form_key");
        view.setTag(R.id.address, "step1:user_last_name");
        Pair<String[], JSONObject> pair = activity.getCalculationAddressAndValue(view);
        assertEquals(null, pair.first);
    }

    @Test
    public void testUpdateUIByMaterialEditText() throws Exception {
        View view = Mockito.mock(MaterialEditText.class);
        String address = "test:key";
        String childKey = "test";
        String previous = "";
        String errorMessage = "false";
        String key = "yes";
        Mockito.doReturn(address).when(view).getTag(R.id.address);
        Mockito.doReturn(childKey).when(view).getTag(R.id.childKey);
        Mockito.doReturn(previous).when(view).getTag(R.id.previous);
        Mockito.doReturn(key).when(view).getTag(R.id.key);
        Mockito.doReturn("edit_text").when(view).getTag(R.id.type);
        String json = " {\n" +
                "        \"key\": \"user_first_name\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"User First name\",\n" +
                "        \"edit_type\": \"name\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter the first name\"\n" +
                "        },\n" +
                "        \"v_regex\": {\n" +
                "          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n" +
                "          \"err\": \"Please enter a valid name\"\n" +
                "        }\n" +
                "      }";
        Mockito.doReturn(new JSONObject(json)).when(view).getTag(R.id.json_object);
        Whitebox.invokeMethod(activity, "updateUiByConstraints", view, true, errorMessage);
        view.getTag(R.id.key);
        assertEquals("yes", view.getTag(R.id.key));


    }

    @Test
    public void testUpdateUIByConstraintsOfLinearLayoutReturnsNull() throws Exception {
        View view = Mockito.mock(LinearLayout.class);
        String address = "test:key";
        String childKey = "test";
        String previous = "";
        String errorMessage = "false";
        String key = "yes";
        Mockito.doReturn(address).when(view).getTag(R.id.address);
        Mockito.doReturn(childKey).when(view).getTag(R.id.childKey);
        Mockito.doReturn(previous).when(view).getTag(R.id.previous);
        Mockito.doReturn(key).when(view).getTag(R.id.key);
        Mockito.doReturn("linear_layout").when(view).getTag(R.id.type);
        String json = " {\n" +
                "        \"key\": \"user_check_box\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"openmrs_data_type\": \"select one\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"label\": \"Do want to select any checkbox?\",\n" +
                "        \"label_text_style\": \"bold\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"None\",\n" +
                "            \"text\": \"None\",\n" +
                "            \"value\": false,\n" +
                "            \"openmrs_choice_id\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"yes\",\n" +
                "            \"text\": \"Yes\",\n" +
                "            \"value\": false,\n" +
                "            \"openmrs_choice_id\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"no\",\n" +
                "            \"text\": \"No\",\n" +
                "            \"value\": false,\n" +
                "            \"openmrs_choice_id\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"other\",\n" +
                "            \"text\": \"Other\",\n" +
                "            \"value\": false,\n" +
                "            \"openmrs_choice_id\": \"\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"false\"\n" +
                "        },\n" +
                "        \"value\": \"[yes]\"\n" +
                "      }";
        Mockito.doReturn(new JSONObject(json)).when(view).getTag(R.id.json_object);
        Whitebox.invokeMethod(activity, "updateUiByConstraints", view, true, errorMessage);
        LinearLayout linearLayout = (LinearLayout) view;
        assertEquals(null, linearLayout.getChildAt(0));
    }

    @Test
    public void testUpdateUIByConstraintsOfRadioButton() throws Exception {
        View view = Mockito.mock(RadioGroup.class);
        String address = "test:key";
        String childKey = "test";
        String previous = "false";
        String errorMessage = "false";
        String key = "yes";
        Mockito.doReturn(address).when(view).getTag(R.id.address);
        Mockito.doReturn(childKey).when(view).getTag(R.id.childKey);
        Mockito.doReturn(previous).when(view).getTag(R.id.previous);
        Mockito.doReturn(key).when(view).getTag(R.id.key);
        Mockito.doReturn("check_box").when(view).getTag(R.id.type);
        String json = " {\n" +
                "                \"key\": \"respiratory_exam_radio_button\",\n" +
                "                \"type\": \"native_radio\",\n" +
                "                \"values\": [\n" +
                "                  \"3:Abnormal\"\n" +
                "                ]\n" +
                "              }";
        Mockito.doReturn(new JSONObject(json)).when(view).getTag(R.id.json_object);
        Whitebox.invokeMethod(activity, "updateUiByConstraints", view, true, errorMessage);
        assertEquals(key, view.getTag(R.id.key));
    }

    @Test
    public void testGetViewKey() throws Exception {
        View view = Mockito.mock(View.class);
        String key = "key";
        String childKey = "childKey";
        JsonFormActivity jsonFormActivity = Mockito.mock(JsonFormActivity.class);
        Mockito.doReturn(childKey).when(view).getTag(R.id.childKey);
        Mockito.doReturn(key).when(view).getTag(R.id.key);
        String returnKey = Whitebox.invokeMethod(jsonFormActivity, "getViewKey", view);
        assertEquals(key + ":" + childKey, returnKey);

    }
}

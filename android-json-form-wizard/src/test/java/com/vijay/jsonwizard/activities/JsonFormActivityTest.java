package com.vijay.jsonwizard.activities;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonFormActivityTest extends BaseActivityTest {
    private JsonFormActivity activity;
    private ActivityController<JsonFormActivity> controller;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, DUMMY_JSON_FORM_STRING);
        controller = Robolectric.buildActivity(JsonFormActivity.class, intent).create().start();
        activity = controller.get();
        activity.getmJSONObject().put(JsonFormConstants.SKIP_BLANK_STEPS, true);

        Assert.assertNotNull(activity);
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
}

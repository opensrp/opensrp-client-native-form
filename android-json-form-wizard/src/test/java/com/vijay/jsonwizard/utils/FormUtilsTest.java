package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.ExpansionPanelValuesModel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class FormUtilsTest extends BaseTest {
    private FormUtils formUtils;
    private JSONObject jsonFormObject;
    private String jsonForm = "{\"count\":\"4\",\"encounter_type\":\"Test\",\"entity_id\":\"\",\"relational_id\":\"\",\"validate_on_submit\":true,\"show_errors_on_submit\":true,\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Test\",\"next\":\"step2\",\"fields\":[{\"key\":\"delivery_complications\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161641AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"check_box\",\"label\":\"Any delivery complications?\",\"label_text_size\":\"18sp\",\"label_text_color\":\"#FF9800\",\"hint\":\"Any delivery complications?\",\"read_only\":true,\"editable\":true,\"exclusive\":[\"none\"],\"options\":[{\"key\":\"none\",\"text\":\"None\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_bleeding\",\"text\":\"Severe bleeding/Hemorrhage\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text_color\":\"#000000\"},{\"key\":\"placenta_previa\",\"text\":\"Placenta previa\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text_size\":\"15sp\"},{\"key\":\"cord_prolapse\",\"text\":\"Cord prolapse\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text_size\":\"10sp\"},{\"key\":\"prolonged_obstructed_labour\",\"text\":\"Prolonged/obstructed labour\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"abnormal_presentation\",\"text\":\"Abnormal presentation\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text_color\":\"#FF9800\"},{\"key\":\"perineal_tear\",\"text\":\"Perineal tear (2, 3 or 4th degree)\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Other\",\"text\":\"Other\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"false\"}},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hidden\":true,\"hint\":\"First name\",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}}}]},\"step2\":{\"title\":\"Test 2\",\"next\":\"step3\",\"fields\":[{\"key\":\"fetal_heartbeat\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"Which medications is she still taking ? Which medications is she still taking ?\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"yes\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"specify fetal heart rate (bpm)...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"content_form\":\"fetal_heartbeat_sub_form\"},{\"key\":\"no\",\"text\":\"No\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"specify fetal heart rate (bpm)...\",\"specify_widget\":\"check_box\",\"specify_info_color\":\"#8C8C8C\",\"content_form\":\"fetal_heartbeat_sub_form\"}],\"v_required\":{\"value\":true,\"err\":\"Please specify if fetal heartbeat is present.\"}},{\"key\":\"fetal_heart_beat_rate_value\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"edit_type\":\"number\"}]},\"step3\":{\"title\":\"Maternal Exam\",\"next\":\"step4\",\"fields\":[{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"20dp\"},{\"key\":\"gravida_label\",\"type\":\"label\",\"label_text_style\":\"bold\",\"text\":\"No. of pregnancies (including this pregnancy)\",\"text_color\":\"#000000\",\"v_required\":{\"value\":true}}]}}";

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        formUtils = new FormUtils();
        jsonFormObject = new JSONObject(jsonForm);
    }

    @Test
    public void testGetDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar res = FormUtils.getDate("today-1w");
        assertEquals(sdf.format(calendar.getTime()), sdf.format(res.getTime()));
    }

    @Test
    public void testGetFieldFromForm() throws JSONException {
        JSONObject field = FormUtils.getFieldFromForm(jsonFormObject, "gravida_label");
        Assert.assertNotNull(field);
        Assert.assertEquals("label", field.getString(JsonFormConstants.TYPE));
    }

    @Test
    public void testGetSpecifyText() throws JSONException {
        String secondaryValues = "[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\",\"values\":[\"rapid_breathing:Rapid breathing:true\"]},{\"key\":\"respiratory_exam_radio_button\",\"type\":\"native_radio\",\"values\":[\"1:Not done\"]},{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"edit_text\",\"values\":[\"other:Respiratory exam answer\"]}]";
        JSONArray jsonArray = new JSONArray(secondaryValues);
        String specifyText = formUtils.getSpecifyText(jsonArray);
        Assert.assertNotNull(specifyText);
        Assert.assertEquals("Rapid breathing, Not done, Respiratory exam answer", specifyText);
    }

    @Test
    public void testCreateExpansionPanelValues() throws JSONException {
        String selectedValues = "[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Blood type test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"Blood type status is required\"},\"index\":\"0\",\"value\":\"done_today\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"10dp\",\"index\":\"1\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_calculation_rules.yml\"}}},\"index\":\"2\",\"value\":\"10-03-2020\"},{\"key\":\"blood_type_test_date\",\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Blood type test date\",\"expanded\":\"false\",\"max_date\":\"today\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_calculation_rules.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Date that the blood test was done.\"},\"index\":\"3\",\"is_visible\":false},{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"a\",\"text\":\"A\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"b\",\"text\":\"B\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ab\",\"text\":\"AB\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163117AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"o\",\"text\":\"O\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"Please specify blood type\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_relevance_rules.yml\"}}},\"index\":\"4\",\"is_visible\":true,\"value\":\"ab\"},{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"Rh factor is required\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_relevance_rules.yml\"}}},\"index\":\"5\",\"is_visible\":true,\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"negative\"},{\"key\":\"rh_factor_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Rh factor negative counseling\",\"toaster_info_text\":\"- Woman is at risk of alloimmunisation if the baby's father is Rh positive or unknown.\\n\\n- Proceed with local protocol to investigate sensitization and the need for referral.\\n\\n- If Rh negative and non-sensitized, woman should receive anti- D prophylaxis postnatally if the baby is Rh positive.\",\"toaster_info_title\":\"Rh factor negative counseling\",\"toaster_type\":\"warning\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"expansion_panel_relevance_rules.yml\"}}},\"index\":\"6\",\"is_visible\":true}]";
        JSONArray fields = formUtils.createExpansionPanelValues(new JSONArray(selectedValues));
        Assert.assertNotNull(fields);
        Assert.assertEquals(5, fields.length());
    }

    @Test
    public void testCheckValuesContent() throws JSONException {
        JSONArray value = new JSONArray("[\n" +
                "  {\n" +
                "    \"key\": \"blood_type_test_status\",\n" +
                "    \"type\": \"extended_radio_button\",\n" +
                "    \"label\": \"Blood type test\",\n" +
                "    \"index\": 0,\n" +
                "    \"values\": [\n" +
                "      \"done_today:Done today\"\n" +
                "    ],\n" +
                "    \"openmrs_attributes\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_entity_id\": \"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"value_openmrs_attributes\": [\n" +
                "      {\n" +
                "        \"key\": \"blood_type_test_status\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]");
        boolean showHidden = formUtils.checkValuesContent(value);
        Assert.assertFalse(showHidden);
    }

    @Test
    public void testCheckValuesContentWithMultipleValues() throws JSONException {
        JSONArray value = new JSONArray("[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}]");
        boolean showHidden = formUtils.checkValuesContent(value);
        Assert.assertTrue(showHidden);
    }

    @Test
    public void testAddFormDetails() {
        String form = "{\"count\":\"1\",\"encounter_type\":\"Test\",\"entity_id\":\"\",\"relational_id\":\"\",\"validate_on_submit\":true,\"show_errors_on_submit\":true,\"form_version\":\"0.0.1\",\"step1\":{\"title\":\"Basic Form One\",\"next\":\"step2\",\"fields\":[{\"key\":\"user_image\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"}]}}";
        String updatedForm = formUtils.addFormDetails(form);
        Assert.assertNotNull(updatedForm);
        Assert.assertTrue(updatedForm.contains("appVersionName"));
    }

    @Test
    public void testAddFormDetailsWithNull() {
        String updatedForm = formUtils.addFormDetails(null);
        Assert.assertNotNull(updatedForm);
        Assert.assertTrue(StringUtils.isAllBlank(updatedForm));
        Assert.assertFalse(updatedForm.contains("appVersionName"));
    }

    @Test
    public void testLoadExpansionPanelValues() throws JSONException {
        JSONArray jsonArray = new JSONArray("[{\"key\":\"accordion_panel_demo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"text\":\"Expansion Panel Demo\",\"type\":\"expansion_panel\",\"content_form\":\"expansion_panel_sub_form\",\"container\":\"anc_test\",\"value\":[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"index\":4,\"values\":[\"ab:AB\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163117AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"rh_factor\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"index\":5,\"values\":[\"positive:Positive\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}],\"required_fields\":[\"blood_type_test_status\",\"blood_type\",\"rh_factor\"]}]");
        JSONArray values = formUtils.loadExpansionPanelValues(jsonArray, "accordion_panel_demo");
        Assert.assertNotNull(values);
        Assert.assertEquals(5, values.length());
    }

    @Test
    public void testCreateSecondaryValuesMap() throws JSONException {
        JSONArray jsonArray = new JSONArray("[{\"key\":\"accordion_panel_demo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"text\":\"Expansion Panel Demo\",\"type\":\"expansion_panel\",\"content_form\":\"expansion_panel_sub_form\",\"container\":\"anc_test\",\"value\":[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"index\":4,\"values\":[\"ab:AB\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163117AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"rh_factor\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"index\":5,\"values\":[\"positive:Positive\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}],\"required_fields\":[\"blood_type_test_status\",\"blood_type\",\"rh_factor\"]}]");
        JSONArray values = formUtils.loadExpansionPanelValues(jsonArray, "accordion_panel_demo");
        Assert.assertNotNull(values);

        Map<String, ExpansionPanelValuesModel> valuesModelMap = formUtils.createSecondaryValuesMap(values);
        Assert.assertNotNull(valuesModelMap);
        Assert.assertTrue(valuesModelMap.containsKey("blood_type_test_status"));

        ExpansionPanelValuesModel valuesModel = valuesModelMap.get("blood_type_test_status");
        Assert.assertNotNull(valuesModel);
        Assert.assertEquals("Blood type test", valuesModel.getLabel());
    }

    @Test
    public void testAddExpansionPanelFormValuesShouldPopulateValueAttributeOfJsonObj() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        String widgetRadioExpansionPanelValue = "{\"key\":\"ultrasound\",\"type\":\"extended_radio_button\",\"label\":\"Ultrasound test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"ultrasound\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}";
        String widgetCheckBoxExpansionPanelValue = "{\"key\":\"ultrasound_notdone\",\"type\":\"check_box\",\"label\":\"Reason\",\"index\":4,\"values\":[\"expired_stock:Expired stock:true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"161476AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"ultrasound_notdone\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}";

        String widgetRadio = "{\"key\":\"ultrasound\",\"openmrs_entity_parent\":\"159617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163725AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Ultrasound test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true}}";
        String widgetCheckbox = "{\"key\":\"ultrasound_notdone\",\"openmrs_entity_parent\":\"161476AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Reason\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"expired_stock\",\"text\":\"Expired stock\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"161476AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"v_required\":{\"value\":true}}";
        JSONObject jsonObjectRadio = new JSONObject(widgetRadioExpansionPanelValue);
        JSONObject jsonObjectCheckBox = new JSONObject(widgetCheckBoxExpansionPanelValue);

        jsonArray.put(new JSONObject(widgetRadio)).put(new JSONObject(widgetCheckbox));
        Map<String, ExpansionPanelValuesModel> expansionPanelValuesModelMap = new HashMap<>();
        ExpansionPanelValuesModel expansionPanelValuesModel = new ExpansionPanelValuesModel(
                jsonObjectRadio.optString(JsonFormConstants.KEY),
                jsonObjectRadio.optString(JsonFormConstants.TYPE),
                jsonObjectRadio.optString(JsonFormConstants.LABEL),
                jsonObjectRadio.optInt(JsonFormConstants.INDEX),
                jsonObjectRadio.optJSONArray(JsonFormConstants.VALUES),
                jsonObjectRadio.optJSONObject(JsonFormConstants.OPENMRS_ATTRIBUTES),
                jsonObjectRadio.optJSONArray(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES));
        expansionPanelValuesModelMap.put("ultrasound", expansionPanelValuesModel);

        ExpansionPanelValuesModel expansionPanelValuesModelNotDone = new ExpansionPanelValuesModel(
                jsonObjectCheckBox.optString(JsonFormConstants.KEY),
                jsonObjectCheckBox.optString(JsonFormConstants.TYPE),
                jsonObjectCheckBox.optString(JsonFormConstants.LABEL),
                jsonObjectCheckBox.optInt(JsonFormConstants.INDEX),
                jsonObjectCheckBox.optJSONArray(JsonFormConstants.VALUES),
                jsonObjectCheckBox.optJSONObject(JsonFormConstants.OPENMRS_ATTRIBUTES),
                jsonObjectCheckBox.optJSONArray(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES));
        expansionPanelValuesModelMap.put("ultrasound_notdone", expansionPanelValuesModelNotDone);
        FormUtils formUtils = new FormUtils();
        JSONArray result = formUtils.addExpansionPanelFormValues(jsonArray, expansionPanelValuesModelMap);
        JSONObject objectRadio = result.optJSONObject(0);//expired_stock
        JSONObject objectCheckbox = result.optJSONObject(1);
        Assert.assertEquals("done_today", objectRadio.optString(JsonFormConstants.VALUE));
        JSONArray jsonArrayOptions = objectCheckbox.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        JSONObject expectedOption = null;
        for (int i = 0; i < jsonArrayOptions.length(); i++) {
            JSONObject option = jsonArrayOptions.optJSONObject(i);
            if (option.has(JsonFormConstants.VALUE) && option.optBoolean(JsonFormConstants.VALUE)) {
                expectedOption = option;
            }
        }
        Assert.assertNotNull(expectedOption);
        Assert.assertEquals("expired_stock", expectedOption.optString(JsonFormConstants.KEY));
    }
}

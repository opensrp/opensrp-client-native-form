package com.vijay.jsonwizard.utils;

import com.google.android.apps.common.testing.accessibility.framework.proto.FrameworkProtos;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

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
        String selectedValues = "[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"check_box\",\"label\":\"Abnormal\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"options\":[{\"key\":\"rapid_breathing\",\"text\":\"Rapid breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"TACHYPNEA\",\"openmrs_entity_id\":\"125061\",\"value\":true},{\"key\":\"slow_breathing\",\"text\":\"Slow breathing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":false}]},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Place of birth *\",\"label_info_title\":\"Place of birth spinner\",\"label_info_text\":\"here is some text on this dialog\",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value\":\"Home\"},{\"key\":\"respiratory_exam_abnormal_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"relevance\":{\"step1:respiratory_exam_abnormal\":{\"ex-checkbox\":[{\"or\":[\"other\"]}]}},\"value\":\"Reasons\"},{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"3\",\"options\":[{\"key\":\"1\",\"text\":\"Not done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"2\",\"text\":\"Normal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"3\",\"text\":\"Abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165231AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}]";
        JSONArray fields = formUtils.createExpansionPanelValues(new JSONArray(selectedValues));
        Assert.assertNotNull(fields);
        Assert.assertEquals(3, fields.length());
    }
}

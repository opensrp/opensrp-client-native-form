package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;
import android.widget.RadioButton;


import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLooper;

import java.util.List;
import java.util.Set;

public class NativeRadioButtonFactoryTest extends BaseTest {
    private NativeRadioButtonFactory factory;
    private FormUtils formUtils;
    private JsonFormActivity jsonFormActivity;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Mock
    private RadioButton radioButton;

    private Activity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new NativeRadioButtonFactory();
        formUtils = new FormUtils();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
        activity = Mockito.spy(Activity.class);
    }

    @Test
    public void testNativeRadioButtonFactoryInstantiatesViewsCorrectly() throws Exception {
        String nativeRadioButtonString = "{\"key\":\"respiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"3\",\"options\":[{\"key\":\"1\",\"text\":\"Not done\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"2\",\"text\":\"Normal\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"3\",\"text\":\"Abnormal\",\"specify_info\":\"User sub specify...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"extra_info\":\"Here we go\",\"content_form\":\"user_native_sub_form\",\"secondary_value\":[{\"key\":\"yes\",\"type\":\"date_picker\",\"values\":[\"24-09-2020\"]}],\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"value\":\"3\",\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"},\"read_only\":true,\"editable\":true,\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_constraints_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_calculation_rules.yml\"}}}}";
        JSONObject nativeRadioButtonObject = new JSONObject(nativeRadioButtonString);
        Assert.assertNotNull(nativeRadioButtonString);
        Assert.assertNotNull(formUtils);
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);
        Whitebox.setInternalState(factory, "formUtils", formUtilsSpy);

        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, nativeRadioButtonObject, listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);

    }

    @Test
    public void testCreateSpecifyTextWhenTextIsSet() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "test");
        Assert.assertEquals("(test)", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsEmpty() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "");
        Assert.assertEquals("", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsNull() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", (Object) null);
        Assert.assertEquals("", result);
    }

    @Test
    public void testGetSecondaryDateValue() throws Exception {
        JSONArray jsonArray = new JSONArray("[ \"24-09-2020\"]");
        String date = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getSecondaryDateValue", jsonArray);
        Assert.assertEquals("24-09-2020", date);
    }

    @Test
    public void testGetSecondaryDateValueWithNullValues() throws Exception {
        JSONArray jsonArray = null;
        String date = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getSecondaryDateValue", jsonArray);
        Assert.assertEquals("", date);
    }

    @Test
    public void testGetSecondaryDateValueWithEmptyValues() throws Exception {
        String date = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getSecondaryDateValue", new JSONArray());
        Assert.assertEquals("", date);
    }

    @Test
    public void testGetOptionTextWithSecondaryValue() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"user_sub_form\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"User sub forms?\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"yes\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"User sub specify...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"content_form\":\"user_native_sub_form\",\"secondary_value\":[{\"key\":\"yes\",\"type\":\"date_picker\",\"values\":[\"24-09-2020\"]}]},{\"key\":\"no\",\"text\":\"No\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"value\":\"yes\",\"v_required\":{\"value\":true,\"err\":\"Please specify user native form.\"}}");
        JSONObject item = new JSONObject("{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"User sub specify...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"content_form\":\"user_native_sub_form\",\"secondary_value\":[{\"key\":\"yes\",\"type\":\"date_picker\",\"values\":[\"24-09-2020\"]}]}");
        JSONObject valueObject = new JSONObject();
        valueObject.put(JsonFormConstants.VALUE,"yes");
        valueObject.put(JsonFormConstants.TEXT,"");

        jsonObject.put(JsonFormConstants.VALUE, valueObject);
        String optionText = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getOptionTextWithSecondaryValue", item, jsonObject);
        Assert.assertEquals("Yes:24-09-2020", optionText);
    }

    @Test
    public void testGetOptionTextWithSecondaryValueWithNoSecondaryValue() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"key\":\"user_sub_form\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"User sub forms?\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"yes\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"User sub specify...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"content_form\":\"user_native_sub_form\",\"secondary_value\":[{\"key\":\"yes\",\"type\":\"date_picker\",\"values\":[\"24-09-2020\"]}]},{\"key\":\"no\",\"text\":\"No\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"value\":\"yes\",\"v_required\":{\"value\":true,\"err\":\"Please specify user native form.\"}}");
        JSONObject item = new JSONObject("{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"specify_info\":\"User sub specify...\",\"specify_widget\":\"normal_edit_text\",\"specify_info_color\":\"#8C8C8C\",\"secondary_suffix\":\"bpm\",\"content_form\":\"user_native_sub_form\"}");
        String optionText = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getOptionTextWithSecondaryValue", item, jsonObject);
        Assert.assertEquals("Yes", optionText);
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperties = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(4, editableProperties.size());
        Assert.assertEquals("options.text", editableProperties.iterator().next());
    }

    @Test
    public void testSelectedButton() throws Exception {
        String value = "button";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "button");
        Whitebox.setInternalState(factory, "context", activity);
        radioButton = Mockito.mock(RadioButton.class);
        Whitebox.invokeMethod(factory, "checkSelectedRadioButton", listener, radioButton, value, jsonObject);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Thread.sleep(TIMEOUT);
        Mockito.verify(radioButton).setChecked(true);
    }

    @Test
    public void testSelectedTranslatedButton() throws Exception {
        String value = "{\"value\":\"button\",\"text\":\"text\"}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "button");
        Whitebox.setInternalState(factory, "context", activity);
        radioButton = Mockito.mock(RadioButton.class);
        Whitebox.invokeMethod(factory, "checkSelectedRadioButton", listener, radioButton, value, jsonObject);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Thread.sleep(TIMEOUT);
        Mockito.verify(radioButton).setChecked(true);
    }

    @Test
    public void testShowDateDialog() throws Exception {
        View view = Mockito.mock(View.class);
        View radioButtonView = Mockito.mock(RadioButton.class);
        radioButton = Mockito.mock(RadioButton.class);
        String json = "    {\n" +
                "        \"key\": \"Date_Birth\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"birthdate\",\n" +
                "        \"type\": \"date_picker\",\n" +
                "        \"hint\": \"Child's DOB\",\n" +
                "        \"label_info_title\": \"Child's Date of Birth\",\n" +
                "        \"label_info_text\": \"here is some text on this dialog\",\n" +
                "        \"expanded\": false,\n" +
                "        \"duration\": {\n" +
                "          \"label\": \"Age\"\n" +
                "        },\n" +
                "        \"min_date\": \"today-5y\",\n" +
                "        \"max_date\": \"today\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter the date of birth\"\n" +
                "        }\n" +
                "      }";
        Mockito.doReturn(radioButton).when(view).getTag(R.id.native_radio_button);
        Mockito.doReturn("text:12-01-2000").when(radioButton).getText();
        Mockito.doReturn(jsonFormActivity).when(view).getTag((R.id.specify_context));
        Whitebox.setInternalState(factory, "context", jsonFormActivity);
        Mockito.doReturn(new JSONObject(json)).when(radioButton).getTag(R.id.option_json_object);
        Mockito.doReturn(radioButtonView).when(radioButtonView).getTag(R.id.specify_textview);
        FragmentManager fragment = Mockito.spy(FragmentManager.class);
        Whitebox.setInternalState(NativeRadioButtonFactory.class, "TAG", fragment);
        NativeRadioButtonFactory.showDateDialog(view);
        Assert.assertEquals("text:12-01-2000",radioButton.getText());
    }
}

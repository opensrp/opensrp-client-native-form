package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NativeRadioButtonFactoryTest extends BaseTest {
    private NativeRadioButtonFactory factory;
    private FormUtils formUtils;
    @Mock
    private JsonFormActivity context;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private Resources resources;
    @Mock
    private CommonListener listener;
    @Mock
    private LinearLayout rootLayout;
    @Mock
    private ConstraintLayout constraintLayout;
    @Mock
    private ConstraintLayout constraintLayoutTwo;
    @Mock
    private RadioGroup radioGroup;
    @Mock
    private RelativeLayout radioGroupLayout;
    @Mock
    private RelativeLayout radioGroupLayoutTwo;
    @Mock
    private ImageView imageView;
    @Mock
    private ConstraintLayout labelConstraintLayout;
    @Mock
    private RadioButton radioButton;
    @Mock
    private CustomTextView labelText;
    @Mock
    private ImageView editButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new NativeRadioButtonFactory();
        formUtils = new FormUtils();
    }

    @Test
    public void testNativeRadioButtonFactoryInstantiatesViewsCorrectly() throws Exception {
        String nativeRadioButtonString = "{\"key\":\"respiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"3\",\"options\":[{\"key\":\"1\",\"text\":\"Not done\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"2\",\"text\":\"Normal\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"3\",\"text\":\"Abnormal\",\"specify_info\":\"specify...\",\"specify_widget\":\"check_box\",\"specify_info_color\":\"#8C8C8C\",\"content_form\":\"respiratory_exam_sub_form\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}";
        JSONObject nativeRadioButtonObject = new JSONObject(nativeRadioButtonString);
        Assert.assertNotNull(nativeRadioButtonString);

        Assert.assertNotNull(factory);
        NativeRadioButtonFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Assert.assertNotNull(formUtils);
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);
        Whitebox.setInternalState(factorySpy, "formUtils", formUtilsSpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getLinearRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(radioGroup).when(factorySpy).getRadioGroup(ArgumentMatchers.eq(nativeRadioButtonObject), ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString());
        Assert.assertNotNull(radioGroup);

        Mockito.doReturn(radioGroupLayoutTwo).when(factorySpy).getRadioGroupLayout(context);
        Assert.assertNotNull(radioGroupLayoutTwo);

        Mockito.doReturn(imageView).when(radioGroupLayout).findViewById(R.id.info_icon);
        Assert.assertNotNull(imageView);

        Mockito.doReturn(radioButton).when(radioGroupLayout).findViewById(R.id.mainRadioButton);
        Assert.assertNotNull(radioButton);

        Mockito.doReturn(radioGroupLayout).when(factorySpy).getRadioGroupLayout(ArgumentMatchers.eq(nativeRadioButtonObject), ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new JSONObject()));
        Assert.assertNotNull(radioGroupLayout);

        Mockito.doReturn(constraintLayout).when(formUtilsSpy).getRootConstraintLayout(context);
        Assert.assertNotNull(constraintLayout);

        Mockito.doReturn(constraintLayoutTwo).when(formUtilsSpy).getConstraintLayout(ArgumentMatchers.anyString(), ArgumentMatchers.eq(new JSONArray()), ArgumentMatchers.eq(nativeRadioButtonObject), ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Assert.assertNotNull(constraintLayoutTwo);

        Mockito.doNothing().when(formUtilsSpy).showInfoIcon(ArgumentMatchers.anyString(), ArgumentMatchers.eq(new JSONObject()), ArgumentMatchers.eq(listener), ArgumentMatchers.eq(new HashMap<String, String>()), ArgumentMatchers.eq(imageView), ArgumentMatchers.eq(new JSONArray()));

        Mockito.doReturn(labelText).when(labelConstraintLayout).findViewById(R.id.label_text);
        Assert.assertNotNull(labelText);

        Mockito.doReturn(editButton).when(labelConstraintLayout).findViewById(R.id.label_edit_button);
        Assert.assertNotNull(editButton);

        Mockito.doReturn(labelConstraintLayout).when(formUtilsSpy).createLabelLinearLayout(ArgumentMatchers.anyString(), ArgumentMatchers.eq(new JSONArray()), ArgumentMatchers.eq(new JSONObject()), ArgumentMatchers.eq(context), ArgumentMatchers.eq(listener));
        Assert.assertNotNull(labelConstraintLayout);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, nativeRadioButtonObject, listener);
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
        String date = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "getSecondaryDateValue", null);
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
}

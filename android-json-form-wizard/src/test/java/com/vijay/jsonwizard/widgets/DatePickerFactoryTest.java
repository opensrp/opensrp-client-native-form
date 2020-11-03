package com.vijay.jsonwizard.widgets;

import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.shadow.ShadowDialogFragment;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class DatePickerFactoryTest extends BaseTest {
    private DatePickerFactory factory;
    private FormUtils formUtils;
    private JsonFormActivity jsonFormActivity;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Captor
    private ArgumentCaptor<View> viewArgumentCaptor;

    @Captor
    private ArgumentCaptor<DatePickerDialog> datePickerDialogArgumentCaptor;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new DatePickerFactory();
        formUtils = new FormUtils();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testDatePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        String datePicker = "{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"date_picker\",\"hint\":\"Date first seen *\",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"12-05-2020\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\",\"duration\":{\"label\":\"AGE\"}}";
        Assert.assertNotNull(formUtils);
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);
        Whitebox.setInternalState(factory, "formUtils", formUtilsSpy);

        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(datePicker), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        View rootLayout = viewList.get(0);
        Assert.assertEquals(3, ((RelativeLayout) rootLayout).getChildCount());

        MaterialEditText materialEditText = (MaterialEditText) ((RelativeLayout) rootLayout).getChildAt(0);

        Assert.assertEquals("First_Health_Facility_Contact", materialEditText.getTag(R.id.key));
        Assert.assertEquals("", materialEditText.getTag(R.id.openmrs_entity_parent));
        Assert.assertEquals("concept", materialEditText.getTag(R.id.openmrs_entity));
        Assert.assertEquals("163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", materialEditText.getTag(R.id.openmrs_entity_id));
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        DatePickerFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("duration.label", editableProperties.iterator().next());
    }


    @Config(shadows = {ShadowDialogFragment.class})
    @Test
    public void testShowDatePickerDialog() throws Exception {
        JsonFormActivity jsonFormActivitySpy = Mockito.spy(jsonFormActivity);
        DatePickerFactory datePickerFactory = Mockito.spy(factory);
        String datePicker = "{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"date_picker\",\"hint\":\"Date first seen *\",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"12-05-2020\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\",\"duration\":{\"label\":\"AGE\"}}";
        List<View> viewList = datePickerFactory.getViewsFromJson("RandomStepName", jsonFormActivitySpy, formFragment, new JSONObject(datePicker), listener);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        Assert.assertEquals(1, viewList.size());
        Mockito.verify(jsonFormActivitySpy).addFormDataView(viewArgumentCaptor.capture());
        viewArgumentCaptor.getValue().performClick();
        Mockito.verify(datePickerFactory).showDatePickerDialog(Mockito.eq(jsonFormActivitySpy), datePickerDialogArgumentCaptor.capture(), ArgumentMatchers.any(MaterialEditText.class));
        DatePickerDialog datePickerDialog = datePickerDialogArgumentCaptor.getValue();
        Assert.assertEquals("12-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(Whitebox.getInternalState(datePickerDialog, "date")));

    }

}

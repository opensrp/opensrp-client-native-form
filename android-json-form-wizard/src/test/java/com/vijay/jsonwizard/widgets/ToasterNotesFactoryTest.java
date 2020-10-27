package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

public class ToasterNotesFactoryTest extends BaseTest {
    private ToasterNotesFactory factory;
    private JsonFormActivity jsonFormActivity;

    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ToasterNotesFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testInfoToasterInstantiatesViewsCorrectly() throws Exception {
        String labelString = "{\"key\":\"ultrasound_info_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Test toaster notes\",\"toaster_type\":\"info\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_calculation_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_constraints_rules.yml\"}}}}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(labelString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testWarningToasterInstantiatesViewsCorrectly() throws Exception {

        String labelString = "{\"key\":\"ultrasound_info_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Test toaster notes\",\"toaster_type\":\"warning\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_calculation_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_constraints_rules.yml\"}}}}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(labelString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testProblemToasterInstantiatesViewsCorrectly() throws Exception {

        String labelString = "{\"key\":\"ultrasound_info_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Test toaster notes\",\"toaster_type\":\"problem\",\"toaster_info_text\":\"Toaster text\",\"toaster_info_title\":\"Toaster titles\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_calculation_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_constraints_rules.yml\"}}}}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(labelString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testPositiveToasterInstantiatesViewsCorrectly() throws Exception {
        String labelString = "{\"key\":\"ultrasound_info_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Test toaster notes\",\"toaster_type\":\"positive\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_calculation_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ultrasound_sub_form_constraints_rules.yml\"}}}}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(labelString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperties = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(2, editableProperties.size());
        Assert.assertEquals("toaster_info_text", editableProperties.iterator().next());
    }
}

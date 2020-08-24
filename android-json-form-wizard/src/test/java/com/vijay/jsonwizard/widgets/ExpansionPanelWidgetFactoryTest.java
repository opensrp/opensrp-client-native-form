package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

public class ExpansionPanelWidgetFactoryTest extends BaseTest {
    private ExpansionPanelFactory factory;
    private JsonFormActivity jsonFormActivity;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ExpansionPanelFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testExpansionPanelWidgetFactoryInstantiatesViewsCorrectly() throws Exception {
        String expansionPanelString = "{\"key\":\"accordion_syphilis\",\"openmrs_entity_parent\":\"667899AAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"12345AAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text\":\"Syphilis test\",\"accordion_info_text\":\"A syphilis test is recommended for all pregnant women at the first contact and again at the first contact of 3rd trimester (28 weeks). Women who are already confirmed positive for syphilis do not need to be tested.\",\"accordion_info_title\":\"Syphilis test\",\"type\":\"expansion_panel\",\"display_bottom_section\":true,\"content_form\":\"tests_syphilis_sub_form\",\"container\":\"anc_test\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_constraints_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\",\"values\":[\"rapid_breathing:Rapid breathing:true\",\"slow_breathing:Slow breathing:true\",\"other:Other (specify):true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"TACHYPNEA\",\"openmrs_entity_id\":\"125061\"},{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"Place_Birth\",\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"respiratory_exam_radio_button\",\"type\":\"extended_radio_button\",\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"normal_edit_text\",\"values\":[\"Here lots of stuff\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}}]}";
        JSONObject expansionPanelObject = new JSONObject(expansionPanelString);
        Assert.assertNotNull(expansionPanelString);

        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, expansionPanelObject, listener, false);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        LinearLayout linearLayout = (LinearLayout) viewList.get(0);
        Assert.assertNotNull(linearLayout);
        RelativeLayout expansionHeaderLayout = (RelativeLayout) linearLayout.getChildAt(0);
        Assert.assertNotNull(expansionHeaderLayout);
        RelativeLayout expansionLayout = (RelativeLayout) expansionHeaderLayout.getChildAt(0);
        Assert.assertNotNull(expansionLayout);
        CustomTextView topBarView = (CustomTextView) expansionLayout.getChildAt(1);
        Assert.assertNotNull(topBarView);

        Assert.assertEquals("accordion_syphilis", topBarView.getTag(R.id.key));
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperties = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(2, editableProperties.size());
        Assert.assertEquals("accordion_info_text", editableProperties.iterator().next());
    }
}

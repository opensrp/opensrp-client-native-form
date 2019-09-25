package com.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.views.CustomTextView;
import com.vijay.jsonwizard.widgets.ExpansionWidgetFactory;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.List;

public class ExpansionPanelWidgetFactoryTest extends BaseTest {
    private ExpansionWidgetFactory factory;
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
    private RelativeLayout expansionHeader;

    @Mock
    private RelativeLayout expansion_header_layout;

    @Mock
    private ImageView statusImage;

    @Mock
    private ImageView infoIcon;

    @Mock
    private CustomTextView headerText;

    @Mock
    private LinearLayout contentLayout;

    @Mock
    private LinearLayout contentView;

    @Mock
    private LinearLayout bottomButtonsLayout;

    @Mock
    private Button recordButton;

    @Mock
    private Button undoButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ExpansionWidgetFactory();
    }

    @Test
    @PrepareForTest ({LayoutInflater.class})
    public void testExpansionPanelWidgetFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        ExpansionWidgetFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        Mockito.doReturn(expansionHeader).when(rootLayout).findViewById(R.id.expansionHeader);
        Assert.assertNotNull(expansionHeader);

        Mockito.doReturn(expansion_header_layout).when(expansionHeader).findViewById(R.id.expansion_header_layout);
        Assert.assertNotNull(expansion_header_layout);

        Mockito.doReturn(statusImage).when(expansion_header_layout).findViewById(R.id.statusImageView);
        Assert.assertNotNull(statusImage);

        Mockito.doReturn(infoIcon).when(expansionHeader).findViewById(R.id.accordion_info_icon);
        Assert.assertNotNull(infoIcon);

        Mockito.doReturn(headerText).when(expansion_header_layout).findViewById(R.id.topBarTextView);
        Assert.assertNotNull(headerText);

        Mockito.doReturn(contentLayout).when(rootLayout).findViewById(R.id.contentLayout);
        Assert.assertNotNull(contentLayout);

        Mockito.doReturn(contentView).when(contentLayout).findViewById(R.id.contentView);
        Assert.assertNotNull(contentView);

        Mockito.doReturn(bottomButtonsLayout).when(rootLayout).findViewById(R.id.accordion_bottom_navigation);
        Assert.assertNotNull(bottomButtonsLayout);

        Mockito.doReturn(recordButton).when(bottomButtonsLayout).findViewById(R.id.ok_button);
        Assert.assertNotNull(recordButton);

        Mockito.doReturn(undoButton).when(bottomButtonsLayout).findViewById(R.id.undo_button);
        Assert.assertNotNull(undoButton);

        String expansionPanelString = "{\"key\":\"accordion_syphilis\",\"openmrs_entity_parent\":\"667899AAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"12345AAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"text\":\"Syphilis test\",\"accordion_info_text\":\"A syphilis test is recommended for all pregnant women at the first contact and again at the first contact of 3rd trimester (28 weeks). Women who are already confirmed positive for syphilis do not need to be tested.\",\"accordion_info_title\":\"Syphilis test\",\"type\":\"expansion_panel\",\"display_bottom_section\":true,\"content_form\":\"tests_syphilis_sub_form\",\"container\":\"anc_test\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_constraints_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\",\"values\":[\"rapid_breathing:Rapid breathing:true\",\"slow_breathing:Slow breathing:true\",\"other:Other (specify):true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"TACHYPNEA\",\"openmrs_entity_id\":\"125061\"},{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"Place_Birth\",\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"respiratory_exam_radio_button\",\"type\":\"extended_radio_button\",\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"normal_edit_text\",\"values\":[\"Here lots of stuff\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}}]}";
        JSONObject expansionPanelObject = new JSONObject(expansionPanelString);
        Assert.assertNotNull(expansionPanelString);


        List<View> viewList = factorySpy
                .getViewsFromJson("RandomStepName", context, formFragment, expansionPanelObject, listener,
                        false);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);

    }
}

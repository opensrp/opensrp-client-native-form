package com.vijay.jsonwizard.widgets;

import android.text.Editable;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

public class TimePickerFactoryTest extends BaseTest {
    private TimePickerFactory factory;
    private JsonFormActivity jsonFormActivity;

    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Mock
    private View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new TimePickerFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testTimePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        String timePickerWidget = "{\"key\":\"user_time\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"time_picker\",\"hint\":\"Birth Time\",\"expanded\":false,\"duration\":{\"label\":\"Birth Time\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the time of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"22:03\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\"}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(timePickerWidget), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        View rootLayout = viewList.get(0);
        Assert.assertEquals(2, ((RelativeLayout) rootLayout).getChildCount());

        MaterialEditText materialEditText = (MaterialEditText) ((RelativeLayout) rootLayout).getChildAt(0);

        Assert.assertEquals("user_time", materialEditText.getTag(R.id.key));
        Assert.assertEquals("", materialEditText.getTag(R.id.openmrs_entity_parent));
        Assert.assertEquals("", materialEditText.getTag(R.id.openmrs_entity));
        Assert.assertEquals("", materialEditText.getTag(R.id.openmrs_entity_id));
    }

    @Test
    public void testUpdateTimeText() throws Exception {
        String timePickerWidget = "{\"key\":\"user_time\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"time_picker\",\"hint\":\"Birth Time\",\"expanded\":false,\"duration\":{\"label\":\"Birth Time\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the time of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"22:03\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\"}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(timePickerWidget), listener);
        Assert.assertNotNull(viewList);

        View rootLayout = viewList.get(0);
        Assert.assertEquals(2, ((RelativeLayout) rootLayout).getChildCount());

        MaterialEditText materialEditText = (MaterialEditText) ((RelativeLayout) rootLayout).getChildAt(0);
        Mockito.doReturn(jsonFormActivity).when(formFragment).getContext();
        Editable editable = new Editable.Factory().newEditable("23:03");
        Whitebox.invokeMethod(factory, "updateTimeText", materialEditText, 22, 3);
        Assert.assertEquals("23:03", editable.toString());

    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        TimePickerFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("duration.label", editableProperties.iterator().next());
    }
}

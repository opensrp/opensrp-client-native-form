package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.text.Editable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimePickerFactoryTest extends BaseTest {
    private TimePickerFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout rootLayout;

    @Mock
    private MaterialEditText editText;

    @Mock
    private TextView duration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new TimePickerFactory();
    }

    @Test
    public void testTimePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        TimePickerFactory factorySpy = Mockito.spy(factory);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Mockito.doReturn(editText).when(rootLayout).findViewById(R.id.edit_text);
        Mockito.doReturn(duration).when(rootLayout).findViewById(R.id.duration);
        Mockito.doReturn(Locale.ENGLISH).when(factorySpy).getCurrentLocale(context);
        Mockito.doReturn("hrs").when(duration).getTag(ArgumentMatchers.anyInt());

        String timePickerWidget = "{\"key\":\"user_time\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"time_picker\",\"hint\":\"Birth Time\",\"expanded\":false,\"duration\":{\"label\":\"Birth Time\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the time of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"22:03\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\"}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(timePickerWidget), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testUpdateTimeText() throws Exception {
        Assert.assertNotNull(factory);
        TimePickerFactory factorySpy = Mockito.spy(factory);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Mockito.doReturn(editText).when(rootLayout).findViewById(R.id.edit_text);
        Editable editable = new Editable.Factory().newEditable("23:03");
        Whitebox.invokeMethod(factorySpy, "updateTimeText", editText, 22, 3);
        Mockito.doReturn(editable).when(editText).getText();
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

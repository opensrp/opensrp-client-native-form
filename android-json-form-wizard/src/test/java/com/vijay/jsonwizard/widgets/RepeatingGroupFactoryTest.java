package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

public class RepeatingGroupFactoryTest extends FactoryTest {

    private RepeatingGroupFactory factory;
    private JSONObject step;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        factory = new RepeatingGroupFactory();
    }

    @Test
    public void testRepeatingGroupFactoryInstantiatesViewsCorrectly() throws Exception {
        List<View> viewList = invokeGetViewsFromJson();
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testRepeatingGroupCountIsSaved() throws Exception {
        View rootLayout = invokeGetViewsFromJson().get(0);
        ((MaterialEditText) rootLayout.findViewById(R.id.reference_edit_text)).setText("2");

        JSONObject repeatingGroupCountObj = step.getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
        Assert.assertEquals("2", repeatingGroupCountObj.getString(JsonFormConstants.VALUE));
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        RepeatingGroupFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }

    private List<View> invokeGetViewsFromJson() throws Exception {
        step = new JSONObject();
        JSONArray fields = new JSONArray();
        step.put(JsonFormConstants.FIELDS, fields);
        Mockito.doReturn(step).when(jsonFormActivity).getStep(ArgumentMatchers.anyString());

        JSONObject repeatingGroupWidget =  new JSONObject();
        repeatingGroupWidget.put(JsonFormConstants.KEY, "key");
        repeatingGroupWidget.put(JsonFormConstants.VALUE, new JSONArray());
        repeatingGroupWidget.put(RepeatingGroupFactory.REFERENCE_EDIT_TEXT_HINT, "text");
        return factory.getViewsFromJson("step1", jsonFormActivity, Mockito.mock(JsonFormFragment.class),
               repeatingGroupWidget, Mockito.mock(CommonListener.class));
    }
}

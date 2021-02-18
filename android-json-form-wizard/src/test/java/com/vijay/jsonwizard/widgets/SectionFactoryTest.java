package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

public class SectionFactoryTest extends FactoryTest {

    private SectionFactory factory;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private CommonListener commonListener;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        factory = spy(new SectionFactory());
    }

    @Test
    public void testFactoryShouldInitializedCorrectly() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "test_key");
        jsonObject.put(JsonFormConstants.NAME, "test_name");

        List<View> viewList = factory.getViewsFromJson(JsonFormConstants.STEP1, RuntimeEnvironment.application, jsonFormFragment, jsonObject, commonListener);
        assertNotNull(viewList);
        assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperty = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(new HashSet<>(), editableProperty);
    }
}

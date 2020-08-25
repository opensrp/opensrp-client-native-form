package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by Vincent Karuri on 25/08/2020
 */
public class ButtonFactoryTest extends FactoryTest {

    @Mock
    private JsonFormFragment jsonFormFragment;
    @Mock
    private CommonListener commonListener;
    @Mock
    private JSONObject jsonObject;

    private ButtonFactory buttonFactory;

    @Before
    public void setUp() {
        super.setUp();
        buttonFactory = new ButtonFactory();
    }

    @Test
    public void testGetViewsFromJsonShouldCorrectlyInitializeWidget() throws Exception {
        buttonFactory.getViewsFromJson("step1", jsonFormActivity, jsonFormFragment, jsonObject, commonListener);
    }
}
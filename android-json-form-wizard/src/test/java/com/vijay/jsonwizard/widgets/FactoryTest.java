package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

/**
 * Created by Vincent Karuri on 25/08/2020
 */
public class FactoryTest extends BaseTest {

    protected JsonFormActivity jsonFormActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @After
    public void tearDown() {
        jsonFormActivity.finish();
    }
}

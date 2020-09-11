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

public class NumberSelectorFactoryTest extends BaseTest {
    private NumberSelectorFactory factory;
    private JsonFormActivity jsonFormActivity;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new NumberSelectorFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testNumberSelectorFactoryInstantiatesViewsCorrectly() throws Exception {
        String numberSelectorFactoryString = "{\"key\":\"user_form\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"numbers_selector\",\"number_of_selectors\":\"5\",\"start_number\":\"1\",\"max_value\":\"15\",\"text_size\":\"16px\",\"text_color\":\"#000000\",\"selected_text_color\":\"#ffffff\",\"v_required\":{\"value\":true},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}}}";
        JSONObject numberSelectorObject = new JSONObject(numberSelectorFactoryString);
        Assert.assertNotNull(numberSelectorFactoryString);
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, numberSelectorObject, listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetTextWhenStartNumberIsOne() {
        String test = "3";
        int item = 2;
        int startSelectionNumber = 1;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = factory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);

    }

    @Test
    public void testGetTextWhenStartNumberIsZero() {
        String test = "2";
        int item = 2;
        int startSelectionNumber = 0;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = factory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsMoreThanOne() {
        String test = "12";
        int item = 2;
        int startSelectionNumber = 10;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = factory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsLessThanZero() {
        String test = "-2";
        int item = 2;
        int startSelectionNumber = -4;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = factory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsEqualTONumberOfSelector() {
        String test = "5+";
        int item = 4;
        int startSelectionNumber = 1;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = factory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperties = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }
}

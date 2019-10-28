package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class MultiSelectListFactoryTest {

    private MultiSelectListFactory multiSelectListFactory;

    @Mock
    private JSONObject jsonObject;

    @Before
    public void setUp() {
        multiSelectListFactory = new MultiSelectListFactory();
    }

    @Test
    public void prepareSelectedDataShouldReturnEmptyArrayList() {
        multiSelectListFactory.jsonObject = jsonObject;
        Assert.assertEquals(new ArrayList<>(), multiSelectListFactory.prepareSelectedData());
    }

    @Test
    public void prepareSelectedDataShouldReturnFilledArrayList() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUE, "[{\"key\":\"Bacterial Meningitis\",\"property\":{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}}]");
        multiSelectListFactory.jsonObject = jsonObject;
        Assert.assertEquals(1, jsonObject.length());
    }
}
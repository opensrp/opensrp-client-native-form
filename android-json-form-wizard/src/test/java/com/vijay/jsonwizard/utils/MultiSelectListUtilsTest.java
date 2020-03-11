package com.vijay.jsonwizard.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MultiSelectListUtilsTest {

    @Test
    public void testToJsonShouldReturnEmptyJsonArray() {
        List<MultiSelectItem> multiSelectItems = new ArrayList<>();
        Assert.assertTrue((MultiSelectListUtils.toJson(multiSelectItems).length() == 0));
    }

    @Test
    public void testToJsonShouldReturnANonEmptyJsonArray() {
        List<MultiSelectItem> multiSelectItems = new ArrayList<>();
        MultiSelectItem multiSelectItem = new MultiSelectItem();
        multiSelectItem.setKey("key");
        multiSelectItem.setText("value");
        multiSelectItem.setOpenmrsEntityId("1233AAAAA");
        multiSelectItem.setOpenmrsEntity("concept");
        multiSelectItem.setOpenmrsEntityParent("2331AAA");
        multiSelectItem.setValue(new JSONObject().toString());
        multiSelectItems.add(multiSelectItem);
        Assert.assertTrue((MultiSelectListUtils.toJson(multiSelectItems).length() > 0));
        JSONObject jsonObjectResult = MultiSelectListUtils.toJson(multiSelectItems).optJSONObject(0);
        Assert.assertEquals("key", jsonObjectResult.optString(JsonFormConstants.KEY));
        Assert.assertEquals("value", jsonObjectResult.optString(JsonFormConstants.MultiSelectUtils.TEXT));
        Assert.assertEquals("concept", jsonObjectResult.optString(JsonFormConstants.OPENMRS_ENTITY));
        Assert.assertEquals("1233AAAAA", jsonObjectResult.optString(JsonFormConstants.OPENMRS_ENTITY_ID));
        Assert.assertEquals("2331AAA", jsonObjectResult.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT));
    }
}
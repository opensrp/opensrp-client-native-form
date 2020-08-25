package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
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

    private ButtonFactory buttonFactory;

    @Before
    public void setUp() {
        super.setUp();
        buttonFactory = new ButtonFactory();
    }

    @Test
    public void testGetViewsFromJsonShouldCorrectlyInitializeWidget() throws Exception {
        JSONObject jsonObject = getJsonObject();
        Button button = (Button) buttonFactory.getViewsFromJson("step1", jsonFormActivity, jsonFormFragment,
                jsonObject, commonListener).get(0);
        Assert.assertEquals(button.getTag(R.id.key), getJsonObject().get(JsonFormConstants.KEY));
        Assert.assertEquals(button.getTag(R.id.openmrs_entity_parent), jsonObject.get(JsonFormConstants.OPENMRS_ENTITY_PARENT));
        Assert.assertEquals(button.getTag(R.id.openmrs_entity), jsonObject.get(JsonFormConstants.OPENMRS_ENTITY));
        Assert.assertEquals(button.getTag(R.id.openmrs_entity_id), jsonObject.get(JsonFormConstants.OPENMRS_ENTITY_ID));
        Assert.assertEquals(button.getTag(R.id.type), jsonObject.getString(JsonFormConstants.TYPE));
        Assert.assertEquals(button.getTag(R.id.address), "step1" + ":" + jsonObject.getString(JsonFormConstants.KEY));
        Assert.assertEquals(button.getTag(R.id.extraPopup), false);
        Assert.assertEquals(button.getTag(R.id.raw_value), jsonObject.optString(JsonFormConstants.VALUE));
        Assert.assertEquals(button.getTag(R.id.relevance), jsonObject.optString(JsonFormConstants.RELEVANCE));
        Assert.assertTrue(button.isEnabled());
        Assert.assertTrue(button.isFocusable());
        Assert.assertEquals(button.getText(), jsonObject.get(JsonFormConstants.HINT));
    }

    private JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "entity_parent");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, "entity");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "entity_id");
        jsonObject.put(JsonFormConstants.RELEVANCE, "relevance");
        jsonObject.put(JsonFormConstants.HINT, "hint");
        jsonObject.put(JsonFormConstants.KEY, "key");
        jsonObject.put(JsonFormConstants.TYPE, "type");
        jsonObject.put(JsonFormConstants.VALUE, "value");
        jsonObject.put(JsonFormConstants.READ_ONLY, false);
        return jsonObject;
    }
}
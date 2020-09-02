package com.vijay.jsonwizard.widgets;

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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;

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
        Button button = (Button) buttonFactory.getViewsFromJson(STEP1, jsonFormActivity, jsonFormFragment,
                jsonObject, commonListener).get(0);

        Mockito.doReturn(jsonObject)
                .when(jsonFormActivity)
                .getObjectUsingAddress(
                        Mockito.eq(button.getTag(R.id.address).toString().split(":"))
                        , Mockito.eq(false));

        Assert.assertEquals(getJsonObject().get(JsonFormConstants.KEY), button.getTag(R.id.key));
        Assert.assertEquals(jsonObject.get(JsonFormConstants.OPENMRS_ENTITY_PARENT), button.getTag(R.id.openmrs_entity_parent));
        Assert.assertEquals(jsonObject.get(JsonFormConstants.OPENMRS_ENTITY), button.getTag(R.id.openmrs_entity));
        Assert.assertEquals(jsonObject.get(JsonFormConstants.OPENMRS_ENTITY_ID), button.getTag(R.id.openmrs_entity_id));
        Assert.assertEquals(jsonObject.getString(JsonFormConstants.TYPE), button.getTag(R.id.type));
        Assert.assertEquals(STEP1 + ":" + jsonObject.getString(JsonFormConstants.KEY), button.getTag(R.id.address));
        Assert.assertEquals(false, button.getTag(R.id.extraPopup));
        Assert.assertEquals(JsonFormConstants.VALUE, button.getTag(R.id.raw_value));
        Assert.assertEquals(jsonObject.optString(JsonFormConstants.RELEVANCE), button.getTag(R.id.relevance));
        Assert.assertTrue(button.isEnabled());
        Assert.assertTrue(button.isFocusable());
        Assert.assertEquals(button.getText(), jsonObject.get(JsonFormConstants.HINT));

        // default action
        Assert.assertFalse(jsonObject.getJSONObject(JsonFormConstants.ACTION).getBoolean(JsonFormConstants.RESULT));
        Assert.assertEquals(jsonObject.optString(JsonFormConstants.VALUE), Boolean.FALSE.toString());

        // action BEHAVIOUR_FINISH_FORM
        jsonObject.getJSONObject(JsonFormConstants.ACTION).put(JsonFormConstants.BEHAVIOUR, JsonFormConstants.BEHAVIOUR_FINISH_FORM);
        button = (Button) buttonFactory.getViewsFromJson(STEP1, jsonFormActivity, jsonFormFragment,
                jsonObject, commonListener).get(0);
        button.performClick();
        Mockito.verify(jsonFormFragment).save(ArgumentMatchers.eq(false));
        Assert.assertEquals(Boolean.TRUE.toString(), button.getTag(R.id.raw_value));

        // action BEHAVIOUR_NEXT_STEP
        jsonObject.getJSONObject(JsonFormConstants.ACTION).put(JsonFormConstants.BEHAVIOUR, JsonFormConstants.BEHAVIOUR_NEXT_STEP);
        button = (Button) buttonFactory.getViewsFromJson(STEP1, jsonFormActivity, jsonFormFragment,
                jsonObject, commonListener).get(0);
        button.performClick();
        Mockito.verify(jsonFormFragment).next();
    }

    private JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "entity_parent");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, "entity");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "entity_id");
        jsonObject.put(JsonFormConstants.RELEVANCE, JsonFormConstants.RELEVANCE);
        jsonObject.put(JsonFormConstants.HINT, JsonFormConstants.HINT);
        jsonObject.put(JsonFormConstants.KEY, JsonFormConstants.KEY);
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TYPE);
        jsonObject.put(JsonFormConstants.VALUE, JsonFormConstants.VALUE);
        jsonObject.put(JsonFormConstants.READ_ONLY, false);

        JSONObject action = new JSONObject();
        jsonObject.put(JsonFormConstants.ACTION, action);
        return jsonObject;
    }
}
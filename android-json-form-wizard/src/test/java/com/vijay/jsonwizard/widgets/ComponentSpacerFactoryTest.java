package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class ComponentSpacerFactoryTest extends FactoryTest {

    private ComponentSpacerFactory componentSpacerFactory;

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Before
    public void setUp() {
        super.setUp();
        componentSpacerFactory = spy(new ComponentSpacerFactory());
    }

    @Test
    public void testFactoryShouldInitializeCorrectly() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "testKey");
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.SPACER);
        jsonObject.put(JsonFormConstants.SPACER_HEIGHT, "1dp");

        List<View> viewList = componentSpacerFactory.getViewsFromJson(JsonFormConstants.STEP1,
                RuntimeEnvironment.application, jsonFormFragment, jsonObject, commonListener);

        assertNotNull(viewList);

        assertEquals(1, viewList.size());

        assertTrue(viewList.get(0) instanceof LinearLayout);

        LinearLayout linearLayout = (LinearLayout) viewList.get(0);

        assertEquals(jsonObject.getString(JsonFormConstants.KEY), linearLayout.getTag(R.id.key));

        assertEquals(jsonObject.getString(JsonFormConstants.TYPE), linearLayout.getTag(R.id.type));
    }
}
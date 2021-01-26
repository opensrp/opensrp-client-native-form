package com.vijay.jsonwizard.widgets;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class HorizontalLineFactoryTest extends FactoryTest {

    private HorizontalLineFactory horizontalLineFactory;

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Before
    public void setUp() {
        super.setUp();
        horizontalLineFactory = spy(new HorizontalLineFactory());
    }

    @Test
    public void testFactoryShouldInitializeCorrectly() throws Exception {
        String fieldJsonString = "{\"key\":\"divider1\",\"type\":\"h_line\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"bg_color\":\"#000000\",\"height\":\"2dp\",\"width\":\"20dp\",\"right_margin\":\"4dp\",\"left_margin\":\"5dp\"}";
        JSONObject jsonObject = new JSONObject(fieldJsonString);
        List<View> views = horizontalLineFactory.getViewsFromJson(JsonFormConstants.STEP1,
                jsonFormActivity, jsonFormFragment, jsonObject, commonListener, false);

        assertNotNull(views);

        assertEquals(1, views.size());

        View view = views.get(0);

        assertEquals(jsonObject.getString(JsonFormConstants.KEY), view.getTag(R.id.key));

        assertFalse((Boolean) view.getTag(R.id.extraPopup));
        assertTrue(view.getBackground() instanceof ColorDrawable);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        assertEquals(FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("height"), jsonFormActivity), layoutParams.height);
        assertEquals(FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("width"), jsonFormActivity), layoutParams.width);
        assertEquals(FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("right_margin"), jsonFormActivity), layoutParams.rightMargin);
        assertEquals(FormUtils.getValueFromSpOrDpOrPx(jsonObject.optString("left_margin"), jsonFormActivity), layoutParams.leftMargin);
    }
}
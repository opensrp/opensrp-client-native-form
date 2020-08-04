package com.vijay.jsonwizard.widgets;

import android.content.Intent;
import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.vijay.jsonwizard.constants.JsonFormConstants.RDT_CAPTURE_CODE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BasicRDTCaptureFactoryTest extends BaseTest {

    private BasicRDTCaptureFactory basicRDTCaptureFactory;
    private JsonFormActivity jsonFormActivity;
    private Intent intent;
    private JSONObject mJSONObject;

    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        basicRDTCaptureFactory = new BasicRDTCaptureFactory();
        mockJsonFormActivityIntent();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, intent).create().get();
    }

    @Test
    public void testRDTCaptureFactoryShouldCorrectlyInitializeViews() throws Exception {
        JSONObject rdtCapture = new JSONObject();
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "openmrs_entity_parent");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY, "openmrs_entity");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_ID, "openmrs_entity_id");
        rdtCapture.put(JsonFormConstants.KEY, "key");

        List<View> viewList = basicRDTCaptureFactory.getViewsFromJson("step1",
                jsonFormActivity, formFragment, rdtCapture, listener, false);
        assertNotNull(viewList);
        assertEquals(1, viewList.size());

        WidgetArgs widgetArgs = ReflectionHelpers.getField(basicRDTCaptureFactory, "widgetArgs");
        assertEquals(formFragment, widgetArgs.getFormFragment());
        assertEquals(listener, widgetArgs.getListener());
        assertFalse(widgetArgs.isPopup());
        assertEquals(rdtCapture, widgetArgs.getJsonObject());
        assertEquals("step1", widgetArgs.getStepName());
        assertEquals(jsonFormActivity, widgetArgs.getContext());

        HashMap<Integer, OnActivityResultListener> onActivityResultListeners =
                ReflectionHelpers.getField(widgetArgs.getContext(), "onActivityResultListeners");
        assertEquals(basicRDTCaptureFactory, onActivityResultListeners.get(RDT_CAPTURE_CODE));

        View rootLayout = viewList.get(0);
        assertEquals("key", rootLayout.getTag(R.id.key));
        assertEquals("openmrs_entity_parent", rootLayout.getTag(R.id.openmrs_entity_parent));
        assertEquals("openmrs_entity", rootLayout.getTag(R.id.openmrs_entity));
        assertEquals("openmrs_entity_id", rootLayout.getTag(R.id.openmrs_entity_id));
    }

    @Test
    public void testGetCustomTranslatableWidgetFieldsShouldReturnNonNullSet() {
        Set<String> translatableFields = basicRDTCaptureFactory.getCustomTranslatableWidgetFields();
        assertNotNull(translatableFields);
    }

    private void mockJsonFormActivityIntent() throws JSONException {
        mJSONObject = new JSONObject();
        mJSONObject.put(STEP1, new JSONObject());
        mJSONObject.put(JsonFormConstants.ENCOUNTER_TYPE, "encounter_type");
        intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, mJSONObject.toString());
    }
}

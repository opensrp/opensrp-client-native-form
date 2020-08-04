package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

public class BasicRDTCaptureFactoryTest extends BaseTest {

    private BasicRDTCaptureFactory basicRDTCaptureFactory;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Mock
    private View rootLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        basicRDTCaptureFactory = new BasicRDTCaptureFactory();
    }

    @Test
    public void testRDTCaptureFactoryShouldCorrectlyInitializeViews() throws Exception {
        JSONObject rdtCapture = new JSONObject();
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "openmrs_entity_parent");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY, "openmrs_entity");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_ID, "openmrs_entity_id");
        rdtCapture.put(JsonFormConstants.KEY, "key");

        List<View> viewList = basicRDTCaptureFactory.getViewsFromJson("RandomStepName",
                RuntimeEnvironment.application, formFragment, rdtCapture, listener);
        assertNotNull(viewList);
        assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFieldsShouldReturnNonNullSet() {
        Set<String> translatableFields = basicRDTCaptureFactory.getCustomTranslatableWidgetFields();
        assertNotNull(translatableFields);
    }
}

package com.vijay.jsonwizard.widgets;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.shadow.ShadowContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.washington.cs.ubicomplab.rdt_reader.activity.RDTCaptureActivity;

import static com.vijay.jsonwizard.constants.JsonFormConstants.RDT_CAPTURE_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@Config(shadows = {ShadowContextCompat.class})
public class BasicRDTCaptureFactoryTest extends FactoryTest {

    private BasicRDTCaptureFactory basicRDTCaptureFactory;

    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() {
        super.setUp();
        basicRDTCaptureFactory = new BasicRDTCaptureFactory();;
    }

    @Test
    public void testRDTCaptureFactoryShouldCorrectlyInitializeViews() throws Exception {
        JSONObject rdtCapture = new JSONObject();
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "openmrs_entity_parent");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY, "openmrs_entity");
        rdtCapture.put(JsonFormConstants.OPENMRS_ENTITY_ID, "openmrs_entity_id");
        rdtCapture.put(JsonFormConstants.KEY, "key");

        ShadowContextCompat.setPermissionStatus(PackageManager.PERMISSION_GRANTED);

        List<View> viewList = basicRDTCaptureFactory.getViewsFromJson("step1",
                jsonFormActivity, formFragment, rdtCapture, listener, false);

        ShadowContextCompat.setPermissionStatus(1);

        // verify root layout was created
        assertNotNull(viewList);
        assertEquals(1, viewList.size());

        // verify widget args were populated
        WidgetArgs widgetArgs = ReflectionHelpers.getField(basicRDTCaptureFactory, "widgetArgs");
        assertEquals(formFragment, widgetArgs.getFormFragment());
        assertEquals(listener, widgetArgs.getListener());
        assertFalse(widgetArgs.isPopup());
        assertEquals(rdtCapture, widgetArgs.getJsonObject());
        assertEquals("step1", widgetArgs.getStepName());
        assertEquals(jsonFormActivity, widgetArgs.getContext());

        // verify onActivityResultListener was registered
        HashMap<Integer, OnActivityResultListener> onActivityResultListeners =
                ReflectionHelpers.getField(widgetArgs.getContext(), "onActivityResultListeners");
        assertEquals(basicRDTCaptureFactory, onActivityResultListeners.get(RDT_CAPTURE_CODE));

        // verify view tags were added
        View rootLayout = viewList.get(0);
        assertEquals("key", rootLayout.getTag(R.id.key));
        assertEquals("openmrs_entity_parent", rootLayout.getTag(R.id.openmrs_entity_parent));
        assertEquals("openmrs_entity", rootLayout.getTag(R.id.openmrs_entity));
        assertEquals("openmrs_entity_id", rootLayout.getTag(R.id.openmrs_entity_id));

        // verify capture activity was started
        Intent expectedIntent = new Intent(jsonFormActivity, RDTCaptureActivity.class);
        Intent actualIntent = shadowOf(jsonFormActivity).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testGetCustomTranslatableWidgetFieldsShouldReturnNonNullSet() {
        Set<String> translatableFields = basicRDTCaptureFactory.getCustomTranslatableWidgetFields();
        assertNotNull(translatableFields);
    }
}

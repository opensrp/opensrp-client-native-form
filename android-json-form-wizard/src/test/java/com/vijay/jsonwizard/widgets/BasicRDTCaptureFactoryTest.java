package com.vijay.jsonwizard.widgets;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.shadow.ShadowContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.washington.cs.ubicomplab.rdt_reader.activity.RDTCaptureActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RDT_CAPTURE_CODE;
import static edu.washington.cs.ubicomplab.rdt_reader.core.Constants.SAVED_IMAGE_FILE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
        basicRDTCaptureFactory = new BasicRDTCaptureFactory();
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

    @Test
    public void testCaptureActivityIsClosedOnBackPress() {
        Whitebox.setInternalState(basicRDTCaptureFactory, "widgetArgs", getWidgetArgs());
        basicRDTCaptureFactory.onActivityResult(1, RESULT_CANCELED, null);
        verify(jsonFormActivity).finish();
    }

    @Test
    public void testOnActivityResultShouldCorrectlyExtractCaptureValues() throws JSONException {
        Whitebox.setInternalState(basicRDTCaptureFactory, "widgetArgs", getWidgetArgs());
        Intent intent = new Intent();
        intent.putExtra(SAVED_IMAGE_FILE_PATH, "file_path");

        View view = new View(jsonFormActivity);
        view.setTag(R.id.key, "key");
        view.setTag(R.id.openmrs_entity_parent, "entity_parent");
        view.setTag(R.id.openmrs_entity, "entity");
        view.setTag(R.id.openmrs_entity_id, "entity_id");
        Whitebox.setInternalState(basicRDTCaptureFactory, "rootLayout", view);

        basicRDTCaptureFactory.onActivityResult(RDT_CAPTURE_CODE, RESULT_OK, intent);
        verify(jsonFormActivity).writeValue(eq("step1"), eq("key"), eq("file_path"),
                eq("entity_parent"), eq("entity"), eq("entity_id"), eq(false));
        verify(formFragment).next();
        verify(formFragment).save(eq(true));
    }

    private WidgetArgs getWidgetArgs() {
        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withFormFragment(formFragment)
                .withContext(jsonFormActivity)
                .withStepName("step1")
                .withPopup(false)
                .withJsonObject(new JSONObject());
        return widgetArgs;
    }
}

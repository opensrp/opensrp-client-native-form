package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class ToasterNotesFactoryTest extends BaseTest {
    private ToasterNotesFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private View rootLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ToasterNotesFactory();
    }

    @Test
    public void testGpsWidgetFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        ToasterNotesFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getLinearLayout(context);
        Assert.assertNotNull(rootLayout);

       /* Mockito.doReturn(recordButton).when(rootLayout).findViewById(R.id.record_button);
        Assert.assertNotNull(recordButton);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        Mockito.doReturn(latitudeTV).when(rootLayout).findViewById(R.id.latitude);
        Assert.assertNotNull(latitudeTV);

        Mockito.doReturn(longitudeTV).when(rootLayout).findViewById(R.id.longitude);
        Assert.assertNotNull(longitudeTV);

        Mockito.doReturn(altitudeTV).when(rootLayout).findViewById(R.id.altitude);
        Assert.assertNotNull(altitudeTV);

        Mockito.doReturn(accuracyTV).when(rootLayout).findViewById(R.id.accuracy);
        Assert.assertNotNull(accuracyTV);

        Mockito.doReturn("my-test").when(factorySpy).getText(ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());

        Mockito.doReturn(gpsDialog).when(factorySpy).getGpsDialog(recordButton, context, latitudeTV, longitudeTV, altitudeTV, accuracyTV);
        Assert.assertNotNull(gpsDialog);
*/
        String gpsString = "{\"key\":\"gps\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"geopoint\",\"openmrs_data_type\":\"text\",\"type\":\"gps\",\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"},\"read_only\":true,\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }
}

package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GpsDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.mockito.Mockito.when;

public class GpsFactoryTest extends BaseTest {
    private GpsFactory factory;
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

    @Mock
    private TextView latitudeTV;

    @Mock
    private TextView longitudeTV;

    @Mock
    private TextView altitudeTV;

    @Mock
    private TextView accuracyTV;

    @Mock
    private Button recordButton;

    @Mock
    private GpsDialog gpsDialog;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(formFragment.getJsonApi()).thenReturn(context);
        when(context.getAppExecutors()).thenReturn(new AppExecutors());
        factory = new GpsFactory();
    }

    @Test
    public void testGpsWidgetFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        GpsFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(recordButton).when(rootLayout).findViewById(R.id.record_button);
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

        String gpsString = "{\"key\":\"gps\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"geopoint\",\"openmrs_data_type\":\"text\",\"type\":\"gps\",\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"},\"read_only\":true,\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }

    @Test
    public void testAttachLayoutShouldPopulateTextViewsCorrectlyWithLatitudeAndLongitude() throws JSONException {
        GpsFactory factorySpy = Mockito.spy(factory);
        String[] values = new String[]{"-1.2334", "35", "", ""};
        Context context = RuntimeEnvironment.application;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUE, StringUtils.join(values, " "));
        factorySpy.attachLayout(context, jsonObject, recordButton,
                latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        TextView[] views = new TextView[]{latitudeTV, longitudeTV, altitudeTV, accuracyTV};
        int[] stringIds = new int[]{R.string.latitude, R.string.longitude, R.string.altitude, R.string.accuracy};
        for (int i = 0; i < values.length; i++) {
            Mockito.verify(views[i], Mockito.times(1))
                    .setText(Mockito.eq(factorySpy.getText(context, values[i], stringIds[i])));
        }

        Mockito.verify(recordButton, Mockito.atLeastOnce())
                .setTag(Mockito.eq(R.id.raw_value), Mockito.eq(jsonObject.optString(JsonFormConstants.VALUE).trim()));
    }

    @Test
    public void testAttachLayoutShouldPopulateTextViewsCorrectlyWithAllElements() throws JSONException {
        GpsFactory factorySpy = Mockito.spy(factory);
        String[] values = new String[]{"-1.2334", "35", "0.0", "7.8"};
        Context context = RuntimeEnvironment.application;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUE, StringUtils.join(values, " "));
        factorySpy.attachLayout(context, jsonObject, recordButton,
                latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        TextView[] views = new TextView[]{latitudeTV, longitudeTV, altitudeTV, accuracyTV};
        int[] stringIds = new int[]{R.string.latitude, R.string.longitude, R.string.altitude, R.string.accuracy};
        for (int i = 0; i < values.length; i++) {
            Mockito.verify(views[i], Mockito.times(1))
                    .setText(Mockito.eq(factorySpy.getText(context, values[i], stringIds[i])));
        }

        Mockito.verify(recordButton, Mockito.atLeastOnce())
                .setTag(Mockito.eq(R.id.raw_value), Mockito.eq(jsonObject.optString(JsonFormConstants.VALUE)));
    }

    @Test
    public void testAttachLayoutShouldPopulateTextViewsEmptyString() {
        GpsFactory factorySpy = Mockito.spy(factory);
        String[] values = new String[]{"", "", "", ""};
        Context context = RuntimeEnvironment.application;
        JSONObject jsonObject = new JSONObject();
        factorySpy.attachLayout(context, jsonObject, recordButton,
                latitudeTV, longitudeTV, altitudeTV, accuracyTV);

        TextView[] views = new TextView[]{latitudeTV, longitudeTV, altitudeTV, accuracyTV};
        int[] stringIds = new int[]{R.string.latitude, R.string.longitude, R.string.altitude, R.string.accuracy};
        for (int i = 0; i < values.length; i++) {
            Mockito.verify(views[i], Mockito.times(1))
                    .setText(Mockito.eq(factorySpy.getText(context, values[i], stringIds[i])));
        }

        Mockito.verify(recordButton, Mockito.atLeastOnce())
                .setTag(Mockito.eq(R.id.raw_value), Mockito.eq(""));
    }
}

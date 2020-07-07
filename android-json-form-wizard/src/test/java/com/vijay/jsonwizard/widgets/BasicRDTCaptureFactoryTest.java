package com.vijay.jsonwizard.widgets;

import android.view.View;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

public class BasicRDTCaptureFactoryTest extends BaseTest {
    private BasicRDTCaptureFactory factory;
    @Mock
    private JsonFormActivity context;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Mock
    private View rootLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new BasicRDTCaptureFactory();
    }

    @Test
    public void testRDTCaptureFactoryInstantiatesViewsCorrectly() throws Exception {
        String rdtCaptureString = "{\"key\":\"rdt_capture\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"rdt_capture\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}}}";
        JSONObject rdtCapture = new JSONObject(rdtCaptureString);
        Assert.assertNotNull(rdtCapture);

        BasicRDTCaptureFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Mockito.doReturn(false).when(factorySpy).isPermissionGiven();

        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, rdtCapture, listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        BasicRDTCaptureFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }
}

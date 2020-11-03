package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
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
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

public class BarcodeFactorTest extends BaseTest {
    private BarcodeFactory factory;
    @Mock
    private JsonFormActivity jsonFormActivity;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private CommonListener listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new BarcodeFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testBarCodeFactoryInstantiatesViewsCorrectly() throws Exception {
        String gpsString = "{\"key\":\"user_qr_code\",\"openmrs_entity_parent\":\"no_parent\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"user_qr_code_id\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"User ID\",\"read_only\":true,\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"value\":\"123455646\",\"v_required\":{\"value\":true,\"err\":\"Please enter the user ID\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}}}";
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
        Assert.assertEquals(1, viewList.size());

        RelativeLayout relativeLayout = (RelativeLayout) viewList.get(0);
        Assert.assertNotNull(relativeLayout);

        MaterialEditText materialEditText = (MaterialEditText) relativeLayout.getChildAt(0);

        Assert.assertEquals("user_qr_code", materialEditText.getTag(R.id.key));
        Assert.assertEquals("no_parent", materialEditText.getTag(R.id.openmrs_entity_parent));
        Assert.assertEquals("concept", materialEditText.getTag(R.id.openmrs_entity));
        Assert.assertEquals("user_qr_code_id", materialEditText.getTag(R.id.openmrs_entity_id));
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        BarcodeFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("scanButtonText", editableProperties.iterator().next());
    }
}

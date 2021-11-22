package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class BarcodeFactoryTest extends FactoryTest {
    private BarcodeFactory factory;

    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout rootLayout;

    @Mock
    private MaterialEditText editText;

    @Mock
    private Button scanButton;

    @Before
    public void setUp() {
        super.setUp();
        factory = new BarcodeFactory();
    }

    @Test
    public void testBarCodeFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        BarcodeFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        Mockito.doReturn(editText).when(rootLayout).findViewById(R.id.edit_text);
        Assert.assertNotNull(editText);

        Mockito.doReturn(scanButton).when(rootLayout).findViewById(R.id.scan_button);
        Assert.assertNotNull(scanButton);

        Mockito.doReturn(jsonFormActivity).when(formFragment).getJsonApi();

        String gpsString = "{\"key\":\"user_qr_code\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"User ID\",\"read_only\":true,\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"value\":\"123455646\",\"v_required\":{\"value\":true,\"err\":\"Please enter the user ID\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }
}

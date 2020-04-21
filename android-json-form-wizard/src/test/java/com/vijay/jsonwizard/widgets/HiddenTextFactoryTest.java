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

import java.util.List;

public class HiddenTextFactoryTest extends BaseTest {
    private HiddenTextFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout rootLayout;

    @Mock
    private MaterialEditText hiddenText;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new HiddenTextFactory();
    }

    @Test
    public void testDisableHiddenTextFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        HiddenTextFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        String gpsString = "{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"hidden\",\"value\":\"\",\"disabled\":true,\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_constraints_rules.yml\"}}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(viewList.size(), 0);
    }

    @Test
    public void testHiddenTextFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        HiddenTextFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).inflateLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(hiddenText).when(rootLayout).findViewById(R.id.edit_text);
        Assert.assertNotNull(hiddenText);

        String gpsString = "{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"hidden\",\"value\":\"12-06-1990\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_constraints_rules.yml\"}}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }
}

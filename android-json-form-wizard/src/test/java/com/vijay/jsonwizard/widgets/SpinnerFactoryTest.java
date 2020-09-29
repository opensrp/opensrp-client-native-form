package com.vijay.jsonwizard.widgets;

import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 03/04/2020
 */
public class SpinnerFactoryTest extends BaseTest {
    private SpinnerFactory spinnerFactory;
    private JSONArray spinnerWidgetOptions;
    private JsonFormActivity jsonFormActivity;
    private JSONObject spinnerWidget;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() throws JSONException {
        spinnerFactory = new SpinnerFactory();
        String spinnerWidgetString = "{\"key\":\"response_spinner_with_options\",\"openmrs_entity_parent\":\"test_parent_concept\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"concept_id\",\"type\":\"spinner\",\"hint\":\"Response Spinners\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"no\",\"text\":\"No\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"maybe\",\"text\":\"Maybe\",\"value\":false,\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter response\"},\"openmrs_choice_ids\":{\"user_one\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"user_two\":\"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}";
        spinnerWidget = new JSONObject(spinnerWidgetString);
        spinnerWidgetOptions = spinnerWidget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testSpinnerFactoryInstantiatesViewsCorrectly() throws Exception {
        List<View> viewList = spinnerFactory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, spinnerWidget, listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        View rootLayout = viewList.get(0);
        Assert.assertEquals(3, ((RelativeLayout) rootLayout).getChildCount());

        MaterialSpinner materialSpinner = (MaterialSpinner) ((RelativeLayout) rootLayout).getChildAt(0);

        Assert.assertEquals("response_spinner_with_options", materialSpinner.getTag(R.id.key));
        Assert.assertEquals("test_parent_concept", materialSpinner.getTag(R.id.openmrs_entity_parent));
        Assert.assertEquals("concept", materialSpinner.getTag(R.id.openmrs_entity));
        Assert.assertEquals("concept_id", materialSpinner.getTag(R.id.openmrs_entity_id));
    }

    @Test
    public void testGetOptionsKeyValPairsShouldExtractCorrectPairs() throws Exception {
        Pair<JSONArray, JSONArray> keyValPairs = Whitebox.invokeMethod(spinnerFactory, "getOptionsKeyValPairs", spinnerWidgetOptions);
        JSONArray actualKeys = keyValPairs.first;
        JSONArray actualVals = keyValPairs.second;

        JSONArray expectedKeys = new JSONArray();
        expectedKeys.put("yes");
        expectedKeys.put("no");
        expectedKeys.put("maybe");

        JSONArray expectedVals = new JSONArray();
        expectedVals.put("Yes");
        expectedVals.put("No");
        expectedVals.put("Maybe");
        for (int i = 0; i < actualKeys.length(); i++) {
            assertEquals(actualKeys.getString(i), expectedKeys.getString(i));
            assertEquals(actualVals.getString(i), expectedVals.getString(i));
        }
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(spinnerFactory);
        SpinnerFactory factorySpy = Mockito.spy(spinnerFactory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("options.text", editableProperties.iterator().next());
    }
}

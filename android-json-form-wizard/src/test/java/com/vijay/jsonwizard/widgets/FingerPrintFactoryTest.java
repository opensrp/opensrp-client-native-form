package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FingerPrintFactoryTest extends FactoryTest {

    private FingerPrintFactory fingerPrintFactory;

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Before
    public void setUp() {
        super.setUp();
        fingerPrintFactory = spy(new FingerPrintFactory());
    }

    @Test
    public void testFactoryShouldInitializeCorrectly() throws Exception {
        String fieldJsonString = "{\"key\":\"finger_print\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"finger_print\",\"project_id\":\"tZqJnw0ajK04LMYdZzyw\",\"user_id\":\"test_user\",\"module_id\":\"mpower\",\"finger_print_option\":\"register\",\"uploadButtonText\":\"Take finger print\",\"image_file\":\"\",\"relevance\":{\"step1:user_first_name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"test\\\")\"}}}";
        JSONObject jsonObject = new JSONObject(fieldJsonString);
        List<View> views = fingerPrintFactory.getViewsFromJson(JsonFormConstants.STEP1,
                jsonFormActivity, jsonFormFragment, jsonObject, commonListener, false);

        assertNotNull(views);

        assertEquals(2, views.size());

        assertTrue(views.get(0) instanceof ImageView);

        assertTrue(views.get(1) instanceof Button);

        verify(fingerPrintFactory, times(2))
                .setViewTags(eq(jsonObject), eq(JsonFormConstants.STEP1), any(View.class), eq(false));

        Button uploadButton = (Button) views.get(1);
        assertEquals(jsonObject.getString(JsonFormConstants.UPLOAD_BUTTON_TEXT), uploadButton.getText().toString());
    }

}
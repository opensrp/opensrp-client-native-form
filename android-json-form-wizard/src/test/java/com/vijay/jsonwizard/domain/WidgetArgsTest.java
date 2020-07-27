package com.vijay.jsonwizard.domain;

import android.content.Context;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Vincent Karuri on 21/07/2020
 */
public class WidgetArgsTest {

    @Test
    public void testWidgetArgsConstructorShouldCorrectlySetValues() {
        String stepName = "step_name";
        Context context = mock(Context.class);
        JsonFormFragment formFragment = mock(JsonFormFragment.class);
        JSONObject jsonObject = new JSONObject();
        CommonListener listener = mock(CommonListener.class);
        boolean popup = false;

        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup)
                .withStepName(stepName);

        assertEquals(context, widgetArgs.getContext());
        assertEquals(formFragment, widgetArgs.getFormFragment());
        assertEquals(jsonObject, widgetArgs.getJsonObject());
        assertEquals(listener, widgetArgs.getListener());
        assertEquals(popup, widgetArgs.isPopup());
        assertEquals(stepName, widgetArgs.getStepName());
    }
}

package com.vijay.jsonwizard.customviews;

import android.view.LayoutInflater;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Vincent Karuri on 28/07/2020
 */
public class ToasterLinearLayoutTest extends BaseTest {

    @Test
    public void testLinearLayoutInitializationShouldReturnNonNullLayout() {
        ToasterLinearLayout toasterLinearLayout = (ToasterLinearLayout) LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(R.layout.native_form_toaster_notes, null);
        assertNotNull(toasterLinearLayout);
        toasterLinearLayout.setText("test");
        assertEquals("test", toasterLinearLayout.getText());
    }
}

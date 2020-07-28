package com.vijay.jsonwizard.customviews;

import com.vijay.jsonwizard.BaseTest;

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
        ToasterLinearLayout toasterLinearLayout = new ToasterLinearLayout(RuntimeEnvironment.application);
        assertNotNull(toasterLinearLayout);
        toasterLinearLayout.setText("test");
        assertEquals("test", toasterLinearLayout.getText());
    }
}

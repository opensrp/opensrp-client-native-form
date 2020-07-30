package com.vijay.jsonwizard.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Vincent Karuri on 28/07/2020
 */
public class ExObjectResultTest {

    @Test
    public void testInitShouldCorrectlyPopulateFields() {
        ExObjectResult exObjectResult = new ExObjectResult(true, false);
        assertTrue(exObjectResult.isRelevant());
        assertFalse(exObjectResult.isFinal());
    }
}

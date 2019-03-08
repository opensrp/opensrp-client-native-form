package com.jsonwizard.rules;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.rules.RulesEngineHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by samuelgithengi on 3/7/19.
 */
public class RulesEngineHelperTest extends BaseTest {

    @Test
    public void testIfNull() {
        RulesEngineHelper helper = new RulesEngineHelper();
        assertEquals("0", helper.ifNull(null, "0"));
        assertEquals("1", helper.ifNull("", "1"));
        assertEquals("123", helper.ifNull("123", ""));
    }

}

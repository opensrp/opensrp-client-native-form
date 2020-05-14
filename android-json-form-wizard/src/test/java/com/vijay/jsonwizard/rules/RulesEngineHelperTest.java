package com.vijay.jsonwizard.rules;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.shadow.ShadowRulesEngineDateUtil;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by samuelgithengi on 3/7/19.
 */
public class RulesEngineHelperTest extends BaseTest {

    public static String TEST_TIME_EXPECTED = "2020-05-30T10:15:30Z";

    @Test
    public void testIfNull() {
        RulesEngineHelper helper = new RulesEngineHelper();
        assertEquals("0", helper.ifNull(null, "0"));
        assertEquals("1", helper.ifNull("", "1"));
        assertEquals("123", helper.ifNull("123", ""));
    }


    @Test
    @Config(shadows = {ShadowRulesEngineDateUtil.class})
    public void testGetDateTimeToday() {
        RulesEngineHelper helper = new RulesEngineHelper();
        Assert.assertEquals("2020-05-30 13:15:30", helper.getDateTimeToday());
    }

}

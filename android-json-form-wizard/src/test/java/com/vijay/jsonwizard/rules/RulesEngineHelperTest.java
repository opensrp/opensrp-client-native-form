package com.vijay.jsonwizard.rules;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.shadow.ShadowRulesEngineDateUtil;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by samuelgithengi on 3/7/19.
 */
public class RulesEngineHelperTest extends BaseTest {

    public static String TEST_DATE_TIME = "2020-05-30T10:15:30Z";

    @Test
    public void testIfNull() {
        RulesEngineHelper helper = new RulesEngineHelper();
        assertEquals("0", helper.ifNull(null, "0"));
        assertEquals("1", helper.ifNull("", "1"));
        assertEquals("123", helper.ifNull("123", ""));
    }


    @Test
    @Config(shadows = {ShadowRulesEngineDateUtil.class})
    public void getDateTimeTodayReturnsExpectedDateTime() {
        RulesEngineHelper helper = new RulesEngineHelper();
        assertEquals(new RulesEngineDateUtil().getDateTimeToday(), helper.getDateTimeToday());
    }

    @Test
    @Config(shadows = {ShadowRulesEngineDateUtil.class})
    public void getDateTodayReturnsExpectedDate() {
        RulesEngineHelper helper = new RulesEngineHelper();
        assertEquals("30-05-2020", helper.getDateToday());
    }

    @Test
    public void canGetNonNullValueFromList() {
        List<String> stringList = new ArrayList<>();
        RulesEngineHelper helper = new RulesEngineHelper();
        stringList.add("Hello");
        stringList.add("");
        assertEquals("Hello", helper.getNonBlankValue(stringList));
    }

}

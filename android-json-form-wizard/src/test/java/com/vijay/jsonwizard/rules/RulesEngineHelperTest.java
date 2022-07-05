package com.vijay.jsonwizard.rules;

import static org.junit.Assert.assertEquals;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.shadow.ShadowRulesEngineDateUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 3/7/19.
 */
public class RulesEngineHelperTest extends BaseTest {

    public static String TEST_DATE_TIME = "2020-05-30T10:15:30Z";
    private RulesEngineHelper helper;

    @Before
    public void setUp() {
        helper = new RulesEngineHelper();
    }

    @Test
    public void testIfNull() {
        assertEquals("0", helper.ifNull(null, "0"));
        assertEquals("1", helper.ifNull("", "1"));
        assertEquals("123", helper.ifNull("123", ""));
    }


    @Test
    @Config(shadows = {ShadowRulesEngineDateUtil.class})
    public void getDateTimeTodayReturnsExpectedDateTime() {
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
        stringList.add("Hello");
        stringList.add("");
        assertEquals("Hello", helper.getNonBlankValue(stringList));
    }

    @Test
    public void testGetMothersAge() {
        String dob = "04-07-1990";
        int expectedAge = 32;
        int actualAge = helper.getMothersAge(dob);
        assertEquals(expectedAge, actualAge);
    }

    @Test
    public void testGetDifferenceDays() {
        String dateString1 = "04-07-1990";
        String dateString2 = "06-08-1990";
        Long expectedDays = Long.parseLong("33");
        Long actualDays = helper.getDifferenceDays(dateString2, dateString1);
        assertEquals(expectedDays, actualDays);

    }

}

package com.vijay.jsonwizard.utils;

import android.content.Context;

import com.vijay.jsonwizard.BaseTest;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 2020-03-21.
 */

public class DateUtilTest extends BaseTest {

    @Test
    public void assertDateUtilNotNull() {
        Assert.assertNotNull(new DateUtil());
    }

    @Test
    public void testGetDurationProcessesTimeDurationsCorrectly() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        Locale locale = context.getResources().getConfiguration().locale;

        Assert.assertEquals("1d", DateUtil.getDuration(100000000l, locale, context));
        Assert.assertEquals("5w 1d", DateUtil.getDuration(3110400000l, locale, context));
        Assert.assertEquals("5w", DateUtil.getDuration(3024000000l, locale, context));
        Assert.assertEquals("3m 1w", DateUtil.getDuration(TimeUnit.DAYS.toMillis(100), locale, context));
        Assert.assertEquals("1y", DateUtil.getDuration(31363200000l, locale, context));
        Assert.assertEquals("1y 1m", DateUtil.getDuration(36500000000l, locale, context));
        Assert.assertEquals("2y", DateUtil.getDuration(63113852000l, locale, context));

        Assert.assertEquals("1d", DateUtil.getDuration(100000000l, locale, context));

    }

    @Test
    public void testGetDurationTimeDifferenceReturnsCorrectValuesWhenTwoDatesSpecifed() {
        String dateOne = "01-10-2019";
        String dateTwo = "01-10-2030";

        long expectedOutcome = new LocalDate(2030, 10, 1).toDate().getTime() - new LocalDate(2019, 10, 1).toDate().getTime();
        long timediff = DateUtil.getDurationTimeDifference(dateOne, dateTwo);

        Assert.assertEquals(expectedOutcome, timediff);

    }


    @Test
    public void testGetDurationTimeDifferenceReturnsCorrectValuesWhenOneDatesSpecifed() {
        String dateOne = "01-10-2012";

        long expectedOutcome = new LocalDate().toDate().getTime() - new LocalDate(2012, 10, 1).toDate().getTime();
        long timediff = DateUtil.getDurationTimeDifference(dateOne, null);

        Assert.assertEquals(expectedOutcome, timediff);

    }
}

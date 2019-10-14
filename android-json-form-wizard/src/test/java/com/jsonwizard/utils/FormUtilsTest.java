package com.jsonwizard.utils;

import com.vijay.jsonwizard.utils.FormUtils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;

public class FormUtilsTest {

    @Test
    public void testGetDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar res = FormUtils.getDate("today-1w");
        assertEquals(sdf.format(calendar.getTime()), sdf.format(res.getTime()));
    }
}

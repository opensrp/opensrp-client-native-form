package com.vijay.jsonwizard.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ndegwamartin on 2020-02-04.
 */
public class NumericDatePickerHelperTest {

    @Test
    public void testGetDaysInMonthReturnsCorrectNumberOfDaysForMonth() {

        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(0, false));//January
        Assert.assertEquals(28, NumericDatePickerHelper.getDaysInMonth(1, false));//Febrary
        Assert.assertEquals(29, NumericDatePickerHelper.getDaysInMonth(1, true));//February Leap year
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(2, false));//March
        Assert.assertEquals(30, NumericDatePickerHelper.getDaysInMonth(3, false));//April
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(4, false));//May
        Assert.assertEquals(30, NumericDatePickerHelper.getDaysInMonth(5, false));//June
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(6, false));//July
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(7, false));//August
        Assert.assertEquals(30, NumericDatePickerHelper.getDaysInMonth(8, false));//September
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(9, false));//October
        Assert.assertEquals(30, NumericDatePickerHelper.getDaysInMonth(10, false));//November
        Assert.assertEquals(31, NumericDatePickerHelper.getDaysInMonth(11, false));//December
    }

    @Test
    public void testIsLeapYearWorksCorrectly() {

        Assert.assertFalse(NumericDatePickerHelper.isLeapYear(2003));
        Assert.assertTrue(NumericDatePickerHelper.isLeapYear(2012));
        Assert.assertFalse(NumericDatePickerHelper.isLeapYear(1900));
        Assert.assertFalse(NumericDatePickerHelper.isLeapYear(1985));
        Assert.assertTrue(NumericDatePickerHelper.isLeapYear(1600));
    }
}

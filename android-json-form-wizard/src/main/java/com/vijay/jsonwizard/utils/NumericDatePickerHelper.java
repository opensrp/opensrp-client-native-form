package com.vijay.jsonwizard.utils;

import java.util.Calendar;

/**
 * Created by ndegwamartin on 2020-01-20
 * .
 */
public class NumericDatePickerHelper {

    /**
     * @param month      the month starting from index 0
     * @param isLeapYear boolean for whether is leap year
     * @return number of days in month
     */

    public static int getDaysInMonth(int month, boolean isLeapYear) {
        int result;
        switch (month) {

            case Calendar.SEPTEMBER:
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.NOVEMBER:
                result = 30;
                break;
            case Calendar.FEBRUARY:
                result = isLeapYear ? 29 : 28;
                break;

            default:
                result = 31;
                break;
        }
        return result;

    }

    /**
     * @param year the year to check
     * @return whether year passed as param is a leap year
     */

    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return true;
        }
    }
}

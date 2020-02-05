package com.vijay.jsonwizard.comparisons;


import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by onaio on 23/08/2017.
 */

public class GreaterThanComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenSecondStringsIsAlphabeticallyRankedHigher() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare("stringA", TestConstants.TYPE_STRING, "stringB"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondStringsIsAlphabeticallyRankedLower() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(true, comparison.compare("stringD", TestConstants.TYPE_STRING, "stringC"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstStringNotSetAndSecondStringsIsDefaultString() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_STRING, ""));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondNumberIsGreaterThanTheFirst() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare("1", TestConstants.TYPE_NUMERIC, "3"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondNumberIsLessThanTheFirst() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(true, comparison.compare("4", TestConstants.TYPE_NUMERIC, "1"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstNumberIsNotSetAndSecondIsDefaultString() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_NUMERIC, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstNumberNotSetAndSecondSNumberIsLowerThanDefault() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_NUMERIC, "-1"));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondDateIsLaterThanTheFirst() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2030"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondDateIsLaterThanTheFirst() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(true, comparison.compare("02-02-2030", TestConstants.TYPE_DATE, "01-01-2000"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstDateIsNotSetAndSecondIsDefaultString() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_DATE, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstDateNotSetAndSecondDateIsEarlierThanDefault() {

        GreaterThanComparison comparison = new GreaterThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_DATE, "01-01-1800"));

    }
}
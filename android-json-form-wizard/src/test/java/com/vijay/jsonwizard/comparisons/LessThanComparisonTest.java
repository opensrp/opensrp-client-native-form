package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by onaio on 23/08/2017.
 */

public class LessThanComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenSecondStringsIsAlphabeticallyRankedLower() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare("stringB", TestConstants.TYPE_STRING, "stringA"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondStringsIsAlphabeticallyRankedHigher() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare("stringC", TestConstants.TYPE_STRING, "stringD"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstStringNotSetAndSecondStringsIsDefaultString() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_STRING, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstStringNotSetAndSecondStringsIsAlphabeticallyRankedHigherThanDefaultString() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_STRING, "theSecondString"));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondNumberIsLessThanTheFirst() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare("3", TestConstants.TYPE_NUMERIC, "1"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondNumberIsMoreThanTheFirst() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare("4", TestConstants.TYPE_NUMERIC, "10"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstNumberIsNotSetAndSecondIsDefaultString() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_NUMERIC, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstNumberNotSetAndSecondSNumberIsHigherThanDefault() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_NUMERIC, "100"));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondDateIsEarlierThanTheFirst() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-1990"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondDateIsLaterThanTheFirst() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare("02-02-2000", TestConstants.TYPE_DATE, "01-01-2030"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstDateIsNotSetAndSecondIsDefaultString() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_DATE, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstDateNotSetAndSecondDateIsLaterThanDefault() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_DATE, "03-03-2000"));

    }
}
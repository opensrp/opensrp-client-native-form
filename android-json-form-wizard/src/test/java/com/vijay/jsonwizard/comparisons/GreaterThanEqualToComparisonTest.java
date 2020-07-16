package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by onaio on 23/08/2017.
 */

public class GreaterThanEqualToComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenSecondStringsIsAlphabeticallyRankedHigher() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(false, comparison.compare("stringA", TestConstants.TYPE_STRING, "stringB"));

    }

    @Test
    public void compareShouldReturnTrueWhenTheTwoStringsAreAlphabeticallyEqual() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare("stringC", TestConstants.TYPE_STRING, "stringC"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondStringsIsAlphabeticallyRankedLower() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare("stringD", TestConstants.TYPE_STRING, "stringC"));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstStringNotSetAndSecondStringsIsDefaultString() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_STRING, ""));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondNumberIsGreaterThanTheFirst() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(false, comparison.compare("1", TestConstants.TYPE_NUMERIC, "3"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondNumberIsLessThanTheFirst() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare("4", TestConstants.TYPE_NUMERIC, "1"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstNumberIsNotSetAndSecondIsDefaultString() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_NUMERIC, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstNumberNotSetAndSecondSNumberIsLowerThanDefault() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_NUMERIC, "-1"));

    }


    @Test
    public void compareShouldReturnFalseWhenSecondDateIsLaterThanTheFirst() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(false, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2030"));

    }

    @Test
    public void compareShouldReturnTrueWhenSecondDateIsLaterThanTheFirst() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare("02-02-2030", TestConstants.TYPE_DATE, "01-01-2000"));

    }

    @Test
    public void compareShouldReturnFalseWhenFirstDateIsNotSetAndSecondIsDefaultString() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_DATE, ""));

    }

    @Test
    public void compareShouldReturnTrueWhenFirstDateNotSetAndSecondDateIsEarlierThanDefault() {

        GreaterThanEqualToComparison comparison = new GreaterThanEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_DATE, "01-01-1800"));

    }
}
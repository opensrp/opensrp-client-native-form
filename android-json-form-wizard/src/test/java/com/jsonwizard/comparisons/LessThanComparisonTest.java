package com.jsonwizard.comparisons;

import com.jsonwizard.BaseTest;
import com.jsonwizard.TestConstants;
import com.vijay.jsonwizard.comparisons.LessThanComparison;

import junit.framework.Assert;

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
    public void compareShouldReturnTrueWhenFirstStringNotSetAndSecondStringsIsAlphabeticallyRankedHigherThanDefaultString() {

        LessThanComparison comparison = new LessThanComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_STRING, "theSecondString"));

    }
}
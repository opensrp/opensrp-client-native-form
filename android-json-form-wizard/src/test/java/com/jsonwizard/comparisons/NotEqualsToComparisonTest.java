package com.jsonwizard.comparisons;

import com.jsonwizard.BaseTest;
import com.jsonwizard.TestConstants;
import com.vijay.jsonwizard.comparisons.NotEqualToComparison;

import junit.framework.Assert;

import org.junit.Test;

public class NotEqualsToComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnTrueWhenTheStringAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare("String", TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnTrueWhenOneStringIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheStingsAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(false, comparison.compare("StringA", TestConstants.TYPE_STRING, "StringA"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheNumbersAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare("2", TestConstants.TYPE_NUMERIC, "1"));
    }

    @Test
    public void compareShouldReturnTrueWhenOneNumberIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_STRING, "1"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheNumbersAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(false, comparison.compare("2", TestConstants.TYPE_NUMERIC, "2"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheDatesAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheOneDateIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheDatesAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(false, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2000"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheArraysAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare("['test4','test3']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnTrueWhenOneArrayIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(true, comparison.compare(null, TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheArraysAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals(false, comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

}

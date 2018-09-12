package com.jsonwizard.comparisons;

import com.jsonwizard.BaseTest;
import com.jsonwizard.TestConstants;
import com.vijay.jsonwizard.comparisons.EqualToComparison;

import junit.framework.Assert;

import org.junit.Test;

public class EqualsToComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenTheStringAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare("String", TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnFalseWhenOneStringIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheStingsAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(true, comparison.compare("StringA", TestConstants.TYPE_STRING, "StringA"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheNumbersAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare("2", TestConstants.TYPE_NUMERIC, "1"));
    }

    @Test
    public void compareShouldReturnFalseWhenOneNumberIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_STRING, "1"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheNumbersAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(true, comparison.compare("2", TestConstants.TYPE_NUMERIC, "2"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheDatesAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheOneDateIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheDatesAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(true, comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2000"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheArraysAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare("['test4','test3']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalseWhenOneArrayIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(false, comparison.compare(null, TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheArraysAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertEquals(true, comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

}

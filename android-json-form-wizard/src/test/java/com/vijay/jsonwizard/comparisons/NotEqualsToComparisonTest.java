package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;

import org.junit.Assert;
import org.junit.Test;

public class NotEqualsToComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnTrueWhenTheStringAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("String", TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnTrueWhenOneStringIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare(null, TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheStingsAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("StringA", TestConstants.TYPE_STRING, "StringA"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheNumbersAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("2", TestConstants.TYPE_NUMERIC, "1"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheNumbersAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("2", TestConstants.TYPE_NUMERIC, "2"));
    }

    @Test
    public void compareShouldReturnTrueWhenFirstNumberIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare(null, TestConstants.TYPE_NUMERIC, "1"));
    }

    @Test
    public void compareShouldReturnTrueWhenSecondNumberIsNUll() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("2", TestConstants.TYPE_NUMERIC, null));
    }

    @Test
    public void compareShouldReturnTrueWhenTheDatesAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheOneDateIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare(null, TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheSecondDateIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("01-01-2001", TestConstants.TYPE_DATE, null));
    }

    @Test
    public void compareShouldReturnFalseWhenTheDatesAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2000"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheArraysAreNotEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("['test4','test3']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnTrueWhenOneArrayIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare(null, TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheSecondArrayIsNull() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertTrue(comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, null));
    }

    @Test
    public void compareShouldReturnFalseWhenTheArraysAreEqual() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareArrayShouldReturnFalseWhenTheArrayConversionThrowsAnException() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }


    @Test
    public void testNonAvailableType() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", "Test", "['test','test2']"));
    }

    @Test
    public void testGetFunctionName() {
        NotEqualToComparison comparison = new NotEqualToComparison();
        Assert.assertEquals("notEqualTo", comparison.getFunctionName());
    }
}
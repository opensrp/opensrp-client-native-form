package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestConstants;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class EqualsToComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenTheStringAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("String", TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnFalseWhenOneStringIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare(null, TestConstants.TYPE_STRING, "StrinG"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheStingsAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertTrue(comparison.compare("StringA", TestConstants.TYPE_STRING, "StringA"));
    }

    @Test
    public void compareShouldReturnFalseWhenOneNumberIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare(null, TestConstants.TYPE_STRING, "1"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheNumbersAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("2", TestConstants.TYPE_NUMERIC, "1"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheNumbersAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertTrue(comparison.compare("2", TestConstants.TYPE_NUMERIC, "2"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheFirstNumberIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare(null, TestConstants.TYPE_NUMERIC, "2"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheNumbersAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("2", TestConstants.TYPE_NUMERIC, null));
    }

    @Test
    public void compareShouldReturnFalseWhenTheDatesAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheFirstDateIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare(null, TestConstants.TYPE_DATE, "01-01-2001"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheSecondDateIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("01-01-2001", TestConstants.TYPE_DATE, null));
    }

    @Test
    public void compareShouldReturnTrueWhenTheDatesAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertTrue(comparison.compare("01-01-2000", TestConstants.TYPE_DATE, "01-01-2000"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheArraysAreNotEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("['test4','test3']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalseWhenFirstArrayIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare(null, TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalseWhenSecondArrayIsNull() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, null));
    }

    @Test
    public void compareShouldReturnFalseWhenAnArrayIsFormattedWrong() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, "['test','test2"));
    }

    @Test
    public void compareShouldReturnTrueWhenTheArraysAreEqual() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertTrue(comparison.compare("['test','test2']", TestConstants.TYPE_ARRAY, "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalseWhenTheTypeGivenIsWrong() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", "Demo_Type", "['test','test2']"));
    }

    @Test
    public void compareShouldReturnFalse() {
        EqualToComparison comparison = new EqualToComparison();
        Assert.assertFalse(comparison.compare("['test','test2']", null, "['test','test2']"));
    }

    @Test
    public void testGetFunctionName() {
        EqualToComparison equalToComparison = Mockito.spy(new EqualToComparison());
        String functionName = equalToComparison.getFunctionName();
        Assert.assertEquals("equalTo", functionName);
    }
}

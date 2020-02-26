package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by onaio on 23/08/2017.
 */

public class RegexComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenStringsDoNotMatch() {
        RegexComparison comparison = new RegexComparison();
        Assert.assertFalse(comparison.compare("stringA", "", "stringB"));
    }

    @Test
    public void compareShouldReturnTrueWhenStringsMatch() {
        RegexComparison comparison = new RegexComparison();
        Assert.assertTrue(comparison.compare("stringC", "", "stringC"));
    }

    @Test
    public void compareShowReturnFalseWhenFirstStringIsNUll() {
        RegexComparison comparison = new RegexComparison();
        Assert.assertFalse(comparison.compare(null, "", "stringd"));
    }

    @Test
    public void compareShowReturnFalseWhenSecondStringIsNUll() {
        RegexComparison comparison = new RegexComparison();
        Assert.assertFalse(comparison.compare("stringA", "", null));
    }

    @Test
    public void compareShouldReturnFalseWhenBothAAndBAreNull() {
        RegexComparison comparison = new RegexComparison();
        Assert.assertFalse(comparison.compare(null, "", null));
    }

    @Test
    public void testGetFunctionName() {
        RegexComparison regexComparison = Mockito.spy(new RegexComparison());
        String functionName = regexComparison.getFunctionName();
        Assert.assertEquals("regex", functionName);
    }
}
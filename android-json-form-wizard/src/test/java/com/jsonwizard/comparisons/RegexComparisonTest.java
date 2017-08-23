package com.jsonwizard.comparisons;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.comparisons.RegexComparison;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by onaio on 23/08/2017.
 */

public class RegexComparisonTest extends BaseTest {

    @Test
    public void compareShouldReturnFalseWhenStringsDoNotMatch() {

        RegexComparison comparison = new RegexComparison();
        Assert.assertEquals(false, comparison.compare("stringA", "", "stringB"));

    }

    @Test
    public void compareShouldReturnTrueWhenStringsMatch() {

        RegexComparison comparison = new RegexComparison();
        Assert.assertEquals(true, comparison.compare("stringC", "", "stringC"));

    }
}
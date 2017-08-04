package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MinNumericValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnTrueWhenNumberIsGreaterThanMinSetValue() {

        MinNumericValidator validator = new MinNumericValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_1);
        Assert.assertEquals(true, validator.isValid("5", false));

    }

    @Test
    public void isValidShouldReturnFalseWhenNumberIsLessThanMinSetValue() {

        MinNumericValidator validator = new MinNumericValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_50);
        Assert.assertEquals(false, validator.isValid("10", false));

    }

    @Test
    public void isValidShouldReturnTrueWhenNumberIsEqualToMinSetValue() {

        MinNumericValidator validator = new MinNumericValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_20);
        Assert.assertEquals(true, validator.isValid("20", false));

    }

    @Test
    public void isValidShouldReturnFalseWhenValueSetToNull() {

        MinNumericValidator validator = new MinNumericValidator(DEFAULT_ERROR_MESSAGE, 1);
        Assert.assertEquals(false, validator.isValid(null, false));

    }
}

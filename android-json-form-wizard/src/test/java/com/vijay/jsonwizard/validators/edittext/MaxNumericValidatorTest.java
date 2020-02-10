package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class MaxNumericValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenNumberIsGreaterThanMaxSetValue() {

        MaxNumericValidator validator = new MaxNumericValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1);
        Assert.assertEquals(false, validator.isValid("5", false));

    }

    @Test
    public void isValidShouldReturnTrueWhenNumberIsLessThanMaxSetValue() {

        MaxNumericValidator validator = new MaxNumericValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid("10", false));

    }

    @Test
    public void isValidShouldReturnTrueWhenNumberIsEqualToMaxSetValue() {

        MaxNumericValidator validator = new MaxNumericValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_16);
        Assert.assertEquals(true, validator.isValid("16", false));

    }

    @Test
    public void isValidShouldReturnFalseWhenValueSetToNull() {

        MaxNumericValidator validator = new MaxNumericValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1);
        Assert.assertEquals(false, validator.isValid(null, false));

    }

    @Test
    public void isValidShouldReturnFalseWhenValueSetIsEmpty() {
        MaxNumericValidator validator = new MaxNumericValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1);
        Assert.assertEquals(false, validator.isValid("", false));
    }
}

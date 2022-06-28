package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class MinLengthValidatorTest extends BaseTest {

    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsGreaterThanMinSetValue() {
        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1, false);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnFalseWhenStringLengthIsLessThanMinSetValue() {
        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_50, false);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsEqualToMinSetValue() {
        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_16, false);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnTrueWhenTheStringIsNull() {
        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1, false);
        Assert.assertEquals(false, (validator.isValid(null, false)));
    }
}

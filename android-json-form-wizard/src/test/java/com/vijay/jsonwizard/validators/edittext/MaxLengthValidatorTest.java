package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class MaxLengthValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenStringLengthGreaterThanMaxSetValue() {

        MaxLengthValidator validator = new MaxLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthLessThanMaxSetValue() {

        MaxLengthValidator validator = new MaxLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthEqualToMaxSetValue() {
        MaxLengthValidator validator = new MaxLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_16);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

    @Test
    public void isValidShouldReturnTrueWhenTheStringIsNull() {
        MaxLengthValidator validator = new MaxLengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_1);
        Assert.assertEquals(false, (validator.isValid(null, false)));
    }

}


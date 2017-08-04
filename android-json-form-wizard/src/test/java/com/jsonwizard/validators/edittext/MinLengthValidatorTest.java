package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MinLengthValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsGreaterThanMinSetValue() {

        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_1);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

    @Test
    public void isValidShouldReturnFalseWhenStringLengthIsLessThanMinSetValue() {

        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_50);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsEqualToMinSetValue() {

        MinLengthValidator validator = new MinLengthValidator(DEFAULT_ERROR_MESSAGE, CONSTANT_INT_16);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));

    }

}

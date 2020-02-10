package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class LengthValidatorTest extends BaseTest {

    @Test
    public void isValidShouldReturnFalseWhenIsEmptyTrue() {

        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, true));

    }

    @Test
    public void isValidShouldReturnTrueWhenIsEmptyFalse() {

        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }
}


package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class LengthValidatorTest extends BaseTest {

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsEmptyAndMinimumLengthIsZero() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid("", true));
    }

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsNotEmptyAndIsLowerThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnFalseWhenEditTextValueIsLongerThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, 20);
        Assert.assertEquals(false, validator.isValid("This message is longer", false));
    }

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsLongerThanMinimumLengthAndShorterThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }
}
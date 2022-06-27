package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class LengthValidatorTest extends BaseTest {

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsEmptyAndMinimumLengthIsZeroAndIsNotRequired() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50, false);
        Assert.assertEquals(true, validator.isValid("", true));
    }

    @Test
    public void isValidShouldReturnFalseWhenEditTextValueIsEmptyAndIsRequired() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50, true);
        Assert.assertEquals(false, validator.isValid("", true));
    }

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsNotEmptyAndIsLowerThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50, false);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnFalseWhenEditTextValueIsLongerThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, 20, false);
        Assert.assertEquals(false, validator.isValid("This message is longer", false));
    }

    @Test
    public void isValidShouldReturnTrueWhenEditTextValueIsLongerThanMinimumLengthAndShorterThanMaximumLength() {
        LengthValidator validator = new LengthValidator(DEFAULT_ERROR_MSG, CONSTANT_INT_0, CONSTANT_INT_50, false);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }
}
package com.vijay.jsonwizard.edittext;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.junit.Assert;

import org.junit.Test;

public class RequiredValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenStringIsEmpty() {

        RequiredValidator validator = new RequiredValidator(DEFAULT_ERROR_MSG);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, true));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringExists() {

        RequiredValidator validator = new RequiredValidator(DEFAULT_ERROR_MSG);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnTrueWhenStringExistsAndEnterDateChildSeenErrorMessageSet() {
        String error = "Enter the date that the child was first seen at a health facility for immunization services";
        RequiredValidator validator = new RequiredValidator(error);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

}

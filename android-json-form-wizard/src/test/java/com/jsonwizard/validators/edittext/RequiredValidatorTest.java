package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import junit.framework.Assert;

import org.junit.Test;

public class RequiredValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenWhenStringIsEmpty() {

        RequiredValidator validator = new RequiredValidator(DEFAULT_ERROR_MESSAGE);
        Assert.assertEquals(false, validator.isValid(DEFAULT_TEST_MESSAGE, true));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringExists() {

        RequiredValidator validator = new RequiredValidator(DEFAULT_ERROR_MESSAGE);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

    @Test
    public void isValidShouldReturnTrueWhenStringExistsAndEnterDateChildSeenErrorMessageSet() {
        String error = "Enter the date that the child was first seen at a health facility for immunization services";
        RequiredValidator validator = new RequiredValidator(error);
        Assert.assertEquals(true, validator.isValid(DEFAULT_TEST_MESSAGE, false));
    }

}

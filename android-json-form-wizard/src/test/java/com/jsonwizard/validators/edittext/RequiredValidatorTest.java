package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import junit.framework.Assert;

import org.junit.Test;

public class RequiredValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenWhenStringIsEmpty(){

        RequiredValidator validator = new RequiredValidator("Default Error Message");
        Assert.assertEquals(false,validator.isValid("Native Form Test",true));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringExists(){

        RequiredValidator validator = new RequiredValidator("Default Error Message");
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));
    }

    @Test
    public void isValidShouldReturnTrueWhenStringExistsAndEnterDateChildSeenErrorMessageSet(){

        RequiredValidator validator = new RequiredValidator("Enter the date that the child was first seen at a health facility for immunization services");
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));
    }

}

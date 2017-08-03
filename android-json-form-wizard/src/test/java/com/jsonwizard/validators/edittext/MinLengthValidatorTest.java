package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MinLengthValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsGreaterThanMinSetValue(){

        MinLengthValidator validator = new MinLengthValidator("Default Error Message",1);
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));

    }
    @Test
    public void isValidShouldReturnFalseWhenStringLengthIsLessThanMinSetValue(){

        MinLengthValidator validator = new MinLengthValidator("Default Error Message",50);
        Assert.assertEquals(false,validator.isValid("Native Form Test",false));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthIsEqualToMinSetValue(){

        MinLengthValidator validator = new MinLengthValidator("Default Error Message",16);
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));

    }

}

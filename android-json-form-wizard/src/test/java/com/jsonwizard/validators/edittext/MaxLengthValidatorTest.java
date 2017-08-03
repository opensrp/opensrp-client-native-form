package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MaxLengthValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MaxLengthValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenStringLengthGreaterThanMaxSetValue(){

        MaxLengthValidator validator = new MaxLengthValidator("Default Error Message",1);
        Assert.assertEquals(false,validator.isValid("Native Form Test",false));

    }
    @Test
    public void isValidShouldReturnTrueWhenStringLengthLessThanMaxSetValue(){

        MaxLengthValidator validator = new MaxLengthValidator("Default Error Message",50);
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));

    }

    @Test
    public void isValidShouldReturnTrueWhenStringLengthEqualToMaxSetValue(){

        MaxLengthValidator validator = new MaxLengthValidator("Default Error Message",16);
        Assert.assertEquals(true,validator.isValid("Native Form Test",false));

    }

}


package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MinNumericValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnTrueWhenNumberIsGreaterThanMinSetValue(){

        MinNumericValidator validator = new MinNumericValidator("Default Error Message",1);
        Assert.assertEquals(true,validator.isValid("5",false));

    }
    @Test
    public void isValidShouldReturnFalseWhenNumberIsLessThanMinSetValue(){

        MinNumericValidator validator = new MinNumericValidator("Default Error Message",50);
        Assert.assertEquals(false,validator.isValid("10",false));

    }

    @Test
    public void isValidShouldReturnTrueWhenNumberIsEqualToMinSetValue(){

        MinNumericValidator validator = new MinNumericValidator("Default Error Message",20);
        Assert.assertEquals(true,validator.isValid("20",false));

    }
}

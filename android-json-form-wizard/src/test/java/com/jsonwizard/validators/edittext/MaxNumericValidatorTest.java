package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;

import junit.framework.Assert;

import org.junit.Test;

public class MaxNumericValidatorTest extends BaseTest {
    @Test
    public void isValidShouldReturnFalseWhenNumberIsGreaterThanMaxSetValue(){

        MaxNumericValidator validator = new MaxNumericValidator("Default Error Message",1);
        Assert.assertEquals(false,validator.isValid("5",false));

    }
    @Test
    public void isValidShouldReturnTrueWhenNumberIsLessThanMaxSetValue(){

        MaxNumericValidator validator = new MaxNumericValidator("Default Error Message",50);
        Assert.assertEquals(true,validator.isValid("10",false));

    }

    @Test
    public void isValidShouldReturnTrueWhenNumberIsEqualToMaxSetValue(){

        MaxNumericValidator validator = new MaxNumericValidator("Default Error Message",16);
        Assert.assertEquals(true,validator.isValid("16",false));

    }

}

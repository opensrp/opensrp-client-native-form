package com.jsonwizard.validators.edittext;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.validators.edittext.LengthValidator;

import junit.framework.Assert;

import org.junit.Test;

public class LengthValidatorTest extends BaseTest {

    @Test
    public void isValidShouldReturnFalseWhenIsEmptyTrue() {

        LengthValidator validator = new LengthValidator("Default Error Message", 0, 50);
        Assert.assertEquals(false, validator.isValid("Native Form Test", true));

    }

    @Test
    public void isValidShouldReturnTrueWhenIsEmptyFalse() {

        LengthValidator validator = new LengthValidator("Default Error Message", 0, 50);
        Assert.assertEquals(true, validator.isValid("Native Form Test", false));
    }
}


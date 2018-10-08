package com.vijay.jsonwizard.validators.edittext;

import com.vijay.jsonwizard.widgets.MaterialEditTextFactory;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class MaxLengthValidator extends LengthValidator {

    public MaxLengthValidator(String errorMessage, int maxLength) {
        super(errorMessage, MaterialEditTextFactory.MIN_LENGTH, maxLength);
    }
}

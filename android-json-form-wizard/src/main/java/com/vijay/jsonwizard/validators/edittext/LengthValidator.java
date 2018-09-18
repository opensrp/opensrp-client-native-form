package com.vijay.jsonwizard.validators.edittext;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class LengthValidator extends METValidator {

    protected int minLength = 0;
    protected int maxLength = Integer.MAX_VALUE;

    public LengthValidator(String errorMessage, int minLength, int maxLength) {
        super(errorMessage);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(CharSequence charSequence, boolean isEmpty) {
        return charSequence != null ? !isEmpty && charSequence.length() >= minLength && charSequence.length() <= maxLength && charSequence != null
                : false;

    }
}

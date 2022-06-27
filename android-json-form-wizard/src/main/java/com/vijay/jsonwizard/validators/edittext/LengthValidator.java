package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class LengthValidator extends METValidator {

    protected int minLength = 0;
    protected int maxLength = Integer.MAX_VALUE;
    protected boolean isRequired;

    public LengthValidator(String errorMessage, int minLength, int maxLength, boolean isRequired) {
        super(errorMessage);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.isRequired = isRequired;
    }

    @Override
    public boolean isValid(@NonNull CharSequence charSequence, boolean isEmpty) {
        if (isRequired && isEmpty)
            return false;
        else if (!isRequired && isEmpty)
            return true;
        else
            return (charSequence != null && charSequence.length() >= minLength && charSequence.length() <= maxLength);
    }
}

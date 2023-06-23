package com.vijay.jsonwizard.validators.edittext;

import androidx.annotation.NonNull;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import org.jetbrains.annotations.NotNull;

public class ReferenceFieldValidator extends METValidator {
    MaterialEditText referenceField;
    public ReferenceFieldValidator(@NonNull @NotNull String errorMessage,MaterialEditText referenceField) {
        super(errorMessage);
        this.referenceField = referenceField;
    }

    @Override
    public boolean isValid(@NonNull @NotNull CharSequence charSequence, boolean isEmpty) {
        if(!isEmpty) {
            String referenceText = referenceField.getText().toString();
            return referenceText.equals(charSequence.toString());
        }
        return true;
    }
}

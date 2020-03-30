package com.vijay.jsonwizard.utils;

import org.joda.time.LocalDate;

/**
 * Holds numeric date picker validation params
 *
 * Created by ndegwamartin on 2020-03-20.
 */
public class NumericDatePickerValidator {

    private boolean isValid;
    private LocalDate selectedDate;
    private Violation violation;

    public enum Violation {
        MALFORMED_DATE,
        MAX_DATE,
        MIN_DATE
    }

    public NumericDatePickerValidator(boolean isValid, LocalDate selectedDate, Violation violation) {
        this.isValid = isValid;
        this.selectedDate = selectedDate;
        this.violation = violation;
    }

    public boolean isValid() {
        return isValid;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public Violation getViolation() {
        return violation;
    }
}

package com.vijay.jsonwizard.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.NumericDatePickerHelper;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by ndegwamartin on 2020-01-20.
 * Custom date picker widget that represents months as numeric digits
 */
public class NumericDatePicker extends DatePicker {

    private NumberPicker dayPicker;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;
    private long minDate;
    private long maxDate;
    private DatePicker.OnDateChangedListener onDateChangedListener;

    private static final LocalDate localDate = new LocalDate();

    private int minDay;
    private int minMonth;
    private int minYear;
    private int maxDay;
    private int maxMonth;
    private int maxYear;

    private final NumberPicker.OnValueChangeListener onValueChangeListener =
            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                    resetPickers();

                    if (onDateChangedListener != null) {
                        onDateChangedListener.onDateChanged(NumericDatePicker.this, getYear(), getMonth(), getDayOfMonth());
                    }
                }
            };

    private final NumberPicker.Formatter pickerDigitFormatter = new NumberPicker.Formatter() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.ENGLISH);
        final Object[] arguments = new Object[1];

        public String format(int value) {
            arguments[0] = value;
            stringBuilder.delete(0, stringBuilder.length());
            formatter.format("%02d", arguments);
            return formatter.toString();
        }
    };

    public NumericDatePicker(Context context) {
        super(context);
        setupView();
    }

    public NumericDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public NumericDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumericDatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView();

    }

    /**
     * Init's the widget components
     */
    private void setupView() {

        disableSuperCanvas();
        checkConstraintsConstraint();

        inflate(getContext(), R.layout.number_picker_view, this);

        dayPicker = initNumericPicker(R.id.day, 1, 30);

        monthPicker = initNumericPicker(R.id.month, 1, 12);

        yearPicker = initNumericPicker(R.id.year, 0, localDate.getYear() + 1000);

        updateDate(localDate.getYear(), localDate.getMonthOfYear() - 1, localDate.getDayOfMonth());

    }

    @VisibleForTesting
    public NumberPicker initNumericPicker(int viewId, int minConstraint, int maxConstraint) {

        NumberPicker numberPicker = findViewById(viewId);
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        setDateConstraints(numberPicker, minConstraint, maxConstraint);
        numberPicker.setFormatter(pickerDigitFormatter);
        numberPicker.setWrapSelectorWheel(true);

        return numberPicker;
    }

    /**
     * Disables view canvas of super
     */
    private void disableSuperCanvas() {
        ((NumericDatePicker) super.getRootView()).getChildAt(0).setVisibility(GONE);
    }

    /**
     * Helper,sets the pickers constraints individually
     *
     * @param numberPicker the number picker widget
     * @param value        value to set
     * @param isMax        boolean to flag max or min value
     */
    protected void setDateConstraints(NumberPicker numberPicker, int value, boolean isMax) {

        if (isMax) {
            numberPicker.setMaxValue(value);
        } else {
            numberPicker.setMinValue(value);
        }

    }

    /**
     * Helper,sets all the pickers constraints simultaneously
     *
     * @param numberPicker the number picker widget
     * @param minDateValue value to set
     * @param maxDateValue boolean to flag max or min value
     */
    protected void setDateConstraints(NumberPicker numberPicker, int minDateValue, int maxDateValue) {

        setDateConstraints(numberPicker, minDateValue, false);

        setDateConstraints(numberPicker, maxDateValue, true);

    }

    /**
     * Resets widgets to correct state
     */
    private void resetPickers() {

        checkConstraintsConstraint();

        if (getMinDate() != 0 || getMaxDate() != 0) {


            processResetPickersMaxDate();

            processResetPickersMinDate();
        }

        fixWeirdNumberPickerBug(dayPicker);
    }

    private void processResetPickersMinDate() {
        if (getMinDate() != 0) {

            //Cache max values
            int cacheMaxDay = dayPicker.getMaxValue();
            int cacheMaxMonth = monthPicker.getMaxValue();
            int cacheMaxYear = yearPicker.getMaxValue();


            yearPicker.setDisplayedValues(null);
            monthPicker.setDisplayedValues(null);
            dayPicker.setDisplayedValues(null);

            yearPicker.setMinValue(minYear);
            yearPicker.setWrapSelectorWheel(false);


            //reinstate minValues
            dayPicker.setMaxValue(cacheMaxDay);
            monthPicker.setMaxValue(cacheMaxMonth);
            yearPicker.setMaxValue(cacheMaxYear);


            if (minYear == getYear() && minMonth == getMonth()) {
                monthPicker.setMinValue(minMonth + 1);
                monthPicker.setValue(minMonth + 1);
                monthPicker.setWrapSelectorWheel(false);

                if (getDayOfMonth() <= minDay) {
                    dayPicker.setMinValue(minDay);
                    dayPicker.setValue(minDay);
                    dayPicker.setMaxValue(dayPicker.getMaxValue() > minDay ? minDay : dayPicker.getMaxValue());
                    dayPicker.setWrapSelectorWheel(false);
                } else {
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(dayPicker.getMaxValue());
                    dayPicker.setWrapSelectorWheel(getMaxDate() == 0 ? true : dayPicker.getWrapSelectorWheel());
                }

            } else {
                monthPicker.setMinValue(1);
                monthPicker.setMaxValue(monthPicker.getMaxValue());
                monthPicker.setWrapSelectorWheel(getMaxDate() == 0 ? true : monthPicker.getWrapSelectorWheel());

                dayPicker.setMinValue(1);
                dayPicker.setMaxValue(dayPicker.getMaxValue());
                dayPicker.setWrapSelectorWheel(getMaxDate() == 0 ? true : dayPicker.getWrapSelectorWheel());

                yearPicker.setMaxValue(yearPicker.getMaxValue());
            }
        }
    }

    private void processResetPickersMaxDate() {
        if (getMaxDate() != 0) {

            //cache min values
            int cacheMinDay = dayPicker.getMinValue();
            int cacheMinMonth = monthPicker.getMinValue();
            int cacheMinYear = yearPicker.getMinValue();

            yearPicker.setDisplayedValues(null);
            monthPicker.setDisplayedValues(null);
            dayPicker.setDisplayedValues(null);


            yearPicker.setMaxValue(maxYear);
            yearPicker.setWrapSelectorWheel(false);

            //reinstate minValues
            dayPicker.setMinValue(cacheMinDay);
            monthPicker.setMinValue(cacheMinMonth);
            yearPicker.setMinValue(cacheMinYear);


            if (maxYear == getYear() && maxMonth == getMonth()) {
                monthPicker.setMaxValue(maxMonth + 1);
                monthPicker.setValue(maxMonth + 1);
                monthPicker.setWrapSelectorWheel(false);

                if (getDayOfMonth() >= maxDay) {
                    dayPicker.setMaxValue(maxDay);
                    dayPicker.setValue(maxDay);
                    dayPicker.setWrapSelectorWheel(false);
                } else {
                    dayPicker.setMaxValue(NumericDatePickerHelper.getDaysInMonth(getMonth(), NumericDatePickerHelper.isLeapYear(getYear())));
                    dayPicker.setWrapSelectorWheel(true);

                }

            } else {
                monthPicker.setMaxValue(12);
                monthPicker.setWrapSelectorWheel(true);
                dayPicker.setMaxValue(NumericDatePickerHelper.getDaysInMonth(getMonth(), NumericDatePickerHelper.isLeapYear(getYear())));
                dayPicker.setWrapSelectorWheel(true);
            }


        }
    }

    /**
     * Weired bug where the day value doesn't show unless user touches widget
     * https://issuetracker.google.com/issues/36952035
     */
    private void fixWeirdNumberPickerBug(NumberPicker mPicker) {
        // Fix for bug in Android Picker where the first element is not shown
        EditText firstItem = (EditText) mPicker.getChildAt(0);
        if (firstItem != null) {
            firstItem.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void init(int year, int month, int dayOfMonth, DatePicker.OnDateChangedListener onDateChangedListener) {

        updateDate(year, month, dayOfMonth);

        setOnDateChangedListener(onDateChangedListener);
    }

    @Override
    public void setOnDateChangedListener(DatePicker.OnDateChangedListener onDateChangedListener) {

        this.onDateChangedListener = onDateChangedListener;
    }

    @Override
    public void updateDate(int year, int month, int dayOfMonth) {

        if (isMaxConstraintViolated(year, month + 1, dayOfMonth)) {
            throw new IllegalStateException("You have set a date later than the Maximum allowed date settings");
        }

        if (isMinConstraintViolated(year, month + 1, dayOfMonth)) {
            throw new IllegalStateException("You have set a date later than the Minimum allowed date settings");
        }

        dayPicker.setValue(dayOfMonth);
        monthPicker.setValue(month + 1);
        yearPicker.setValue(year);
        resetPickers();
    }

    /**
     * Checks to see if there is a minimum value set and whether its value is greater than the parameter provided
     *
     * @param year       year of date
     * @param month      month of date
     * @param dayOfMonth day of month of date
     * @return boolean value indicating whether the constraint was violated
     */
    private boolean isMinConstraintViolated(int year, int month, int dayOfMonth) {
        return minDate > 0 && (new LocalDate(year, month, dayOfMonth).isBefore(new LocalDate(minYear, minMonth, minDay)));
    }

    /**
     * Checks to see if there is a maximum value set and whether its value is less than the parameter provided
     *
     * @param year       year of date
     * @param month      month of date
     * @param dayOfMonth day of month of date
     */
    private boolean isMaxConstraintViolated(int year, int month, int dayOfMonth) {
        return maxDate > 0 && (new LocalDate(year, month, dayOfMonth).isAfter(new LocalDate(maxYear, maxMonth, maxDay)));
    }

    @Override
    public int getYear() {

        return yearPicker.getValue();
    }

    @Override
    public int getMonth() {
        return monthPicker.getValue() - 1;
    }

    @Override
    public int getDayOfMonth() {

        return dayPicker.getValue();
    }

    @Override
    public long getMinDate() {
        return minDate;
    }

    @Override
    public void setMinDate(long minDate) {

        this.minDate = minDate;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(minDate);

        minDay = cal.get(Calendar.DAY_OF_MONTH);
        minMonth = cal.get(Calendar.MONTH);
        minYear = cal.get(Calendar.YEAR);

        resetPickers();
    }

    @Override
    public long getMaxDate() {
        return this.maxDate;
    }

    @Override
    public void setMaxDate(long maxDate) {

        this.maxDate = maxDate;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(maxDate);

        maxDay = cal.get(Calendar.DAY_OF_MONTH);
        maxMonth = cal.get(Calendar.MONTH);
        maxYear = cal.get(Calendar.YEAR);

        resetPickers();

    }

    public void checkConstraintsConstraint() {
        if (minDate != 0 && maxDate != 0 && new Date(minDate).after(new Date(maxDate))) {
            throw new IllegalStateException("Min constrained date is greater than the Max constraint date");
        }
    }

    /**
     * Sets the current date of the spinner to be displayed
     *
     * @param date
     */
    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}

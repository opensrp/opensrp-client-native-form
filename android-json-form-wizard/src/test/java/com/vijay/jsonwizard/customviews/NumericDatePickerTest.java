package com.vijay.jsonwizard.customviews;

import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ndegwamartin on 2020-02-03.
 */
public class NumericDatePickerTest extends BaseTest {

    private AttributeSet attributeSet;

    @Mock
    private DatePicker.OnDateChangedListener onDateChangedListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        attributeSet = Robolectric.buildAttributeSet().addAttribute(R.attr.theme, "AppTheme").build();

    }

    @Test
    public void testNumericDatePickerConstructorInitsTodayDateCorrectly() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        LocalDate localDate = new LocalDate();

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(localDate.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(localDate.getYear(), datePicker.getYear());


        //Second constructor
        datePicker = new NumericDatePicker(RuntimeEnvironment.application, attributeSet);

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(localDate.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(localDate.getYear(), datePicker.getYear());


        //third constructor
        datePicker = new NumericDatePicker(RuntimeEnvironment.application, attributeSet, R.style.AppTheme);

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(localDate.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(localDate.getYear(), datePicker.getYear());

        //fourth constructor
        datePicker = new NumericDatePicker(RuntimeEnvironment.application, attributeSet, R.style.AppTheme, R.style.AppTheme);

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(localDate.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(localDate.getYear(), datePicker.getYear());


    }

    @Test
    public void testNumericDatePickerSetMaxValueLimitsCorrectly() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        LocalDate localDate = new LocalDate();
        long maxTimeStamp = localDate.plusYears(1).toDate().getTime();//Max date 1 year from today

        datePicker.setMaxDate(maxTimeStamp);

        Assert.assertTrue(datePicker.getMaxDate() > 0);
        Assert.assertEquals(maxTimeStamp, datePicker.getMaxDate());

        //Try set a date within max
        LocalDate dateFiveMonthsFromToday = localDate.plusMonths(5);
        datePicker.setDate(dateFiveMonthsFromToday.toDate());

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(dateFiveMonthsFromToday.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(dateFiveMonthsFromToday.getYear(), datePicker.getYear());

    }

    @Test
    public void testNumericDatePickerSetMaxValueDoesNotThrowExceptionDateMoreThanMax() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        LocalDate localDate = new LocalDate();
        long maxTimeStamp = localDate.plusDays(5).toDate().getTime();//Max date 1 year from today

        datePicker.setMaxDate(maxTimeStamp);

        Assert.assertTrue(maxTimeStamp > 0);
        Assert.assertEquals(maxTimeStamp, datePicker.getMaxDate());

        //Try set a date outside max
        Date dateTwoYearsFromToday = localDate.plusYears(2).toDate();
        datePicker.setDate(dateTwoYearsFromToday);
    }


    @Test
    public void testNumericDatePickerSetMinValueLimitsCorrectly() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        LocalDate localDate = new LocalDate();
        long minTimeStamp = localDate.minusYears(1).toDate().getTime();//Min date 1 year before today

        datePicker.setMinDate(minTimeStamp);

        Assert.assertTrue(datePicker.getMinDate() > 0);
        Assert.assertEquals(minTimeStamp, datePicker.getMinDate());

        //Try set a date within min
        LocalDate dateFiveMonthsBeforeToday = localDate.minusMonths(6);
        datePicker.setDate(dateFiveMonthsBeforeToday.toDate());

        Assert.assertEquals(localDate.getDayOfMonth(), datePicker.getDayOfMonth());
        Assert.assertEquals(dateFiveMonthsBeforeToday.getMonthOfYear() - 1, datePicker.getMonth());
        Assert.assertEquals(dateFiveMonthsBeforeToday.getYear(), datePicker.getYear());

    }

    @Test
    public void testNumericDatePickerSetMinValueDoesNotThrowsExceptionDateMoreThanMin() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        LocalDate localDate = new LocalDate();
        long minTimeStamp = localDate.minusYears(1).toDate().getTime();//Min date 1 year before today

        datePicker.setMinDate(minTimeStamp);

        Assert.assertTrue(minTimeStamp > 0);
        Assert.assertEquals(minTimeStamp, datePicker.getMinDate());

        //Try set a date prior to min date

        Date dateThreeYearsBeforeToday = localDate.minusYears(3).toDate();
        datePicker.setDate(dateThreeYearsBeforeToday);
    }

    @Test(expected = IllegalStateException.class)
    public void testNumericDatePickerSetMinMaxValueThrowsExceptionMinMoreThanMax() {

        //First constructor
        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);
        datePicker.setOnDateChangedListener(onDateChangedListener);

        LocalDate localDate = new LocalDate();

        datePicker.setMinDate(localDate.toDate().getTime());
        datePicker.setMaxDate(localDate.minusYears(1).toDate().getTime());
    }

    @Test
    public void testInitMethodInitializesDatePickerCorrectly() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);
        datePicker.init(2001, 8, 3, onDateChangedListener);


        Assert.assertEquals(3, datePicker.getDayOfMonth());
        Assert.assertEquals(8, datePicker.getMonth());
        Assert.assertEquals(2001, datePicker.getYear());

    }

    @Test
    public void testResetDatePickerCorrectsMalformedDateAfterSpinningMonth() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.FEBRUARY + 1);
        monthPicker.setTag(R.id.previous, Calendar.JANUARY + 1);

        NumberPicker dayPicker = ReflectionHelpers.getField(datePicker, "dayPicker");
        dayPicker.setValue(30);

        ReflectionHelpers.setField(datePicker, "previousDay", 1);

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.month);
        datePicker.resetDatePicker();

        Assert.assertEquals(1, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.FEBRUARY, datePicker.getMonth());

    }

    @Test
    public void testResetDatePickerCorrectsMalformedDateAfterSpinningDay() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.SEPTEMBER + 1);

        NumberPicker dayPicker = ReflectionHelpers.getField(datePicker, "dayPicker");
        dayPicker.setValue(31);
        dayPicker.setTag(R.id.previous, 30);

        ReflectionHelpers.setField(datePicker, "previousDay", 30);


        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.day);
        datePicker.resetDatePicker();

        Assert.assertEquals(30, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.SEPTEMBER, datePicker.getMonth());
    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMaxConstraintsViolatedAfterUpdatingYear() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "maxDate", 1405890000000l);

        ReflectionHelpers.setField(datePicker, "maxYear", 2014);

        ReflectionHelpers.setField(datePicker, "maxMonth", Calendar.JULY);

        ReflectionHelpers.setField(datePicker, "maxDay", 21);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(2020);

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.NOVEMBER + 1);

        Assert.assertEquals(Calendar.NOVEMBER, datePicker.getMonth());

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.year);
        datePicker.resetDatePicker();

        int dayOfMonthToday = new LocalDate().getDayOfMonth();
        int expectedDayOfMonth = dayOfMonthToday;
        if (dayOfMonthToday > 21) {
            expectedDayOfMonth = 21;
        }

        Assert.assertEquals(2014, datePicker.getYear());
        Assert.assertEquals(expectedDayOfMonth, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.JULY, datePicker.getMonth());

    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMaxConstraintsViolatedAfterUpdatingMonth() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "maxDate", 1595192400000L);

        ReflectionHelpers.setField(datePicker, "maxYear", 2020);

        ReflectionHelpers.setField(datePicker, "maxMonth", Calendar.AUGUST);

        ReflectionHelpers.setField(datePicker, "maxDay", 21);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(2020);

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.SEPTEMBER + 1);
        monthPicker.setTag(R.id.previous, Calendar.AUGUST + 1);

        Assert.assertEquals(Calendar.SEPTEMBER, datePicker.getMonth());

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.month);
        datePicker.resetDatePicker();

        int dayOfMonthToday = new LocalDate().getDayOfMonth();
        int expectedDayOfMonth = dayOfMonthToday;
        if (dayOfMonthToday > 21) {
            expectedDayOfMonth = 21;
        }

        Assert.assertEquals(expectedDayOfMonth, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.AUGUST, datePicker.getMonth());

    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMaxConstraintsViolatedAfterUpdatingDay() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "maxDate", 1592254800000l);

        ReflectionHelpers.setField(datePicker, "maxYear", 2020);

        ReflectionHelpers.setField(datePicker, "maxMonth", Calendar.JUNE);

        ReflectionHelpers.setField(datePicker, "maxDay", 16);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(2020);

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.JUNE + 1);

        NumberPicker dayPicker = ReflectionHelpers.getField(datePicker, "dayPicker");
        dayPicker.setValue(17);
        dayPicker.setTag(R.id.previous, 16);
        Assert.assertEquals(17, datePicker.getDayOfMonth());

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.day);
        datePicker.resetDatePicker();

        Assert.assertEquals(16, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.JUNE, datePicker.getMonth());

    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMinConstraintsViolatedAfterUpdatingYear() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "minDate", 1360530000000l);

        ReflectionHelpers.setField(datePicker, "minYear", 2013);

        ReflectionHelpers.setField(datePicker, "minMonth", Calendar.FEBRUARY);

        ReflectionHelpers.setField(datePicker, "minDay", 11);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(2010);

        Assert.assertEquals(2010, datePicker.getYear());

        NumberPicker dayPicker = ReflectionHelpers.getField(datePicker, "dayPicker");
        dayPicker.setValue(17);

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.year);
        datePicker.resetDatePicker();

        Assert.assertEquals(2013, datePicker.getYear());
        Assert.assertEquals(17, datePicker.getDayOfMonth());

    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMinConstraintsViolatedAfterUpdatingMonth() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "minDate", 1099515600000l);

        ReflectionHelpers.setField(datePicker, "minYear", 2004);

        ReflectionHelpers.setField(datePicker, "minMonth", Calendar.NOVEMBER);

        ReflectionHelpers.setField(datePicker, "minDay", 4);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(2004);

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.OCTOBER + 1);
        monthPicker.setTag(R.id.previous, Calendar.NOVEMBER + 1);

        Assert.assertEquals(Calendar.OCTOBER, datePicker.getMonth());

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.month);
        datePicker.resetDatePicker();

        Assert.assertEquals(Calendar.NOVEMBER, datePicker.getMonth());

    }

    @Test
    public void testResetDatePickerCreatesValidDateWhenMinConstraintsViolatedAfterUpdatingDay() {

        NumericDatePicker datePicker = new NumericDatePicker(RuntimeEnvironment.application);

        //Set malformed date

        ReflectionHelpers.setField(datePicker, "minDate", 815000400000l);

        ReflectionHelpers.setField(datePicker, "minYear", 1995);

        ReflectionHelpers.setField(datePicker, "minMonth", Calendar.OCTOBER);

        ReflectionHelpers.setField(datePicker, "minDay", 30);

        NumberPicker yearPicker = ReflectionHelpers.getField(datePicker, "yearPicker");
        yearPicker.setValue(1995);

        NumberPicker monthPicker = ReflectionHelpers.getField(datePicker, "monthPicker");
        monthPicker.setValue(Calendar.OCTOBER + 1);

        NumberPicker dayPicker = ReflectionHelpers.getField(datePicker, "dayPicker");
        dayPicker.setValue(29);
        dayPicker.setTag(R.id.previous, 30);
        Assert.assertEquals(29, datePicker.getDayOfMonth());

        ReflectionHelpers.setField(datePicker, "changedPickerId", R.id.day);
        datePicker.resetDatePicker();

        Assert.assertEquals(30, datePicker.getDayOfMonth());
        Assert.assertEquals(Calendar.OCTOBER, datePicker.getMonth());

    }
}
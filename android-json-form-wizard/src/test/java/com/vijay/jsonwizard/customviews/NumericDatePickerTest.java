package com.vijay.jsonwizard.customviews;

import android.util.AttributeSet;
import android.widget.DatePicker;

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
}

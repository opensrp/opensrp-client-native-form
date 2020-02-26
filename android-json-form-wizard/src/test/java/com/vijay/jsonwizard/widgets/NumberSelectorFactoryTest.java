package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class NumberSelectorFactoryTest extends BaseTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTextWhenStartNumberIsOne() {
        NumberSelectorFactory numberSelectorFactory = Mockito.spy(new NumberSelectorFactory());
        String test = "3";
        int item = 2;
        int startSelectionNumber = 1;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = numberSelectorFactory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);

    }

    @Test
    public void testGetTextWhenStartNumberIsZero() {
        NumberSelectorFactory numberSelectorFactory = Mockito.spy(new NumberSelectorFactory());
        String test = "2";
        int item = 2;
        int startSelectionNumber = 0;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = numberSelectorFactory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsMoreThanOne() {
        NumberSelectorFactory numberSelectorFactory = Mockito.spy(new NumberSelectorFactory());
        String test = "12";
        int item = 2;
        int startSelectionNumber = 10;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = numberSelectorFactory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsLessThanZero() {
        NumberSelectorFactory numberSelectorFactory = Mockito.spy(new NumberSelectorFactory());
        String test = "-2";
        int item = 2;
        int startSelectionNumber = -4;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = numberSelectorFactory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }

    @Test
    public void testGetTextWhenStartNumberIsEqualTONumberOfSelector() {
        NumberSelectorFactory numberSelectorFactory = Mockito.spy(new NumberSelectorFactory());
        String test = "5+";
        int item = 4;
        int startSelectionNumber = 1;
        int numberOfSelectors = 5;
        int maxValue = 20;

        String number = numberSelectorFactory.getText(item, startSelectionNumber, numberOfSelectors, maxValue);
        Assert.assertEquals(test, number);
    }
}

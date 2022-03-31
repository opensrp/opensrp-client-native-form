package com.vijay.jsonwizard.views;

import com.vijay.jsonwizard.BaseTest;

import org.junit.Test;
import org.mockito.Mockito;

public class CustomTextViewTest extends BaseTest {

    @Test
    public void testSetText() {
        String text = "<testing>";
        CustomTextView mockedView = Mockito.mock(CustomTextView.class);
        mockedView.setText(text);
        Mockito.verify(mockedView, Mockito.times(1)).setText(text);


    }
}

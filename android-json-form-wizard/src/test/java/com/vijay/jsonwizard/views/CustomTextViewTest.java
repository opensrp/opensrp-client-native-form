package com.vijay.jsonwizard.views;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.vijay.jsonwizard.BaseTest;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

public class CustomTextViewTest extends BaseTest {
    @Mock
    private Context context;

    @Before
    public void setUp() throws JSONException {
        Application application = Mockito.spy(Application.class);
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(context).when(application).getApplicationContext();
    }

    @Test
    public void testSetText() {
        CustomTextView customTextView = new CustomTextView(RuntimeEnvironment.application.getApplicationContext());
        String text = "<testing>";
        customTextView.setText(text);
        Assert.assertEquals("<testing>", customTextView.getText());
    }

    @Test
    public void testSetTextColor() {
        CustomTextView customTextView = new CustomTextView(RuntimeEnvironment.application.getApplicationContext());
        int expectedTextColor = Color.parseColor("#00ff00");
        customTextView.setTextColor(expectedTextColor);
        Assert.assertEquals(expectedTextColor, customTextView.getTextColors().getDefaultColor());
    }
}

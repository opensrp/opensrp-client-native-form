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
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

public class CustomTextViewTest extends BaseTest {
    @Mock
    private Context context;
    private CustomTextView customTextView;
    @Mock
    private CustomTextView customTextViewMock;

    @Before
    public void setUp() throws JSONException {
        customTextView = new CustomTextView(RuntimeEnvironment.application.getApplicationContext());
        Application application = Mockito.spy(Application.class);
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(context).when(application).getApplicationContext();
        Whitebox.setInternalState(customTextView, "hintOnText", true);
        Mockito.doReturn(true).when(customTextViewMock).isEnabled();
    }

    @Test
    public void testSetText() {
        String text = "test<testing>test";
        customTextView.setText(text);
        Assert.assertEquals("test<testing>test", customTextView.getText().toString());
    }

    @Test
    public void testSetTextColor() {
        customTextView = new CustomTextView(RuntimeEnvironment.application.getApplicationContext());
        int expectedTextColor = Color.parseColor("#00ff00");
        customTextView.setTextColor(expectedTextColor);
        Assert.assertEquals(expectedTextColor, customTextView.getTextColors().getDefaultColor());
    }

    @Test
    public void testIsHintOnText() {
        boolean isHintOnText = customTextView.isHintOnText();
        Assert.assertTrue(isHintOnText);
    }
}

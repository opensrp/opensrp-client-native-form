package com.vijay.jsonwizard.views;

import android.app.Application;
import android.content.Context;

import com.vijay.jsonwizard.BaseTest;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CustomTextViewTest extends BaseTest {
    @Mock
    private CustomTextView customTextView;
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
        String text = "<testing>";
        customTextView.setText(text);
        Mockito.verify(customTextView, Mockito.times(1)).setText(text);
    }
}

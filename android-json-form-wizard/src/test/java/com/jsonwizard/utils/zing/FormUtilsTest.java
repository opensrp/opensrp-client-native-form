package com.jsonwizard.utils.zing;

import android.content.Context;
import android.content.res.Resources;
import android.test.mock.MockContext;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.utils.FormUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class FormUtilsTest extends BaseTest {

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private DisplayMetrics displayMetrics;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @PrepareForTest({FormUtils.class, TypedValue.class})
    @Test
    public void testSpToPx() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        float sp = 30.0f;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.mockStatic(TypedValue.class);
        PowerMockito.when(FormUtils.spToPx(context, sp)).thenReturn(30);

        int px = FormUtils.spToPx(context, sp);
        Assert.assertEquals(30, px);
        PowerMockito.verifyStatic(FormUtils.class, VerificationModeFactory.times(1));
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }

    @PrepareForTest({FormUtils.class, TypedValue.class})
    @Test
    public void testDpToPixels() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        float dp = 30.0f;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.spToPx(context, dp)).thenReturn(30);

        int px = FormUtils.spToPx(context, dp);
        Assert.assertEquals(30, px);
    }

    @PrepareForTest({FormUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnSpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String spString = "30sp";
        int expected = 30;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getValueFromSpOrDpOrPx(spString, context)).thenReturn(expected);

        int px = FormUtils.getValueFromSpOrDpOrPx(spString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({FormUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithADpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String dpString = "30dp";
        int expected = 30;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getValueFromSpOrDpOrPx(dpString, context)).thenReturn(expected);

        int px = FormUtils.getValueFromSpOrDpOrPx(dpString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({FormUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAPxInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String pxString = "30px";
        int expected = 30;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getValueFromSpOrDpOrPx(pxString, context)).thenReturn(expected);

        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({FormUtils.class, TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String string = "String";
        int expected = 0;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(FormUtils.getValueFromSpOrDpOrPx(string, context)).thenReturn(expected);
        PowerMockito.when(!TextUtils.isEmpty(string)).thenReturn(false);

        int px = FormUtils.getValueFromSpOrDpOrPx(string, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class, FormUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithEmptyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String string = "";
        int expected = 0;
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(FormUtils.getValueFromSpOrDpOrPx(string, context)).thenReturn(expected);
        PowerMockito.when(!TextUtils.isEmpty(string)).thenReturn(false);

        int px = FormUtils.getValueFromSpOrDpOrPx(string, context);
        Assert.assertEquals(expected, px);
    }
}

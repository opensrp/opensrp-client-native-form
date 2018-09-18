package com.jsonwizard.utils.zing;

import android.content.Context;
import android.content.res.Resources;
import android.test.mock.MockContext;
import android.text.TextUtils;
import android.util.DisplayMetrics;

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

    @Test
    public void testSpToPx() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        float sp = 30;
        int px = FormUtils.spToPx(context, sp);
        Assert.assertEquals(0, px);
    }

    @PrepareForTest({FormUtils.class, TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnSpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.mockStatic(TextUtils.class);

        String sp = "30sp";
        int px = FormUtils.getValueFromSpOrDpOrPx(sp, context);
        Assert.assertEquals(30, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithADpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String dp = "30dp";
        int px = FormUtils.getValueFromSpOrDpOrPx(dp, context);
        Assert.assertEquals(30, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithAPxInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String pxString = "30px";
        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(30, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithAnyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String pxString = "String";
        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(0, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithEmptyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        PowerMockito.verifyStatic(TextUtils.class);
        String pxString = "";
        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(0, px);
        PowerMockito.verifyStatic(TextUtils.class, VerificationModeFactory.times(1));
        TextUtils.isEmpty(pxString);
    }
}

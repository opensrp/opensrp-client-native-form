package com.jsonwizard.utils.zing;

import android.content.Context;
import android.content.res.Resources;
import android.test.mock.MockContext;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.FormUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

    @PrepareForTest({TextUtils.class, TypedValue.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnSpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        String spString = "30sp";
        int expected = 30;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(TypedValue.class);
        PowerMockito.when(!TextUtils.isEmpty(spString)).thenReturn(false);
        PowerMockito.when(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, expected, displayMetrics)).thenReturn(Float
                .valueOf(expected));

        int px = FormUtils.getValueFromSpOrDpOrPx(spString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class, TypedValue.class, FormUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithADpInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();

        String dpString = "30dp";
        int expected = 0;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(TypedValue.class);
        PowerMockito.when(!TextUtils.isEmpty(dpString)).thenReturn(false);
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        int px = FormUtils.getValueFromSpOrDpOrPx(dpString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAPxInput() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(20.0f).when(resources).getDimension(R.dimen.default_label_text_size);

        String pxString = "30px";
        int expected = 30;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(pxString)).thenReturn(false);

        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(20.0f).when(resources).getDimension(R.dimen.default_label_text_size);

        String string = "String";
        int expected = 20;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(string)).thenReturn(false);

        int px = FormUtils.getValueFromSpOrDpOrPx(string, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithEmptyString() {
        MockContext mockContext = Mockito.spy(MockContext.class);
        Mockito.doReturn(context).when(mockContext).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(20.0f).when(resources).getDimension(R.dimen.default_label_text_size);

        String string = "";
        int expected = 20;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(string)).thenReturn(false);

        int px = FormUtils.getValueFromSpOrDpOrPx(string, context);
        Assert.assertEquals(expected, px);
    }
}

package com.jsonwizard.utils.zing;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
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
import org.powermock.reflect.Whitebox;

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

    @PrepareForTest({TypedValue.class})
    @Test
    public void testSpToPx() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        float spString = 30.0f;
        int expected = 30;
        PowerMockito.mockStatic(TypedValue.class);
        PowerMockito.when(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, expected, displayMetrics)).thenReturn(Float
                .valueOf(expected));

        int px = FormUtils.spToPx(context, spString);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TypedValue.class})
    @Test
    public void testDpToPx() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        Whitebox.setInternalState(displayMetrics, "density", 5);

        float dpString = 30.0f;
        int expected = 150;

        int px = FormUtils.dpToPixels(context, dpString);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class, TypedValue.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAnSpInput() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
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
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        String dpString = "30dp";
        int expected = 150;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(TypedValue.class);
        PowerMockito.when(!TextUtils.isEmpty(dpString)).thenReturn(false);
        Whitebox.setInternalState(displayMetrics, "density", 5);

        int px = FormUtils.getValueFromSpOrDpOrPx(dpString, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDpOrPxWithAPxInput() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
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
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();
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
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();

        String string = "";
        int expected = 0;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(string)).thenReturn(true);

        int px = FormUtils.getValueFromSpOrDpOrPx(string, context);
        Assert.assertEquals(expected, px);
    }

    @PrepareForTest({TextUtils.class})
    @Test
    public void testGetValueFromSpOrDPOrPxwithNull() {
        Application application = Mockito.spy(Application.class);
        Mockito.doReturn(context).when(application).getApplicationContext();

        int expected = 0;
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(null)).thenReturn(true);

        int px = FormUtils.getValueFromSpOrDpOrPx(null, context);
        Assert.assertEquals(expected, px);

    }
}
